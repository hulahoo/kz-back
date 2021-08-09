package kz.uco.tsadv.web.screens.organizationincentivemonthresult;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.AggregationInfo;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.entity.dbview.OrganizationIncentiveMonthResultView;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveIndicators;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveResult;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.entity.WindowProperty;
import kz.uco.uactivity.service.ActivityService;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

@UiController("tsadv_OrganizationIncentiveMonthResult.browse")
@UiDescriptor("organization-incentive-month-result-browse.xml")
@LookupComponent("organizationIncentiveMonthResultsTable")
@LoadDataBeforeShow
public class OrganizationIncentiveMonthResultBrowse extends StandardLookup<OrganizationIncentiveMonthResultView> {

    protected String PERIOD_DATE_FORMAT = "MMM yyyy";
    @Inject
    private GroupTable<OrganizationIncentiveMonthResultView> organizationIncentiveMonthResultsTable;
    @Inject
    private Metadata metadata;
    @Inject
    private Notifications notifications;
    @Inject
    private DataManager dataManager;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private NotificationService notificationService;
    @Inject
    private FrontConfig frontConfig;
    @Inject
    private ActivityService activityService;
    @Inject
    private CommonService commonService;


    protected class LocalizedDateFormatter implements Function<Object, String>{

        protected String format;

        LocalizedDateFormatter(String format){
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        @Override
        public String apply(Object value) {
            Date date = (Date) value;
            UserSessionSource us = AppBeans.get(UserSessionSource.NAME);
            Locale currentLocale = us.getLocale();
            if(currentLocale.getLanguage().equals("kz")) currentLocale = new Locale("ru");
            DateFormat df = new SimpleDateFormat(format,currentLocale);
            return df.format(date);
        }

    }

    public class EmptyStringFormatter implements Function<Object, String> {
        @Override
        public String apply(Object o) {
            return "";
        }
    }

    protected class StatusColumnAggregationStrategy implements AggregationStrategy<DicIncentiveResultStatus,String>{
        @Override
        public String aggregate(Collection<DicIncentiveResultStatus> propertyValues) {
            if(propertyValues != null && !propertyValues.isEmpty()) {
                return propertyValues.stream().findAny().get().getLangValue();
            }
            return "";
        }

        @Override
        public Class<String> getResultClass() {
            return String.class;
        }
    }

    protected class CommentColumnAggregationStrategy implements AggregationStrategy<String,String>{
        @Override
        public String aggregate(Collection<String> propertyValues) {
            if(propertyValues != null && !propertyValues.isEmpty()) {
                return propertyValues.stream().findAny().get();
            }
            return "";
        }

        @Override
        public Class<String> getResultClass() {
            return String.class;
        }
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        initTableUI();
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        organizationIncentiveMonthResultsTable.expandAll();

    }

    public void sendNotification() {
        PersonGroupExt personGroupExt = getResponsiblePersonFromOrganizationIncentiveIndicator();
        TsadvUser user = employeeService.getUserExtByPersonGroupId(personGroupExt.getId());

        UUID incentiveResultId = organizationIncentiveMonthResultsTable.getSingleSelected().getId();
        OrganizationIncentiveResult incentiveResult = dataManager.load(OrganizationIncentiveResult.class)
                .query("select e from tsadv_OrganizationIncentiveResult e where e.id = :id")
                .parameter("id",incentiveResultId)
                .view("organizationIncentiveResults-notification-view")
                .one();

        Map<String, Object> params = new HashMap<>();
        String link = frontConfig.getFrontAppUrl() + "/incentive-approve/"+incentiveResult.getOrganizationIncentiveMonthResult().getId();
        String hlink = "<a href=\"" + link + "\" target=\"_blank\">%s " + "</a>";
        params.put("userName", user.getFullName());
        params.put("linkRu", String.format(hlink,"ЗДЕСЬ"));
        params.put("linkKz", String.format(hlink,"осы жерде"));
        params.put("linkEn", String.format(hlink,"Click"));

        ActivityType activityType = dataManager.load(ActivityType.class)
                .query("select e from uactivity$ActivityType e where e.code = 'NOTIFICATION'")
                .one();
        activityService.createActivity(
                user,
                user,
                activityType,
                StatusEnum.active,
                "description",
                null,
                new Date(),
                null,
                "",
                null,
                "incentive.approve.forApprover",
                params);
        notificationService.sendParametrizedNotification("incentive.approve.forApprover", user, params);

    }

