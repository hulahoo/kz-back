package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.cuba.gui.components.*;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("all")
public class EnrollmentEdit<T extends Enrollment> extends AbstractEnrollmentEditor<T> {

    @Inject
    protected GroupBoxLayout sessionGroupBox;

    @Inject
    protected FieldGroup fieldGroup;

    @Named("fieldGroup.course")
    protected PickerField<Course> courseField;

    @Named("fieldGroup.personGroup")
    protected PickerField personGroupField;

    @Named("fieldGroup.date")
    protected DateField dateField;

    @Named("fieldGroup.status")
    protected LookupField<EnrollmentStatus> statusField;

    @Named("fieldGroup.reason")
    protected TextArea reasonField;

    @Override
    protected void postInit() {
        super.postInit();

        initReasonField(statusField.getValue());
        initVisibleComponent(courseField.getValue());
    }

    @Override
    public void ready() {
        super.ready();

//        courseField.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                initVisibleComponent(e.getValue() != null ? (Course) e.getValue() : null);
//            }
//        });
//
//        statusField.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                EnrollmentStatus enrollmentStatus = e.getValue() != null ? (EnrollmentStatus) e.getValue() : null;
//                initReasonField(enrollmentStatus);
//            }
//        });

        PickerField.LookupAction lookupAction = personGroupField.addLookupAction();
        lookupAction.setLookupScreen("base$PersonGroupAllPerson.browse");
    }

    protected void initReasonField(EnrollmentStatus enrollmentStatus) {
        reasonField.setVisible(enrollmentStatus != null && enrollmentStatus.equals(EnrollmentStatus.CANCELLED));
    }

    @Override
    public GroupBoxLayout getSectionsGroupBox() {
        return sessionGroupBox;
    }

    @Override
    public void editable(boolean editable) {
        //fieldGroup.setEditable(editable);
    }
}