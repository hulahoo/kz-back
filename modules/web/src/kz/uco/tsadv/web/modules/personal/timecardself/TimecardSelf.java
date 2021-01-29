package kz.uco.tsadv.web.modules.personal.timecardself;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.DatePicker;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.dictionary.DicMonth;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class TimecardSelf extends AbstractWindow {

    @Inject
    private Button button;
    @Inject
    private DatePicker datepicker;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private CommonService commonService;
    @Inject
    protected ReportGuiManager reportGuiManager;
    @Inject
    protected Messages messages;

    public void report() {
        Map<String, Object> params = new HashMap<>();

        PersonGroupExt personGroupExt = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);
        if (personGroupExt == null) {
            showNotification("There is no personGroupId for user", NotificationType.ERROR);
            return;
        }

        PositionGroupExt positionGroupExt = employeeService.getPositionGroupByPersonGroupId(personGroupExt.getId(), View.MINIMAL);

        OrganizationGroupExt organizationGroup = employeeService.getOrganizationGroupExtByPositionGroup(positionGroupExt, View.MINIMAL);


        if (organizationGroup == null) {
            showNotification(messages.getMessage("kz.uco.tsadv.web.modules.personal.timecard", "chooseDepartment"), NotificationType.HUMANIZED);
            return;
        }
        params.put("pDepartment", organizationGroup);
        String dateFormated = new SimpleDateFormat("MMyyyy").format(datepicker.getValue());

        DicMonth dicMonth = commonService.getEntity(DicMonth.class, dateFormated);

        if (dicMonth == null) {
            showNotification(messages.getMessage("kz.uco.tsadv.web.modules.personal.timecard", "no.Dictionary.for.month"), NotificationType.HUMANIZED);
            return;
        }

        params.put("pMonth", dicMonth);
        params.put("pEnableAttach", false);
        params.put("pIsIntern", false);

        LoadContext<Report> reportLoadContext = LoadContext.create(Report.class)
                .setQuery(LoadContext.createQuery("select r from report$Report r where r.code = :code")
                        .setParameter("code", "KCHR-9"))
                .setView("report.edit");
        Report report = dataManager.load(reportLoadContext);

        reportGuiManager.printReport(report, params);
    }
}
