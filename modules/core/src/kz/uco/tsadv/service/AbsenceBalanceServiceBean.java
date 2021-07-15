package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.CategoryAttributeValue;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.api.Null;
import kz.uco.tsadv.entity.dbview.AbsenceBalanceV;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(AbsenceBalanceService.NAME)
public class AbsenceBalanceServiceBean implements AbsenceBalanceService {

    public static final String ANNUAL_DAYS_COUNT = "ANNUAL_DAYS_COUNT";
    public static final String TRANSFER = "TRANSFER";

    @Inject
    protected CommonService commonService;
    @Inject
    protected TimesheetService timesheetService;
    @Inject
    protected DatesService datesService;
    @Inject
    protected DynamicAttributesService dynamicAttributesService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected AbsenceService absenceService;
    @Inject
    protected Persistence persistence;

    @Override
    @Deprecated
    public List<AbsenceBalanceV> getAbsenceBalance(UUID personGroupId) {
        throw new RuntimeException("remove");
    }

    @Override
    public AbsenceBalance createNewAbsenceBalance(PersonGroupExt personGroup, PositionGroupExt positionGroup) {
        if (personGroup == null) {
            return null;
        }
        if (personGroup.getPerson() == null) {
            return null;
        }
        AbsenceBalance absenceBalance = metadata.create(AbsenceBalance.class);
        Date dateFrom = personGroup.getPerson().getHireDate();
        Date dateTo = DateUtils.addYears(dateFrom, 1);
        dateTo = DateUtils.addDays(dateTo, -1);
        int additionalBalanceDays = 0;
        int balanceDays = getBalanceDaysFromGlobalValue();
        if (positionGroup != null) {
            additionalBalanceDays = getAdditionalBalanceDays(positionGroup);
            balanceDays = getBalanceDays(null, positionGroup);
        }

        absenceBalance.setAdditionalBalanceDays((double) additionalBalanceDays);
        absenceBalance.setBalanceDays((double) balanceDays);
        absenceBalance.setDateFrom(dateFrom);
        absenceBalance.setDateTo(dateTo);
        absenceBalance.setLongAbsenceDays((double) 0);
        absenceBalance.setDaysLeft(balanceDays - absenceBalance.getLongAbsenceDays());
        absenceBalance.setDaysSpent((double) 0);
        absenceBalance.setAdditionalBalanceDays((double) additionalBalanceDays);
        absenceBalance.setExtraDaysSpent((double) 0);
        absenceBalance.setPersonGroup(personGroup);
        absenceBalance.setExtraDaysLeft((double) additionalBalanceDays);
        return absenceBalance;
    }

    @Override
    public AbsenceBalance createNewAbsenceBalance(Absence absence, PersonGroupExt personGroup, PositionGroupExt positionGroup, Date lastEndDate) {
        if (personGroup == null) {
            return null;
        }
        AbsenceBalance absenceBalance = metadata.create(AbsenceBalance.class);
        Date dateFrom = lastEndDate == null ? getDateFrom(personGroup) : DateUtils.addDays(lastEndDate, 1);
        Date dateTo = DateUtils.addYears(dateFrom, 1);
        dateTo = DateUtils.addDays(dateTo, -1);
        int additionalBalanceDays = 0;
        int balanceDays = getBalanceDaysFromGlobalValue();
        if (positionGroup != null) {
            additionalBalanceDays = getAdditionalBalanceDays(positionGroup);
            balanceDays = getBalanceDays(null, positionGroup);
        }

        absenceBalance.setAdditionalBalanceDays((double) additionalBalanceDays);
        absenceBalance.setBalanceDays((double) balanceDays);
        absenceBalance.setDateFrom(dateFrom);
        absenceBalance.setDateTo(dateTo);
        absenceBalance.setPersonGroup(personGroup);
        absenceBalance.setLongAbsenceDays(calculateLongAbsenceDays(absenceBalance, absence, null));
        absenceBalance.setDaysLeft(balanceDays - absenceBalance.getLongAbsenceDays());
        absenceBalance.setDaysSpent((double) 0);
        absenceBalance.setAdditionalBalanceDays((double) additionalBalanceDays);
        absenceBalance.setExtraDaysSpent((double) 0);
        absenceBalance.setExtraDaysLeft((double) additionalBalanceDays);
        return absenceBalance;
    }

    protected Date getDateFrom(PersonGroupExt personGroup) {
        List<AbsenceBalance> absenceBalanceList = commonService.getEntities(AbsenceBalance.class,
                "select e from tsadv$AbsenceBalance e where e.dateTo = " +
                        "(select max(le.dateTo) from tsadv$AbsenceBalance le where le.personGroup.id = :personGroupId)" +
                        " and e.personGroup.id = :personGroupId", ParamsMap.of("personGroupId", personGroup.getId()), View.LOCAL);
        if (absenceBalanceList != null && !absenceBalanceList.isEmpty()) {
            return DateUtils.addDays(absenceBalanceList.get(0).getDateTo(), 1);
        }
        return personGroup.getPerson().getHireDate();
    }

    @Override
    public AbsenceBalance createNextAbsenceBalances(AbsenceBalance lastAbsenceBalance, Absence absence, int excessDaysFromPrevious) {
        AbsenceBalance newBalance = metadata.create(AbsenceBalance.class);
        PersonGroupExt personGroup = lastAbsenceBalance.getPersonGroup();
        Date dateFrom = getActualDateFrom(lastAbsenceBalance);
        Date dateTo = getActualDateTo(dateFrom);
        int balanceDays = getBalanceDays(personGroup, null);


        /*Category category = dynamicAttributesService.getCategory("ABSENCETYPE", "tsadv$DicAbsenceType");
        List<CategoryAttribute> categoryAttributes = dynamicAttributesService.getCategoryAttributes(category);
        CategoryAttribute categoryAttribute = categoryAttributes.stream().filter(c -> c.getCode().equals("ABSENCETYPELONG")).findFirst().orElse(null);
        boolean isLongAbsence = false;
        CategoryAttributeValue categoryAttributeValue = dynamicAttributesService.getCategoryAttributeValue(absence.getType(), categoryAttribute);
        if (categoryAttributeValue!=null) {
            isLongAbsence = categoryAttributeValue.getBooleanValue();
        }

        if (isLongAbsence) {
            balanceDays = balanceDays-countExcludedDays(absence.getAbsenceDays(), balanceDays);
        }*/


        int additionalBalanceDays = getAdditionalBalanceDays(personGroup);
        int extraDaysSpent = 0;
        int extraDaysLeft = additionalBalanceDays - extraDaysSpent;

        newBalance.setPersonGroup(personGroup);
        newBalance.setDateFrom(dateFrom);
        newBalance.setDateTo(dateTo);
        newBalance.setBalanceDays((double) balanceDays);
        newBalance.setAdditionalBalanceDays((double) additionalBalanceDays);
        newBalance.setDaysSpent((double) 0);
        newBalance.setExtraDaysSpent((double) extraDaysSpent);
        newBalance.setExtraDaysLeft((double) extraDaysLeft);

        newBalance.setDaysLeft((double) balanceDays);
        newBalance = dataManager.commit(newBalance);
        return newBalance;

    }

    @Override
    public AbsenceBalance createNextAbsenceBalancesWithoutPersistence(AbsenceBalance lastAbsenceBalance, Absence absence, int excessDaysFromPrevious) {
        AbsenceBalance newBalance = metadata.create(AbsenceBalance.class);
        PersonGroupExt personGroup = lastAbsenceBalance.getPersonGroup();
        Date dateFrom = getActualDateFrom(lastAbsenceBalance);
        Date dateTo = getActualDateTo(dateFrom);
        int balanceDays = getBalanceDays(personGroup, null);
        int additionalBalanceDays = getAdditionalBalanceDays(personGroup);
        int extraDaysSpent = 0;
        int extraDaysLeft = additionalBalanceDays - extraDaysSpent;

        newBalance.setPersonGroup(personGroup);
        newBalance.setDateFrom(dateFrom);
        newBalance.setDateTo(dateTo);
        newBalance.setBalanceDays((double) balanceDays);
        newBalance.setAdditionalBalanceDays((double) additionalBalanceDays);
        newBalance.setDaysSpent((double) 0);
        newBalance.setExtraDaysSpent((double) extraDaysSpent);
        newBalance.setExtraDaysLeft((double) extraDaysLeft);

        newBalance.setDaysLeft((double) balanceDays);
        return newBalance;
    }

