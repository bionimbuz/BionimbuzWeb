package jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

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
import play.jobs.Job;
import play.mvc.Router;

public class InstanceCreationJob extends Job {

    private static final int MAX_ATTEMPTS = 30;
    private static final String MACHINE_ID = "%s@machine";
    private static final String DEFAULT_EXECUTOR_PORT = Play.configuration.getProperty("executor.port", "8181");
    private static final String BASE_URL = SettingModel.getStringSetting(Name.setting_external_url);
    private static final String REFRESH_STATUS_URL = BASE_URL + Router.reverse("guest.ExternalAccessController.refreshStatus").url;
    private static final String REFRESH_TOKEN_URL = BASE_URL + Router.reverse("guest.ExternalAccessController.refreshToken").url;

    private final Long instanceId;
    private final Long userId;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors.
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public InstanceCreationJob(final Long instanceId, final Long userId) {
        super();
        this.instanceId = instanceId;
        this.userId = userId;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see play.jobs.Job#doJob()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void doJob() {

        final InstanceModel instance = this.createCloudInstance();
        final Command command = InstanceCreationJob.generateCommandToExecute(instance);
        InstanceCreationJob.executeCommand(instance, command);
    }

    private InstanceModel createCloudInstance() {

        final InstanceModel instance = InstanceModel.findById(this.instanceId);
        try {

            final ComputingApi api = new ComputingApi(instance.getPlugin().getUrl());
            final PluginComputingInstanceModel instanceToCreate = createPluginInstance(instance);
            final List<VwCredentialModel> listCredentials = VwCredentialModel.searchUserAndPlugin(
                    this.userId,
                    instance.getPlugin().getId(),
                    instance.getCredentialUsage());

            for (final VwCredentialModel vwCredential : listCredentials) {

                try {
                    final String credentialData = vwCredential
                            .getCredentialData()
                            .getContentAsString();

                    TokenModel token;
                    token = Authorization.getToken(
                            instance.getPlugin().getCloudType(),
                            instance.getPlugin().getInstanceWriteScope(),
                            credentialData);

                    final Body<PluginComputingInstanceModel> body = api.createInstance(
                            token.getToken(),
                            token.getIdentity(),
                            instanceToCreate);
                    if (body == null || body.getContent() == null) {
                        continue;
                    }

                    final PluginComputingInstanceModel instanceCreated = body.getContent();

                    instance.setCloudInstanceName(instanceCreated.getName());
                    instance.setCloudInstanceIp(instanceCreated.getExternalIp());
                    instance.setCredential(vwCredential.getCredential());
                    instance.setInstanceIdentity(String.format(MACHINE_ID, instance.getId()));
                    instance.save();
                    break;

                } catch (final Exception e) {
                    Logger.info(e, "Credential [%s] cannot be used for user [%s] => [%s]",
                            vwCredential.getId(),
                            vwCredential.getUser().getId(),
                            e.getMessage());
                }
            }
        } catch (final Exception e) {
            Logger.warn(e, "Instance cannot be created [%s]", e.getMessage());
        }
        return instance;
    }

    private static Command generateCommandToExecute(final InstanceModel instance) {

        final ApplicationArgumentsModel arguments = instance.getApplicationArguments();

        final ExecutorModel executor = instance.getExecutor();

        final Command command = new Command();
        command.setArgs(arguments.getArguments());
        command.setCommandLine(executor.getCommandLine());
        command.setExecutionScript(executor.getExecutionScript());
        command.setRefreshStatusUrl(InstanceCreationJob.REFRESH_STATUS_URL);
        command.setSecureFileAccess(
                generateSecureAccess(
                        instance.getInstanceIdentity(),
                        InstanceCreationJob.REFRESH_TOKEN_URL));
        command.setListInputPathsWithExtension(
                InstanceCreationJob.generateInputPaths(arguments));

        return command;
    }

    private static void executeCommand(final InstanceModel instance, final Command command) {

        final ExecutionApi executorApi = new ExecutionApi(instance.getCloudInstanceIp() + ":" + DEFAULT_EXECUTOR_PORT);

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

    private static List<Pair<String, String>> generateInputPaths(final ApplicationArgumentsModel arguments) {

        final List<Pair<String, String>> listInputs = new ArrayList<>();
        for (final ApplicationFileInputModel input : arguments.getApplicationInputFiles()) {

            final SpaceFileModel spaceFile = input.getSpaceFile();
            final Long spaceFileId = spaceFile.getId();
            final String extension = FilenameUtils.getExtension(spaceFile.getName());
            final Map<String, Object> args = new HashMap();
            args.put("id", spaceFileId);
            final String inputUrl = InstanceCreationJob.BASE_URL +
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
