package app.common.utils;

import java.util.Calendar;
import java.util.Date;

public class DateArithmeticUtil {
    
    private Date date;
    
    public class DateArithmetic {
        private Integer value;
        private Calendar calendar = Calendar.getInstance();
        
        private DateArithmetic(final Integer value, final Date date) {
            this.value = value;
            this.calendar.setTime(date);
        }
        
        public Date seconds() {
            calendar.add(Calendar.SECOND, value);
            return calendar.getTime();
        }
        public Date minutes() {
            calendar.add(Calendar.MINUTE, value);
            return calendar.getTime();
        }
        public Date hours() {
            calendar.add(Calendar.HOUR, value);
            return calendar.getTime();
        }
        
        public Date days() {
            calendar.add(Calendar.DATE, value);
            return calendar.getTime();
        }
        public Date months() {
            calendar.add(Calendar.MONTH, value);
            return calendar.getTime();
        }
        public Date years() {
            calendar.add(Calendar.YEAR, value);
            return calendar.getTime();
        }
    }
    
    
    private DateArithmeticUtil(final Date date) {
        this.date = date;
    }

    public static DateArithmeticUtil from(final Date date) {
        return new DateArithmeticUtil(date);
    }

    // Value can be negative (past) or positive (future)
    public DateArithmetic add(final Integer value) {
        return new DateArithmetic(value, date);
    }    
}
