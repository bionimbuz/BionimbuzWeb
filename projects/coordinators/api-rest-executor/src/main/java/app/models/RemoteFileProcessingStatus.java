package app.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RemoteFileProcessingStatus {

    private Integer total = 0;
    private Double progress = 0d;
    private Set<String> succeded = new TreeSet<>();
    private Map<String, String> failed = new HashMap<>();
    private Object _lock_ = new Object();
    
    public RemoteFileProcessingStatus(final Integer total) {
        this.total = total;
    }
    
    public void addSucceded(final String fileName) {
        synchronized (_lock_) {
            if(failed.containsKey(fileName)){
                failed.remove(fileName);
            }
            succeded.add(fileName);
            updateProgress();
        }
    }
    
    public void addFailed(final String fileName, final String message) {
        synchronized (_lock_) {
            if(succeded.contains(fileName)){
                succeded.remove(fileName);
            }
            failed.put(fileName, message);
            updateProgress();
        }
    }
    
    public Double getProgress() {
        synchronized (_lock_) {
            return progress;
        }
    }
    
    private void updateProgress() {
        Integer current = failed.size() + succeded.size();        
        progress = current.doubleValue() / total.doubleValue();
        if(progress > 1d) 
            progress = 1d;
    }
}
