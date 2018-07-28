package jobs;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.io.Files;

import common.fields.EncryptedFileField;
import controllers.security.SecurityController;
import models.CredentialModel;
import models.ExecutorModel;
import models.GroupModel;
import models.ImageModel;
import models.PluginModel;
import models.RoleModel;
import models.RoleModel.RoleType;
import models.TestModel;
import models.TestModel.TestEnum;
import models.TestRelationModel;
import models.UserGroupModel;
import models.UserModel;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class DevelopmentStartupJob extends Job {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see play.jobs.Job#doJob()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void doJob() {

        if (Play.mode.isProd()) {
            return;
        }

        // Check if Job has already ran
        if (UserModel.findByEmail("guest@bionimbuz.org.br") != null) {
            return;
        }

        Fixtures.executeSQL(new File("db/init/1.sql"));

        final PluginModel pluginGCE = this.insertGCEPlugin();
        this.insertGCEImages(pluginGCE);
        
        final PluginModel pluginAWS = this.insertAWSPlugin();
        this.insertAWSImages(pluginAWS);

        final PluginModel pluginLocal = this.insertLocalPlugin();
        this.insertLocalImages(pluginLocal);

        this.insertTestModels(10);
        //        this.insertTempPlugins(2);

        final UserModel userAdmin = UserModel.findByEmail("master@bionimbuz.org.br");
        final UserModel userNormal = this.insertTempUserNormal();
        this.insertTempGroup(
                "Test Group",
                userAdmin,
                userNormal);
        this.insertTempGroup(
                "Test Group 2",
                userAdmin,
                userNormal);

        this.insertTempGroup(
                "Admin Group",
                userAdmin);
        this.insertTempGroup(
                "Normal Group",
                userNormal);
        this.insertGCECredential(pluginGCE, userAdmin);
        this.insertGCECredential(pluginGCE, userNormal);
        this.insertGCECredential(pluginGCE, userNormal);

        this.insertAWSCredential(pluginAWS, userAdmin);
        this.insertAWSCredential(pluginAWS, userNormal);

        this.insertLocalCredential(pluginLocal, userAdmin);

        this.insertExecutor(pluginGCE, pluginAWS, pluginLocal);
    }

    private static void insertExecutor(final PluginModel... plugins) {
        final ExecutorModel executor = new ExecutorModel();
        final List<ImageModel> listImages = new ArrayList<>();
        for (final PluginModel plugin : plugins) {
            plugin.refresh();
            listImages.add(
                    plugin.getListImages().get(0));
        }
        executor.setName("Apache");
        executor.setStartupScript(
                "#!/bin/bash \n"
                + "apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html");
        executor.setScriptExtension("sh");
        executor.setFirewallTcpRules("80,8080");
        executor.setListImages(listImages);
        executor.save();
    }

    private static void insertTempGroup(final String name, final UserModel... users) {
        final GroupModel group = new GroupModel();
        group.setName(name);
        group.save();

        for (final UserModel user : users) {
            final UserGroupModel userGroup = new UserGroupModel(user, group);
            userGroup.setJoined(true);
            userGroup.setOwner(true);
            userGroup.save();
        }
    }

    private static void insertGCEImages(final PluginModel plugin) {
        ImageModel image = new ImageModel();
        image.setName("ubuntu-1804-bionic-v20180522");
        image.setUrl("https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1804-bionic-v20180522");
        image.setPlugin(plugin);
        image.save();

        image = new ImageModel();
        image.setName("ubuntu-1204-precise-v20141028");
        image.setUrl("https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1204-precise-v20141028");
        image.setPlugin(plugin);
        image.save();
    }
    
    private void insertAWSImages(final PluginModel plugin) {
        ImageModel image = new ImageModel();
        image.setName("ubuntu-xenial-16.04-amd64-server-20180627");
        image.setUrl("ubuntu/images/hvm-ssd/ubuntu-xenial-16.04-amd64-server-20180627");
        image.setPlugin(plugin);
        image.save();
    }

    private static void insertLocalImages(final PluginModel plugin) {
        final ImageModel image = new ImageModel();
        image.setName("linux-4.13.0-45-generic-amd64");
        image.setUrl("local-image-url");
        image.setPlugin(plugin);
        image.save();
    }

    private static void insertGCECredential(final PluginModel plugin, final UserModel user) {
        final CredentialModel model = new CredentialModel();
        final EncryptedFileField data = new EncryptedFileField(
                readCredential("credential.gce.file", "conf/credentials/credentials-gcp.json").getBytes());
        model.setCredentialData(data);
        model.setCredentialDataType("application/json");
        model.setEnabled(true);
        model.setName("Credential Google");
        model.setPlugin(plugin);
        model.setUser(user);
        model.save();
    }
    
    private static void insertAWSCredential(final PluginModel plugin, final UserModel user) {
        final CredentialModel model = new CredentialModel();
        final EncryptedFileField data = new EncryptedFileField(
                readCredential("credential.aws.file", "conf/credentials/credentials-aws.csv").getBytes());
        model.setCredentialData(data);
        model.setCredentialDataType("text/csv");
        model.setEnabled(true);
        model.setName("Credential AWS");
        model.setPlugin(plugin);
        model.setUser(user);
        model.save();
    }

    private static void insertLocalCredential(final PluginModel plugin, final UserModel user) {
        final CredentialModel model = new CredentialModel();
        final EncryptedFileField data = new EncryptedFileField(new byte[] {});
        model.setCredentialData(data);
        model.setCredentialDataType("");
        model.setEnabled(true);
        model.setName("Local Machine");
        model.setPlugin(plugin);
        model.setUser(user);
        model.save();
    }

    private static PluginModel insertGCEPlugin() {
        final PluginModel model = new PluginModel();
        model.setAuthType(app.models.PluginInfoModel.AuthenticationType.AUTH_BEARER_TOKEN);
        model.setCloudType("google-compute-engine");
        model.setEnabled(true);
        model.setName("Google Cloud Platform");
        model.setPluginVersion("0.1");
        model.setUrl("http://localhost:8080");
        model.setInstanceReadScope("https://www.googleapis.com/auth/compute.readonly");
        model.setInstanceWriteScope("https://www.googleapis.com/auth/compute");
        model.setStorageReadScope("https://www.googleapis.com/auth/devstorage.read_only");
        model.setStorageWriteScope("https://www.googleapis.com/auth/devstorage.read_write");
        model.save();
        return model;
    }
    
    private PluginModel insertAWSPlugin() {
        final PluginModel model = new PluginModel();
        model.setAuthType(app.models.PluginInfoModel.AuthenticationType.AUTH_AWS);
        model.setCloudType("aws-ec2");
        model.setEnabled(true);
        model.setName("Amazon Web Services");
        model.setPluginVersion("0.1");
        model.setUrl("http://localhost:8484");
        model.setInstanceReadScope("");
        model.setInstanceWriteScope("");
        model.setStorageReadScope("");
        model.setStorageWriteScope("");
        model.save();
        return model;
    }

    private static PluginModel insertLocalPlugin() {
        final PluginModel model = new PluginModel();
        model.setAuthType(app.models.PluginInfoModel.AuthenticationType.AUTH_SUPER_USER);
        model.setCloudType("local-machine");
        model.setEnabled(true);
        model.setName("Local Machine");
        model.setPluginVersion("0.1");
        model.setUrl("http://localhost:8282");
        model.setInstanceReadScope("");
        model.setInstanceWriteScope("");
        model.setStorageReadScope("");
        model.setStorageWriteScope("");
        model.save();
        return model;
    }

    @SuppressWarnings("deprecation")
    private void insertTestModels(final int lenght) {
        try {

            Date date = null;
            for (int i = 1; i <= lenght; i++) {
                final TestRelationModel relation = new TestRelationModel(i + " - Relation");
                relation.save();
                final TestModel model = new TestModel();
                model.setBooleanField(i % 2 == 0);
                date = new Date();
                date.setYear(date.getYear() + 1);
                model.setDateFutureField(date);
                date = new Date();
                date.setYear(date.getYear() - 1);
                model.setDatePastField(date);
                model.setDecimalField(i + 2.4);
                model.setEnumField(TestEnum.values()[i % 4]);
                model.setFileField(null);
                model.setHiddenField(i + "hiddenValue");
                model.setIntField(i);
                model.setLongTextField(i + this.getLoremIpsum().substring(0, 253));
                model.setPasswordField(i + "#############");
                model.setTextField(i + this.getLoremIpsum().substring(0, 50));
                model.setRelationField(relation);
                model.setEmailField(i + "test@test.com");
                model.setUrlField("http://localhost.com/" + i);
                model.setPhoneField("+55 61 99999999" + i);
                model.setIpv4Field("1.1.1.1");
                final List<TestRelationModel> options = new ArrayList<>();
                options.add(relation);
                model.setMultiSelectField(options);
                final List<TestRelationModel> options2 = new ArrayList<>();
                options2.add(relation);
                model.setMultiSelectField2(options2);

                model.save();
            }

            date = new Date();
        } catch (final Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }

    private static String getLoremIpsum() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " + "sed do eiusmod tempor incididunt ut labore et dolore " + "magna aliqua. Ut enim ad minim veniam, quis nostrud "
                + "exercitation ullamco laboris nisi ut aliquip ex ea commodo " + "consequat. Duis aute irure dolor in reprehenderit in "
                + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. " + "Excepteur sint occaecat cupidatat non proident, sunt in "
                + "culpa qui officia deserunt mollit anim id est laborum.";
    }

    //    private void insertTempPlugins(final int lenght) {
    //        try {
    //            for (int i = 1; i <= lenght; i++) {
    //                final PluginModel model = new PluginModel();
    //                model.setCloudType("cloud " + i);
    //                model.setName("name " + i);
    //                model.setPluginVersion("v" + i);
    //                model.setUrl("http://localhost:" + i);
    //                model.save();
    //                this.insertGCEImages(model);
    //            }
    //        } catch (final Exception e) {
    //            Logger.error(e.getMessage(), e);
    //        }
    //    }

    //    private UserModel insertTempUserAdmin() {
    //        try {
    //            UserModel user = UserModel.findByEmail("master@bionimbuz.org.br");
    //            if (user != null) {
    //                return null;
    //            }
    //
    //            final RoleModel role = RoleModel.findById(RoleType.ADMIN);
    //
    //            user = new UserModel();
    //            user.setRole(role);
    //            user.setEmail("master@bionimbuz.org.br");
    //            user.setName("Administrador do Sistema");
    //            user.setPass(SecurityController.getSHA512("master"));
    //            user.setJoined(true);
    //            user.save();
    //            return user;
    //        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
    //            Logger.error(e.getMessage(), e);
    //            return null;
    //        }
    //    }

    private static UserModel insertTempUserNormal() {
        try {
            UserModel user = UserModel.findByEmail("guest@bionimbuz.org.br");
            if (user != null) {
                return null;
            }

            final RoleModel role = RoleModel.findById(RoleType.NORMAL);

            user = new UserModel();
            user.setRole(role);
            user.setEmail("guest@bionimbuz.org.br");
            user.setName("Usuário do Sistema");
            user.setPass(SecurityController.getSHA512("guest"));
            user.setJoined(true);
            user.save();
            return user;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.error(e.getMessage(), e);
            return null;
        }
    }

    private static String readCredential(final String property, final String defaultPath) {
        String fileContents = null;
        try {
            fileContents = Files.toString(
                    new File(System.getProperty(property, defaultPath)),
                    Charset.defaultCharset());
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return fileContents;
    }
}
