package jobs;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import controllers.security.SecurityController;
import models.PluginModel;
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

        Lang.change("br");
        this.insertTestModels(10);
        this.insertTempPlugins(10);
        this.insertTempUser();
    }

    private void insertTestModels(int lenght) {
        try {
            TestRelationModel relation = null;

            for (int i = 1; i <= lenght; i++) {
                relation = new TestRelationModel("Relation " + i);
                relation.save();
            }

            for (int i = 1; i <= lenght; i++) {
                final TestModel model = new TestModel();
                model.setBooleanField(i % 2 == 0);
                model.setDateFutureField(new Date());
                model.setDecimalField(i + 2.4);
                model.setEnumField(TestEnum.values()[i % 4]);
                model.setFileField(null);
                model.setHiddenField("hiddenValue");
                model.setIntField(i);
                model.setLongTextField(this.getLoremIpsum().substring(0, 254));
                model.setPasswordField("333333333");
                model.setTextField(this.getLoremIpsum().substring(0, 50));
                model.setRelationField(relation);

                model.save();
            }
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

    private void insertTempUser() {
        try {
            UserModel user = UserModel.findByEmail("master@bionimbuz.org.br");
            if (user != null) {
                return;
            }

            user = new UserModel();
            user.setEmail("master@bionimbuz.org.br");
            user.setName("Administrador do Sistema");
            user.setPass(SecurityController.getSHA512("master"));
            user.save();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.error(e.getMessage(), e);
        }
    }
}