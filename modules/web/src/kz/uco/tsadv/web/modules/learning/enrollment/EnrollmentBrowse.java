package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.service.CertificationService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class EnrollmentBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<CourseSectionAttempt, UUID> courseSectionAttemptDs;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Named("courseSectionAttemptTable.edit")
    protected EditAction courseSectionAttemptEdit;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected CertificationService certificationService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        courseSectionAttemptDs.addItemChangeListener(new Datasource.ItemChangeListener<CourseSectionAttempt>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<CourseSectionAttempt> e) {
                CourseSectionAttempt courseSectionAttempt = e.getItem();
                if (courseSectionAttempt != null) {
                    if (courseSectionAttempt.getCourseSection().getFormat().getCode().equalsIgnoreCase("offline")) {
                        courseSectionAttemptEdit.setEnabled(true);
                    } else {
                        courseSectionAttemptEdit.setEnabled(false);
                    }
                }
            }
        });
    }

    public Component checkIsCertification(Enrollment enrollment) {
        CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
        checkBox.setValue(enrollment.getCertificationEnrollment() != null && enrollment.getCertificationEnrollment().getId() != null);
        checkBox.setEditable(false);
        return checkBox;
    }

    public void generate() {
        certificationService.checkCertificationCourse();
    }

    public void redirectCard(Enrollment enrollment, String name) {
        PersonGroupExt personGroup = enrollment.getPersonGroup();
        if (personGroup != null) {
            AssignmentExt assignment = getAssignment(personGroup.getId());
            if (assignment != null) {
                openEditor("person-card", assignment, WindowManager.OpenType.THIS_TAB);
            } else {
                showNotification("Assignment is NULL!");
            }
        }
    }

    protected AssignmentExt getAssignment(UUID personGroupId) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "  and e.primaryFlag = true " +
                        "and e.personGroup.id = :personGroupId")
                .setParameter("personGroupId", personGroupId)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("assignment.card");
        return dataManager.load(loadContext);
    }
}