package kz.uco.tsadv.web.modules.personal.selflearning;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.learning.dictionary.DicCourseFormat;
import kz.uco.tsadv.modules.learning.enums.CertificationStatus;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.enums.Months;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

public class CourseSchedule extends AbstractWindow {

    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Inject
    private DataManager dataManager;

    @Inject
    private UserSession userSession;

    @Inject
    private VBoxLayout courseVBox;

    @Inject
    private ComponentsFactory componentsFactory;

    private PersonGroupExt personGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
        if (personGroup==null){
            showNotification(getMessage("msg.warning.title"), getMessage("user.hasnt.related.person"), NotificationType.TRAY);
        }else {
            groupEnrollments();
        }
    }

    private void groupEnrollments() {
        List<Enrollment> enrollments = getAllEnrollments();
        if (!enrollments.isEmpty()) {

            Map<Integer, List<CourseSectionSessionPair>> courseMap = new HashMap<>();

            for (Enrollment enrollment : enrollments) {
                Course course = enrollment.getCourse();
                List<CourseSection> courseSections = course.getSections();
                if (courseSections != null && !courseSections.isEmpty()) {
                    for (CourseSection courseSection : courseSections) {
                        if (courseSection.getDeleteTs() == null) {
                            DicCourseFormat dicCourseFormat = courseSection.getFormat();
                            if (dicCourseFormat != null && dicCourseFormat.getCode() != null
                                    && dicCourseFormat.getCode().equals("offline")) {

                                List<CourseSectionAttempt> attempts = getAttempts(enrollment, courseSection);

                                if (attempts != null && !attempts.isEmpty()) {
                                    CourseSectionAttempt attempt = attempts.get(0);
                                    CourseSectionSession session = attempt.getCourseSectionSession();
                                    if (session != null && session.getDeleteTs() == null) {
                                        int month = month(session.getStartDate());

                                        List<CourseSectionSessionPair> courses = courseMap.computeIfAbsent(month, k -> new ArrayList<>());
                                        courses.add(new CourseSectionSessionPair(enrollment, null, course, courseSection, session));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            List<CertificationEnrollment> certificationEnrollments = fillCertificationEnrollment();
            if (!certificationEnrollments.isEmpty()) {
                for (CertificationEnrollment certificationEnrollment : certificationEnrollments) {
                    Date certificationDate = DateUtils.addDays(certificationEnrollment.getNextDate(), certificationEnrollment.getCertification().getLifeDay());

                    int month = month(certificationDate);
                    List<CourseSectionSessionPair> courses = courseMap.computeIfAbsent(month, k -> new ArrayList<>());
                    courses.add(new CourseSectionSessionPair(null, certificationEnrollment, null, null, null));
                }
            }

            if (!courseMap.isEmpty()) {
                fillCourses(courseMap);
            }
        }
    }

    private List<CertificationEnrollment> fillCertificationEnrollment() {
        LoadContext<CertificationEnrollment> loadContext = LoadContext.create(CertificationEnrollment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$CertificationEnrollment e " +
                        "where e.personGroupId.id = :pId " +
                        "and e.status = :status");
        query.setParameter("pId", personGroup.getId());
        query.setParameter("status", CertificationStatus.ACTIVE);
        loadContext.setView("certificationEnrollment.browse");
        loadContext.setQuery(query);
        return dataManager.loadList(loadContext);
    }

    private void fillCourses(Map<Integer, List<CourseSectionSessionPair>> courseMap) {
        for (Map.Entry<Integer, List<CourseSectionSessionPair>> entry : courseMap.entrySet()) {
            addHeader(entry.getKey(), entry.getValue().size());
            addBody(entry.getValue());
        }
    }

    private void addHeader(Integer month, int courseCount) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setWidthFull();
        hBoxLayout.setStyleName("course-schedule-header m-" + month);
        Label monthLabel = label(messages.getMessage(Months.fromId(month)), "course-schedule-month");
        hBoxLayout.add(monthLabel);

        Label countLabel = htmlLabel(String.format(getMessage("course.schedule.course.count"), courseCount));
        countLabel.setStyleName("course-schedule-count");
        hBoxLayout.add(countLabel);
        hBoxLayout.expand(monthLabel);
        courseVBox.add(hBoxLayout);
    }

    private void addBody(List<CourseSectionSessionPair> list) {
        for (CourseSectionSessionPair item : list) {
            boolean isCertification = item.getCertification() != null;

            Course course = isCertification ? item.getCertification().getCertification().getCourse() : item.getCourse();

            HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
            hBoxLayout.setSpacing(true);

            hBoxLayout.setStyleName("course-schedule-body-item");
            hBoxLayout.add(Utils.getCourseImageEmbedded(course, "60px", null));

            GridLayout grid = componentsFactory.createComponent(GridLayout.class);
            grid.setAlignment(Alignment.MIDDLE_CENTER);
            grid.setColumns(3);
            grid.setRows(4);
            hBoxLayout.add(grid);
            courseVBox.add(hBoxLayout);

            Action courseCard = new BaseAction("course-card") {
                @Override
                public void actionPerform(Component component) {
                    if (isCertification) {
                        Course reloadCourse = dataManager.reload(course, "course.tree");

                        openWindow("course-card",
                                WindowManager.OpenType.THIS_TAB,
                                ParamsMap.of("course", reloadCourse));
                    } else {
                        Enrollment enrollment = dataManager.reload(item.getEnrollment(), "enrollment.browse");

                        openWindow("course-card",
                                WindowManager.OpenType.THIS_TAB,
                                ParamsMap.of("course", enrollment.getCourse(),
                                        "enrollment", enrollment));
                    }
                }
            };

            if (isCertification) {
                addRow(grid, "course.schedule.course", course.getName(), 0);

                Date nextDate = item.getCertification().getNextDate();

                if (nextDate != null) {
                    String parsedDate = parseDate(
                            DateUtils.addDays(nextDate, -item.getCertification().getCertification().getNotifyDay()),
                            DateUtils.addDays(nextDate, item.getCertification().getCertification().getLifeDay()));

                    if (parsedDate != null) {
                        addRow(grid, "course.schedule.course.section.session.dt", parsedDate, 3);
                    }
                }
            } else {
                Action learningCenter = new BaseAction("learning-center") {
                    @Override
                    public void actionPerform(Component component) {
                        openWindow("learning-center",
                                WindowManager.OpenType.DIALOG,
                                ParamsMap.of("dicLearningCenter", item.getCourseSectionSession().getLearningCenter()));
                    }
                };

                addRow(grid, "course.schedule.course", item.getCourse().getName(), 0, courseCard);
                addRow(grid, "course.schedule.course.section", item.getCourseSection().getSectionName(), 1);
                addRow(grid, "course.schedule.course.section.session.lc", item.getCourseSectionSession().getLearningCenter().getLangValue(), 2, learningCenter);

                String parsedDate = parseDateTime(item.getCourseSectionSession().getStartDate(), item.getCourseSectionSession().getEndDate());
                if (parsedDate != null) {
                    addRow(grid, "course.schedule.course.section.session.dt", parsedDate, 3);
                }
            }
        }
    }

    private void addRow(GridLayout grid, String keyCode, String value, int row) {
        grid.add(htmlLabel(String.format("<span class=\"course-schedule-key\">%s</span>", getMessage(keyCode))), 0, row);
        grid.add(label(":", "course-schedule-divider"), 1, row);
        grid.add(htmlLabel(String.format("<span class=\"course-schedule-value\">%s</span>", value)), 2, row);
    }

    private void addRow(GridLayout grid, String keyCode, String value, int row, Action action) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(value);
        linkButton.setStyleName("course-schedule-value-link");
        linkButton.setAction(action);

        grid.add(htmlLabel(String.format("<span class=\"course-schedule-key\">%s</span>", getMessage(keyCode))), 0, row);
        grid.add(label(":", "course-schedule-divider"), 1, row);
        grid.add(linkButton, 2, row);
    }

    private String parseDateTime(Date startDate, Date endDate) {
        String result = null;
        if (startDate == null && endDate == null) return null;

        if (startDate != null) {
            result = dateTimeFormat.format(startDate);
        }

        if (endDate != null) {
            if (result != null) result += " - ";
            result += dateTimeFormat.format(endDate);
        }
        return result;
    }

    private String parseDate(Date startDate, Date endDate) {
        String result = null;
        if (startDate == null && endDate == null) return null;

        if (startDate != null) {
            result = dateFormat.format(startDate);
        }

        if (endDate != null) {
            if (result != null) result += " - ";
            result += dateFormat.format(endDate);
        }
        return result;
    }

    private Integer month(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    private Label htmlLabel(String keyCode, String value) {
        return htmlLabel(String.format(
                "<span class=\"course-schedule-key\">%s</span> : <span class=\"course-schedule-value\">%s</span>",
                getMessage(keyCode), value));
    }

    private Label htmlLabel(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setHtmlEnabled(true);
        label.setValue(value);
        return label;
    }

    private Label label(String value, String cssClass) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        label.setStyleName(cssClass);
        return label;
    }

    private List<Enrollment> getAllEnrollments() {
        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$Enrollment e  " +
                        "where e.personGroupId.id = :pId " +
                        "and e.status = :status");
        query.setParameter("pId", personGroup.getId());
        query.setParameter("status", EnrollmentStatus.APPROVED);
        loadContext.setView("enrollment.course.schedule");
        loadContext.setQuery(query);
        return dataManager.loadList(loadContext);
    }

    private List<CourseSectionAttempt> getAttempts(Enrollment enrollment, CourseSection courseSection) {
        LoadContext<CourseSectionAttempt> loadContext = LoadContext.create(CourseSectionAttempt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$CourseSectionAttempt e  " +
                        "where e.enrollment.id = :eId " +
                        "and e.courseSection.id = :csId");
        query.setParameter("eId", enrollment.getId());
        query.setParameter("csId", courseSection.getId());
        loadContext.setView("courseSectionAttempt.course.schedule");
        loadContext.setQuery(query);
        return dataManager.loadList(loadContext);
    }

    class CourseSectionSessionPair {
        private Enrollment enrollment;
        private CertificationEnrollment certification;
        private Course course;
        private CourseSection courseSection;
        private CourseSectionSession courseSectionSession;

        public CourseSectionSessionPair(Enrollment enrollment, CertificationEnrollment certification, Course course, CourseSection courseSection, CourseSectionSession courseSectionSession) {
            this.enrollment = enrollment;
            this.certification = certification;
            this.course = course;
            this.courseSection = courseSection;
            this.courseSectionSession = courseSectionSession;
        }

        public CertificationEnrollment getCertification() {
            return certification;
        }

        public void setCertification(CertificationEnrollment certification) {
            this.certification = certification;
        }

        public Enrollment getEnrollment() {
            return enrollment;
        }

        public void setEnrollment(Enrollment enrollment) {
            this.enrollment = enrollment;
        }

        public Course getCourse() {
            return course;
        }

        public void setCourse(Course course) {
            this.course = course;
        }

        public CourseSection getCourseSection() {
            return courseSection;
        }

        public void setCourseSection(CourseSection courseSection) {
            this.courseSection = courseSection;
        }

        public CourseSectionSession getCourseSectionSession() {
            return courseSectionSession;
        }

        public void setCourseSectionSession(CourseSectionSession courseSectionSession) {
            this.courseSectionSession = courseSectionSession;
        }
    }
}