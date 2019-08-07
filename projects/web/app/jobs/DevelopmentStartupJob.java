package jobs;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import controllers.security.SecurityController;
import models.UserModel;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class DevelopmentStartupJob extends Job {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see play.jobs.Job#doJob()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void doJob() {
        final UserModel masterUser = UserModel.findByEmail("master@bionimbuz.org.br");
        if (masterUser == null || masterUser.getPass() != null) {
            return;
        }
        updatePasswords();
    }

    private static void updatePasswords() {

        final List<UserModel> listUsers = UserModel.findAll();

        for (final UserModel user : listUsers) {
            try {
                final String emailPrefix = user.getEmail().substring(0, user.getEmail().indexOf("@"));
                user.setPass(SecurityController.getSHA512(emailPrefix));
                user.save();
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

}