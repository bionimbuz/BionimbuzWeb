package app.common;

import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.OperationApi;

public class ThreadHelper {

    public static int waitForOperation(final OperationApi api, Operation operation){
       int timeout = 60; // seconds
       int time = 0;

       while (operation.status() != Operation.Status.DONE){
          if (time >= timeout){
             return 1;
          }
          time++;
          try {
             Thread.sleep(1000);
          } catch (InterruptedException e) {
             e.printStackTrace();
          }

          operation = api.get(operation.selfLink());
       }
       //TODO: Check for errors.
       return 0;
    }
}
