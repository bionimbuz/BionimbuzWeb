package app.common;

import app.common.utils.StringUtils;

public class LineCmdUtils {
    
    public static String generateFilePath(
            final String dir, 
            final String prefix,
            final int id,
            final String extension) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(dir);
        fileName.append(prefix);
        fileName.append(id);      
        if(!StringUtils.isEmpty(extension)){
            if(extension.charAt(0) != SystemConstants.DOT) {
                fileName.append(SystemConstants.DOT);
            }
            fileName.append(extension);
        } 
        return fileName.toString();
    }
}
