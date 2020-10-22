package kz.uco.tsadv.web.modules.personal.myabsencebalance;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportTemplate;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.shared.Person;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AbsenceBalance;
import kz.uco.tsadv.modules.personal.model.VacationConditions;
import kz.uco.tsadv.service.AbsenceBalanceService;

import javax.inject.Inject;
import java.util.*;

public class MyAbsenceBalance extends AbstractWindow {
    protected PersonGroupExt personGroup;
    protected List<VacationConditions> list;

    @Inject
    protected Label balance;
    @Inject
    protected AbsenceBalanceService absenceBalanceService;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected UserSession userSession;
    @Inject
    private CommonService commonService;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private DataManager dataManager;
    @Inject
    private ReportGuiManager reportGuiManager;

    @Override
    public void init(Map<String, Object> params) {
        list = absenceBalanceService.getAllVacationConditionsList(userSession.getAttribute(StaticVariable.POSITION_GROUP_ID));
    }

    @Override
    public void ready() {
        balance.setValue(getMessage("todayBalance") + ": " + absenceBalanceService.getCurrentAbsenceDays(personGroup));
    }

    public Component generateTypeDaysCell(AbsenceBalance entity) {
        Label label = componentsFactory.createComponent(Label.class);
        for (VacationConditions vacationConditions : list) {
            if (vacationConditions.getVacationDurationType() != null &&
                    !after(entity.getDateFrom(), vacationConditions.getEndDate()) &&
                    !after(vacationConditions.getStartDate(), entity.getDateTo())) {
                switch (vacationConditions.getVacationDurationType().getId()) {
                    case "WORK":
                        label.setValue(VacationDurationType.WORK);
                        break;
                    case "CALENDAR":
                        label.setValue(VacationDurationType.CALENDAR);
                        break;
                }
            }
        }
        return label;
    }

    protected boolean after(Date dateFrom, Date endDate) {
        if (dateFrom == null || endDate == null) {
            return true;
        }
        return dateFrom.after(endDate);
    }

    public void report() {
        Map<String, Object> params = new HashMap<>();
        UUID personGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);

        Person person = commonService.getEntity(Person.class, " select e from base$PersonExt e " +
                        " where :date between e.startDate and e.endDate " +
                        "   and e.group.id = :groupId ",
                ParamsMap.of("groupId", personGroupId, "date", CommonUtils.getSystemDate())
                , "person-view");

        LoadContext<Report> reportLoadContext = LoadContext.create(Report.class)
                .setQuery(LoadContext.createQuery("select r from report$Report r where r.name = :name")
                        .setParameter("name", "Отчет по расшифровке баланса"))
                .setView("report.edit");
        Report report = dataManager.load(reportLoadContext);

        if (report == null) {
            showNotification(getMessage("report.Message"), NotificationType.ERROR);
            return;
        }

        ReportTemplate reportTemplate = commonService.getEntity(ReportTemplate.class, " select t from report$ReportTemplate t " +
                        " where t.report.id = :reportId",
                ParamsMap.of("reportId", report.getId()), View.MINIMAL);
        if (reportTemplate != null) {
            report.setDefaultTemplate(reportTemplate);
            dataManager.commit(report);
        } else {
            showNotification(getMessage("template.Message"), NotificationType.ERROR);
            return;
        }

        params.put("base_person", person);

        reportGuiManager.printReport(report, params);
    }
}