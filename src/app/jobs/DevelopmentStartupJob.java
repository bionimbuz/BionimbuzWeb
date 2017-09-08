package jobs;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import controllers.security.SecurityController;
import models.UserModel;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class DevelopmentStartupJob extends Job {

    public void doJob() {
        insertTempUser();
    }        
    private static void insertTempUser() {
        try {
            UserModel user = UserModel.findByEmail("master");
            if(user != null)
                return;
            
            user = new UserModel();
            user.setEmail("master");
            user.setName("master");
            user.setPass(SecurityController.getSHA512("master"));
            user.save();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}