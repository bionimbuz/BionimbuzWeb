package common.fields;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BinaryType;

import common.security.DesEncrypter;
import play.Play;

public class EncryptedFileField extends FileField {    
 
    protected EncryptedFileField(byte[] file) {
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
            DesEncrypter cript = new DesEncrypter(getDESKey());
            return new EncryptedFileField(cript.decryptBytes(val));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void nullSafeSet(PreparedStatement ps, Object o, int i, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        try {
            if(o != null) {
                DesEncrypter cript;
                cript = new DesEncrypter(getDESKey());
                ps.setBytes(i, cript.encryptBytes(((EncryptedFileField) o).getFile()));
            } else {
                ps.setNull(i, Types.LONGVARBINARY);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
