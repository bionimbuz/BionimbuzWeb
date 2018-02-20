package common.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    
    final Date date;
    
    private DateUtil(final Date date) {
        this.date = date;
    }
    
    public static DateUtil is(final Date date) {
        return new DateUtil(date);
    }
    
    public boolean greaterThan(final Date comparedDate) {
        return compareDates(this.date, comparedDate) > 0;        
    }    
    public boolean greaterThanOrEqualsTo(final Date comparedDate) {
        return compareDates(this.date, comparedDate) >= 0;
    }    
    public boolean lessThan(final Date comparedDate) {
        return compareDates(this.date, comparedDate) < 0;        
    } 
    public boolean lessThanOrEqualsTo(final Date comparedDate) {
        return compareDates(this.date, comparedDate) <= 0;
        
    }
    public boolean equalsThan(final Date comparedDate) {
        return compareDates(this.date, comparedDate) == 0;
    }
    
    private static int compareDates(
            final Date currentDate,
            final Date comparedDate) {        
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);
        Calendar lastUpdateFound = Calendar.getInstance();
        lastUpdateFound.setTime(comparedDate);        
        return currentCalendar.compareTo(lastUpdateFound);
    }
}
