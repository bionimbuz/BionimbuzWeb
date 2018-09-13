package common.fields;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BinaryType;

import common.security.AESEncrypter;
import play.Logger;
import play.Play;

public class EncryptedFileField extends FileField {

    public EncryptedFileField(byte[] file) {
        super(file);
    }
    protected EncryptedFileField(String UUID, String type) {
        super(UUID, type);
    }

    /*
     * Implementations of org.hibernate.usertype.UserType
     */
    @Override
    public Class returnedClass() {
        return EncryptedFileField.class;
    }
    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
        try {
            byte [] val = (byte []) BinaryType.INSTANCE.nullSafeGet(resultSet, names[0], sessionImplementor, o);
            AESEncrypter cript = new AESEncrypter(Play.secretKey);
            // DesEncrypter cript = new DesEncrypter(getDESKey());
            return new EncryptedFileField(cript.decryptBytes(val));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            return null;
        }
    }
    @Override
    public void nullSafeSet(PreparedStatement ps, Object o, int i, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        try {
            if(o != null) {
                AESEncrypter cript = new AESEncrypter(Play.secretKey);
                // DesEncrypter cript = new DesEncrypter(getDESKey());
                ps.setBytes(i, cript.encryptBytes(((EncryptedFileField) o).getFile()));
            } else {
                ps.setNull(i, Types.LONGVARBINARY);
            }
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
        }
    }
    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if(o == null) {
            return null;
        }
        return new EncryptedFileField(((EncryptedFileField)o).getFile());
    }
    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        if (o == null) return null;
        return ((EncryptedFileField) o).getFile();
    }
    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        if (cached == null) return null;
        return new EncryptedFileField((byte[]) cached);
    }

    private String getDESKey() {
        return Play.configuration.getProperty("application.secret.des", "").trim();
    }

    public String getContentAsString() throws IOException {
        InputStream iStream = get();
        if(iStream == null)
            return "";
        StringWriter writer = new StringWriter();
        IOUtils.copy(iStream, writer, "UTF-8");
        return writer.toString();
    }

}
