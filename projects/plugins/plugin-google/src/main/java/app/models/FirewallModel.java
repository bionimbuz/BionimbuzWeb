package app.models;

import java.util.Date;
import java.util.List;

import app.common.SystemConstants;

public class FirewallModel {
    
    public static enum PROTOCOL {
        tcp,
        udp
    }
    
    private String name; 
    private Integer port;
    private PROTOCOL protocol;
    private List<String> lstRanges;   
    private Date creationDate;     

    public FirewallModel() {}
    
    public FirewallModel(PROTOCOL protocol, Integer port, List<String> lstRanges) {
        this(generateName(protocol, port), protocol, port, lstRanges, null);
    }
    
    public FirewallModel(String name, PROTOCOL protocol, Integer port, List<String> lstRanges, Date creationDate) {
        this.name = name;
        this.port = port;
        this.protocol = protocol;
        this.lstRanges = lstRanges;
        this.creationDate = creationDate;
    }
    
    public static String generateName(PROTOCOL protocol, Integer port) {
        return SystemConstants.BNZ_FIREWALL + "-" + protocol + "-" + String.valueOf(port); 
    }    
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public PROTOCOL getProtocol() {
        return protocol;
    }
    public void setProtocol(PROTOCOL protocol) {
        this.protocol = protocol;
    }
    public List<String> getLstRanges() {
        return lstRanges;
    }
    public void setLstRanges(List<String> lstRanges) {
        this.lstRanges = lstRanges;
    }
}
