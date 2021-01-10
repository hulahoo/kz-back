package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.learning.enums.CertificationStatus;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adilbekov Yernar
 */
public abstract class AbstractEnrollmentEditor<T extends Enrollment> extends AbstractEditor<T> {

    protected static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    protected static final Logger log = LoggerFactory.getLogger(AbstractEnrollmentEditor.class);

    @Inject
    protected DataManager dataManager;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Metadata metadata;

    @Inject
    protected UserSession userSession;

    @Inject
    protected CourseService courseService;

    protected boolean editable;
    protected boolean hasOfflineSection = false;
    protected int offlineSectionCount;
    protected Map<CourseSection, CourseSectionSession> sessionMap = new HashMap<>();

    public abstract GroupBoxLayout getSectionsGroupBox();

    public abstract void editable(boolean editable);

    @Override
    protected void postInit() {
        super.postInit();

        if (!PersistenceHelper.isNew(getItem())) {
            switch (getItem().getStatus()) {
                case REQUEST:
                case WAITLIST:
                case REQUIRED_SETTING: {
                    editable = true;
                    break;
                }
                default: {
                    editable = false;
                }
            }
        } else {
            editable = true;
        }

        editable(editable);
    }

    protected void initVisibleComponent(Course course) {
        getSectionsGroupBox().removeAll();
        sessionMap.clear();
        hasOfflineSection = false;

        if (course != null) {
            List<CourseSection> courseSections = getCourseSections(course);

            for (CourseSection section : courseSections) {
                String code = section.getFormat().getCode();
                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                hBoxLayout.setSpacing(true);
                hBoxLayout.add(label(section.getSectionName()));

                if (code != null && code.equalsIgnoreCase("offline")) {
                    hasOfflineSection = true;
                    offlineSectionCount++;

                    LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);

                    if (PersistenceHelper.isNew(getItem())) {
                        linkButton.setId("emptyLink");
                        linkButton.setCaption(getMessage("courseSection.session.choose"));
                    } else {
                        CourseSectionSession courseSectionSession = getCourseSectionSession(section);
                        if (courseSectionSession != null) {
                            String linkName = courseSectionSession.getLearningCenter().getLangValue() + " ("
                                    + dateTimeFormat.format(courseSectionSession.getStartDate()) + " - "
                                    + dateTimeFormat.format(courseSectionSession.getEndDate()) + ")";

                            linkButton.setId(courseSectionSession.getId().toString());
                            linkButton.setCaption(linkName);

                            sessionMap.put(section, courseSectionSession);
                        } else {
                            linkButton.setCaption(getMessage("courseSection.session.choose"));
                        }
                    }

                    linkButton.setAction(new BaseAction("select-session") {
                        @Override
                        public void actionPerform(Component component) {
                            openLookup("register-course-sessions",
                                    new Lookup.Handler() {
                                        @Override
                                        public void handleLookup(Collection items) {
                                            if (!items.isEmpty()) {
                                                CourseSectionSession courseSectionSession = (CourseSectionSession) items.iterator().next();
                                                String linkName = courseSectionSession.getLearningCenter().getLangValue() + " ("
                                                        + dateTimeFormat.format(courseSectionSession.getStartDate()) + " - "
                                                        + dateTimeFormat.format(courseSectionSession.getEndDate()) + ")";

                                                linkButton.setCaption(linkName);
                                                linkButton.setId(String.valueOf(courseSectionSession.getId()));

                                                sessionMap.put(section, courseSectionSession);
                                            }
                                        }
                                    }, WindowManager.OpenType.DIALOG,
                                    ParamsMap.of(StaticVariable.COURSE_SECTION_ID, section.getId()));
                        }
                    });

                    hBoxLayout.add(linkButton);
                }

                getSectionsGroupBox().add(hBoxLayout);
            }
        }
    }

    @Override
    public boolean validateAll() {
        if (super.validateAll()) {
            String warningTitle = getMessage("msg.warning.title");

            Course course = getItem().getCourse();
            PersonGroupExt personGroup = getItem().getPersonGroup();

            String messageAlreadyExist = String.format(getMessage("add.cert.enrollment.error.2"),
                    personGroup.getPerson().getFullName(),
                    course.getName());

            String fillSessionMap = getMessage("course.fill.session.map");

            if (PersistenceHelper.isNew(getItem())) {
                if (hasEnrollment(course, personGroup, true)) {
                    showNotification(warningTitle, messageAlreadyExist, NotificationType.TRAY);
                    return false;
                } else {
                    if ((hasOfflineSection && sessionMap.isEmpty()) || (hasOfflineSection && !sessionMap.isEmpty() && sessionMap.size() != offlineSectionCount)) {
                        showNotification(warningTitle, fillSessionMap, NotificationType.TRAY);
                        return false;
                    }
                }
            } else {
                if (!getItem().getStatus().equals(EnrollmentStatus.CANCELLED) && hasEnrollment(course, personGroup, false)) {
                    showNotification(warningTitle, messageAlreadyExist, NotificationType.TRAY);
                    return false;
                } else {
                    if ((hasOfflineSection && sessionMap.isEmpty()) || (hasOfflineSection && !sessionMap.isEmpty() && sessionMap.size() != offlineSectionCount)) {
                        showNotification(warningTitle, fillSessionMap, NotificationType.TRAY);
                        return false;
                    }
                }
            }

            if (getClass().getSimpleName().equalsIgnoreCase(EnrollmentEdit.class.getSimpleName())) {
                if (getItem().getCertificationEnrollment() == null || getItem().getCertificationEnrollment().getId() == null) {
                    if (hasActiveCertification(getItem().getCourse(), getItem().getPersonGroup())) {
                        String message = String.format(getMessage("add.cert.enrollment.error"),
                                getItem().getPersonGroup().getPerson().getFullName(),
                                getItem().getCourse().getName());

                        showNotification(warningTitle, message, NotificationType.TRAY);
                        return false;
                    }
                }

                if (getItem().getStatus().equals(EnrollmentStatus.APPROVED)) {
                    if (hasOfflineSection && !hasFreeSpace(sessionMap, true)) {
                        return false;
                    }
                }
            } else {
                getItem().setStatus(EnrollmentStatus.fromId(hasOfflineSection ? hasFreeSpace(sessionMap, false) ? 1 : 2 : 1));
            }

            return true;
        }
        return false;
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed || !PersistenceHelper.isNew(getItem())) {
            if (hasOfflineSection && !sessionMap.isEmpty()) {
                try {
                    deleteAllSectionSession();

                    for (Map.Entry<CourseSection, CourseSectionSession> entry : sessionMap.entrySet()) {
                        CourseSectionAttempt courseSectionAttempt = metadata.create(CourseSectionAttempt.class);
                        courseSectionAttempt.setEnrollment(getItem());
                        courseSectionAttempt.setCourseSection(entry.getKey());
                        courseSectionAttempt.setCourseSectionSession(entry.getValue());
                        courseSectionAttempt.setSuccess(false);
                        courseSectionAttempt.setActiveAttempt(false);
                        courseSectionAttempt.setAttemptDate(entry.getValue().getStartDate());
                        dataManager.commit(courseSectionAttempt);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showNotification(getMessage("msg.error.title"), ex.getMessage(), NotificationType.TRAY);
                    return false;
                }
            }
        }
        return super.postCommit(committed, close);
    }

    protected boolean hasEnrollment(Course course, PersonGroupExt personGroup, boolean isCreate) {
        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);
        loadContext.setQuery(LoadContext.createQuery(String.format(
                "select e from tsadv$Enrollment e " +
                        "where e.course.id = :courseId " +
                        "and e.personGroupId.id = :personGroupId " +
                        "and e.status not in (4,5) %s",
                isCreate ? "" : String.format("and e.id <> '%s'", getItem().getId())))
                .setParameter("courseId", course.getId())
                .setParameter("personGroupId", personGroup.getId()));
        return dataManager.getCount(loadContext) > 0;
    }

    protected boolean hasActiveCertification(Course course, PersonGroupExt personGroup) {
        LoadContext<CertificationEnrollment> loadContext = LoadContext.create(CertificationEnrollment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$CertificationEnrollment e join e.certification c " +
                        "where e.personGroupId.id = :pId " +
                        "and e.status = :status " +
                        "and c.course.id = :cId");
        query.setParameter("pId", personGroup.getId());
        query.setParameter("status", CertificationStatus.ACTIVE);
        query.setParameter("cId", course.getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    protected List<CourseSection> getCourseSections(Course course) {
        LoadContext<CourseSection> loadContext = LoadContext.create(CourseSection.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$CourseSection e " +
                        "where e.course.id = :courseId")
                .setParameter("courseId", course.getId()))
                .setView("courseSection.edit");
        return dataManager.loadList(loadContext);
    }

    protected CourseSectionSession getCourseSectionSession(CourseSection courseSection) {
        LoadContext<CourseSectionSession> loadContext = LoadContext.create(CourseSectionSession.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$CourseSectionSession e " +
                        "where e.id = (" +
                        "   select a.courseSectionSession.id from tsadv$CourseSectionAttempt a " +
                        "   where a.enrollment.id = :eId " +
                        "   and a.courseSection.id = :csId)")
                .setParameter("eId", getItem().getId())
                .setParameter("csId", courseSection.getId()));

        loadContext.setView("courseSectionSession.browse");

        return dataManager.load(loadContext);
    }

    protected void deleteAllSectionSession() {
        courseService.deleteAllAttempt(getItem());
    }

    protected boolean hasFreeSpace(Map<CourseSection, CourseSectionSession> sessionMap, boolean showMessage) {
        for (CourseSectionSession session : sessionMap.values()) {
            LoadContext<CourseSectionAttempt> loadContext = LoadContext.create(CourseSectionAttempt.class);
            loadContext.setQuery(LoadContext.createQuery(
                    "select e from tsadv$CourseSectionAttempt e join e.enrollment c " +
                            "where e.courseSectionSession.id = :sId " +
                            "and e.success = :success " +
                            "and c.status in (3) ")
                    .setParameter("sId", session.getId())
                    .setParameter("success", Boolean.FALSE));

            long count = dataManager.getCount(loadContext);

//            log.info(String.format("count: %s, maxCount: %s", count, session.getMaxPerson()));

            if (dataManager.getCount(loadContext) >= session.getMaxPerson()) {
                if (showMessage) {
                    String message = String.format(getMessage("add.cert.enrollment.error.3"),
                            session.getLearningCenter().getLangValue(),
                            dateTimeFormat.format(session.getStartDate()) + " - " + dateTimeFormat.format(session.getEndDate()));

                    showNotification(getMessage("msg.warning.title"),
                            message,
                            NotificationType.TRAY_HTML);
                }

                return false;
            }
        }
        return true;
    }

    protected Label label(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }
}
