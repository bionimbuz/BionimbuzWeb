package controllers.adm;

import java.util.Date;

import common.constants.SystemConstants;
import controllers.CRUD.For;
import controllers.Check;
import models.FileModel;
import play.data.binding.Binder;

@For(FileModel.class)
@Check("/applications/uploads")
public class ApplicationUploadController extends BaseAdminController  {

    public static void show() throws Exception {    	
    	FileModel coordinatorJar = 
    			getDefaultFile(SystemConstants.DEFAULT_COORDINATOR_JAR);
    	FileModel executorJar = 
    			getDefaultFile(SystemConstants.DEFAULT_EXECUTOR_JAR);
        render(coordinatorJar, executorJar);
    }
    
    public static void save() throws Exception {
    	FileModel coordinatorJar = 
    			getDefaultFile(SystemConstants.DEFAULT_COORDINATOR_JAR);
    	FileModel executorJar = 
    			getDefaultFile(SystemConstants.DEFAULT_EXECUTOR_JAR);
        Binder.bindBean(params.getRootParamNode(), "coordinatorJar", coordinatorJar);    
        Binder.bindBean(params.getRootParamNode(), "executorJar", executorJar);  
        
        coordinatorJar.setLastUpdate(new Date());
        coordinatorJar.save();     
        executorJar.setLastUpdate(new Date());
        executorJar.save();
        redirect(request.controller + ".show");      	
    }
    
    private static FileModel getDefaultFile(final String name) {
    	FileModel obj  = 
    			FileModel.findByName(name);
    	if(obj == null) 
    		obj = new FileModel();
    	if(obj.getFileData() != null) {
    		obj.getFileData().setFileName(name);
    	}
    	obj.setName(name);
    	return obj;
    }
}