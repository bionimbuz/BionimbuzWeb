package common.binders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import common.fields.FileField;
import play.data.Upload;
import play.data.binding.Binder;
import play.data.binding.Global;
import play.data.binding.TypeBinder;
import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Scope.Params;

@Global
public class CustomBinaryBinder implements TypeBinder<FileField> {

    @SuppressWarnings("deprecation")
	@Override
    public Object bind(String name, Annotation[] annotations, String value, Class actualClass, Type genericType) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        try {
            Request req = Request.current();
            if (req != null && req.args != null) {
                FileField b = (FileField) actualClass.newInstance();
                List<Upload> uploads = (List<Upload>) req.args.get("__UPLOADS");
                if(uploads != null){
                    for (Upload upload : uploads) {
                        if (upload.getFieldName().equals(value) && upload.getFileName().trim().length() > 0) {
                            b.set(upload.asStream(), upload.getContentType());
                            b.setFileName(upload.getFileName());
                            return b;
                        }
                    }
                }
            }

            if (Params.current() != null && Params.current().get(value + "_delete_") != null) {
                return null;
            }
            return Binder.MISSING;
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }
}