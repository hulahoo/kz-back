package kz.uco.tsadv.web.modules.personal.calculationSheet;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportTemplate;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicLanguage;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.EmployeeService;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalculationSheet extends AbstractWindow {
    @Inject
    protected LookupField monthsStartField, monthsEndField;
    @Inject
    protected DateField<Date> yearsStartField, yearsEndField;
    @Inject
    protected LookupField lang;
    @Inject
    protected ReportGuiManager reportGuiManager;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionDatasource<DicLanguage, UUID> dicLanguagesDs;

    protected List<String> months;
    protected Date date;
    protected SimpleDateFormat format;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        format = new SimpleDateFormat("MMMM", userSessionSource.getLocale());

        dicLanguagesDs.refresh();
        if (userSessionSource.getLocale().getLanguage() != null)
            for (DicLanguage dicLanguage : dicLanguagesDs.getItems()) {
                if (userSessionSource.getLocale().getLanguage().toLowerCase().equals(dicLanguage.getCode().toLowerCase())) {
                    lang.setValue(dicLanguage);
                    break;
                }
            }

        date = DateUtils.setDays(BaseCommonUtils.truncDate(new Date()), 1);

        initMonths();
        initYears(yearsStartField);
        initYears(yearsEndField);
    }

    protected void initYears(DateField<Date> dateField) {
        dateField.setValue(date);
        dateField.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                dateField.setValue(date);
            }
        });
    }

    protected void initMonths() {
        this.months = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < 12; i++) {
            cal.set(Calendar.MONTH, i);
            months.add(format.format(cal.getTime()));
        }

        cal.setTime(date);
        int monthIndex = cal.get(Calendar.MONTH);

        monthsStartField.setOptionsList(this.months);
        monthsStartField.setValue(this.months.get(monthIndex));

        monthsEndField.setOptionsList(this.months);
        monthsEndField.setValue(this.months.get(monthIndex));
    }

    public void report() {
        Map<String, Object> params = new HashMap<>();
        String langCode = ((DicLanguage) lang.getValue()).getCode().toUpperCase();

        UUID personGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);

        Date dateFromValue = DateUtils.setMonths(yearsStartField.getValue(), months.indexOf(monthsStartField.getValue()));
        Date dateToValue = DateUtils.addDays(DateUtils.addMonths(DateUtils.setMonths(yearsEndField.getValue(), months.indexOf(monthsEndField.getValue())), 1), -1);

        if (dateToValue.before(dateFromValue)) {
            showNotification(getMessage("datefrom.after.dateTo"), NotificationType.TRAY);
            return;
        }

        if (personGroupId == null) {
            showNotification(getMessage("person.Message"), NotificationType.ERROR);
            return;
        }

        PersonExt personExt = commonService.getEntity(PersonExt.class, " select e from base$PersonExt e " +
                        " where :date between e.startDate and e.endDate " +
                        "   and e.group.id = :groupId ",
                ParamsMap.of("groupId", personGroupId, "date", CommonUtils.getSystemDate())
                , "person-view");

        if (personExt == null) {
            showNotification(getMessage("person.Message"), NotificationType.ERROR);
            return;
        }

        AssignmentExt assignment = employeeService.getAssignment(personExt.getGroup().getId(), "assignment.edit");

        if (assignment == null || assignment.getLegacyId() == null) {
            showNotification(getMessage("person.legacyId"), NotificationType.ERROR);
            return;
        }

        LoadContext<Report> reportLoadContext = LoadContext.create(Report.class)
                .setQuery(LoadContext.createQuery("select r from report$Report r where r.code = :code")
                        .setParameter("code", "KCHR-128"))
                .setView("report.edit");
        Report report = dataManager.load(reportLoadContext);

        if (report == null) {
            showNotification(getMessage("report.Message"), NotificationType.ERROR);
            return;
        }

        ReportTemplate reportTemplate = commonService.getEntity(ReportTemplate.class, " select t from report$ReportTemplate t " +
                        " where t.report.id = :reportId and t.code = :code",
                ParamsMap.of("reportId", report.getId(), "code", langCode), View.MINIMAL);
        if (reportTemplate != null) {
            report.setDefaultTemplate(reportTemplate);
            dataManager.commit(report);
        } else {
            showNotification(getMessage("template.Message"), NotificationType.ERROR);
            return;
        }

        if (langCode.equalsIgnoreCase("en")) {
            langCode = "US";
        }

        params.put("DATE_FROM", dateFromValue);
        params.put("DATE_TO", dateToValue);
        params.put("LANG", langCode);
        params.put("LEGACY_ID", assignment.getLegacyId());

        reportGuiManager.printReport(report, params);
    }
}