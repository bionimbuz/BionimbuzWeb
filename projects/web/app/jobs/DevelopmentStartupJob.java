package jobs;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import controllers.security.SecurityController;
import models.PluginModel;
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
        insertTempPlugins(50);
        insertTempUser();
    }
    
    private void insertTempPlugins(int lenght) {
        try {
            for(int i=1;i<=lenght;i++) {
                PluginModel model = new PluginModel();
                model.setCloudType("cloud " + i);
                model.setName("name "+i);
                model.setPluginVersion("v"+i);
                model.setUrl("http://localhost:"+i);            
                model.save();
            }
        } catch (Exception e) {
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