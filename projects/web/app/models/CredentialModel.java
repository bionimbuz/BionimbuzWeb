package models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import common.binders.FileFieldType;
import common.fields.EncryptedFileField;
import play.data.binding.NoBinding;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_credential")
public class CredentialModel extends GenericModel  {

    @Id
    @GeneratedValue
    private Long id;
    @MinSize(3)
    @MaxSize(50)
    @Required    
    private String name;
    @Required
    @FileFieldType("credentialDataType")
    private EncryptedFileField credentialData;     
    @NoBinding
    private String credentialDataType;    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private PluginModel plugin;
    @Min(0)
    private int priority = 0;
    private boolean enabled = true;
    
    public CredentialModel() {
        super();
    }

    public EncryptedFileField getCredentialData() {
        return credentialData;
    }
    public void setCredentialData(EncryptedFileField credentialData) {
        this.credentialData = credentialData;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public PluginModel getPlugin() {
        return plugin;
    }
    public void setPlugin(PluginModel plugin) {
        this.plugin = plugin;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCredentialDataType() {
        return credentialDataType;
    }
    public void setCredentialDataType(String credentialDataType) {
        this.credentialDataType = credentialDataType;
    }
}
