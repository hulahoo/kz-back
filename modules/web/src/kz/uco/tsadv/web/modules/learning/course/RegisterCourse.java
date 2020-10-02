package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.enums.CertificationStatus;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.CourseService;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

public class RegisterCourse extends AbstractLookup {

    protected static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    protected boolean isAllDataProvided = true;

    @Inject
    protected UserSession userSession;

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected CourseService courseService;

    @Inject
    protected EmployeeService employeeService;

    @Inject
    protected VBoxLayout confirmBlock;

    @Inject
    protected ComponentsFactory componentsFactory;

    protected int offlineSectionCount;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("course") && params.get("course") != null) {
            Course course = (Course) params.get("course");

            PersonGroupExt personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
            PersonExt person = personGroup != null ? personGroup.getPerson() : null;

            if (personGroup != null && person != null) {
                Map<CourseSection, CourseSectionSession> sessionMap = new HashMap<>();

                boolean hasOfflineSection = createConfirm(course, person, sessionMap);

                addAction(new BaseAction("yes") {
                    @Override
                    public void actionPerform(Component component) {
                        if (hasEnrollment(course, personGroup)) {
                            showNotification(
                                    getMessage("Course.section.register.warn"),
                                    String.format(getMessage("Course.section.register.warn.body"), person.getFullName()),
                                    NotificationType.TRAY_HTML
                            );
                        } else {
                            if (hasActiveCertification(course, personGroup)) {
                                showNotification(
                                        getMessage("Course.section.register.warn"),
                                        String.format(getMessage("course.register.error.2"), person.getFullName(), course.getName()),
                                        NotificationType.TRAY_HTML
                                );
                                return;
                            }

                            if ((hasOfflineSection && sessionMap.isEmpty()) || (hasOfflineSection && !sessionMap.isEmpty() && sessionMap.size() != offlineSectionCount)) {
                                showNotification(getMessage("msg.warning.title"), getMessage("course.fill.session.map"), NotificationType.TRAY);
                                return;
                            }

                            try {
                                EnrollmentStatus status = getStatus(course, hasOfflineSection, sessionMap);

                                Enrollment enrollment = metadata.create(Enrollment.class);
                                enrollment.setCourse(course);
                                enrollment.setDate(new Date());
                                enrollment.setPersonGroup(personGroup);
                                enrollment.setStatus(status);
                                dataManager.commit(enrollment);

                                if (hasOfflineSection && !sessionMap.isEmpty() && sessionMap.size() == offlineSectionCount) {
                                    for (Map.Entry<CourseSection, CourseSectionSession> entry : sessionMap.entrySet()) {
                                        CourseSectionAttempt courseSectionAttempt = metadata.create(CourseSectionAttempt.class);
                                        courseSectionAttempt.setEnrollment(enrollment);
                                        courseSectionAttempt.setCourseSection(entry.getKey());
                                        courseSectionAttempt.setCourseSectionSession(entry.getValue());
                                        courseSectionAttempt.setSuccess(false);
                                        courseSectionAttempt.setActiveAttempt(false);
                                        courseSectionAttempt.setAttemptDate(entry.getValue().getStartDate());
                                        dataManager.commit(courseSectionAttempt);
                                    }
                                }

                                sendNotification(personGroup, enrollment, "register.course.notification", null);

                                showNotification(getMessage("Course.section.register.success"),
                                        String.format(getMessage("Course.section.register.success.body"), person.getFullName()),
                                        NotificationType.TRAY_HTML
                                );
                            } catch (Exception ex) {
                                showNotification(getMessage("msg.error.title"),
                                        getMessage("course.register.error"),
                                        NotificationType.TRAY
                                );
                            }
                        }

                        close(this.id);
                    }
                });

            } else {
                notifyAboutError("currentUserHasNotPerson.message");
            }
        } else {
            notifyAboutError("noCourseSpecified.message");
        }

        addAction(new BaseAction("no") {
            @Override
            public void actionPerform(Component component) {
                close(Window.CLOSE_ACTION_ID);
            }
        });
    }

    @Override
    public void ready() {
        super.ready();

        if (!isAllDataProvided) {
            close(Window.CLOSE_ACTION_ID);
        }
    }

    protected void notifyAboutError(String messageCode) {

        showNotification(getMessage(messageCode), NotificationType.TRAY);

        isAllDataProvided = false;

        addAction(new BaseAction("yes") {       // фиктивное действие
            @Override
            public void actionPerform(Component component) {
                close(Window.CLOSE_ACTION_ID);
            }
        });

    }

    protected boolean hasActiveCertification(Course course, PersonGroupExt personGroup) {
        LoadContext<CertificationEnrollment> loadContext = LoadContext.create(CertificationEnrollment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$CertificationEnrollment e join e.certification c " +
                        "where e.personGroup.id = :pId " +
                        "and e.status = :status " +
                        "and c.course.id = :cId");
        query.setParameter("pId", personGroup.getId());
        query.setParameter("status", CertificationStatus.ACTIVE);
        query.setParameter("cId", course.getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    protected void sendNotification(PersonGroupExt personGroup, Enrollment enrollment, String notificationCode, Map<String, Object> params) {
        AssignmentExt assignment = getAssignment(personGroup.getId());
        if (assignment != null && assignment.getOrganizationGroup() != null) {
            List<OrganizationHrUser> hrUsers = employeeService.getHrUsers(assignment.getOrganizationGroup().getId(), "LEARNING_ADMINISTRATOR");

            Map<String, Object> maps = params == null ? new HashMap<>() : params;
            maps.put("enrollment", enrollment);
            maps.put("person", assignment.getPersonGroup().getPerson());

            for (OrganizationHrUser hrUser : hrUsers) {
                UserExt userExt = hrUser.getUser();
                maps.put("user", userExt);
                try {
                    courseService.sendParametrizedNotification(notificationCode, userExt, maps);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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

    protected boolean hasEnrollment(Course course, PersonGroupExt personGroup) {
        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$Enrollment e " +
                        "where e.course.id = :courseId " +
                        "and e.personGroup.id = :personGroupId " +
                        "and e.status not in (4,5)")
                .setParameter("courseId", course.getId())
                .setParameter("personGroupId", personGroup.getId()));
        return dataManager.getCount(loadContext) > 0;
    }

    protected boolean createConfirm(Course course, PersonExt person, Map<CourseSection, CourseSectionSession> sessionMap) {
        boolean hasOfflineSection = false;
        offlineSectionCount = 0;

        HBoxLayout courseName = componentsFactory.createComponent(HBoxLayout.class);
        courseName.setAlignment(Alignment.TOP_CENTER);
        courseName.addStyleName("register-course-name");

        Label nameLabel = label(course.getName());
        courseName.add(nameLabel);
        confirmBlock.add(courseName);

        GroupBoxLayout groupBox = componentsFactory.createComponent(GroupBoxLayout.class);
        groupBox.setOrientation(GroupBoxLayout.Orientation.VERTICAL);
        confirmBlock.add(groupBox);
        groupBox.setCaption(getMessage("CourseCard.course.sections"));

        /*html.append("<tr>")
                .append("<td>").append(getMessage("Course.courseStartDate")).append("</td>")
                .append("<td>").append(dateFormat.format(course.getCourseStartDate()))
                .append("</td></tr>");*/

        if (!course.getSections().isEmpty()) {
            for (CourseSection section : course.getSections()) {
                String code = section.getFormat().getCode();
                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                hBoxLayout.setSpacing(true);
                hBoxLayout.add(label(section.getSectionName()));

                if (code != null && code.equalsIgnoreCase("offline")) {
                    hasOfflineSection = true;
                    offlineSectionCount++;

                    List<CourseSectionSession> sessions = section.getSession();

                    if (sessions != null && !sessions.isEmpty()) {
                        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                        linkButton.setId("emptyLink");
                        linkButton.setCaption(getMessage("courseSection.session.choose"));
                        linkButton.setAction(new BaseAction("select-session") {
                            @Override
                            public void actionPerform(Component component) {
                                openLookup("register-course-sessions",
                                        new Handler() {
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
                }

                groupBox.add(hBoxLayout);
            }
        }

        String message = String.format(
                getMessage("Course.section.register.confirm.text"),
                person.getFullName());

        Label label = label(message);
        label.setHtmlEnabled(true);
        label.addStyleName("register-confirm-text");
        confirmBlock.add(label);
        return hasOfflineSection;
    }

    protected Label label(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }

    /**
     * 1-Request, 2-Waitlist, 3-Approved, 4-Cancelled, 5-Completed
     */
    protected EnrollmentStatus getStatus(Course course, boolean hasOfflineSection, Map<CourseSection, CourseSectionSession> sessionMap) {
        int id;

        if (course.getSelfEnrollment()) {
            if (hasOfflineSection) {
                id = hasFreeSpace(sessionMap) ? 3 : 2;
            } else {
                id = 3;
            }
        } else {
            if (hasOfflineSection) {
                id = hasFreeSpace(sessionMap) ? 1 : 2;
            } else {
                id = 1;
            }
        }
        return EnrollmentStatus.fromId(id);
    }

    protected boolean hasFreeSpace(Map<CourseSection, CourseSectionSession> sessionMap) {
        for (CourseSectionSession session : sessionMap.values()) {
            LoadContext<CourseSectionAttempt> loadContext = LoadContext.create(CourseSectionAttempt.class);
            loadContext.setQuery(LoadContext.createQuery(
                    "select e from tsadv$CourseSectionAttempt e join e.enrollment c " +
                            "where e.courseSectionSession.id = :sId " +
                            "and e.success = :success " +
                            "and c.status in (3) ")
                    .setParameter("sId", session.getId())
                    .setParameter("success", Boolean.FALSE));

            if (dataManager.getCount(loadContext) >= session.getMaxPerson()) {
                return false;
            }
        }
        return true;
    }
}