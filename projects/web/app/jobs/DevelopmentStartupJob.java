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
    	UserModel masterUser = UserModel.findByEmail("master@bionimbuz.org.br");
    	if(masterUser == null || masterUser.getPass() != null) {
			return;
    	}
    	updatePasswords();
    }
    
    private static void updatePasswords() {

        final List<UserModel> listUsers = UserModel.findAll();
    	
    	for(UserModel user : listUsers) {
    		try {
				user.setPass(
						SecurityController.getSHA512(user.getEmail()));
				user.save();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}
    }
    
}