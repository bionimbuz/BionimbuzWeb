package common.fields.validation;

import common.constants.I18N;
import common.constants.SystemConstants;
import play.data.validation.Check;

public class NetworkPortsCheck extends Check{

    @Override
    public boolean isSatisfied(Object validatedObject, Object value) {
        // Must be validated with @Required annotation
        if(value == null)
            return true;
        String strValue = value.toString().trim();
        // Must be validated with @Required annotation
        if(strValue.isEmpty())
            return true;
        
        String [] ports = 
                strValue.split(
                        SystemConstants.SPLIT_EXP_COMMA);        
        for(String strPort : ports) {
            try {
                int port = Integer.parseInt(strPort);
                if(port < 0 || port > 65535) {
                    setMessage(I18N.validation_port_out_of_range);
                    return false;
                }
            } catch(NumberFormatException e) {
                setMessage(I18N.validation_number_format);
                return false;
            }            
        }        
        return true;
    }
}

