package kz.uco.tsadv.web.modules.personal.timecard.employeelistfortimecard;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationService;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class EmployeeListForTimecardBrowse extends AbstractLookup {
    protected static final String IMAGE_CELL_HEIGHT = "40px";
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected OrganizationService organizationService;
    @Inject
    protected GroupTable<PersonExt> personsTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        addMonthBeginParameter(params);
        personsTable.setMultiSelect(params.containsKey("multiselect"));
        personsTable.addGeneratedColumn(
                "group.currentAssignmentWithSuspendedAndTerminatedStatus.organizationGroup",
                this::generateOrgPath);
    }

    protected void addMonthBeginParameter(Map<String, Object> params) {
        Date timeMachineMoment = (Date) params.get("date");
        Date monthBeginDate = getMonthBeginDateForTerminated(timeMachineMoment);
        params.put("monthBegin", monthBeginDate);
    }

    /**
     * Возвращает минимальную дату увольнения так чтобы уволенные в этом месяцы появились в списке сотрудников для формирования табеля
     * а имеено возвращает 2-е число, таким образом туда попадают ещё работающие 1-го числа и дальше
     */
    protected Date getMonthBeginDateForTerminated(Date date) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    protected Component generateOrgPath(PersonExt personExt) {
        Label label = componentsFactory.createComponent(Label.class);
        OrganizationGroupExt organizationGroup = Optional.ofNullable(personExt)
                .map(PersonExt::getGroup)
                .map(PersonGroupExt::getCurrentAssignmentWithSuspendedAndTerminatedStatus)
                .map(AssignmentExt::getOrganizationGroup)
                .orElse(null);
        if (organizationGroup != null) {
            String organizationName = organizationGroup.getOrganizationName();
            label.setValue(organizationName);
            String organizationPathToHint = organizationService.getOrganizationPathToHint(organizationGroup.getId(), CommonUtils.getSystemDate());
            label.setDescription(organizationPathToHint);
        }
        return label;
    }

    public Component generateUserImageCell(PersonExt entity) {
        Image image = WebCommonUtils.setImage(entity.getImage(), null, IMAGE_CELL_HEIGHT);
        image.addStyleName("circle-image");
        return image;
    }

    public void redirectCard(PersonExt person, String name) {
        AssignmentExt assignment = employeeService.getAssignment(person.getGroup().getId(), "assignment.card");

        if (assignment != null) {
            openEditor("person-card", assignment, WindowManager.OpenType.THIS_TAB);
        } else {
            showNotification("Assignment is NULL!");
        }
    }


}