package models;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import common.binders.FileFieldName;
import common.binders.FileFieldType;
import common.fields.FileField;
import controllers.CRUD.Hidden;
import play.data.binding.NoBinding;
import play.data.validation.Email;
import play.data.validation.IPv4Address;
import play.data.validation.InFuture;
import play.data.validation.InPast;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Phone;
import play.data.validation.Range;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_test")
public class TestModel extends GenericModel {

    public static enum TestEnum {
        TEST_1, TEST_2, TEST_3, TEST_4
    }

    @Id
    @GeneratedValue
    private Long id;
    @NoBinding
    @Column(updatable = false, insertable = false)
    private Long id_relation;    
    @Required
    private Boolean booleanField;
    @Required
    @InFuture
    private Date dateFutureField;
    @Required
    @InPast
    private Date datePastField;
    @Required
    private TestEnum enumField;
    @Hidden
    private String hiddenField;
    @Required
    @MinSize(1)
    @MaxSize(500)
    private String longTextField;
    @Required
    @Min(1)
    @Max(10)
    private Integer intField;
    @Required
    @Range(min = 1, max = 10)
    private Double decimalField;
    @Required
    @Password
    private String passwordField;
    @Required
    @MinSize(1)
    @MaxSize(100)
    private String textField;
    @Required
    @Email
    private String emailField;
    @Required
    @URL
    private String urlField;
    @Required
    @Phone
    private String phoneField;
    @Required
    @IPv4Address
    private String ipv4Field;
    
    @Required
    @FileFieldName("fileFieldName")
    @FileFieldType("fileFieldType")
    private FileField fileField;       
    @NoBinding
    private String fileFieldType;
    @NoBinding
    private String fileFieldName;    
    @Required
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_relation")
    private TestRelationModel relationField;
    @Required
    @ManyToMany
    @JoinTable(name = "tb_test_relation_nxn", joinColumns = @JoinColumn(name = "id_test", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_test_relation", referencedColumnName = "id"))
    private List<TestRelationModel> multiSelectField;
    @Required
    @ManyToMany
    @JoinTable(name = "tb_test_relation_nxn_2", joinColumns = @JoinColumn(name = "id_test2", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_test_relation2", referencedColumnName = "id"))
    private List<TestRelationModel> multiSelectField2;

    public TestModel() {
        super();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Boolean getBooleanField() {
        return booleanField;
    }
    public void setBooleanField(Boolean booleanField) {
        this.booleanField = booleanField;
    }
    public TestEnum getEnumField() {
        return enumField;
    }
    public void setEnumField(TestEnum enumField) {
        this.enumField = enumField;
    }
    
    static SecretKey key = null;
    
    static {
        try {
            key = KeyGenerator.getInstance("DES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    
//    
//    public Blob getFileField() {
//        try {
//            DesEncrypter encrypter = new DesEncrypter(key);
//            ByteArrayOutputStream output = new ByteArrayOutputStream();                
//            encrypter.decrypt(fileField.get(), output);                
//            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());            
//            fileField.set(input, fileField.type());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        
//        return fileField;
//    }
//    public void setFileField(Blob fileField) {
//        if (fileField != null) {
//            try {
//                DesEncrypter encrypter = new DesEncrypter(key);
//                ByteArrayOutputStream output = new ByteArrayOutputStream();                
//                encrypter.encrypt(fileField.get(), output);                
//                ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
//                fileField.set(input, fileField.type());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        
//        }
//        this.fileField = fileField;
//    }

    public FileField getFileField() {
        return fileField;
    }

    public void setFileField(FileField fileField) {
        this.fileField = fileField;
    }

    public String getHiddenField() {
        return hiddenField;
    }
    public void setHiddenField(String hiddenField) {
        this.hiddenField = hiddenField;
    }
    public String getLongTextField() {
        return longTextField;
    }
    public void setLongTextField(String longTextField) {
        this.longTextField = longTextField;
    }
    public Integer getIntField() {
        return intField;
    }
    public void setIntField(Integer intField) {
        this.intField = intField;
    }
    public Double getDecimalField() {
        return decimalField;
    }
    public void setDecimalField(Double decimalField) {
        this.decimalField = decimalField;
    }
    public String getPasswordField() {
        return passwordField;
    }
    public void setPasswordField(String passwordField) {
        this.passwordField = passwordField;
    }
    public String getTextField() {
        return textField;
    }
    public void setTextField(String textField) {
        this.textField = textField;
    }
    public TestRelationModel getRelationField() {
        return relationField;
    }
    public void setRelationField(TestRelationModel relationField) {
        this.relationField = relationField;
    }
    public Date getDateFutureField() {
        return dateFutureField;
    }
    public void setDateFutureField(Date dateFutureField) {
        this.dateFutureField = dateFutureField;
    }
    public Date getDatePastField() {
        return datePastField;
    }
    public void setDatePastField(Date datePastField) {
        this.datePastField = datePastField;
    }
    public String getEmailField() {
        return emailField;
    }
    public void setEmailField(String emailField) {
        this.emailField = emailField;
    }
    public String getUrlField() {
        return urlField;
    }
    public void setUrlField(String urlField) {
        this.urlField = urlField;
    }
    public String getPhoneField() {
        return phoneField;
    }
    public void setPhoneField(String phoneField) {
        this.phoneField = phoneField;
    }
    public String getIpv4Field() {
        return ipv4Field;
    }
    public void setIpv4Field(String ipv4Field) {
        this.ipv4Field = ipv4Field;
    }
    public List<TestRelationModel> getMultiSelectField() {
        return multiSelectField;
    }
    public void setMultiSelectField(List<TestRelationModel> multiSelectField) {
        this.multiSelectField = multiSelectField;
    }
    public List<TestRelationModel> getMultiSelectField2() {
        return multiSelectField2;
    }
    public void setMultiSelectField2(
            List<TestRelationModel> multiSelectField2) {
        this.multiSelectField2 = multiSelectField2;
    }

    @Override
    public String toString() {
        return this.textField;
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
