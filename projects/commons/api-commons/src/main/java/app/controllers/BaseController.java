package app.controllers;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import app.common.IVersion;
import app.common.exceptions.VersionException;
import app.models.Body;

public abstract class BaseController implements IVersion {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    private void assertVersionSupported(final String version) throws VersionException {
        if(version.equals(getAPIVersion()))
            return;
        throw new VersionException("Resquested plugin version [" + version + "]"
                + " is different from this plugin version [" + getAPIVersion() + "]");
    }

    protected <T> ResponseEntity< Body<T> > callImplementedMethod(
            String methodName,
            final String version,
            Object... objects) {
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "";
        try {
            assertVersionSupported(version);
            Class<?> [] classes = getArgsClasses(objects);
            Method method =
                    this.getClass().getDeclaredMethod(methodName, classes);
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            ResponseEntity<Body<T>> res =
                    (ResponseEntity<Body<T>>) method.invoke(this, objects);
            return res;
        } catch (VersionException e) {
            LOGGER.error(e.getMessage(), e);
            errorStatus = HttpStatus.MOVED_PERMANENTLY;
            errorMessage = e.getMessage();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            errorMessage = e.getMessage();
        }
        return ResponseEntity
                .status(errorStatus)
                .body(new Body<T>(errorMessage));
    }

    private Class<?> [] getArgsClasses(final Object... objects) {
        Class<?> [] classes = new Class<?>[objects.length];
        int i=0;
        for (Object object : objects) {
            classes[i++] = (object instanceof List) ?
                    List.class : object.getClass();
        }
        return classes;
    }
}
