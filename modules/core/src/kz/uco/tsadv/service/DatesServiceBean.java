package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import javafx.util.Pair;
import kz.uco.tsadv.modules.timesheet.config.TimecardConfig;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service(DatesService.NAME)
public class DatesServiceBean implements DatesService {

    private static final Long DAYS_IN_MILLIS = (long) 24 * 60 * 60 * 1000;
    private static final Long HOURS_IN_MILLIS = (long) 60 * 60 * 1000;
    private static final Long MINUTS_IN_MILLIS = (long) 60 * 1000;
    private static final SimpleDateFormat DAY_OF_MONTH_FORMAT = new SimpleDateFormat("dd");

    public double calculateDifferenceInHours(Date endDate, Date startDate) {
        Long diff = endDate.getTime() - startDate.getTime();
        long diffHoursIntegerPart = diff / HOURS_IN_MILLIS;
        long leftAfterHoursSecs = diff % HOURS_IN_MILLIS;
        long diffMinutes = leftAfterHoursSecs / MINUTS_IN_MILLIS;
        double fractionalPart = (double) diffMinutes / 60;
        double hours = diffHoursIntegerPart + fractionalPart;
        Integer roundPlaces = AppBeans.get(Configuration.class).getConfig(TimecardConfig.class).getHoursRound();
        if (roundPlaces != null) {
            hours = roundAvoid(hours, roundPlaces);
        }
        return hours;
    }

    public double calculateDifferenceInHours(Date endDate, Date startDate, Boolean isRoundResult) {
        Long diff = endDate.getTime() - startDate.getTime();
        long diffHoursIntegerPart = diff / HOURS_IN_MILLIS;
        long leftAfterHoursSecs = diff % HOURS_IN_MILLIS;
        long diffMinutes = leftAfterHoursSecs / MINUTS_IN_MILLIS;
        double fractionalPart = (double) diffMinutes / 60;
        double hours = diffHoursIntegerPart + fractionalPart;
        if (isRoundResult) {
            hours = roundAvoid(hours, 2);
        }
        return hours;
    }

    @Override
    public int getFullDaysCount(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
    }

