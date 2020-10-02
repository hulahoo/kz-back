package kz.uco.tsadv.web.coursesectionsession;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.PopupButton;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class MyCourseSectionSessionBrowse extends AbstractLookup {

    @Inject
    protected ComponentsFactory componentsFactory;

    @Named("courseSectionSessionsTable.edit")
    protected EditAction courseSectionSessionsTableEdit;

    @Inject
    protected PopupButton popupButton;
    @Inject
    protected GroupDatasource<CourseSectionSession, UUID> courseSectionSessionsDs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Named("courseSectionSessionsTable.create")
    protected CreateAction courseSectionSessionsTableCreate;
    @Inject
    protected UserSession userSession;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID) == null) {
            showNotification(getMessage("msg.warning.title"), getMessage("user.hasnt.related.person"), NotificationType.TRAY);
        }
        courseSectionSessionsTableCreate.setWindowParams(
                ParamsMap.of("fromMyCourseSectionSession", "true"));
        courseSectionSessionsTableEdit.setWindowId("tsadv$MyCourseSectionSession.edit");
        enableActions(false);
        courseSectionSessionsDs.addItemChangeListener(e -> {
            enableActions(e.getItem() != null);
        });
    }

    protected void enableActions(boolean isEnable) {
        popupButton.setEnabled(isEnable);
    }

    public Component generateRecordCountCell(CourseSectionSession entity) {
        Label recordsCount = componentsFactory.createComponent(Label.class);
        recordsCount.setValue(entity.getCourseSessionEnrollmentList() != null ?
                entity.getCourseSessionEnrollmentList().size() : 0);
        return recordsCount;
    }

    public void onNotEnrolled(Component source) {
        List<UUID> enrollmentIdList = new ArrayList<>();
        if (courseSectionSessionsDs.getItem().getCourseSessionEnrollmentList() != null) {
            courseSectionSessionsDs.getItem().getCourseSessionEnrollmentList().forEach(courseSessionEnrollment -> {
                enrollmentIdList.add(courseSessionEnrollment.getEnrollment().getId());
            });
        }
        openLookup(
                "tsadv$EnrollmentPerson.lookup",
                getHandler(),
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("fromNotEnrolledAction", true,
                        "courseId", courseSectionSessionsDs.getItem().getCourseSection() != null ?
                                courseSectionSessionsDs.getItem().getCourseSection().getCourse().getId() : null,
                        "enrollmentIds", enrollmentIdList));
    }

    public void onCopy(Component source) {
        List<UUID> enrollmentIdList = new ArrayList<>();
        if (courseSectionSessionsDs.getItem().getCourseSessionEnrollmentList() != null) {
            courseSectionSessionsDs.getItem().getCourseSessionEnrollmentList().forEach(courseSessionEnrollment -> {
                enrollmentIdList.add(courseSessionEnrollment.getEnrollment().getId());
            });
        }
        openLookup(
                "tsadv$EnrollmentPerson.lookup",
                getHandler(),
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("fromCopyAction", true,
                        "courseId", courseSectionSessionsDs.getItem().getCourseSection() != null ?
                                courseSectionSessionsDs.getItem().getCourseSection().getCourse().getId() : null,
                        "excludedCourseSectionSessionId", courseSectionSessionsDs.getItem().getId(),
                        "enrollmentIds", enrollmentIdList));
    }

    protected Handler getHandler() {
        return items -> {
            CommitContext commitContext = new CommitContext();
            CourseSectionSession courseSectionSession = courseSectionSessionsDs.getItem();
            CourseSection courseSection = courseSectionSession.getCourseSection();

            for (Object item : items) {
                Enrollment enrollment = (Enrollment) item;
                CourseSessionEnrollment courseSessionEnrollment = metadata.create(CourseSessionEnrollment.class);
                courseSessionEnrollment.setCourseSession(courseSectionSession);
                courseSessionEnrollment.setEnrollment(enrollment);
                courseSessionEnrollment.setEnrollmentDate(new Date());
                courseSessionEnrollment.setStatus(EnrollmentStatus.APPROVED);
                commitContext.addInstanceToCommit(courseSessionEnrollment);

                CourseSectionAttempt sectionAttempt = metadata.create(CourseSectionAttempt.class);
                sectionAttempt.setCourseSection(courseSection);
                sectionAttempt.setCourseSectionSession(courseSectionSession);
                sectionAttempt.setEnrollment(enrollment);
                commitContext.addInstanceToCommit(sectionAttempt);
            }
            dataManager.commit(commitContext);
            courseSectionSessionsDs.refresh();
        };
    }
}