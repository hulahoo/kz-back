package kz.uco.tsadv.service;



import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DatesService {
    String NAME = "tsadv_DatesService";

    double calculateDifferenceInHours(Date endDate, Date startDate);

    double calculateDifferenceInHours(Date endDate, Date startDate, Boolean isRoundResult);

    int getFullDaysCount(Date startDate, Date endDate); /* returns full days count - including both start and end dates */

    Date getFirstDate(Date date1, Date date2);

    Date getSecondDate(Date date1, Date date2);

    Date getDateTime(Date date, Date time);

    boolean between(Date startDate, Date endDate, Date currentDate);

    boolean intersectPeriods(Date dateFrom1, Date dateTo1, Date dateFrom2, Date dateTo2);

    boolean overlapPeriods(Date dateFrom1, Date dateTo1, Date dateFrom2, Date dateTo2);

    boolean after(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean insideStartTouching(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean enclosing(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean enclosingStartTouching(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean enclosingEndTouching(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean exactMatch(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean startInside(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean inside(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean insideEndTouching(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean endInside(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    boolean before(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    int getDayOfMonth(Date date);

    Date getMonthBegin(Date date);

    Date getDayBegin(Date date);

    Date getMonthEnd(Date date);

    int getMonthNumber(Date date);

    int getMonthsDifference(Date date1, Date date2);

    List<Date> getAllMonths(Date updateTs);

    int getIntersectionLengthInDays(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    Map<Calendar, Calendar> getItersectionPeriod(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    String getHoursWithMinutes(double hours);

    String getHoursWithTwoDigitsAfterComma(double hours);

    Date toNearestWholeMinute(Date date);

    Period getPeriodFromTwoDates(Date startDate,Date endDate);

}