    @Override
    public Date getActualDateTo(Date dateFrom) {
        if (dateFrom == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateFrom);
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    @Override
    public Date getActualDateFrom(AbsenceBalance lastAbsenceBalance) {


        Map<String, Object> map = new HashMap<>();
        PersonGroupExt personGroup = lastAbsenceBalance.getPersonGroup();
        map.put("personGroupId", personGroup.getId());
        if (lastAbsenceBalance == null) {
            return (personGroup.getPerson().getHireDate());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastAbsenceBalance.getDateTo());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    @Override
    public int getBalanceDays(PersonGroupExt personGroup, PositionGroupExt positionGroup) {
        int result = 0;
        List<VacationConditions> vacationConditionsList;
        if (personGroup != null) {
            vacationConditionsList = getVacationConditionsList(personGroup);
        } else {
            vacationConditionsList = getVacationConditionsList(positionGroup);
        }
        for (VacationConditions ad : vacationConditionsList) {
            if (ad.getMainDaysNumber() != null) {
                result += ad.getMainDaysNumber() + ad.getAdditionalDays();
            }
        }

        if (result == 0) {
            result = getBalanceDaysFromGlobalValue();
        }
        return result;
    }

    public int getBalanceDays(PersonGroupExt personGroup, PositionGroupExt positionGroup, Date date) {
        int result = 0;
        List<VacationConditions> vacationConditionsList;
        if (personGroup != null) {
            vacationConditionsList = getVacationConditionsList(personGroup, date);
        } else {
            vacationConditionsList = getVacationConditionsList(positionGroup, date);
        }
        for (VacationConditions ad : vacationConditionsList) {
            if (ad.getMainDaysNumber() != null) {
                result += ad.getMainDaysNumber() + ad.getAdditionalDays();
            }
        }

        if (result == 0) {
            result = getBalanceDaysFromGlobalValue(date);
        }
        return result;
    }

    protected int getBalanceDaysFromGlobalValue() {
        return getBalanceDaysFromGlobalValue(null);
    }

    protected int getBalanceDaysFromGlobalValue(Date date) {
        String queryString = "select e from tsadv$GlobalValue e" +
                " where e.code = '" + ANNUAL_DAYS_COUNT + "' " +
                "   and :systemDate between e.startDate and e.endDate";
        Map<String, Object> params = new HashMap<>();
        params.put("systemDate", date != null ? date : CommonUtils.getSystemDate());
        GlobalValue globalValues = commonService.getEntity(GlobalValue.class, queryString, params, null);
        if (globalValues != null) {
            return Integer.valueOf(globalValues.getValue());
        }
        return 0;
    }

    @Override
    public int getAdditionalBalanceDays(PersonGroupExt personGroupExt) {
        return getAdditionalBalanceDays(personGroupExt, null);
    }

    public int getAdditionalBalanceDays(PersonGroupExt personGroupExt, Date date) {
        List<VacationConditions> vacationConditionsList = getVacationConditionsList(personGroupExt, date);

        int result = 0;
        for (VacationConditions ad : vacationConditionsList) {
            result += ad.getAdditionalDays();
        }
        return result;
    }

    @Override
    public int getAdditionalBalanceDays(PositionGroupExt positionGroupExt) {
        List<VacationConditions> vacationConditionsList = getVacationConditionsList(positionGroupExt);

        int result = 0;
        for (VacationConditions ad : vacationConditionsList) {
            result += ad.getAdditionalDays();
        }
        return result;
    }

    @Override
    public int getAnnualDaysSpent(PersonGroupExt personGroup, Absence absence) {
        int annualDaysSpent = 0;
        annualDaysSpent += absence.getAbsenceDays();
        Category category = absence.getCategory();
        if (category != null && category.getName().equalsIgnoreCase("ANNUAL")) {
            CategoryAttributeValue categoryAttributeValue = dynamicAttributesService.getCategoryAttributeValue(category, absence, "ANNUALAddDays");
            annualDaysSpent -= categoryAttributeValue != null ? categoryAttributeValue.getIntValue() : 0;
        }
        return annualDaysSpent;
    }

    public int getDaysDifference(PersonGroupExt personGroup, Absence originalAbsence, Absence changeAbsence) {
        int days;
        /* when change absence is truncate original */
        Date startDate = originalAbsence.getDateFrom();
        Date endDate = changeAbsence.getDateFrom();
        endDate = DateUtils.addDays(endDate, -1);

        /* full recall from vacation */
        if (changeAbsence.getDateFrom().equals(originalAbsence.getDateFrom()) && changeAbsence.getDateTo().equals(originalAbsence.getDateTo())) {
            return 0;
        }
        /* when change absence is extends original */
        if (changeAbsence.getDateFrom().equals(originalAbsence.getDateTo())) {
            endDate = changeAbsence.getDateTo();
        }
        /* when change absence transfers original*/
        if (changeAbsence.getType().getCode().equals(TRANSFER)) {
            startDate = changeAbsence.getDateFrom();
            endDate = changeAbsence.getDateTo();
        }

        days = datesService.getFullDaysCount(startDate, endDate);
        Boolean ignoreHolidays = originalAbsence.getType().getIgnoreHolidays();
        if (ignoreHolidays != null && !ignoreHolidays) {
            Absence changedAbsence = originalAbsence;
            changeAbsence.setDateFrom(startDate);
            changeAbsence.setDateTo(endDate);
            kz.uco.tsadv.modules.timesheet.model.Calendar calendar = getCurrentAssignmentExt(personGroup.getId()).getGroup().getAnalytics().getCalendar();
            if (calendar != null) {
                days = days - timesheetService.getAllHolidays(calendar, changedAbsence.getDateFrom(), changedAbsence.getDateTo());
            }
        }
        if (originalAbsence.getAdditionalDays() != null) {
            days -= originalAbsence.getAdditionalDays();
        }
        return days;
    }

    @Override
    public int getCurrentAbsenceDays(PersonGroupExt personGroup) {

        List<AbsenceBalanceV> absenceBalances = commonService.getEntities(AbsenceBalanceV.class,
                "select e from tsadv$AbsenceBalanceV e " +
                        "                where e.personGroup.id = :personGroupId " +
                        "                   and  e.dateFrom <= :systemDate " +
                        "                order by e.dateFrom desc ",
                ParamsMap.of("personGroupId", personGroup.getId(), "systemDate", CommonUtils.getSystemDate()),
                View.LOCAL);

        if (absenceBalances.isEmpty()) {
            return 0;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd.MM.yyyy");
        Date today = null;
        try {
            today = formatter.parse(formatter.format(CommonUtils.getSystemDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date finalToday = today;


        int allDaysLeft = absenceBalances.stream()
                .filter(a -> !datesService.overlapPeriods(a.getDateFrom(), a.getDateTo(), finalToday, finalToday))
                .mapToInt(a -> (a.getBalanceDays() != null ? a.getBalanceDays() : 0) - (a.getLongAbsenceDays() != null ? a.getLongAbsenceDays() : 0) - (a.getDaysSpent() != null ? a.getDaysSpent() : 0)).sum();

        AbsenceBalanceV currentBalance = absenceBalances.stream()
                .filter(a -> datesService.overlapPeriods(a.getDateFrom(), a.getDateTo(), finalToday, finalToday))
                .findFirst().orElse(null);

        return allDaysLeft + (int) Math.ceil(getCurrentAbsenceDaysByCurrentBalance(currentBalance, personGroup));
    }

    protected double getCurrentAbsenceDaysByCurrentBalance(AbsenceBalanceV currentBalance, PersonGroupExt personGroup) {
        if (Objects.isNull(currentBalance)) return 0.0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(CommonUtils.getSystemDate());
        long daysInPeriodForNow = ((cal.getTimeInMillis() - currentBalance.getDateFrom().getTime()) / (1000 * 60 * 60 * 24)) + 1;
        long allDaysInPeriod = ((currentBalance.getDateTo().getTime() - currentBalance.getDateFrom().getTime()) / (1000 * 60 * 60 * 24)) + 1;

        return (double) (currentBalance.getBalanceDays() != null ? currentBalance.getBalanceDays() : 0)
                * daysInPeriodForNow / allDaysInPeriod
                - (currentBalance.getLongAbsenceDays() != null ? currentBalance.getLongAbsenceDays() : 0)
                - (currentBalance.getDaysSpent() != null ? currentBalance.getDaysSpent() : 0);
    }

    @Override
    public void recountBalanceDays(Absence absence, List<AbsenceBalance> absenceBalanceList, PositionGroupExt positionGroup) {
        /*if (absence == null || absenceBalanceList == null || positionGroup == null || absenceBalanceList.isEmpty()) {
            return;
        }
        *//*List<AbsenceBalance> absenceBalanceList = getAbsenceBalanceList(absence, positionGroup);
        if (absenceBalanceList.isEmpty()) {
            return;
        }*//*
        List<AbsenceBalance> changedAbsenceBalances = new ArrayList<>();
        if (absenceService.isLongAbsence(absence)) {
            for (AbsenceBalance absenceBalance : absenceBalanceList) {

                int absenceDaysInCurrentPeriod = getIntersectionLengthInDays(absence, absenceBalance);
                int newBalanceDays = countNewBalanceDays(absenceDaysInCurrentPeriod, getBalanceDays(null, positionGroup));
                absenceBalance.setBalanceDays(newBalanceDays);
                if (absenceBalance.getDaysSpent() == null) {
                    absenceBalance.setDaysSpent(0);
                }
                absenceBalance.setDaysLeft(newBalanceDays - absenceBalance.getDaysSpent());
                changedAbsenceBalances.add(absenceBalance);

            }
        }
        if (!changedAbsenceBalances.isEmpty()) {
            dataManager.commit(new CommitContext(changedAbsenceBalances));
        }*/
    }

    @Override
    public void recountBalanceDaysOnDelete(Absence absence, PositionGroupExt positionGroup) {
        /*if (absence == null || positionGroup == null) {
            return;
        }
        List<AbsenceBalance> existingAbsenceBalances = getAbsenceBalanceList(absence, positionGroup);
        if (existingAbsenceBalances.isEmpty()) {
            return;
        }
        List<AbsenceBalance> changedAbsenceBalances = new ArrayList<>();
        if (absenceService.isLongAbsence(absence)) {
            for (AbsenceBalance absenceBalance : existingAbsenceBalances) {
                if (absenceIntersectsWithPeriod(absence, absenceBalance)) {
                    int newBalanceDays = getBalanceDays(null, positionGroup);
                    absenceBalance.setBalanceDays(newBalanceDays);
                    if (absenceBalance.getDaysSpent() == null) {
                        absenceBalance.setDaysSpent(0);
                    }
                    absenceBalance.setDaysLeft(newBalanceDays - absenceBalance.getDaysSpent());
                    changedAbsenceBalances.add(absenceBalance);
                }
            }
        }
        if (!changedAbsenceBalances.isEmpty()) {
            dataManager.commit(new CommitContext(changedAbsenceBalances));
        }*/
    }

    @Override
    public void recountBalanceDaysOnChangingLongAbsence(Absence absence, List<AbsenceBalance> absenceBalanceList, PositionGroupExt positionGroup) {
        /*if (absence == null || absenceBalanceList == null || positionGroup == null || absenceBalanceList.isEmpty()) {
            return;
        }
        List<AbsenceBalance> changedAbsenceBalances = new ArrayList<>();
        if (absenceService.isLongAbsence(absence)) {
            for (AbsenceBalance absenceBalance : absenceBalanceList) {
                int newBalanceDays = getBalanceDays(null, positionGroup);
                absenceBalance.setBalanceDays(newBalanceDays);
                if (absenceBalance.getDaysSpent() == null) {
                    absenceBalance.setDaysSpent(0);
                }
                absenceBalance.setDaysLeft(newBalanceDays - absenceBalance.getDaysSpent());
                changedAbsenceBalances.add(absenceBalance);
            }
        }
        if (!changedAbsenceBalances.isEmpty()) {
            dataManager.commit(new CommitContext(changedAbsenceBalances));
        }*/
    }

    @Override
    public List<AbsenceBalance> getAbsenceBalanceList(Absence absence, PositionGroupExt positionGroup) {

        List<AbsenceBalance> result = new ArrayList<>();

        if (absence == null) {
            return result;
        }

        String queryString = "select e from tsadv$AbsenceBalance e" +
                " where e.personGroup.id = :personGroupId" +
                " order by e.dateFrom";

        /*String queryString = "select e from tsadv$AbsenceBalance e" +
                " where (e.daysSpent is null or e.daysSpent < e.balanceDays) and e.personGroup.id = :personGroupId" +
                " order by e.dateFrom";*/

        /*String queryString = "select e from tsadv$AbsenceBalance e" +
                " where e.daysLeft > 0 and e.personGroup.id = :personGroupId" +
                " order by e.dateFrom";*/
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", absence.getPersonGroup());

        List<AbsenceBalance> existingAbsenceBalances = commonService.getEntities(AbsenceBalance.class, queryString, params, "absenceBalance.edit");
        if (existingAbsenceBalances.isEmpty()) {
            AbsenceBalance absenceBalance = createNewAbsenceBalance(absence.getPersonGroup(), positionGroup);
            if (absenceBalance != null) {
                dataManager.commit(absenceBalance);
            }
        }
        result = commonService.getEntities(AbsenceBalance.class, queryString, params, "absenceBalance.edit");
        return result;
    }

    @Override
    public void sendNotificationBeforeEndLongAbsence() {
        Date sysDate = CommonUtils.getSystemDate();
        List<UUID> absenceTypeLongIdList = absenceService.getAbsenceTypeLong(null).stream().map(DicAbsenceType::getId).collect(Collectors.toList());
        List<Absence> absenceList = commonService.getEntities(Absence.class,
                "select e from tsadv$Absence e " +
                        "where e.dateTo - e.dateFrom >= 30 " +
                        "   and e.type.id in :listTypeId " +
                        "   and e.dateTo in :days",
                ParamsMap.of("listTypeId", absenceTypeLongIdList,
                        "days", Arrays.asList(DateUtils.addDays(sysDate, 7),
                                DateUtils.addDays(sysDate, 14),
                                DateUtils.addDays(sysDate, 30),
                                DateUtils.addDays(sysDate, 40))),
                "absence.viewForNotify");
        absenceList.forEach(absence -> {
            try {
                PositionGroupExt positionGroupExt = employeeService.getPositionGroupByPersonGroupId(absence.getPersonGroup().getId(), View.MINIMAL);
                if (Objects.isNull(positionGroupExt)) {
                    throw new RuntimeException(String.format("Scheduler_sendNotificationBeforeEndLongAbsence: In Absence[%s] not found position!", absence.getId()));
                }
                List<TsadvUser> receiverManager = employeeService.recursiveFindManager(positionGroupExt.getId());
                if (CollectionUtils.isEmpty(receiverManager)) {
                    throw new RuntimeException(String.format("Scheduler_sendNotificationBeforeEndLongAbsence: In Absence[%s] not found manager!", absence.getId()));
                }

                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("fullName", absence.getPersonGroup().getFullName());
                paramsMap.put("employeeNumber", absence.getPersonGroup().getPerson().getEmployeeNumber());
                paramsMap.put("dateOfExpiration", new SimpleDateFormat("dd.MM.yyyy").format(absence.getDateTo()));
                paramsMap.put("typeRu", absence.getType().getLangValue1());
                paramsMap.put("typeEn", absence.getType().getLangValue3());

                for (TsadvUser userExt : receiverManager) {
                    if (userExt == null) continue;
                    if (absence.getPersonGroup().getPersonLatinFioWithEmployeeNumber() != null) {
                        paramsMap.put("fullNameLatin", absence.getPersonGroup().getPersonLatinFioWithEmployeeNumber(userExt.getLanguage()));
                    } else {
                        paramsMap.put("fullNameLatin", absence.getPersonGroup().getFullName());
                    }
                    notificationService.sendParametrizedNotification("absence.notify.beforeEndLongTime", userExt, paramsMap);
                }

            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        });
    }

    protected AssignmentExt getCurrentAssignmentExt(UUID personGroupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("absencePersonGroupId", personGroupId);
        AssignmentExt assignmentExt = commonService.getEntity(AssignmentExt.class, "select e" +
                        "                          from base$AssignmentExt e" +
                        "                          where e.personGroup.id = :absencePersonGroupId and e.deleteTs is null",
                map, "assignment-with-org-calendar");
        return assignmentExt;
    }

    protected List<Absence> getAnnualAbsences(PersonGroupExt personGroup) {
        LoadContext<Absence> loadContext = LoadContext.create(Absence.class);
        loadContext.setQuery(LoadContext.createQuery(" select e from tsadv$Absence e join e.type t" +
                " where e.personGroup.id = :personGroupId  and t.code ='ANNUAL' and e.deleteTs is null ")
                .setParameter("personGroupId", personGroup.getId()))
                .setView("absence-with-dynamic-attributes.view");
        return dataManager.loadList(loadContext);
    }


    protected List<VacationConditions> getVacationConditionsList(PersonGroupExt personGroup) {
        return getVacationConditionsList(personGroup, null);
    }

    protected List<VacationConditions> getVacationConditionsList(PersonGroupExt personGroup, Date date) {
        String queryString = "select e from tsadv$VacationConditions e" +
                " where e.positionGroup.id = (select a.positionGroup.id from base$AssignmentExt a" +
                " where a.personGroup.id = :personGroupId" +
                "   and :systemDate between a.startDate and a.endDate" +
                "   and a.primaryFlag = 'true')" +
                "   and :date between e.startDate and e.endDate";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroup.getId());
        params.put("date", date != null ? date : CommonUtils.getSystemDate());
        params.put("systemDate", CommonUtils.getSystemDate());
        return commonService.getEntities(VacationConditions.class, queryString, params, "vacationConditions-view");
    }

    @Override
    public List<VacationConditions> getAllVacationConditionsList(@Nullable UUID positionGroupId) {
        if (positionGroupId == null) return new ArrayList<>();

        return commonService.getEntities(VacationConditions.class,
                "select e from tsadv$VacationConditions e where e.positionGroup.id = :positionGroupId",
                ParamsMap.of("positionGroupId", positionGroupId),
                "vacationConditions-view");
    }

    protected List<VacationConditions> getVacationConditionsList(PositionGroupExt positionGroup) {
        return getVacationConditionsList(positionGroup, null);
    }

    protected List<VacationConditions> getVacationConditionsList(PositionGroupExt positionGroup, Date date) {
        String queryString = "select e from tsadv$VacationConditions e " +
                " where e.positionGroup.id = :positionGroupId " +
                "   and :date between e.startDate and e.endDate";
        Map<String, Object> params = new HashMap<>();
        params.put("positionGroupId", positionGroup.getId());
        params.put("date", date != null ? date : CommonUtils.getSystemDate());
        return commonService.getEntities(VacationConditions.class, queryString, params, null);
    }

    protected int countNewBalanceDays(int absenceDays, int balanceDays) {
        if (absenceDays < 1) {
            return balanceDays;
        }
        int daysCounted = 365 - absenceDays;
        double monthsCounted = daysCounted / (365 / 12.0);
        double newBalanceDouble = (balanceDays / 12.0) * monthsCounted;
        return ((int) Math.ceil(newBalanceDouble));
    }

    protected int getIntersectionLengthInDays(Absence absence, AbsenceBalance absenceBalance) {
        int result = 0;
        if (absence == null || absenceBalance == null) {
            return result;
        }
        Date absenceStartDate = absence.getDateFrom();
        Date absenceEndDate = absence.getDateTo();
        Date absenceBalanceStartDate = absenceBalance.getDateFrom();
        Date absenceBalanceEndDate = absenceBalance.getDateTo();
        if (!absenceIntersectsWithPeriod(absence, absenceBalance)) {
            return result;
        } else {
            result = datesService.getIntersectionLengthInDays(absenceStartDate, absenceEndDate, absenceBalanceStartDate, absenceBalanceEndDate);
        }
        return result;
    }

    @Override
    public boolean absenceIntersectsWithPeriod(Absence absence, AbsenceBalance absenceBalance) {
        if (absence == null || absenceBalance == null) {
            return false;
        }
        Date absenceStartDate = absence.getDateFrom();
        Date absenceEndDate = absence.getDateTo();
        Date absenceBalanceStartDate = absenceBalance.getDateFrom();
        Date absenceBalanceEndDate = absenceBalance.getDateTo();
        return datesService.intersectPeriods(absenceStartDate, absenceEndDate, absenceBalanceStartDate, absenceBalanceEndDate);
    }

    @Override
    public List<AbsenceBalance> getExistingAbsenceBalances(Absence absence) {
        String queryString = "select e from tsadv$AbsenceBalance e" +
                " where e.personGroup.id = :personGroupId" +
                " order by e.dateFrom";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", absence.getPersonGroup());
        return commonService.getEntities(AbsenceBalance.class, queryString, params, "absenceBalance.edit");
    }

    @Override
    public void recountBalanceDays(PersonGroupExt personGroupExt, Absence absence, Absence excludedAbsence, AbsenceBalance absenceBalance, Integer previousAbsenceDays) {
        /*Date dismissalDate = getDismissalDate(personGroupExt);
        //Достаем с базы все балансы данного пользователя
        List<AbsenceBalance> absenceBalanceList = getAbsenceBalanceList(personGroupExt, dismissalDate);
        //Дополняем список редактируемым в данный момент балансом если таковой имеется
        if (absenceBalance != null) {
            AbsenceBalance editedAbsenceBalance = absenceBalanceList.stream().filter(ab -> ab.getId().equals(absenceBalance.getId())).findFirst().orElse(null);
            if (editedAbsenceBalance != null) {
                absenceBalanceList.set(absenceBalanceList.indexOf(editedAbsenceBalance), absenceBalance);
            }
        }
        //Достаем с базы все отсутствия данного пользователя влияющие на потрачено дней
        List<Absence> absenceList = getAbsenceList(personGroupExt, dismissalDate);
        //Дополняем список редактируемым в данный момент отсутствием влияющим на потрачено дней если таковой имеется
        if (absence != null
                && (checkUseInBalance(absence)
                || "ANNUAL_EXTENTION".equals(absence.getType().getCode()))) {
            Absence editedAbsence = absenceList.stream().filter(a -> a.getId().equals(absence.getId())).findFirst().orElse(null);
            if (editedAbsence == null) {
                absenceList.add(absence);
            } else {
                absenceList.set(absenceList.indexOf(editedAbsence), absence);
            }
        }

        //Если происходит удаление отсутствия
        if (excludedAbsence != null) {
            //Если отсутствие с типом отмена и родителем влияющим на потрачено дней то включаем родителя и его продления в список расчета
            if ("CANCEL".equals(excludedAbsence.getType().getCode())
                    && (excludedAbsence.getParentAbsence() != null
                    && excludedAbsence.getParentAbsence().getType() != null
                    && checkUseInBalance(excludedAbsence.getParentAbsence())
                    || "ANNUAL_EXTENTION".equals(excludedAbsence.getParentAbsence().getType().getCode()))) {
                absenceList.add(excludedAbsence.getParentAbsence());
                List<Absence> annualAbsenceList = commonService.getEntities(Absence.class,
                        "select e from tsadv$Absence e" +
                                "  where e.personGroup.id = :personGroupId" +
                                "    and e.type.code = 'ANNUAL_EXTENTION'" +
                                "    and e.parentAbsence.id in :parentAbsenceList" +
                                "    and (select count(c) from tsadv$Absence c where c.parentAbsence.id = e.id and c.type.code = 'CANCEL') = 0",
                        ParamsMap.of("personGroupId", personGroupExt.getId(),
                                "parentAbsenceList", Arrays.asList(excludedAbsence.getParentAbsence())),
                        "absence.view");
                absenceList.addAll(annualAbsenceList);
            } else {
                absenceList.removeIf(a -> a.getId().equals(excludedAbsence.getId())
                        || a.getParentAbsence() != null
                        && a.getParentAbsence().getId().equals(excludedAbsence.getId())
                        && "ANNUAL_EXTENTION".equals(a.getType().getCode()));
            }
            //если тип отсутствия PAIDSOCIAL, удаляем из попадающего на него баланса отпусков количество положеных дней, равное дням отсутствия.
            *//*if (excludedAbsence.getType().getCode().equalsIgnoreCase("PAIDSOCIAL")) {
                for (int i = 0; i < absenceBalanceList.size(); i++) {
                    AbsenceBalance ab = absenceBalanceList.get(i);
                    if (datesService.intersectPeriods(excludedAbsence.getDateFrom(), excludedAbsence.getDateTo(), ab.getDateFrom(), ab.getDateTo())) {
                        ab.setBalanceDays(ab.getBalanceDays() - excludedAbsence.getAbsenceDays());
                        break;
                    }
                }
            }*//*
        }

        //Сброс оставшихся и потраченных дней, вычисление исключаемых положенных дней в счет длительного отсутсвтие
        if (absenceBalanceList != null) {
            for (int i = 0; i < absenceBalanceList.size(); i++) {
                AbsenceBalance ab = absenceBalanceList.get(i);
                Integer socialAbsenceDays = 0;
                for (Absence absence1 : absenceList) {
                    if (absence1.getType().getCode().equalsIgnoreCase("PAIDSOCIAL")
                            && datesService.intersectPeriods(absence1.getDateFrom(), absence1.getDateTo(), ab.getDateFrom(), ab.getDateTo())
                            && checkUseInBalance(absence1)) {
                        socialAbsenceDays += absence1.getAbsenceDays();
                    }
                }
                ab.setOverallBalanceDays(ab.getBalanceDays()+socialAbsenceDays);
//                ab.setAdditionalBalanceDays(getAdditionalBalanceDays(personGroupExt, ab.getDateFrom()));
//                ab.setBalanceDays(getBalanceDays(personGroupExt, null, ab.getDateFrom()));
                ab.setLongAbsenceDays(calculateLongAbsenceDays(ab, absence, excludedAbsence));
                ab.setDaysSpent(ab.getOverallBalanceDays()-ab.getBalanceDays());
                ab.setDaysLeft(ab.getBalanceDays() - ab.getLongAbsenceDays());
                ab.setExtraDaysSpent(0);
                ab.setExtraDaysLeft(ab.getAdditionalBalanceDays());
            }
        }


        //Распределение дней по балансам
        Integer totalAbsenceDays = getTotalAbsenceDays(absenceList);
        for (int i = 0; i < absenceBalanceList.size(); i++) {
            if (totalAbsenceDays == 0) break;
            AbsenceBalance ab = absenceBalanceList.get(i);

            totalAbsenceDays = distributionAbsenceDays(totalAbsenceDays, ab);
        }

        //если остались не распределенные дни отстутствия создаем новые периоды и распределяем дни
        while (totalAbsenceDays != 0) {
            Date lastEndDate = absenceBalanceList.size() > 0 ? absenceBalanceList.get(absenceBalanceList.size() - 1).getDateTo() : null;
            AbsenceBalance ab = createNewAbsenceBalance(absence, personGroupExt, getPositionGroup(personGroupExt), lastEndDate);
            totalAbsenceDays = distributionAbsenceDays(totalAbsenceDays, ab);
            absenceBalanceList.add(ab);
        }
        if (absenceBalanceList.isEmpty() ||
                absenceBalanceList.get(absenceBalanceList.size() - 1).getDaysLeft() != absenceBalanceList.get(absenceBalanceList.size() - 1).getBalanceDays()) {
            absenceBalanceList.add(createNewAbsenceBalance(null, personGroupExt, getPositionGroup(personGroupExt),
                    absenceBalanceList.size() > 0 ? absenceBalanceList.get(absenceBalanceList.size() - 1).getDateTo() : null));
        }

        //Распределение дополнительных дней по балансам
        Integer totalAdditionalAbsenceDays = getTotalAdditionalAbsenceDays(absenceList);
        for (int i = 0; i < absenceBalanceList.size(); i++) {
            if (totalAdditionalAbsenceDays == 0) break;
            AbsenceBalance ab = absenceBalanceList.get(i);
            totalAdditionalAbsenceDays = distributionAdditionalAbsenceDays(totalAdditionalAbsenceDays, ab);
        }

        //Пересчитываем поля "положено дней" и "использовано дней" в балансе отпусков, выпадающем на отсутствие с типом PAIDSOCIAL
        *//*for (Absence absence1 : absenceList) {
            if (absence1 != null
                    && absence1.getType().getCode().equalsIgnoreCase("PAIDSOCIAL")
                    && checkUseInBalance(absence1)) {
                for (int i = 0; i < absenceBalanceList.size(); i++) {
                    AbsenceBalance ab = absenceBalanceList.get(i);
                    if (datesService.intersectPeriods(absence1.getDateFrom(), absence1.getDateTo(), ab.getDateFrom(), ab.getDateTo())) {
                        if (absence1.equals(absence)) {
                            ab.setBalanceDays(ab.getBalanceDays()
                                    + absence1.getAbsenceDays()
                                    - (previousAbsenceDays != null ? previousAbsenceDays : 0));
                        }
                        ab.setDaysSpent(ab.getDaysSpent() + absence1.getAbsenceDays());
                        ab.setDaysLeft(ab.getBalanceDays() - ab.getDaysSpent());
                        break;
                    }
                }
            }
        }*//*

        dataManager.commit(new CommitContext(absenceBalanceList));*/
    }

    protected boolean checkUseInBalance(Absence absence) {
        return absence.getType().getUseInBalance() != null
                && absence.getType().getUseInBalance();
    }

    @Override
    public void createMissingAbsenceBalances() {
        List<UUID> personGroupIds;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery("SELECT e.id from base_person_group e\n" +
                    "   JOIN base_assignment a ON a.person_group_id = e.id and a.delete_ts is null \n" +
                    "   JOIN tsadv_dic_assignment_status status ON status.id = a.assignment_status_id \n" +
                    "       AND (status.code = 'ACTIVE'\n" +
                    "       OR status.code = 'SUSPENDED')\n" +
                    "   WHERE current_date BETWEEN a.start_date AND a.end_date\n" +
                    "       and e.delete_ts is null");
            personGroupIds = query.getResultList();
        }

        if (!personGroupIds.isEmpty()) {

            System.out.println("Absence balance create scheduler started. " + personGroupIds.size());
            for (UUID id : personGroupIds) {
                try {

                    PersonGroupExt personGroup = commonService.getEntity(PersonGroupExt.class, id);
                    if (personGroup == null) continue;

                    List<AbsenceBalance> absenceBalances = getAbsenceBalanceList(personGroup, CommonUtils.getSystemDate());
                    if (absenceBalances != null && !absenceBalances.isEmpty()) {
                        AbsenceBalance lastAbsenceBalance = absenceBalances.get(absenceBalances.size() - 1);
                        Date currentDate = CommonUtils.getSystemDate();
                        if (lastAbsenceBalance.getDateTo().before(currentDate)) {
                            Date lastAbsenceBalanceDate = lastAbsenceBalance.getDateTo();
                            while (lastAbsenceBalanceDate.before(currentDate)) {
                                AbsenceBalance newAbsenceBalance = createNewAbsenceBalance(null, personGroup, null, lastAbsenceBalance.getDateTo());
                                if (newAbsenceBalance != null) {
                                    dataManager.commit(newAbsenceBalance);
                                }
                                List<AbsenceBalance> absenceBalancesUpdated = getAbsenceBalanceList(personGroup, CommonUtils.getSystemDate());
                                lastAbsenceBalance = absenceBalancesUpdated.get(absenceBalancesUpdated.size() - 1);
                                lastAbsenceBalanceDate = lastAbsenceBalance.getDateTo();
                                System.out.println("Added new absence balance for personGroupId: " + id + " absenceBalanceStartDate: " + lastAbsenceBalance.getDateFrom() + " absenceBalanceEndDate: " + lastAbsenceBalance.getDateTo());
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    protected PositionGroupExt getPositionGroup(PersonGroupExt personGroupExt) {
        return commonService.getEntity(PositionGroupExt.class,
                "select e.positionGroup from base$AssignmentExt e" +
                        " where :systemDate between e.startDate and e.endDate" +
                        "   and e.personGroup.id = :personGroupId" +
                        "   and e.primaryFlag = true",
                ParamsMap.of("systemDate", CommonUtils.getSystemDate(),
                        "personGroupId", personGroupExt.getId()),
                "_minimal");
    }

//    protected Integer distributionAbsenceDays(Integer totalAbsenceDays, AbsenceBalance ab) {
//        if (totalAbsenceDays > ab.getDaysLeft()) {
//            totalAbsenceDays -= ab.getDaysLeft();
//            ab.setDaysSpent(ab.getDaysLeft());
//            ab.setDaysLeft(0);
//        } else {
//            ab.setDaysSpent(ab.getDaysSpent() + totalAbsenceDays);
//            ab.setDaysLeft(ab.getDaysLeft() - totalAbsenceDays);
//            totalAbsenceDays = 0;
//        }
//        return totalAbsenceDays;
//    }
//
//    protected Integer distributionAdditionalAbsenceDays(Integer totalAdditionalAbsenceDays, AbsenceBalance ab) {
//        if (totalAdditionalAbsenceDays > ab.getExtraDaysLeft()) {
//            totalAdditionalAbsenceDays -= ab.getExtraDaysLeft();
//            ab.setExtraDaysSpent(ab.getExtraDaysLeft());
//            ab.setExtraDaysLeft(0);
//        } else {
//            ab.setExtraDaysSpent(ab.getExtraDaysSpent() + totalAdditionalAbsenceDays);
//            ab.setExtraDaysLeft(ab.getExtraDaysLeft() - totalAdditionalAbsenceDays);
//            totalAdditionalAbsenceDays = 0;
//        }
//        return totalAdditionalAbsenceDays;
//    }

    protected Integer getTotalAbsenceDays(List<Absence> absenceList) {
        Integer result = 0;
        for (Absence absence : absenceList) {
            switch (absence.getType().getCode()) {
                case "RECALL":
                    result -= absence.getAbsenceDays();
                    break;
                case "CANCEL":
//                    result -= absence.getParentAbsence().getAbsenceDays(); //todo проверить если родитель с useInBalance
                    break;
                case "TRANSFER":
                    break;
                /*case "PAIDSOCIAL":
                    if (checkUseInBalance(absence)) {
                        result += absence.getAbsenceDays()*2;
                    }*/
                default:
                    result += absence.getAbsenceDays();
                    break;
            }
        }
        return result;
    }

    protected Integer getTotalAdditionalAbsenceDays(List<Absence> absenceList) {
        Integer result = 0;
        for (Absence absence : absenceList) {
            if (absence.getAdditionalDays() != null) {
                switch (absence.getType().getCode()) {
                    case "RECALL":
                        result -= absence.getAdditionalDays();
                        break;
                    case "CANCEL":
                        break;
                    case "TRANSFER":
                        break;
                    default:
                        result += absence.getAdditionalDays();
                        break;
                }
            }
        }
        return result;
    }

    protected List<AbsenceBalance> getAbsenceBalanceList(PersonGroupExt personGroup, Date dismissalDate) {
        PersonExt personExt = commonService.getEntity(PersonExt.class,
                "select e from base$PersonExt e where e.group.id = :personGroupId" +
                        " and :systemDate between e.startDate and e.endDate",
                ParamsMap.of("personGroupId", personGroup.getId(),
                        "systemDate", CommonUtils.getSystemDate()),
                View.LOCAL);
        String query = "select e " +
                "         from tsadv$AbsenceBalance e" +
                "        where e.personGroup.id = :personGroupId" +
                "          and e.dateFrom >= :hireDate " +
                "          and e.dateFrom <= :dismissalDate";
        View view = new View(AbsenceBalance.class)
                .addProperty("dateFrom")
                .addProperty("dateTo")
                .addProperty("balanceDays")
                .addProperty("additionalBalanceDays")
                .addProperty("overallBalanceDays")
                .addProperty("daysSpent")
                .addProperty("daysLeft")
                .addProperty("extraDaysSpent")
                .addProperty("extraDaysLeft")
                .addProperty("longAbsenceDays")
                .addProperty("personGroup", new View(PersonGroupExt.class));
        LoadContext<AbsenceBalance> loadContext = LoadContext.create(AbsenceBalance.class);
        loadContext.setQuery(LoadContext.createQuery(query)
                .setParameter("personGroupId", personGroup.getId())
                .setParameter("hireDate", personExt.getHireDate())
                .setParameter("dismissalDate", dismissalDate != null ? dismissalDate : CommonUtils.getEndOfTime()))
                .setView(view);
        List<AbsenceBalance> absenceBalanceList = dataManager.loadList(loadContext);
        absenceBalanceList.sort((o1, o2) -> o1.getDateFrom().getTime() > o2.getDateFrom().getTime() ? 1 : -1);
        return absenceBalanceList;
    }

    protected Date getDismissalDate(PersonGroupExt personGroupExt) {
        Dismissal dismissal = commonService.getEntity(Dismissal.class,
                "select e from tsadv$Dismissal e " +
                        "     join base$AssignmentExt a on a.group.id = e.assignmentGroup.id " +
                        "      and :systemDate between a.startDate and a.endDate" +
                        "      and a.personGroup.id = :personGroupId" +
                        " where e.personGroup.id = :personGroupId" +
                        "   and a.primaryFlag = true",
                ParamsMap.of("personGroupId", personGroupExt.getId(),
                        "systemDate", CommonUtils.getSystemDate()),
                View.LOCAL);
        if (dismissal != null && dismissal.getDismissalDate() != null) {
            return dismissal.getDismissalDate();
        }
        return null;
    }

    protected List<Absence> getAbsenceList(PersonGroupExt personGroupExt, Date dismissalDate) {
        PersonExt personExt = commonService.getEntity(PersonExt.class,
                "select e from base$PersonExt e " +
                        "where e.group.id = :personGroupId" +
                        "  and :systemDate between e.startDate and e.endDate",
                ParamsMap.of("personGroupId", personGroupExt.getId(),
                        "systemDate", CommonUtils.getSystemDate()),
                View.LOCAL);
        List<Absence> absenceList = getAbsences(personGroupExt, dismissalDate, personExt);
        absenceList.sort((o1, o2) -> o1.getDateFrom().getTime() > o2.getDateFrom().getTime() ? 1 : -1);
        return absenceList;
    }

    /*protected List<Absence> getAnnualAbsences(PersonGroupExt personGroupExt, Date dismissalDate, PersonExt personExt, List<Absence> absenceList) {
        return commonService.getEntities(Absence.class,
                "select e from tsadv$Absence e" +
                        "  where e.personGroup.id = :personGroupId" +
                        "    and e.type.code = 'ANNUAL_EXTENTION'" +
                        "    and e.parentAbsence.id in :parentAbsenceList" +
                        "    and (select count(c) from tsadv$Absence c where c.parentAbsence.id = e.id and c.type.code = 'CANCEL') = 0" +
                        "    and e.dateTo >= :hireDate" +
                        "    and e.dateTo <= :dismissalDate",
                ParamsMap.of("personGroupId", personGroupExt.getId(),
                        "parentAbsenceList", absenceList,
                        "hireDate", personExt.getHireDate(),
                        "dismissalDate", dismissalDate != null ? dismissalDate : CommonUtils.getEndOfTime()),
                "absence.view");
    }*/

    protected List<Absence> getAbsences(PersonGroupExt personGroupExt, Date dismissalDate, PersonExt personExt) {
        return commonService.getEntities(Absence.class,
                "select e from tsadv$Absence e" +
                        "  where e.personGroup.id = :personGroupId" +
                        "    and e.type.useInBalance = true " +
                        "    and (e.parentAbsence is null " +
                        "     or e.parentAbsence.type.useInBalance = true) " +
                        "    and (select count(c) from tsadv$Absence c " +
                        "          where c.parentAbsence.id = e.id " +
                        "            and c.type.code = 'CANCEL') = 0 " +
                        "    and e.dateTo >= :hireDate" +
                        "    and e.dateTo <= :dismissalDate",
                ParamsMap.of("personGroupId", personGroupExt.getId(),
                        "hireDate", personExt.getHireDate(),
                        "dismissalDate", dismissalDate != null ? dismissalDate : CommonUtils.getEndOfTime()),
                "absence.view");
    }

    protected Double calculateLongAbsenceDays(AbsenceBalance absenceBalance, Absence editedAbsence, Absence excludedAbsence) {
        double longAbsenceDays = 0;
        List<Absence> longAbsenceList = getLongAbsenceList(absenceBalance.getPersonGroup(), editedAbsence, excludedAbsence, absenceBalance);
        for (Absence absence : longAbsenceList) {
            if (absence.getDateFrom().getTime() >= absenceBalance.getDateFrom().getTime() &&
                    absence.getDateTo().getTime() <= absenceBalance.getDateTo().getTime()) {
                longAbsenceDays += ((Long) ((absence.getDateTo().getTime() - absence.getDateFrom().getTime()) / (24 * 60 * 60 * 1000))).intValue() + 1;
            } else if (absence.getDateFrom().getTime() >= absenceBalance.getDateFrom().getTime() &&
                    absence.getDateFrom().getTime() <= absenceBalance.getDateTo().getTime() &&
                    absence.getDateTo().getTime() > absenceBalance.getDateTo().getTime()) {
                longAbsenceDays += ((Long) ((absenceBalance.getDateTo().getTime() - absence.getDateFrom().getTime()) / (24 * 60 * 60 * 1000))).intValue() + 1;
            } else if (absence.getDateFrom().getTime() < absenceBalance.getDateFrom().getTime() &&
                    absence.getDateTo().getTime() >= absenceBalance.getDateFrom().getTime() &&
                    absence.getDateTo().getTime() <= absenceBalance.getDateTo().getTime()) {
                longAbsenceDays += ((Long) ((absence.getDateTo().getTime() - absenceBalance.getDateFrom().getTime()) / (24 * 60 * 60 * 1000))).intValue() + 1;
            } else if (absence.getDateFrom().getTime() < absenceBalance.getDateFrom().getTime() &&
                    absence.getDateTo().getTime() > absenceBalance.getDateTo().getTime()) {
                longAbsenceDays += ((Long) ((absenceBalance.getDateTo().getTime() - absenceBalance.getDateFrom().getTime()) / (24 * 60 * 60 * 1000))).intValue() + 1;
            }
        }
        longAbsenceDays = longAbsenceDays * absenceBalance.getBalanceDays() / (((Long) ((absenceBalance.getDateTo().getTime() - absenceBalance.getDateFrom().getTime()) / (24 * 60 * 60 * 1000))).intValue() + 1);
        return longAbsenceDays;
    }

    protected List<Absence> getLongAbsenceList(PersonGroupExt personGroupExt, Absence editedAbsence, Absence excludedAbsence, AbsenceBalance absenceBalance) {

        LoadContext lc = new LoadContext(DicAbsenceType.class);
        lc.setQuery(LoadContext.createQuery("select e from tsadv$DicAbsenceType e"));
        lc.setLoadDynamicAttributes(true);
        List<DicAbsenceType> absenceTypeList = dataManager.loadList(lc);
        absenceTypeList.removeIf(dicAbsenceType -> dicAbsenceType.getValue("+ABSENCETYPELONG") == null ||
                !(Boolean) dicAbsenceType.getValue("+ABSENCETYPELONG"));

        List<Absence> longAbsenceList = getPreliminaryLongAbsenceList(personGroupExt, absenceBalance, absenceTypeList);
        Absence excludedRecallAbsence = null;
        if (excludedAbsence != null) {
            if ("CANCEL".equals(excludedAbsence.getType().getCode())) {
                addOrReplaceToAbsenceList(excludedAbsence, longAbsenceList);
            } else if ("RECALL".equals(excludedAbsence.getType().getCode())) {
                excludedRecallAbsence = excludedAbsence;
            } else {
                longAbsenceList.removeIf(absence -> absence.getId().equals(excludedAbsence.getId()));
            }
        }

        Absence editedRecallAbsence = null;
        if (editedAbsence != null) {
            if (absenceTypeList.stream().filter(dicAbsenceType -> dicAbsenceType.getId().equals(editedAbsence.getType().getId())).findFirst().orElse(null) != null)
                addOrReplaceToAbsenceList(editedAbsence, longAbsenceList);
            if ("RECALL".equals(editedAbsence.getType().getCode())) {
                editedRecallAbsence = editedAbsence;
            }
        }
        List<Absence> recallAbsenceList = commonService.getEntities(Absence.class,
                "select e from tsadv$Absence e" +
                        "  where e.parentAbsence.id in :longAbsenceList " +
                        "    and e.personGroup.id = :personGroupId" +
                        "    and e.type.code = 'RECALL'",
                ParamsMap.of("longAbsenceList", longAbsenceList,
                        "personGroupId", personGroupExt.getId()),
                "absence.view");
        if (editedRecallAbsence != null)
            addOrReplaceToAbsenceList(editedRecallAbsence, recallAbsenceList);
        if (excludedRecallAbsence != null) {
            recallAbsenceList.remove(excludedAbsence);
        }
        for (Absence longAbsence : longAbsenceList) {
            for (Absence recallAbsence : recallAbsenceList) {
                if (recallAbsence.getParentAbsence().equals(longAbsence)
                        && recallAbsence.getDateFrom().getTime() >= longAbsence.getDateFrom().getTime()
                        && recallAbsence.getDateFrom().getTime() <= longAbsence.getDateTo().getTime()) {
                    longAbsence.setDateTo(DateUtils.addDays(recallAbsence.getDateFrom(), -1));
                }
            }
        }
        return longAbsenceList;
    }

    protected List<Absence> getPreliminaryLongAbsenceList(PersonGroupExt personGroupExt, AbsenceBalance absenceBalance, List<DicAbsenceType> absenceTypeList) {
        return commonService.getEntities(Absence.class,
                "select e from tsadv$Absence e" +
                        "  where e.personGroup.id = :personGroupId" +
                        "    and (e.dateFrom >= :absenceBalanceDateFrom and e.dateFrom <= :absenceBalanceDateTo" +
                        "     or e.dateTo >= :absenceBalanceDateFrom and e.dateTo <= :absenceBalanceDateTo" +
                        "     or e.dateFrom < :absenceBalanceDateFrom and e.dateTo > :absenceBalanceDateTo)" +
                        "    and e.type.id in :absenceTypeIdList" +
                        "    and (select count(c) from tsadv$Absence c where c.parentAbsence.id = e.id and c.type.code = 'CANCEL') = 0",
                ParamsMap.of("personGroupId", personGroupExt.getId(),
                        "absenceTypeIdList", absenceTypeList,
                        "absenceBalanceDateFrom", absenceBalance.getDateFrom(),
                        "absenceBalanceDateTo", absenceBalance.getDateTo()),
                "_local");
    }

    protected void addOrReplaceToAbsenceList(Absence editedAbsence, List<Absence> absenceList) {
        Absence a = absenceList.stream().filter(absence -> absence.getId().equals(editedAbsence.getId())).findFirst().orElse(null);
        if (a != null) {
            absenceList.set(absenceList.indexOf(a), editedAbsence);
        } else {
            absenceList.add(editedAbsence);
        }
    }

    @Override
    public double getAbsenceBalance(UUID absenceTypeId, UUID personGroupId, Date absenceDate) {
        DicAbsenceType type = dataManager.load(DicAbsenceType.class)
                .id(absenceTypeId)
                .view(View.LOCAL)
                .one();

        return type.getIsEcologicalAbsence()
                ? this.getEnvironmentalDays(personGroupId, absenceDate)
                : this.getAbsenceBalance(personGroupId, absenceDate);
    }

    @Override
    public double getAbsenceBalance(UUID personGroupId, Date absenceDate) {
        double finalAbsenceBalance = 0.0;
        try {
            if (personGroupId != null && absenceDate != null) {
                List<AbsenceBalance> oneAbsenceBalanceList = dataManager.load(AbsenceBalance.class)
                        .query("select e from tsadv$AbsenceBalance e " +
                                " where e.personGroup.id = :personGroupId " +
                                " and e.dateFrom = :absenceDate " +
                                " order by e.dateFrom desc")
                        .parameter("personGroupId", personGroupId)
                        .parameter("absenceDate", absenceDate)
                        .view("absenceBalance.edit")
                        .list();
                if (!oneAbsenceBalanceList.isEmpty()) {
                    return Null.nullReplace(oneAbsenceBalanceList.get(0).getDaysLeft(), 0d) + Null.nullReplace(oneAbsenceBalanceList.get(0).getExtraDaysLeft(), 0d);
                } else {
                    List<AbsenceBalance> absenceBalanceMinList = dataManager.load(AbsenceBalance.class)
                            .query("select e from tsadv$AbsenceBalance e " +
                                    " where e.personGroup.id = :personGroupId " +
                                    " and :absenceDate >= e.dateFrom " +
                                    " order by e.dateFrom desc")
                            .parameter("personGroupId", personGroupId)
                            .parameter("absenceDate", absenceDate)
                            .view("absenceBalance.edit")
                            .list();
                    List<AbsenceBalance> absenceBalanceMaxList = dataManager.load(AbsenceBalance.class)
                            .query("select e from tsadv$AbsenceBalance e " +
                                    " where e.personGroup.id = :personGroupId " +
                                    " and :absenceDate <= e.dateFrom " +
                                    " order by e.dateFrom desc")
                            .parameter("personGroupId", personGroupId)
                            .parameter("absenceDate", absenceDate)
                            .view("absenceBalance.edit")
                            .list();

                    AbsenceBalance minAbsenceBalance = !absenceBalanceMinList.isEmpty() ? absenceBalanceMinList.get(0) : null;
                    AbsenceBalance maxAbsenceBalance = !absenceBalanceMaxList.isEmpty() ? absenceBalanceMaxList.get(0) : null;
                    if (minAbsenceBalance == null || maxAbsenceBalance == null) {
                        return finalAbsenceBalance;
                    }
                    finalAbsenceBalance = ((double) datesService.getDayOfMonth(absenceDate)
                            * ((Null.nullReplace(maxAbsenceBalance.getDaysLeft(), 0d) + Null.nullReplace(maxAbsenceBalance.getExtraDaysLeft(), 0d) + Null.nullReplace(maxAbsenceBalance.getEcologicalDaysLeft(), 0d))
                            - (Null.nullReplace(minAbsenceBalance.getDaysLeft(), 0d) + Null.nullReplace(minAbsenceBalance.getExtraDaysLeft(), 0d) + Null.nullReplace(minAbsenceBalance.getEcologicalDaysLeft(), 0d)))
                            / datesService.getFullDaysCount(minAbsenceBalance.getDateFrom(), maxAbsenceBalance.getDateFrom()))
                            + Null.nullReplace(minAbsenceBalance.getDaysLeft(), 0d) + Null.nullReplace(minAbsenceBalance.getExtraDaysLeft(), 0d) + Null.nullReplace(minAbsenceBalance.getEcologicalDaysLeft(), 0d);
                }
            }
            return finalAbsenceBalance;
        } catch (Exception ignored) {
            return 0.0;
        }
    }

    @Override
    public double getEnvironmentalDays(UUID personGroupId, Date absenceDate) {
        double finalEnvironmentalDays = 0.0;
        try {
            if (personGroupId != null && absenceDate != null) {
                List<AbsenceBalance> oneAbsenceBalanceList = dataManager.load(AbsenceBalance.class)
                        .query("select e from tsadv$AbsenceBalance e " +
                                " where e.personGroup.id = :personGroupId " +
                                " and e.dateFrom = :absenceDate " +
                                " order by e.dateFrom desc")
                        .parameter("personGroupId", personGroupId)
                        .parameter("absenceDate", absenceDate)
                        .view("absenceBalance.edit")
                        .list();
                if (!oneAbsenceBalanceList.isEmpty()) {
                    return oneAbsenceBalanceList.get(0).getEcologicalDaysLeft();
                } else {
                    List<AbsenceBalance> absenceBalanceMinList = dataManager.load(AbsenceBalance.class)
                            .query("select e from tsadv$AbsenceBalance e " +
                                    " where e.personGroup.id = :personGroupId " +
                                    " and :absenceDate >= e.dateFrom " +
                                    " order by e.dateFrom desc")
                            .parameter("personGroupId", personGroupId)
                            .parameter("absenceDate", absenceDate)
                            .view("absenceBalance.edit")
                            .list();
                    List<AbsenceBalance> absenceBalanceMaxList = dataManager.load(AbsenceBalance.class)
                            .query("select e from tsadv$AbsenceBalance e " +
                                    " where e.personGroup.id = :personGroupId " +
                                    " and :absenceDate <= e.dateFrom " +
                                    " order by e.dateFrom desc")
                            .parameter("personGroupId", personGroupId)
                            .parameter("absenceDate", absenceDate)
                            .view("absenceBalance.edit")
                            .list();

                    AbsenceBalance minAbsenceBalance = !absenceBalanceMinList.isEmpty() ? absenceBalanceMinList.get(0) : null;
                    AbsenceBalance maxAbsenceBalance = !absenceBalanceMaxList.isEmpty() ? absenceBalanceMaxList.get(0) : null;
                    if (minAbsenceBalance == null || maxAbsenceBalance == null) {
                        return finalEnvironmentalDays;
                    }
                    finalEnvironmentalDays = ((double) datesService.getDayOfMonth(absenceDate)
                            * (maxAbsenceBalance.getEcologicalDaysLeft()
                            - minAbsenceBalance.getEcologicalDaysLeft())
                            / datesService.getFullDaysCount(minAbsenceBalance.getDateFrom(), maxAbsenceBalance.getDateFrom()))
                            + minAbsenceBalance.getEcologicalDaysLeft();
                }
            }
            return finalEnvironmentalDays;
        } catch (Exception ignored) {
            return 0.0;
        }
    }
}