package app.models;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PluginComputingInstanceModel extends Body<PluginComputingInstanceModel> {

    private String id = "";
    private String name = "";
    private String machineType = "";
    private Date creationDate;
    private String internalIp = "";
    private String externalIp = "";
    private String startupScript = "";
    private String region = "";
    private String zone = "";
    private String imageUrl = "";
    private String type = "";
    private List<Integer> firewallUdpPorts;
    private List<Integer> firewallTcpPorts;

    public PluginComputingInstanceModel() {
        super();
    }

    public PluginComputingInstanceModel(
            String id,
            String name,
            String machineType,
            Date creationDate) {
        super();
        this.id = id;
        this.name = name;
        this.machineType = machineType;
        this.creationDate = creationDate;
    }

    public static String generateUniqueName(
            List<PluginComputingInstanceModel> currentInstances,
            final String prefix) {
        Set<Integer> setOfNameIds = getSetOfCurrentIds(currentInstances, prefix);

        int newId = setOfNameIds.size();
        while(setOfNameIds.contains(++newId));
        return generateNameForId(newId, prefix);
    }

    public static String generateNameForId(final Integer id, final String prefix) {
        return prefix + "-" + id;
    }

    public static Integer extractIdFromName(final String name, final String prefix) {
        String [] nameSpplited = name.split(prefix);
        if(nameSpplited.length != 2)
            return -1;
        return Integer.parseInt(nameSpplited[1].replaceAll("-", ""));
    }

    protected static Set<Integer> getSetOfCurrentIds(final List<PluginComputingInstanceModel> currentInstances, final String prefix) {
        Set<Integer> ids = new TreeSet<>();
        for (PluginComputingInstanceModel instanceModel : currentInstances) {
            Integer id = extractIdFromName(instanceModel.getName(), prefix);
            if(id >= 0) {
                ids.add(id);
            }
        }
        return ids;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMachineType() {
        return machineType;
    }
    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public String getInternalIp() {
        return internalIp;
    }
    public void setInternalIp(String internalIp) {
        this.internalIp = internalIp;
    }
    public String getExternalIp() {
        return externalIp;
    }
    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }
    public String getStartupScript() {
        return startupScript;
    }
    public void setStartupScript(String startupScript) {
        this.startupScript = startupScript;
    }
    public String getZone() {
        return zone;
    }
    public void setZone(String zone) {
        this.zone = zone;
    }
    @JsonIgnore
    public URI getImageUri() {
        return URI.create(imageUrl);
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public List<Integer> getFirewallUdpPorts() {
        return firewallUdpPorts;
    }
    public void setFirewallUdpPorts(List<Integer> firewallUdpPorts) {
        this.firewallUdpPorts = firewallUdpPorts;
    }
    public List<Integer> getFirewallTcpPorts() {
        return firewallTcpPorts;
    }
    public void setFirewallTcpPorts(List<Integer> firewallTcpPorts) {
        this.firewallTcpPorts = firewallTcpPorts;
    }
}
