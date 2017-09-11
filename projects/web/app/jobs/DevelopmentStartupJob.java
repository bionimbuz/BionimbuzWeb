package jobs;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import controllers.security.SecurityController;
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
        insertTempUser();
    }

    private static void insertTempUser() {
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