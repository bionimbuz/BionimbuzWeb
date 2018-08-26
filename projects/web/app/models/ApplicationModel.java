package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import common.fields.validation.NetworkPortsCheck;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Inheritance
@DiscriminatorColumn(name="type")
@Table(name = "tb_application")
public class ApplicationModel extends GenericModel {
    
    @Id
    @GeneratedValue
    private Long id;
    @Required
    @MaxSize(100)
    @MinSize(3)
    private String name;
    @Required
    @MaxSize(1000)
    @MinSize(3)
    @Column(length=1000)
    private String startupScript;
    @MaxSize(10)
    private String scriptExtension;
    @MinSize(5)
    private String commandLine;
    @CheckWith(NetworkPortsCheck.class)
    private String firewallUdpRules;
    @CheckWith(NetworkPortsCheck.class)
    private String firewallTcpRules;
    @Required
    @ManyToMany
    @JoinTable(name = "tb_application_image", 
        joinColumns = @JoinColumn(name = "id_application", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(name = "id_image", referencedColumnName = "id"))
    private List<ImageModel> listImages;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public String getStartupScript() {
        return startupScript;
    }
    public void setStartupScript(String startupScript) {
    	this.startupScript = startupScript;
        if(startupScript != null && !startupScript.isEmpty()) {
        	this.startupScript = startupScript.replace("\r", "");    		
        }
    }
    public List<ImageModel> getListImages() {
        return listImages;
    }
    public void setListImages(List<ImageModel> listImages) {
        this.listImages = listImages;
    }    
    public String getFirewallUdpRules() {
        return firewallUdpRules;
    }
    public void setFirewallUdpRules(String firewallUdpRules) {
        this.firewallUdpRules = firewallUdpRules;
    }
    public String getFirewallTcpRules() {
        return firewallTcpRules;
    }
    public void setFirewallTcpRules(String firewallTcpRules) {
        this.firewallTcpRules = firewallTcpRules;
    }    
    public String getScriptExtension() {
        return scriptExtension;
    }
    public void setScriptExtension(String scriptExtension) {
        this.scriptExtension = scriptExtension;
    }    
    public final String getCommandLine() {
        return commandLine;
    }
    public final void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
