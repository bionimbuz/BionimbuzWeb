package app.common.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static List<Integer> splitToIntList(final String value, final String separator){
        List<Integer> res = new ArrayList<>();
        if(value == null || value.trim().isEmpty())
            return res;        
        String [] ports = 
                value.trim().split(separator);  
        for(String strPort : ports) {
            res.add(Integer.parseInt(strPort));
        }
        return res;
    }
    
    public static boolean isEmpty(final String str) {
        return str == null || str.isEmpty();
    }    
}