    /* set time to date */
    public Date getDateTime(Date date, Date time) {
        SimpleDateFormat hoursFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minutesFormat = new SimpleDateFormat("mm");

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hoursFormat.format(time)));
        c.set(Calendar.MINUTE, Integer.parseInt(minutesFormat.format(time)));

        return c.getTime();
    }

    /**
     * startDate <= date <= endDate or startDate = null and startDate <= date or endDate = null and date <= endDate or true
     *
     * @param startDate
     * @param endDate
     * @param currentDate
     * @return
     */
    @Override
    public boolean between(Date startDate, Date endDate, Date currentDate) {
        if (startDate == null && endDate == null)
            return true;
        else if (startDate == null)
            return currentDate.before(endDate) || currentDate.equals(endDate);
        else if (endDate == null)
            return startDate.before(currentDate) || startDate.equals(currentDate);
        else
            return (startDate.before(currentDate) && currentDate.before(endDate)) || startDate.equals(currentDate) || endDate.equals(currentDate);
    }


    public Date getFirstDate(Date date1, Date date2) {
        return (date1.after(date2)) ? date2 : date1;
    }

    public Date getSecondDate(Date date1, Date date2) {
        return (date1.after(date2)) ? date1 : date2;
    }


    /* Here and bellow - 1st period is orange color and blue one - is 2nd period(in picture) */
    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean intersectPeriods(Date dateFrom1, Date dateTo1, Date dateFrom2, Date dateTo2) {
        return !dateFrom1.before(dateFrom2) && !dateFrom1.after(dateTo2)
                || !dateTo1.before(dateFrom2) && !dateTo1.after(dateTo2)
                || !dateFrom2.before(dateFrom1) && !dateFrom2.after(dateTo1)
                || !dateTo2.before(dateFrom1) && !dateTo2.after(dateTo1);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean overlapPeriods(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (endDate1.before(startDate2) && startDate1.before(endDate2)) {
            return false;
        }
        if (startDate1.after(startDate2) && startDate1.after(endDate2)) {
            return false;
        }
        return (endDate1.after(startDate2) || startDate1.equals(startDate2)) && (startDate1.before(endDate2) || endDate1.equals(endDate2));
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean after(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        return startDate2.after(endDate1);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean insideStartTouching(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (!overlapPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }
        return startDate2.equals(startDate1) && endDate2.before(endDate1);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean enclosing(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (!intersectPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }
        if (!overlapPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }

        return startDate1.after(startDate2) && endDate1.before(endDate2);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean enclosingStartTouching(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (!overlapPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }
        return startDate2.equals(startDate1) && endDate2.after(endDate1);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean enclosingEndTouching(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (!overlapPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }
        return endDate1.equals(endDate2) && startDate2.before(startDate1);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean exactMatch(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (!overlapPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }
        return endDate1.equals(endDate2) && startDate1.equals(startDate2);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean startInside(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (!overlapPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }
        return startDate2.after(startDate1) && startDate1.before(endDate1)
                && endDate2.after(endDate1);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean inside(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (!overlapPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }
        return startDate2.after(startDate1) && endDate2.before(endDate1);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean insideEndTouching(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (!overlapPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }
        return startDate2.after(startDate1) && endDate1.equals(endDate2);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean endInside(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (!overlapPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return false;
        }
        return startDate1.before(endDate2) && endDate1.after(endDate2);
    }

    /*https://i.stack.imgur.com/0c6q0.png*/
    public boolean before(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        return startDate2.after(endDate1);
    }


    public int getDayOfMonth(Date date) {
        return Integer.parseInt(DAY_OF_MONTH_FORMAT.format(date));
    }

    public Date getMonthBegin(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.getTime();
    }

    @Override
    public Date getDayBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    protected Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public Date getMonthEnd(Date date) {
        return DateUtils.addDays(DateUtils.addMonths(getMonthBegin(date), 1), -1);
    }

    @Override
    public int getMonthNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    @Override
    public int getMonthsDifference(Date date1, Date date2) {
        return getMonthNumber(date2) - getMonthNumber(date1);
    }

    private double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    @Override
    public List<Date> getAllMonths(Date date) {
        ArrayList<Date> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Calendar calendar = getCalendar(date);
            calendar.set(Calendar.MONTH, i);
            list.add(calendar.getTime());
        }
        return list;
    }

    @Override
    public int getIntersectionLengthInDays(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        int result = 0;
        if (startDate1 == null || endDate1 == null || startDate2 == null || endDate2 == null) {
            return result;
        }
        if (!intersectPeriods(startDate1, endDate1, startDate2, endDate2)) {
            return result;
        } else {
            if (startInside(startDate1, endDate1, startDate2, endDate2)) {
                result = getFullDaysCount(startDate2, endDate1);

            } else if (
                    insideStartTouching(startDate1, endDate1, startDate2, endDate2) ||
                            inside(startDate1, endDate1, startDate2, endDate2) ||
                            insideEndTouching(startDate1, endDate1, startDate2, endDate2)) {
                result = getFullDaysCount(startDate2, endDate2);

            } else if (
                    enclosingStartTouching(startDate1, endDate1, startDate2, endDate2) ||
                            enclosing(startDate1, endDate1, startDate2, endDate2) ||
                            enclosingEndTouching(startDate1, endDate1, startDate2, endDate2) ||
                            exactMatch(startDate1, endDate1, startDate2, endDate2)
            ) {
                result = getFullDaysCount(startDate1, endDate1);
            } else if (endInside(startDate1, endDate1, startDate2, endDate2)) {
                result = getFullDaysCount(startDate1, endDate2);
            }
        }
        return result;
    }

    @Override
    public Pair<Calendar, Calendar> getItersectionPeriod(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        Pair<Calendar, Calendar> result = null;
        //first period enter in second period
        if (!startDate1.before(startDate2)
                && !endDate1.after(endDate2)) {
            c1.setTime(startDate1);
            c2.setTime(endDate1);
            result = new Pair<>(c1, c2);
        }
        //from first period start to second period end
        if (!startDate1.before(startDate2)
                && !startDate1.after(endDate2)
                && !startDate1.before(endDate2)) {
            c1.setTime(startDate1);
            c2.setTime(endDate2);
            result = new Pair<>(c1, c2);
        }
        //from second period start to first period end
        if (startDate1.before(startDate2)
                && endDate1.after(startDate2)
                && !endDate1.after(endDate2)) {
            c1.setTime(startDate2);
            c2.setTime(endDate1);
            result = new Pair<>(c1, c2);
        }
        //second period enter to first period
        if (!startDate1.after(startDate2)
                && !endDate1.before(endDate2)) {
            c1.setTime(startDate2);
            c2.setTime(endDate2);
            result = new Pair<>(c1, c2);
        }
        return result;
    }

    @Override
    public String getHoursWithMinutes(double hours) {
        int intHours = (int) hours;
        double minutes = hours - intHours;
        minutes = minutes * 60;
        if (minutes != 0) {
            return String.format("%d:%02d", intHours, Math.round(minutes));
        }
        return String.valueOf(intHours);
    }

    @Override
    public String getHoursWithTwoDigitsAfterComma(double hours) {
        if ((hours % 1) != 0) {
            return String.valueOf(roundAvoid(hours, 2));
        }
        return String.valueOf((int) hours);
    }


    @Override
    public Date toNearestWholeMinute(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        if (c.get(Calendar.SECOND) >= 30)
            c.add(Calendar.MINUTE, 1);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    @Override
    public Period getPeriodFromTwoDates(Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            return Period.between(new java.sql.Date(startDate.getTime()).toLocalDate(),
                    new java.sql.Date(endDate.getTime()).toLocalDate());
        } else {
            return null;
        }
    }

}