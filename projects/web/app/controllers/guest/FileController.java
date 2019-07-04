package controllers.guest;

import common.fields.FileField;
import controllers.CRUD.For;
import controllers.adm.BaseAdminController;
import models.FileModel;

@For(FileModel.class)
public class FileController extends BaseAdminController  {

    public static void download(final String name) throws Exception {
    	FileModel fileFound = FileModel.findByName(name);
    	
    	if(fileFound == null || fileFound.getFileData() == null)
            notFound();
    	FileField fileData = fileFound.getFileData();
        renderBinary(fileData.get(), name, fileData.length());
    }
}