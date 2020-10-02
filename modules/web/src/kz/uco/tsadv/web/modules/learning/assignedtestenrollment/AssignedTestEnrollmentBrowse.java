package kz.uco.tsadv.web.modules.learning.assignedtestenrollment;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.dbview.AssignedTestEnrollment;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.service.CourseService;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.*;

public class AssignedTestEnrollmentBrowse extends AbstractLookup {
    @Inject
    protected GroupDatasource<AssignedTestEnrollment, UUID> assignedTestEnrollmentsDs;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CourseService courseService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected TimeSource timeSource;

    /**
     * Начать прохождение теста
     */
    public void startTesting() {
        AssignedTestEnrollment assignedTest = assignedTestEnrollmentsDs.getItem();
        if (assignedTest != null &&
                isAvailableByAttemptsAmount() &&
                isAvailableByPeriodBetweenAttempts()
        ) {

            UUID enrollmentId = assignedTest.getEnrollment() != null ? assignedTest.getEnrollment().getId() : null;
            if (enrollmentId == null) {
                showNotification("Enrollment ID is null!");
                return;
            }

            Enrollment enrollment = loadEnrollment(assignedTest.getEnrollment());
            Course course = enrollment.getCourse();
            List<CourseSection> courseSections = course.getSections();
            CourseSection courseSection = null;
            if (courseSections != null && !courseSections.isEmpty()) {
                for (CourseSection searchCourseSection : courseSections) {
                    if (searchCourseSection.getId().equals(
                            assignedTest.getCourseSection() != null ?
                                    assignedTest.getCourseSection().getId() :
                                    null)
                    ) {
                        courseSection = searchCourseSection;
                        break;
                    }
                }
            }

            if (courseSection == null) {
                showNotification("CourseSection not found!");
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("courseSection", courseSection);
            params.put("enrollment", enrollment);

            openOnlineSectionWindow(params, "start-online-section");
        }
    }

    public void passTest() {    // осталено для совместимости
        startTesting();
    }

    /**
     * Доступен ли тест для прохождения исходя из допустимого Периода между попытками прохождения теста
     */
    protected boolean isAvailableByPeriodBetweenAttempts() {
        AssignedTestEnrollment assignedTest = assignedTestEnrollmentsDs.getItem();
        if (assignedTest != null && assignedTest.getTest() != null && assignedTest.getEnrollment() != null) {
            Test test = loadTest(assignedTest.getTest());
            if (test.getDaysBetweenAttempts() != null && test.getDaysBetweenAttempts() != 0) {

                Date verifyPeriodStartDate = getVerifyPeriodStartDate(test.getDaysBetweenAttempts());
                Date verifyPeriodEndDate = getVerifyPeriodEndDate();
                Long attemptsAmount = getAttemptsAmount(assignedTest, test, verifyPeriodStartDate, verifyPeriodEndDate);

                if (attemptsAmount > 0) {
                    showNotification(getMessage("daysBetweenAttempts"));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Возвращает Дату начала периода проверки наличия попыток
     * (Сейчас - Дней)
     * @param daysBetweenAttempts Дней между попытками
     */
    protected Date getVerifyPeriodStartDate(Integer daysBetweenAttempts) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timeSource.currentTimestamp());
        cal.add(Calendar.DATE, - daysBetweenAttempts);
        return cal.getTime();
    }

    /**
     * Возвращает Дату начала периода проверки наличия попыток
     * (Сейчас)
     */
    protected Date getVerifyPeriodEndDate() {
        return timeSource.currentTimestamp();
    }

    protected Long getAttemptsAmount(
            AssignedTestEnrollment assignedTest,
            Test test,
            Date verifyPeriodStartDate,
            Date verifyPeriodEndDate
    ) {
        return commonService.getCount(
                            CourseSectionAttempt.class,
                            "" +
                                    "select e " +
                                    "  from tsadv$CourseSectionAttempt e " +
                                    " where e.test.id = :testId " +
                                    "   and e.enrollment.id = :enrollmentId " +
                                    "   and e.attemptDate between :startDate and :endDate",
                            ParamsMap.of("testId", test.getId(),
                                    "enrollmentId", assignedTest.getEnrollment().getId(),
                                    "startDate", verifyPeriodStartDate,
                                    "endDate", verifyPeriodEndDate));
    }

    protected boolean daysBetweenAttempts() {    // осталено для совместимости
        return isAvailableByPeriodBetweenAttempts();
    }

    /**
     * Доступен ли тест для прохождения исходя из оставшегося количества попыток
     */
    protected boolean isAvailableByAttemptsAmount() {
        AssignedTestEnrollment assignedTest = assignedTestEnrollmentsDs.getItem();

        Long userAttemptsAmount = getUserAttemptsAmount(assignedTest);

        Test test = loadTest(assignedTest.getTest());
        if (test == null) {
            showNotification("Test not found");
            return false;
        }
        if (userAttemptsAmount >= test.getMaxAttempt()) {
            showNotification(getMessage("noAttempts"));
            return false;
        }
        return true;
    }

    protected boolean hasTestAttempt() {    // осталено для совместимости
        return isAvailableByAttemptsAmount();
    }

    /**
     * Возвращает количество (попыток) прохождений заданного теста
     */
    protected Long getUserAttemptsAmount(AssignedTestEnrollment assignedTest) {
        UUID testId = assignedTest.getTest() != null ? assignedTest.getTest().getId() : null;
        LoadContext<CourseSectionAttempt> loadContext = LoadContext.create(CourseSectionAttempt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "" +
                        "select e " +
                        "  from tsadv$CourseSectionAttempt e " +
                        " where e.test.id = :testId " +
                        "   and e.enrollment.id = :enrollmentId");
        query.setParameter("testId", testId);
        query.setParameter("enrollmentId", assignedTest.getEnrollment() != null ? assignedTest.getEnrollment().getId() : null);
        loadContext.setQuery(query);
        loadContext.setView(View.MINIMAL);
        return dataManager.getCount(loadContext);
    }

    protected Enrollment loadEnrollment(Enrollment enrollment) {
        return dataManager.reload(enrollment, "enrollment.for.testing");
    }

    protected void openOnlineSectionWindow(Map<String, Object> params, String windowAlias) {
        openWindow(windowAlias, WindowManager.OpenType.THIS_TAB, params)
                .addCloseListener(actionId -> assignedTestEnrollmentsDs.refresh());
    }

    protected Test loadTest(Test test) {
        return dataManager.reload(test, View.LOCAL);
    }

    public void attempts() {
        try {
            AssignedTestEnrollment assignedTest = assignedTestEnrollmentsDs.getItem();
            if (assignedTest != null) {
                UUID enrollmentId = assignedTest.getEnrollment() != null ? assignedTest.getEnrollment().getId() : null;
                if (enrollmentId == null) {
                    throw new RuntimeException("Enrollment ID is null!");
                }

                openWindow("assigned-test-attempts",
                        WindowManager.OpenType.DIALOG,
                        ParamsMap.of("enrollmentId", enrollmentId));
            }
        } catch (Exception ex) {
            showNotification(ex.getMessage(), NotificationType.TRAY);
        }
    }

    public void addEnrollment() {
        AbstractEditor abstractEditor = openEditor(metadata.create(Enrollment.class), WindowManager.OpenType.DIALOG);
        abstractEditor.addCloseWithCommitListener(() -> assignedTestEnrollmentsDs.refresh());
    }

    public void removeEnrollment() {
        showOptionDialog("Подтверждение",
                "Вы действительно хотите удалить выбранную запись?",
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                try {
                                    AssignedTestEnrollment assignedTest = assignedTestEnrollmentsDs.getItem();
                                    if (assignedTest == null) {
                                        throw new RuntimeException("Assigned test not selected!");
                                    }

                                    UUID enrollmentId = assignedTest.getEnrollment() != null
                                            ? assignedTest.getEnrollment().getId() :
                                            null;
                                    if (enrollmentId == null) {
                                        throw new RuntimeException("Enrollment ID is null!");
                                    }

                                    courseService.removeEnrollment(enrollmentId);
                                    assignedTestEnrollmentsDs.refresh();
                                } catch (Exception ex) {
                                    showNotification(ex.getMessage(), NotificationType.TRAY);
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });
    }
}