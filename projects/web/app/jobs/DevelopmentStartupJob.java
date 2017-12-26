package jobs;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import controllers.security.SecurityController;
import models.MenuModel;
import models.PluginModel;
import models.RoleModel;
import models.RoleModel.RoleType;
import models.TestModel;
import models.TestModel.TestEnum;
import models.TestRelationModel;
import models.UserModel;
import play.Logger;
import play.i18n.Lang;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class DevelopmentStartupJob extends Job {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see play.jobs.Job#doJob()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void doJob() {
        if (RoleModel.findAll().size() > 0) {
            return;
        }
        Lang.change("br");
        this.insertProfiles();
        this.insertMenu("Plugins", "/adm/plugins", RoleType.ADMIN);
        this.insertMenu("Credential", "/credentials", RoleType.ADMIN);
        this.insertMenu("UserGroup", "/user/groups", RoleType.ADMIN);
        this.insertMenu("Group", "/groups", RoleType.ADMIN);

        this.insertTestModels(10);
        this.insertTempPlugins(2);
        this.insertTempUserAdmin();
        this.insertTempUserNormal();
    }

    private void insertProfiles() {
        RoleModel role = null;

        role = new RoleModel();
        role.setId(RoleType.ADMIN);
        role.save();

        role = new RoleModel();
        role.setId(RoleType.NORMAL);
        role.save();
    }

    private void insertMenu(String name, String path, RoleType roleType) {
        if (MenuModel.containsMenuProfile(path, roleType)) {
            return;
        }

        MenuModel menu = MenuModel.find("path = ?1", path).first();
        if (menu == null) {
            menu = new MenuModel();
            menu.setEnabled(true);
            menu.setName(name);
            menu.setPath(path);
            menu.save();
        }

        final RoleModel role = RoleModel.findById(roleType);
        List<MenuModel> listMenus = role.getListMenus();
        if (listMenus == null) {
            listMenus = new ArrayList<>();
        }
        listMenus.add(menu);
        role.setListMenus(listMenus);
        role.save();
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
            }
        } catch (final Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }

    private void insertTempUserAdmin() {
        try {
            UserModel user = UserModel.findByEmail("master@bionimbuz.org.br");
            if (user != null) {
                return;
            }

            final RoleModel role = RoleModel.findById(RoleType.ADMIN);

            user = new UserModel();
            user.setRole(role);
            user.setEmail("master@bionimbuz.org.br");
            user.setName("Administrador do Sistema");
            user.setPass(SecurityController.getSHA512("master"));
            user.setJoined(true);
            user.save();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.error(e.getMessage(), e);
        }
    }

    private void insertTempUserNormal() {
        try {
            UserModel user = UserModel.findByEmail("guest@bionimbuz.org.br");
            if (user != null) {
                return;
            }

            final RoleModel role = RoleModel.findById(RoleType.NORMAL);

            user = new UserModel();
            user.setRole(role);
            user.setEmail("guest@bionimbuz.org.br");
            user.setName("Usuário do Sistema");
            user.setPass(SecurityController.getSHA512("guest"));
            user.setJoined(true);
            user.save();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.error(e.getMessage(), e);
        }
    }
}