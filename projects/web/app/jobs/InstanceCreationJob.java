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
import models.ApplicationFileOutputModel;
import models.ExecutorModel;
import models.ImageModel;
import models.InstanceModel;
import models.SettingModel;
import models.SettingModel.Name;
import models.SpaceFileModel;
import models.VwCredentialModel;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.mvc.Router;

public class InstanceCreationJob {

    private static int MAX_SIMULTANEOUS_JOBS = 10;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_JOBS);
    private static final String MACHINE_ID = "%s@machine";
    private static final String DEFAULT_EXECUTOR_PORT = Play.configuration.getProperty("executor.port", "8181");

    private static final int MAX_ATTEMPTS = 30;
    private static final int TIME_BETWEEN_ATTEMPTS = 30 * 1000; // half minute

    public static synchronized void create(final InstanceModel instance, final Long userId) {

        Hibernate.initialize(instance.getExecutor().getListImages());
        final List<ApplicationFileInputModel> lstInputFile = instance.getApplicationArguments().getApplicationInputFiles();
        for (final ApplicationFileInputModel element : lstInputFile) {
            Hibernate.initialize(element.getSpaceFile());
            Hibernate.initialize(element.getSpaceFile().getSpace());
        }
        final List<ApplicationFileOutputModel> lstOutputFile = instance.getApplicationArguments().getApplicationOutputFiles();
        for (final ApplicationFileOutputModel element : lstOutputFile) {
            Hibernate.initialize(element.getSpaceFile());
            Hibernate.initialize(element.getSpaceFile().getSpace());
        }
        threadPool.submit(new Job(instance, userId));
    }

    private static class Job implements Runnable {

        private InstanceModel instance;
        private final Long userId;
        private final String baseUrl;
        private final String refreshStatusUrl;
        private final String refreshTokenUrl;

        public Job(final InstanceModel instance, final Long userId) {
            super();
            this.instance = instance;
            this.userId = userId;
            this.baseUrl = SettingModel.getStringSetting(Name.setting_external_url);
            this.refreshStatusUrl = this.baseUrl +
                    Router.reverse("guest.ExternalAccessController.refreshStatus").url;
            this.refreshTokenUrl = this.baseUrl +
                    Router.reverse("guest.ExternalAccessController.refreshToken").url;
        }

        @Override
        public void run() {
            this.createCloudInstance();
            final Command command = this.generateCommandToExecute();
            this.executeCommand(command);
        }

        private void executeCommand(final Command command) {

            final ExecutionApi executorApi = new ExecutionApi(
                    this.instance.getCloudInstanceIp() + ":" + DEFAULT_EXECUTOR_PORT);

            int attempts = 0;
            while (attempts++ < MAX_ATTEMPTS) {
                try {
                    final Body<Boolean> body = executorApi.startExecution(command);
                    if (body == null || body.getContent() == null) {
                        continue;
                    }
                } catch (final IOException e) {
                    Logger.warn("Command execution attempt failed, attempting [%s]", attempts);
                }
            }
        }

        private Command generateCommandToExecute() {

            final ApplicationArgumentsModel arguments = this.instance.getApplicationArguments();

            final ExecutorModel executor = this.instance.getExecutor();

            final Command command = new Command();
            command.setArgs(arguments.getArguments());
            command.setCommandLine(executor.getCommandLine());
            command.setExecutionScript(executor.getExecutionScript());
            command.setRefreshStatusUrl(this.refreshStatusUrl);
            command.setSecureFileAccess(
                    generateSecureAccess(
                            this.instance.getInstanceIdentity(),
                            this.refreshTokenUrl));
            command.setListInputPathsWithExtension(
                    this.generateInputPaths(arguments));

            return command;
        }

        private List<Pair<String, String>> generateInputPaths(
                final ApplicationArgumentsModel arguments) {
            final List<Pair<String, String>> listInputs = new ArrayList<>();
            for (final ApplicationFileInputModel input : arguments.getApplicationInputFiles()) {

                final SpaceFileModel spaceFile = input.getSpaceFile();
                final Long spaceFileId = spaceFile.getId();
                final String extension = FilenameUtils.getExtension(spaceFile.getName());
                final Map<String, Object> args = new HashMap();
                args.put("id", spaceFileId);
                final String inputUrl = this.baseUrl +
                        Router.reverse("guest.ExternalAccessController.download", args).url;

                listInputs.add(new Pair<>(inputUrl, extension));
            }
            return listInputs;
        }

        private static SecureCoordinatorAccess generateSecureAccess(
                final String identity,
                final String refreshTokenUrl) {

            final String token = ExternalAccessController.generateToken(identity);
            final SecureCoordinatorAccess secureAccess = new SecureCoordinatorAccess(token, refreshTokenUrl);
            return secureAccess;
        }

        private void createCloudInstance() {

            try {

                JPA.startTx("default", false);
                //                this.instance = this.instance.merge();
                this.instance = InstanceModel.findById(this.instance.getId());

                final ComputingApi api = new ComputingApi(this.instance.getPlugin().getUrl());
                final PluginComputingInstanceModel instanceToCreate = createPluginInstance(this.instance);

                final List<VwCredentialModel> listCredentials = VwCredentialModel.searchUserAndPlugin(
                        this.userId,
                        this.instance.getPlugin().getId(),
                        this.instance.getCredentialUsage());

                for (final VwCredentialModel vwCredential : listCredentials) {

                    try {
                        final String credentialData = vwCredential
                                .getCredentialData()
                                .getContentAsString();

                        TokenModel token;
                        token = Authorization.getToken(
                                this.instance.getPlugin().getCloudType(),
                                this.instance.getPlugin().getInstanceWriteScope(),
                                credentialData);

                        final Body<PluginComputingInstanceModel> body = api.createInstance(
                                token.getToken(),
                                token.getIdentity(),
                                instanceToCreate);
                        if (body == null || body.getContent() == null) {
                            continue;
                        }

                        final PluginComputingInstanceModel instanceCreated = body.getContent();

                        this.instance.setCloudInstanceName(instanceCreated.getName());
                        this.instance.setCloudInstanceIp(instanceCreated.getExternalIp());
                        this.instance.setCredential(vwCredential.getCredential());
                        this.instance.setInstanceIdentity(String.format(MACHINE_ID, this.instance.getId()));
                        this.instance.save();
                        break;

                    } catch (final Exception e) {
                        Logger.info(e, "Credential [%s] cannot be used for user [%s] => [%s]",
                                vwCredential.getId(),
                                vwCredential.getUser().getId(),
                                e.getMessage());
                    }
                }
            } catch (final Exception e) {
                JPA.setRollbackOnly();
                Logger.warn(e, "Instance cannot be created [%s]", e.getMessage());
            } finally {
                JPA.closeTx("default");
            }
        }

        private static PluginComputingInstanceModel createPluginInstance(final InstanceModel instance) {

            final PluginComputingInstanceModel res = new PluginComputingInstanceModel();

            for (final ImageModel image : instance.getExecutor().getListImages()) {
                if (image.getPlugin().getId() != instance.getPlugin().getId()) {
                    continue;
                }
                res.setImageUrl(image.getUrl());
                break;
            }

            final ExecutorModel executor = instance.getExecutor();
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
