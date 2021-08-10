package kz.uco.tsadv.web.screens.organizationincentivemonthresult;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.components.data.TableItems;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.NotificationService;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.entity.dbview.OrganizationIncentiveMonthResultView;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveIndicators;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@UiController("tsadv_OrganizationIncentiveMonthResult.browse")
@UiDescriptor("organization-incentive-month-result-browse.xml")
@LookupComponent("organizationIncentiveMonthResultsTree")
@LoadDataBeforeShow
public class OrganizationIncentiveMonthResultBrowse extends StandardLookup<OrganizationIncentiveMonthResultView> {

    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected FrontConfig frontConfig;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected TreeTable<OrganizationIncentiveMonthResultView> organizationIncentiveMonthResultsTree;
    @Inject
    protected MessageTools messageTools;
    @Inject
    protected UserSession userSession;

    public void sendNotification() {

        OrganizationIncentiveMonthResultView monthResult = organizationIncentiveMonthResultsTree.getSingleSelected();
        if (monthResult == null) return;

        List<TsadvUser> approvers = getApprovers(monthResult);

        if (approvers.isEmpty()) return;

        UUID monthResultId = Optional.ofNullable(monthResult.getParent()).orElse(monthResult).getId();

        Map<String, Object> params = new HashMap<>();
        String link = frontConfig.getFrontAppUrl() + "/incentive-approve/" + monthResultId;
        String hlink = "<a href=\"" + link + "\" target=\"_blank\">%s " + "</a>";
        params.put("linkRu", String.format(hlink, "ЗДЕСЬ"));
        params.put("linkKz", String.format(hlink, "Осы жерде"));
        params.put("linkEn", String.format(hlink, "Click"));

        User sessionUser = userSession.getUser();

        ActivityType activityType = dataManager.load(ActivityType.class)
                .query("select e from uactivity$ActivityType e where e.code = 'NOTIFICATION'")
                .one();

        OrganizationIncentiveMonthResult result = dataManager.load(OrganizationIncentiveMonthResult.class)
                .id(monthResultId)
                .viewProperties("status")
                .one();

        result.setStatus(null);
        dataManager.commit(result);

        for (TsadvUser user : approvers) {
            params.put("userName", user.getFullName());
            activityService.createActivity(
                    user,
                    sessionUser,
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

    }

    protected List<TsadvUser> getApprovers(OrganizationIncentiveMonthResultView monthResult) {

        List<DicIncentiveIndicators> dicIndicators = this.getIndicators(monthResult);
        List<OrganizationGroupExt> departments = this.getDepartments(monthResult);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthResult.getPeriod());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // increment month - because java.util.Calendar bases on zero-index

        List<PositionGroupExt> positions = dataManager.load(OrganizationIncentiveIndicators.class)
                .query("select e from tsadv_OrganizationIncentiveIndicators e " +
                        "   where e.organizationGroup in :organizations" +
                        "       and e.indicator in :dicIndicators " +
                        "       and (:periodYear between EXTRACT(YEAR from e.dateFrom) and EXTRACT(YEAR from e.dateTo)) " +
                        "       and (:periodMonth between EXTRACT(MONTH from e.dateFrom) and EXTRACT(MONTH from e.dateTo)) ")
                .parameter("organizations", departments)
                .parameter("dicIndicators", dicIndicators)
                .parameter("periodYear", year)
                .parameter("periodMonth", month)
                .viewProperties("approvingPosition")
                .list()
                .stream()
                .map(OrganizationIncentiveIndicators::getApprovingPosition)
                .collect(Collectors.toList());

        return dataManager.load(TsadvUser.class)
                .query("select e from sec$User e " +
                        "   join e.personGroup.assignments s " +
                        "   where s.positionGroup in :positions " +
                        "       and current_date between s.startDate and s.endDate" +
                        "       and s.assignmentStatus.code in ('ACTIVE','SUSPENDED')" +
                        "       and s.primaryFlag = true")
                .parameter("positions", positions)
                .view(View.BASE)
                .list();
    }

    protected List<DicIncentiveIndicators> getIndicators(@Nonnull OrganizationIncentiveMonthResultView monthResult) {
        if (monthResult.getParent() == null) {
            TableItems<OrganizationIncentiveMonthResultView> items = organizationIncentiveMonthResultsTree.getItems();
            if (items != null) {
                return items.getItems().stream()
                        .filter(view -> monthResult.equals(view.getParent()))
                        .map(OrganizationIncentiveMonthResultView::getIndicator)
                        .collect(Collectors.toList());
            }
        } else
            return Collections.singletonList(monthResult.getIndicator());
        return null;
    }

    protected List<OrganizationGroupExt> getDepartments(@Nonnull OrganizationIncentiveMonthResultView monthResult) {
        if (monthResult.getParent() == null) {
            TableItems<OrganizationIncentiveMonthResultView> items = organizationIncentiveMonthResultsTree.getItems();
            if (items != null) {
                return items.getItems().stream()
                        .filter(view -> monthResult.equals(view.getParent()))
                        .map(OrganizationIncentiveMonthResultView::getDepartment)
                        .collect(Collectors.toList());
            }
        } else
            return Collections.singletonList(monthResult.getDepartment());
        return null;
    }

    @Subscribe("organizationIncentiveMonthResultsTree.sendNotification")
    protected void onOrganizationIncentiveMonthResultsTreeSendNotification(Action.ActionPerformedEvent event) {
        this.sendNotification();
    }

    @Install(to = "organizationIncentiveMonthResultsTree.sendNotification", subject = "enabledRule")
    protected boolean organizationIncentiveMonthResultsTreeSendNotificationEnabledRule() {
        OrganizationIncentiveMonthResultView selected = organizationIncentiveMonthResultsTree.getSingleSelected();
        return selected != null && selected.getParent() == null;
    }

}