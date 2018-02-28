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

import app.models.InfoModel.AuthenticationType;
import common.fields.EncryptedFileField;
import controllers.security.SecurityController;
import models.CredentialModel;
import models.ExecutorModel;
import models.GroupModel;
import models.ImageModel;
import models.MenuModel;
import models.PluginModel;
import models.RoleModel;
import models.RoleModel.RoleType;
import models.TestModel;
import models.TestModel.TestEnum;
import models.TestRelationModel;
import models.UserGroupModel;
import models.UserModel;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class DevelopmentStartupJob extends Job {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see play.jobs.Job#doJob()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void doJob() {
        // Check if Job has already ran
        if (UserModel.findByEmail("guest@bionimbuz.org.br") != null) {
            return;
        }
        
        PluginModel plugin = this.insertPlugin();
        this.insertImages(plugin);
        this.insertTestModels(10);
        this.insertTempPlugins(2);

        UserModel userAdmin = 
                UserModel.findByEmail("master@bionimbuz.org.br");
        UserModel userNormal = 
                this.insertTempUserNormal();
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
        this.insertCredential(plugin, userAdmin);
        this.insertCredential(plugin, userNormal);
        this.insertCredential(plugin, userNormal);
        
        insertExecutor(plugin);
    }
    
    private void insertExecutor(PluginModel plugin) {
        ExecutorModel executor = new ExecutorModel();
        List<ImageModel> listImages = new ArrayList<>();
        plugin.refresh();
        listImages.add(
                plugin.getListImages().get(0));
        executor.setName("Apache");
        executor.setStartupScript("apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html");
        executor.setListImages(listImages);
        executor.save();        
    }
    
    private void insertTempGroup(String name, UserModel... users) {
        GroupModel group = new GroupModel();
        group.setName(name);
        group.save();
            
        for(UserModel user : users) {            
            UserGroupModel userGroup = new UserGroupModel(user, group);
            userGroup.setJoined(true);
            userGroup.setOwner(true);  
            userGroup.save();
        }        
    }
    
    private void insertImages(PluginModel plugin) {
        ImageModel image = new ImageModel();
        image.setName("ubuntu-1710-artful-v20180126");
        image.setUrl("https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1710-artful-v20180126");
        image.setPlugin(plugin);
        image.save();
        
        image = new ImageModel();
        image.setName("ubuntu-1204-precise-v20141028");
        image.setUrl("https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1204-precise-v20141028");
        image.setPlugin(plugin);
        image.save();        
    }
    
    private MenuModel insertMenu(
            String name, 
            String iconClass, 
            String path, 
            short order, 
            MenuModel parentMenu, 
            RoleType... roleTypes) {
        
        MenuModel menu = new MenuModel();  
        menu.setName(name);
        menu.setMenuOrder(order);
        menu.setIconClass(iconClass);
        menu.setPath(path);        
        menu.setParentMenu(parentMenu);
        menu.save();
        


        for(RoleType roleType : roleTypes) {
            final RoleModel role = RoleModel.findById(roleType);
            List<MenuModel> menus = role.getListMenus();
            if(menus == null)
                menus = new ArrayList<>();
            menus.add(menu);
            role.setListMenus(menus);   
        }    
        
        return menu;
    }

    private void insertCredential(PluginModel plugin, UserModel user) {
        CredentialModel model = new CredentialModel();     
        EncryptedFileField data = new EncryptedFileField(readCredential().getBytes());
        model.setCredentialData(data);
        model.setCredentialDataType("application/json");   
        model.setEnabled(true);
        model.setName("Credential Google");
        model.setPlugin(plugin);
        model.setUser(user);
        model.save();
    }

    private PluginModel insertPlugin() {
        PluginModel model = new PluginModel();
        model.setAuthType(AuthenticationType.AUTH_BEARER_TOKEN);
        model.setCloudType("google-compute-engine");
        model.setEnabled(true);
        model.setName("Google Cloud Platform");
        model.setPluginVersion("0.1");
        model.setUrl("http://localhost:8080");
        model.setReadScope("https://www.googleapis.com/auth/compute.readonly");
        model.setWriteScope("https://www.googleapis.com/auth/compute");
        model.save();
        return model;
    }

    @SuppressWarnings("deprecation")
    private void insertTestModels(int lenght) {
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

    private String getLoremIpsum() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " + "sed do eiusmod tempor incididunt ut labore et dolore " + "magna aliqua. Ut enim ad minim veniam, quis nostrud "
                + "exercitation ullamco laboris nisi ut aliquip ex ea commodo " + "consequat. Duis aute irure dolor in reprehenderit in "
                + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. " + "Excepteur sint occaecat cupidatat non proident, sunt in "
                + "culpa qui officia deserunt mollit anim id est laborum.";
    }

    private void insertTempPlugins(int lenght) {
        try {
            for (int i = 1; i <= lenght; i++) {
                final PluginModel model = new PluginModel();
                model.setCloudType("cloud " + i);
                model.setName("name " + i);
                model.setPluginVersion("v" + i);
                model.setUrl("http://localhost:" + i);
                model.save();
                insertImages(model);
            }
        } catch (final Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }

    private UserModel insertTempUserAdmin() {
        try {
            UserModel user = UserModel.findByEmail("master@bionimbuz.org.br");
            if (user != null) {
                return null;
            }

            final RoleModel role = RoleModel.findById(RoleType.ADMIN);

            user = new UserModel();
            user.setRole(role);
            user.setEmail("master@bionimbuz.org.br");
            user.setName("Administrador do Sistema");
            user.setPass(SecurityController.getSHA512("master"));
            user.setJoined(true);
            user.save();
            return user;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.error(e.getMessage(), e);
            return null;
        }
    }

    private UserModel insertTempUserNormal() {
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
    
    private static String readCredential() {
        String fileContents = null;        
        try {
            fileContents = 
                    Files.toString(
                        new File(System.getProperty("credential.file")),
                        Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContents;
    }    
}