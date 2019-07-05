package controllers.guest;

import java.util.List;

import app.models.RemoteFileInfo;
import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.SpaceFileModel;
import models.SpaceFileModel.SpaceFile;
import play.Logger;
import play.i18n.Messages;

@For(SpaceFileModel.class)
@Check("/list/space/files")
public class SpaceFileController extends BaseAdminController {
    
    public static void searchFilesForSpace(final Long spaceId) {
        try {
            List<SpaceFile> listSpaceFiles = SpaceFileModel.getSpaceFiles(spaceId);
            if(listSpaceFiles == null)
                notFound(Messages.get(I18N.not_found));
            renderJSON(listSpaceFiles);
        } catch (Exception e) {
            Logger.error(e, "Error searching files from space [%s]", e.getMessage());
            notFound(Messages.get(I18N.not_found));
        }
    }
        
    public static void getFileLocationToDownload(final Long fileId) {        
        RemoteFileInfo res = SpaceFileModel.getDownloadFileInfo(fileId);
        if(res == null)
            notFound(Messages.get(I18N.not_found));
        renderJSON(res);
    }

    public static void getFileLocationToUpload(final Long spaceId, final String fileName) {

        RemoteFileInfo res = SpaceFileModel.getUploadFileInfo(spaceId, fileName);
        if(res == null)
            notFound(Messages.get(I18N.not_found));
        renderJSON(res);        
    }
}
