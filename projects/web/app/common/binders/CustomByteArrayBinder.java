package common.binders;

import java.lang.annotation.Annotation;
import java.util.List;

import play.data.Upload;
import play.mvc.Http.Request;

//@Global
public class CustomByteArrayBinder //implements TypeBinder<byte[]> 
{

//    @Override
    public byte[] bind(final String name, final Annotation[] annotations, final String value, final Class actualClass, final java.lang.reflect.Type genericType) throws Exception {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        final Request req = Request.current();
        if (req != null && req.args != null) {
            final List<Upload> uploads = (List<Upload>) req.args.get("__UPLOADS");
            if (uploads != null) {
                for (final Upload upload : uploads) {
                    if (upload.getFieldName().equals(value)) {
                        if (upload.asFile() == null) {
                            return new byte[] {};
                        }
                        return upload.asBytes();
                    }
                }
            }
        }
        return null;
    }
}