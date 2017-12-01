package common.fields;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BinaryType;
import org.hibernate.usertype.UserType;

import play.db.Model;

public class FileField implements Model.BinaryField, UserType {

    private String type;
    private String fileName;
    private byte[] file;    

    private FileField(byte[] file) {
        this.file = file;
    }    
    private FileField(String UUID, String type) {
        this.type = type;
    }
    
    /*
     * Implementations of play.db.Model.BinaryField
     */
    @Override
    public InputStream get() {
        if (exists()) {
           return new ByteArrayInputStream(file);
        }
        return null;
    }
    @Override
    public void set(InputStream is, String type) {
        try {
            this.type = type;
            byte[] buf = new byte[1024];
            try(ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                int numRead = 0;
                while ((numRead = is.read(buf)) >= 0) {
                    output.write(buf, 0, numRead);
                }
                file = output.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
    @Override
    public long length() {
        return file.length;
    }
    @Override
    public String type() {
        return getType();
    }
    @Override
    public boolean exists() {
        return file != null;
    }       
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getType() {
        return type;
    }    
    public void setType(String type) {
        this.type = type;
    }
    /*
     * Implementations of org.hibernate.usertype.UserType 
     */
    @Override
    public int[] sqlTypes() {
        return new int[] {Types.LONGVARBINARY};
    }
    @Override
    public Class returnedClass() {
        return FileField.class;
    }
    private static boolean equal(Object a, Object b) {
      return a == b || (a != null && a.equals(b));
    }
    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        if(o instanceof FileField && o1 instanceof FileField) {
            return equal(((FileField)o).type, ((FileField)o1).type) &&
                    Arrays.equals(((FileField)o).file, ((FileField)o1).file);
        }
        return equal(o, o1);
    }
    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }
    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
        byte [] val = (byte []) BinaryType.INSTANCE.nullSafeGet(resultSet, names[0], sessionImplementor, o);
        return new FileField(val);
    }
    @Override
    public void nullSafeSet(PreparedStatement ps, Object o, int i, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        if(o != null) {
            ps.setBytes(i, encode((FileField) o));
        } else {
            ps.setNull(i, Types.LONGVARBINARY);
        }
    }
    private byte[] encode(FileField o) {
        return o.file;
    }
    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if(o == null) {
            return null;
        }
        return new FileField(((FileField)o).file);
    }
    @Override
    public boolean isMutable() {
        return true;
    }
    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        if (o == null) return null;
        return encode((FileField) o);
    }
    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        if (cached == null) return null;
        return new FileField((byte[]) cached);
    }
    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
