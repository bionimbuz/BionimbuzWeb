package app.execution.jobs;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.exceptions.SingletonNotInitializedException;
import app.execution.CoordinatorServerAccess;

public class StatusNotifierJob {
    protected static final Logger LOGGER = LoggerFactory.getLogger(StatusNotifierJob.class);

    private static Lock _lock_ = new ReentrantLock();
    
    public static void tryNotification() {
        new Thread(new Runnable() {
            @Override
            public void run() {    
                if(!_lock_.tryLock())
                    return;
                try {      
                    doNotification();
                } finally {
                    _lock_.unlock();
                } 
            }
        }).start();    
    }
    
    public static void waitNotification() {     
        try {
            _lock_.lock();
            doNotification();
        } finally {
            _lock_.unlock();
        } 
    }
    
    private static void doNotification() {     
        try {
            CoordinatorServerAccess.get().sendUpdateStatus();
        } catch (SingletonNotInitializedException e) {
            LOGGER.error(e.getMessage(), e);
        } 
    }
}
