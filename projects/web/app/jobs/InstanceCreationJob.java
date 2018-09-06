package jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Hibernate;

import app.client.ComputingApi;
import app.client.ExecutionApi;
import app.common.Authorization;
import app.common.Pair;
import app.common.utils.StringUtils;
import app.models.Body;
import app.models.Command;
import app.models.PluginComputingInstanceModel;
import app.models.SecureCoordinatorAccess;
import app.models.security.TokenModel;
import common.constants.SystemConstants;
import controllers.guest.ExternalAccessController;
import models.ApplicationArgumentsModel;
import models.ApplicationFileInputModel;
import models.ExecutorModel;
import models.ImageModel;
import models.InstanceModel;
import models.SettingModel;
import models.SettingModel.Name;
import models.SpaceFileModel;
import models.VwCredentialModel;
import play.Logger;
import play.Play;
import play.mvc.Router;

public class InstanceCreationJob {
    
    private static int MAX_SIMULTANEOUS_JOBS = 10;
    private static ExecutorService threadPool = 
            Executors.newFixedThreadPool(MAX_SIMULTANEOUS_JOBS);
    private static final String MACHINE_ID = "%s@machine";
    private static final String DEFAULT_EXECUTOR_PORT = 
            Play.configuration.getProperty("executor.port", "8181");
    
    private static final int MAX_ATTEMPTS = 30;
    private static final int TIME_BETWEEN_ATTEMPTS = 30 * 1000; // half minute
    
    
    public static synchronized void create(InstanceModel instance, Long userId) {    
        Hibernate.initialize(instance.getExecutor().getListImages());
        threadPool.submit(new Job(instance, userId));
    }
    
    private static class Job implements Runnable {
        
        private InstanceModel instance;
        private Long userId;
        private String baseUrl;
        private String refreshStatusUrl;  
        private String refreshTokenUrl;  
        
        public Job(InstanceModel instance, Long userId) {
            super();
            this.instance = instance;
            this.userId = userId;
            this.baseUrl = SettingModel.getStringSetting(Name.setting_external_url);
            this.refreshStatusUrl = 
                    baseUrl +
                    Router.reverse("guest.ExternalAccessController.refreshStatus").url; 
            this.refreshTokenUrl = 
                    baseUrl +
                    Router.reverse("guest.ExternalAccessController.refreshToken").url;
        }

        @Override
        public void run() {
            createCloudInstance();
            Command command = 
                    generateCommandToExecute();
            executeCommand(command);
        }        
        
        private void executeCommand(Command command) {
            
            ExecutionApi executorApi = new ExecutionApi(
                    instance.getCloudInstanceIp() + ":" + DEFAULT_EXECUTOR_PORT);
            
            int attempts = 0;
            while(attempts++ < MAX_ATTEMPTS) {
                try {                    
                    Body<Boolean> body = 
                            executorApi.startExecution(command);                    
                    if(body == null || body.getContent() == null) {
                        continue;
                    }    
                } catch (IOException e) {
                    Logger.warn("Command execution attempt failed, attempting [%s]", attempts);
                }
            }
        }
        
        private Command generateCommandToExecute() {
            
            ApplicationArgumentsModel arguments = 
                    instance.getApplicationArguments();
            
            ExecutorModel executor = 
                    instance.getExecutor();
            
            Command command = new Command();
            command.setArgs(arguments.getArguments());
            command.setCommandLine(executor.getCommandLine());
            command.setExecutionScript(executor.getExecutionScript());
            command.setRefreshStatusUrl(refreshStatusUrl);
            command.setSecureFileAccess(
                    generateSecureAccess(
                            instance.getInstanceIdentity(),
                            refreshTokenUrl));     
            command.setListInputPathsWithExtension(
                    generateInputPaths(arguments));
            
            return command;
        }

