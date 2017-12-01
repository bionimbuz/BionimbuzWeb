package common.fields;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BinaryType;
import org.hibernate.usertype.UserType;

import play.db.Model;

public class EncryptedFileField implements Model.BinaryField, UserType {    

    private String type;
    private String fileName;
    private byte[] file;    

    protected EncryptedFileField(byte[] file) {
        this.file = file;
    }    
    protected EncryptedFileField(String UUID, String type) {
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
    protected byte[] getFile() {
        return file;
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
        return EncryptedFileField.class;
    }
    private static boolean equal(Object a, Object b) {
      return a == b || (a != null && a.equals(b));
    }
    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        if(o instanceof EncryptedFileField && o1 instanceof EncryptedFileField) {
            return equal(((EncryptedFileField)o).type, ((EncryptedFileField)o1).type) &&
                    Arrays.equals(((EncryptedFileField)o).file, ((EncryptedFileField)o1).file);
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
        return new EncryptedFileField(decryptBytes(val));
    }
    @Override
    public void nullSafeSet(PreparedStatement ps, Object o, int i, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        if(o != null) {
            ps.setBytes(i, encryptBytes(((EncryptedFileField) o).file));
        } else {
            ps.setNull(i, Types.LONGVARBINARY);
        }
    }
    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if(o == null) {
            return null;
        }
        return new EncryptedFileField(((EncryptedFileField)o).file);
    }
    @Override
    public boolean isMutable() {
        return true;
    }
    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
    
    
    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        if (o == null) return null;
        return ((EncryptedFileField) o).file;
    }
    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        if (cached == null) return null;
        return new EncryptedFileField((byte[]) cached);
    }
    
    private static byte[] encryptBytes(byte [] bytes) {
        try {
            DesEncrypter encrypter = new DesEncrypter(key);
            ByteArrayOutputStream output = new ByteArrayOutputStream();    
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);  
            encrypter.encrypt(input, output);                          
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }        
    }
    
    private static byte[] decryptBytes(byte [] bytes) {
        try {
            DesEncrypter encrypter = new DesEncrypter(key);
            ByteArrayOutputStream output = new ByteArrayOutputStream();    
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);  
            encrypter.decrypt(input, output);                          
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }    

    static SecretKey key = null;
    
    static {
        try {
            key = KeyGenerator.getInstance("DES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    

    static class DesEncrypter {
        
        private static String ENCRYPT_TYPE = "DES/CBC/PKCS5Padding";
        
        byte[] buf = new byte[1024];
        Cipher ecipher;
        Cipher dcipher;
        DesEncrypter(SecretKey key) throws Exception {
            byte[] iv = new byte[]{
                    (byte) 0x8E, 0x12, 0x39, 
                    (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A};
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            ecipher = Cipher.getInstance(ENCRYPT_TYPE);
            dcipher = Cipher.getInstance(ENCRYPT_TYPE);

            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        }

        public void encrypt(InputStream in, OutputStream out) throws Exception {
            out = new CipherOutputStream(out, ecipher);
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            out.close();
        }

        public void decrypt(InputStream in, OutputStream out) throws Exception {
            in = new CipherInputStream(in, dcipher);
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            out.close();
        }
    }
}
