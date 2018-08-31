package jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.client.ComputingApi;
import app.common.Authorization;
import app.common.utils.StringUtils;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.security.TokenModel;
import common.constants.SystemConstants;
import common.utils.UserCredentialsReader;
import models.ExecutorModel;
import models.ImageModel;
import models.InstanceModel;
import play.Logger;

public class InstanceCreationJob {
    
    private static int MAX_SIMULTANEOUS_JOBS = 10;
    private static ExecutorService threadPool = 
            Executors.newFixedThreadPool(MAX_SIMULTANEOUS_JOBS);
    
    public static synchronized void create(InstanceModel instance) {        
        threadPool.submit(new Job(instance));
    }
    
    public static class Job implements Runnable {
        
        private InstanceModel instance;
        
        public Job(InstanceModel instance) {
            super();
            this.instance = instance;
        }

        @Override
        public void run() {
            createCloudInstance();
        }        
        
        private void createCloudInstance() {   

            ComputingApi api = new ComputingApi(instance.getPlugin().getUrl());
            List<PluginComputingInstanceModel> instancesToCreate = new ArrayList();
            PluginComputingInstanceModel instanceToCreate =
                    createPluginInstance(instance);
            instancesToCreate.add(instanceToCreate);

            UserCredentialsReader credentialReader =
                    new UserCredentialsReader(
                            instance.getPlugin(),
                            instance.getCredentialUsage());

            try {
                for(String credential : credentialReader) {

                    TokenModel token;
                        token = Authorization.getToken(
                                instance.getPlugin().getCloudType(),
                                instance.getPlugin().getInstanceWriteScope(),
                                credential);

                    Body<List<PluginComputingInstanceModel>> body =
                            api.createInstances(
                                    token.getToken(),
                                    token.getIdentity(),
                                    instancesToCreate);
                    if(body.getContent() == null || body.getContent().isEmpty()) {
                        continue;
                    }

                    PluginComputingInstanceModel instanceCreated =
                            body.getContent().get(0);

                    instance.setCloudInstanceName(instanceCreated.getName());
                    instance.setCloudInstanceIp(instanceCreated.getExternalIp());
                    instance.save();
                    break;
                }
            } catch (Exception e) {
                Logger.warn(e, "Operation cannot be completed with credential [%s]", e.getMessage());     
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