        private List<Pair<String, String>> generateInputPaths(
                ApplicationArgumentsModel arguments) {
            List<Pair<String, String>> listInputs = new ArrayList<>();
            for(ApplicationFileInputModel input : arguments.getApplicationInputFiles()) {
                
                SpaceFileModel spaceFile = input.getSpaceFile();
                Long spaceFileId = spaceFile.getId();
                String extension = FilenameUtils.getExtension(spaceFile.getName());
                Map<String, Object> args = new HashMap();
                args.put("id", spaceFileId);
                String inputUrl = 
                        baseUrl +
                        Router.reverse("guest.ExternalAccessController.download", args).url;
                
                listInputs.add(new Pair<String, String>(inputUrl, extension));                
            }
            return listInputs;
        }
        
        private static SecureCoordinatorAccess generateSecureAccess(
                String identity, 
                String refreshTokenUrl) {
            
            String token = 
                    ExternalAccessController.generateToken(identity);
            SecureCoordinatorAccess secureAccess = 
                    new SecureCoordinatorAccess(token, refreshTokenUrl);
            return secureAccess;
        }
        
        private void createCloudInstance() {   

            try {
                
                ComputingApi api = new ComputingApi(instance.getPlugin().getUrl());
                PluginComputingInstanceModel instanceToCreate =
                        createPluginInstance(instance);
                
                List<VwCredentialModel> listCredentials = 
                        VwCredentialModel.searchForCurrentUserAndPlugin(
                                instance.getPlugin().getId(),
                                instance.getCredentialUsage());            

                for(VwCredentialModel vwCredential : listCredentials) {

                    try {
                        String credentialData = vwCredential
                                .getCredentialData()
                                .getContentAsString();
                    
                        TokenModel token;
                            token = Authorization.getToken(
                                    instance.getPlugin().getCloudType(),
                                    instance.getPlugin().getInstanceWriteScope(),
                                    credentialData);
    
                        Body<PluginComputingInstanceModel> body =
                                api.createInstance(
                                        token.getToken(),
                                        token.getIdentity(),
                                        instanceToCreate);
                        if(body == null || body.getContent() == null) {
                            continue;
                        }

                        PluginComputingInstanceModel instanceCreated =
                                body.getContent();

                        instance.setCloudInstanceName(instanceCreated.getName());
                        instance.setCloudInstanceIp(instanceCreated.getExternalIp());
                        instance.setCredential(vwCredential.getCredential());
                        instance.save();
                        instance.setInstanceIdentity(
                                String.format(MACHINE_ID, instance.getId()));
                        break;        
                    
                    } catch (Exception e) {
                        Logger.info(e, "Credential [%s] cannot be used for user [%s] => [%s]", 
                                vwCredential.getId(),
                                vwCredential.getUser().getId(),
                                e.getMessage());  
                    }            
                }
            } catch (Exception e) {
                Logger.warn(e, "Instance cannot be created [%s]", e.getMessage());     
            }
        }

        private static PluginComputingInstanceModel createPluginInstance(InstanceModel instance){

            PluginComputingInstanceModel res = new PluginComputingInstanceModel();

            for(ImageModel image : instance.getExecutor().getListImages()) {
                if(image.getPlugin().getId() != instance.getPlugin().getId()) {
                    continue;
                }
                res.setImageUrl(image.getUrl());
                break;
            }

            ExecutorModel executor = instance.getExecutor();
            res.setFirewallUdpPorts(
                    StringUtils.splitToIntList(
                            executor.getFirewallUdpRules(),
                            SystemConstants.SPLIT_EXP_COMMA));
            res.setFirewallTcpPorts(
                    StringUtils.splitToIntList(
                            executor.getFirewallTcpRules(),
                            SystemConstants.SPLIT_EXP_COMMA));
            res.setMachineType(instance.getTypeName());
            res.setType(instance.getTypeName());
            res.setRegion(instance.getRegionName());
            res.setZone(instance.getZoneName());
            res.setStartupScript(instance.getExecutor().getStartupScript());

            return res;
        }
    }
}
