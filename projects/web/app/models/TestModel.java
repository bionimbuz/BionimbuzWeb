package models;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
    
    public static enum TestEnum{
        TEST_1,
        TEST_2,
        TEST_3,
        TEST_4
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
    @Required
    private File fileField;
    @Required
    @Hidden
    private String hiddenField;
    @Required
    @MinSize(1)
    @MaxSize(100)
    private String longTextField;
    @Required
    @Min(1)
    @Max(10)
    private Integer intField;
    @Required
    @Range(min=1, max=10)
    private Double decimalField;
    @Required
    @Password
    private String passwordField;
    @Required
    @MinSize(1)
    @MaxSize(500)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_relation")
    private TestRelationModel relationField;
    
    
    
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
    public File getFileField() {
        return fileField;
    }
    public void setFileField(File fileField) {
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
}