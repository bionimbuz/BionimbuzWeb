package controllers.guest;

import common.fields.FileField;
import controllers.BaseController;
import controllers.CRUD.For;
import models.FileModel;

@For(FileModel.class)
public class FileController extends BaseController  {

    public static void download(final String name) throws Exception {
        final FileModel fileFound = FileModel.findByName(name);

        if(fileFound == null || fileFound.getFileData() == null) {
            notFound();
        }
        final FileField fileData = fileFound.getFileData();
        renderBinary(fileData.get(), name, fileData.length());
    }
}