    protected PersonGroupExt getResponsiblePersonFromOrganizationIncentiveIndicator(){
        if(organizationIncentiveMonthResultsTable.getSingleSelected() == null) return null;

        OrganizationIncentiveMonthResultView incentiveMonthResultView = organizationIncentiveMonthResultsTable.getSingleSelected();
        Date incentiveMonthResultViewPeriod = incentiveMonthResultView.getPeriod();
        OrganizationGroupExt incentiveMonthResultOrganizationGroup = incentiveMonthResultView.getDepartment();
        DicIncentiveIndicators incentiveMonthResultIndicator = incentiveMonthResultView.getIndicator();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(incentiveMonthResultViewPeriod);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++; // increment month - because java.util.Calendar bases on zero-index

        String query = " select e from tsadv_OrganizationIncentiveIndicators e " +
                        " where e.organizationGroup = :orgGroup " +
                        " and e.indicator = :indicator" +
                        " and (:periodYear between EXTRACT(YEAR from e.dateFrom) and EXTRACT(YEAR from e.dateTo)) " +
                        " and (:periodMonth between EXTRACT(MONTH from e.dateFrom) and EXTRACT(MONTH from e.dateTo)) ";

        OrganizationIncentiveIndicators organizationIncentiveIndicator = dataManager.load(OrganizationIncentiveIndicators.class)
                .query(query)
                .setParameters(ParamsMap.of(
                                "orgGroup",incentiveMonthResultOrganizationGroup,
                                "indicator",incentiveMonthResultIndicator,
                                "periodYear",year,
                                "periodMonth",month))
                .list().stream().findFirst().orElse(null);

        if (organizationIncentiveIndicator == null) return null;

        PositionGroupExt responsiblePosition = organizationIncentiveIndicator.getResponsiblePosition();

        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                .query("select e.personGroup from base$AssignmentExt e " +
                        "   where e.positionGroup.id = :positionGroupId" +
                        "       and :systemDate between e.startDate and e.endDate " +
                        "       and e.primaryFlag = 'TRUE' " +
                        "       and e.assignmentStatus.code <> 'TERMINATED' " +
                        "   ORDER BY e.assignDate DESC")
                .setParameters(ParamsMap.of(
                        "positionGroupId", responsiblePosition.getId(),
                        "systemDate", CommonUtils.getSystemDate()))
                .list().stream().findFirst().orElse(null);

        return personGroupExt;
    }


    protected void initTableUI(){
        organizationIncentiveMonthResultsTable.getColumn("status").setFormatter(new EmptyStringFormatter());
        organizationIncentiveMonthResultsTable.getColumn("comment").setFormatter(new EmptyStringFormatter());

        AggregationInfo statusAggInfo = new AggregationInfo();
        statusAggInfo.setPropertyPath(metadata.getClass(OrganizationIncentiveMonthResultView.class).getPropertyPath("status"));
        statusAggInfo.setStrategy(new StatusColumnAggregationStrategy());

        organizationIncentiveMonthResultsTable.setAggregatable(true);
        Table.Column statusColumn = organizationIncentiveMonthResultsTable.getColumn("status");
        statusColumn.setAggregation(statusAggInfo);

        AggregationInfo commentAggInfo = new AggregationInfo();
        commentAggInfo.setPropertyPath(metadata.getClass(OrganizationIncentiveMonthResultView.class).getPropertyPath("comment"));
        commentAggInfo.setStrategy(new CommentColumnAggregationStrategy());
        Table.Column commentColumn = organizationIncentiveMonthResultsTable.getColumn("comment");
        commentColumn.setAggregation(commentAggInfo);


        LocalizedDateFormatter localizedDateFormatter = new LocalizedDateFormatter(PERIOD_DATE_FORMAT);
        organizationIncentiveMonthResultsTable.getColumn("period").setFormatter(localizedDateFormatter);
    }


}