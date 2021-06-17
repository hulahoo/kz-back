package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import kz.uco.base.entity.shared.Person;
import kz.uco.base.service.NotificationService;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSectionScormResult;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

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

    @Named("fieldGroup1.reason")
    protected TextArea reasonField;
    @Inject
    protected GroupTable<CourseSectionScormResult> courseSectionScormResultTable;
    @Named("courseSectionScormResultTable.edit")
    protected EditAction courseSectionScormResultTableEdit;
    @Inject
    private FrontConfig frontConfig;
    @Inject
    private NotificationService notificationService;

    @Override
    protected void postInit() {
        super.postInit();

        initReasonField(statusField.getValue());
//        initVisibleComponent(courseField.getValue());
    }

    @Override
    public void ready() {
        super.ready();
        courseSectionScormResultTable.expandAll();
        courseSectionScormResultTableEdit.setOpenType(WindowManager.OpenType.DIALOG);
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

    public void sendNotify() {
        TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e " +
                        " where e.personGroup = :personGroup ")
                .parameter("personGroup", getItem().getPersonGroup())
                .view("userExt.edit")
                .list().stream().findFirst().orElse(null);
        if (tsadvUser != null) {
            Person person = getItem().getPersonGroup() != null ? getItem().getPersonGroup().getPerson() : null;
            if (person != null) {
                Map<String, Object> maps = new HashMap<>();

                String requestLink = "<a href=\"" + frontConfig.getFrontAppUrl()
                        + "/learning-history/"
                        + "\" target=\"_blank\">%s " + "</a>";
                maps.put("linkRu", String.format(requestLink, "История обучения"));
                maps.put("linkEn", String.format(requestLink, "Training History"));
                maps.put("linkKz", String.format(requestLink, "Оқу үлгерімі"));

                maps.put("studentFioRu", person.getFirstName() + " " + person.getLastName());
                maps.put("studentFioEn", person.getFirstNameLatin() != null
                        && !person.getFirstNameLatin().isEmpty()
                        && person.getLastNameLatin() != null
                        && !person.getLastNameLatin().isEmpty()
                        ? person.getFirstNameLatin() + " " + person.getLastNameLatin()
                        : person.getFirstName() + " " + person.getLastName());

                notificationService.sendParametrizedNotification("tdc.new.trainerComment", tsadvUser, maps);
                showNotification(getMessage("sendSucces"), NotificationType.TRAY);
            }
        }
    }
}