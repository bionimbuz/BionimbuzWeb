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
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.BinaryType;

import common.security.DesEncrypter;
import play.Logger;
import play.Play;

public class EncryptedFileField extends FileField {

    public EncryptedFileField(final byte[] file) {
        super(file);
    }

    protected EncryptedFileField(final String UUID, final String type) {
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
    public Object nullSafeGet(final ResultSet resultSet, final String[] names, final SharedSessionContractImplementor sessionImplementor, final Object o) throws HibernateException, SQLException {
        try {
            final byte[] val = (byte[]) BinaryType.INSTANCE.nullSafeGet(resultSet, names[0], sessionImplementor, o);
            //            AESEncrypter cript = new AESEncrypter(Play.secretKey);
            final DesEncrypter cript = new DesEncrypter(this.getDESKey());
            return new EncryptedFileField(cript.decryptBytes(val));
        } catch (final Exception e) {
            Logger.error(e, e.getMessage());
            return null;
        }
    }

    @Override
    public void nullSafeSet(final PreparedStatement ps, final Object o, final int i, final SharedSessionContractImplementor sessionImplementor) throws HibernateException, SQLException {
        try {
            if (o != null) {
                //                AESEncrypter cript = new AESEncrypter(Play.secretKey);
                final DesEncrypter cript = new DesEncrypter(this.getDESKey());
                ps.setBytes(i, cript.encryptBytes(((EncryptedFileField) o).getFile()));
            } else {
                ps.setNull(i, Types.LONGVARBINARY);
            }
        } catch (final Exception e) {
            Logger.error(e, e.getMessage());
        }
    }

    @Override
    public Object deepCopy(final Object o) throws HibernateException {
        if (o == null) {
            return null;
        }
        return new EncryptedFileField(((EncryptedFileField) o).getFile());
    }

    @Override
    public Serializable disassemble(final Object o) throws HibernateException {
        if (o == null) {
            return null;
        }
        return ((EncryptedFileField) o).getFile();
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        if (cached == null) {
            return null;
        }
        return new EncryptedFileField((byte[]) cached);
    }

    private String getDESKey() {
        return Play.configuration.getProperty("application.secret.des", "").trim();
    }

    public String getContentAsString() throws IOException {
        final InputStream iStream = this.get();
        if (iStream == null) {
            return "";
        }
        final StringWriter writer = new StringWriter();
        IOUtils.copy(iStream, writer, "UTF-8");
        return writer.toString();
    }

}
