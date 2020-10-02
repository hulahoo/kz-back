package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.TemplateEnrollment;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class TemplateForMassEnrollment extends AbstractWindow {
    @Inject
    protected Metadata metadata;
    protected Map<String, Object> param;

    @Named("fieldGroup.date")
    protected DateField<Date> dateField;
    @Named("fieldGroup.reason")
    protected TextField<String> reasonField;
    @Named("fieldGroup.status")
    protected LookupField<EnrollmentStatus> statusField;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Datasource<TemplateEnrollment> templateEnrollmentDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param = params;

        templateEnrollmentDs.setItem(metadata.create(TemplateEnrollment.class));
//        addBeforeCloseWithShortcutListener(event -> {
//            close("cancel", true);
//        });
//        addBeforeCloseWithCloseButtonListener(event -> {
//            close("cancel", true);
//        });

    }

    public void onCancelBtnClick() {
        close("cancel", true);
    }

    public void onEnrollBtnClick() {

        if (param.containsKey("courseId") && param.get("courseId") != null
                && param.containsKey("persons") && param.get("persons") != null) {
            List<Enrollment> list = new ArrayList<>();
            List<PersonGroupExt> personGroupExtList = (List<PersonGroupExt>) param.get("persons");
            for (PersonGroupExt personGroupExt : personGroupExtList) {
                Enrollment enrollment = metadata.create(Enrollment.class);
                enrollment.setId(UUID.randomUUID());
                enrollment.setCourse((Course) param.get("courseId"));
                enrollment.setPersonGroup(personGroupExt);
                if (statusField.getValue() != null) {
                    enrollment.setStatus(statusField.getValue());
                }
                if (dateField.getValue() != null) {
                    enrollment.setDate(dateField.getValue());
                }
                if (reasonField.getValue() != null) {
                    enrollment.setReason(reasonField.getValue());
                }
                list.add(enrollment);
            }
            if (dataManager.commit(new CommitContext(list)).isEmpty()) {
                close(CLOSE_ACTION_ID, true);
            } else {
                close("commit", true);
            }
        } else {
            close(CLOSE_ACTION_ID, true);
        }
    }
}