package models;

import static common.constants.SystemConstants.CMD_LINE_ARGS;
import static common.constants.SystemConstants.CMD_LINE_INPUTS;
import static common.constants.SystemConstants.CMD_LINE_OUTPUTS;

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
import javax.persistence.Transient;

import org.hibernate.annotations.DiscriminatorOptions;

import app.common.utils.StringUtils;
import common.constants.SystemConstants;
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
@DiscriminatorOptions(force=true)
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
    @Required
    @Column(nullable=false)
    private Boolean executionScriptEnabled;
    @MaxSize(1000)
    @Column(length=1000)
    private String executionScript;
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
    @Transient
    public String getCommandLineWithDefault() {
        if(executionScriptEnabled) {
            return SystemConstants.DEFAULT_APP_SCRIPT + " " + commandLine;
        }
        return commandLine;
    }
    public String getCommandLine() {
        return commandLine;
    }
    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }
    public String getExecutionScript() {
        return executionScript;
    }
    public void setExecutionScript(String executionScript) {
        this.executionScript = executionScript;
        if(executionScript != null && !executionScript.isEmpty()) {
            this.executionScript = executionScript.replace("\r", "");           
        }
    }
    public Boolean getExecutionScriptEnabled() {
        return executionScriptEnabled;
    }
    public void setExecutionScriptEnabled(Boolean executionScriptEnabled) {
        this.executionScriptEnabled = executionScriptEnabled;
    }
	@Transient
    public boolean hasArguments() {
        if(StringUtils.isEmpty(this.commandLine)) {
            return false;
        }
        return this.commandLine.contains(CMD_LINE_ARGS);
    }
    @Transient
    public int countInputs() {
        if(StringUtils.isEmpty(this.commandLine)) {
            return 0;
        }
        return org.apache.commons.lang.StringUtils.countMatches(this.commandLine, CMD_LINE_INPUTS);
    }
    @Transient
    public int countOutputs() {
        if(StringUtils.isEmpty(this.commandLine)) {
            return 0;
        }
        return org.apache.commons.lang.StringUtils.countMatches(this.commandLine, CMD_LINE_OUTPUTS);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
