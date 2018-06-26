package app.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
    
    @Value("${application.system.mem:#{0}}")
    private Double memory;
    @Value("${application.system.cpu:#{null}}")
    private Short cpu;
    @Value("${application.system.ip:#{null}}")
    private String ip;
    
    public ApplicationProperties() {        
    }
    
    public Double getSystemMemory() {
        return memory;
    }
    public Short getSystemCpu() {
        if(cpu == null) {
            cpu = (short) Runtime.getRuntime().availableProcessors();
        }
        return cpu;
    }
    public String getSystemIP() {
        return ip;
    }
}
