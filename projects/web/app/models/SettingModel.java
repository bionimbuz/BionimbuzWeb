package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.binding.NoBinding;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_setting")
public class SettingModel extends GenericModel {

    private static final String STRING_FORMAT = "";  
    
    public static enum Name {
        setting_number_temp,
        setting_decimal_temp,
        setting_string_temp,
        setting_date_temp
    }
    
    public static enum Type {
        NUMBER,
        DECIMAL,
        STRING,
        DATE
    }
    
    @Id
    @Enumerated(EnumType.STRING)
    private Name settingName;
    @Column(nullable = false)
    private String settingValue;
    @NoBinding
    @Column(nullable = false)
    private String defaultValue;
    @NoBinding
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type settingType;
    @NoBinding
    private boolean required;
    @NoBinding
    private String maxSize;
    @NoBinding
    private String minSize;    
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public SettingModel() {
        super();
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data accessing
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static String getSettingValueOrDefault(final Name name) {
        SettingModel setting = findById(name);
        String value = setting.getSettingValue();
        if(value.isEmpty()) {
            value = setting.getDefaultValue();
        }
        return value;
    }
    public static Integer getIntSetting(final Name name) {
        String value = getSettingValueOrDefault(name);
        if(value == null || value.isEmpty()) {
            return null;
        }
        return Integer.valueOf(value);
    }    
    public static Double getDoubleSetting(final Name name) {
        String value = getSettingValueOrDefault(name);
        if(value == null || value.isEmpty()) {
            return null;
        }
        return Double.valueOf(value);
    }   
    public static String getStringSetting(final Name name) {
        return getSettingValueOrDefault(name);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public Type getSettingType() {
        return settingType;
    }
    public void setSettingType(Type settingType) {
        this.settingType = settingType;
    }
    public Name getSettingName() {
        return settingName;
    }
    public void setSettingName(Name settingName) {
        this.settingName = settingName;
    }
    public String getSettingValue() {
        return settingValue;
    }
    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public String getMaxSize() {
        return maxSize;
    }
    public void setMaxSize(String maxSize) {
        this.maxSize = maxSize;
    }
    public String getMinSize() {
        return minSize;
    }
    public void setMinSize(String minSize) {
        this.minSize = minSize;
    }    
}
