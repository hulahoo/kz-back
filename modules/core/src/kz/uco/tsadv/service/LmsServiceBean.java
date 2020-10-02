package kz.uco.tsadv.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.bali.db.QueryRunner;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.UniqueNumbersAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.app.UserManagementService;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.Book;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.lms.factory.*;
import kz.uco.tsadv.lms.pojo.*;
import kz.uco.tsadv.lms.pojo.AnswerPojo;
import kz.uco.tsadv.lms.pojo.QuestionPojo;
import kz.uco.tsadv.modules.learning.enums.ContentType;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import kz.uco.tsadv.modules.learning.enums.feedback.FeedbackResponsibleRole;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackQuestionType;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackUsageType;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.feedback.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.postgresql.util.PGInterval;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static kz.uco.base.common.Null.emptyReplace;

@Service(LmsService.NAME)
public class LmsServiceBean implements LmsService {
    public static final String SEQUENCE_ENROLLMENT_NUMBER = "SEQUENCE_ENROLLMENT_NUMBER";
    public static final String reportCode = "KNU-64-CERTIFICATE";

    @Inject
    protected DataManager dataManager;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected Persistence persistence;

    @Inject
    protected Metadata metadata;

    @Inject
    protected UniqueNumbersAPI uniqueNumbersAPI;

    @Inject
    protected ReportService reportService;

    @Inject
    protected CommonService commonService;

    @Inject
    protected FileStorageAPI fileStorageAPI;

    @Inject
    private PasswordEncryption passwordEncryption;

    @Inject
    private UserManagementService userManagementService;

    @Inject
    private Messages messages;

    @Override
    public List<EnrollmentPojo> getPersonCourses() {
        return getPersonEnrollment(EnrollmentStatus.APPROVED);
    }

    @Override
    public List<EnrollmentPojo> getPersonHistory() {
        return getPersonEnrollment(EnrollmentStatus.COMPLETED);
    }

    @Override
    public Boolean hasEnrollment(UUID courseId) {
        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        if (userPersonGroupId == null) {
            return null;
        }

        return persistence.callInTransaction(em -> (Boolean) em.createNativeQuery("" +
                "select (select count(*) " +
                "           from tsadv_enrollment " +
                "           where person_group_id = ?1 " +
                "           and course_id = ?2) > 0")
                .setParameter(1, userPersonGroupId)
                .setParameter(2, courseId)
                .getSingleResult());
    }

    protected List<EnrollmentPojo> getPersonEnrollment(EnrollmentStatus enrollmentStatus) {
        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        if (userPersonGroupId == null) {
            return null;
        }

        List<Enrollment> enrollments = dataManager.loadList(LoadContext.create(Enrollment.class)
                .setQuery(
                        LoadContext.createQuery(
                                "select e " +
                                        "from tsadv$Enrollment e " +
                                        "where e.personGroup.id = :personGroup " +
                                        "and e.status = :status")
                                .setParameter("personGroup", userPersonGroupId)
                                .setParameter("status", enrollmentStatus))
                .setView("enrollment.course.schedule"));

        List<EnrollmentPojo> enrollmentsPojo = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            EnrollmentPojo pojo = new EnrollmentPojo();
            pojo.setId(enrollment.getCourse().getId().toString());
            pojo.setName(enrollment.getCourse().getName());
            if (enrollment.getCourse().getLogo() != null) {
                pojo.setLogo(Base64.getEncoder().encodeToString(enrollment.getCourse().getLogo()));
            }
            enrollmentsPojo.add(pojo);
        }

        return enrollmentsPojo;
    }

    @Override
    public CoursePojo loadCourseData(UUID courseId) {
        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        Objects.requireNonNull(userPersonGroupId);

        Object[] course = persistence.callInTransaction(em -> (Object[]) em.createNativeQuery("" +
                "SELECT c.id, " +
                "   c.name, " +
                "   c.description, " +
                "   c.logo, " +
                "   e.id, " +
                "   c.self_enrollment " +
                "FROM tsadv_course c " +
                "   LEFT JOIN tsadv_enrollment e ON c.id = e.course_id AND e.delete_ts is null AND e.person_group_id = ?2 " +
                "WHERE c.id = ?1")
                .setParameter(1, courseId)
                .setParameter(2, userPersonGroupId).getSingleResult());

        CoursePojo coursePojo = new CoursePojo();
        coursePojo.setId(course[0].toString());
        coursePojo.setName((String) course[1]);
        coursePojo.setDescription((String) course[2]);
        if (course[3] != null) {
            coursePojo.setLogo(Base64.getEncoder().encodeToString((byte[]) course[3]));
        }
        coursePojo.setEnrollmentId(course[4] != null ? course[4].toString() : null);
        coursePojo.setSelfEnrollment(course[5] != null ? (Boolean) course[5] : false);

        List<CourseSection> courseSections = dataManager.loadList(LoadContext.create(CourseSection.class)
                .setQuery(LoadContext.createQuery("" +
                        "select cs from tsadv$CourseSection cs where cs.course.id = :courseId order by cs.order")
                        .setParameter("courseId", course[0])).setView("course.section.format"));

        List<CourseSectionPojo> sections = new ArrayList<>();
        for (CourseSection courseSection : courseSections) {
            CourseSectionPojo courseSectionPojo = new CourseSectionPojo();
            courseSectionPojo.setId(courseSection.getId().toString());
            courseSectionPojo.setOrder(courseSection.getOrder());
            courseSectionPojo.setSectionName(courseSection.getSectionName());
            courseSectionPojo.setLangValue1(courseSection.getFormat().getLangValue1());
            sections.add(courseSectionPojo);
        }
        coursePojo.setSections(sections);
        if (coursePojo.getEnrollmentId() != null) {
            Enrollment enrollment = dataManager.load(LoadContext.create(Enrollment.class)
                    .setView(View.MINIMAL)
                    .setId(course[4]));
            for (CourseSectionPojo section : coursePojo.getSections()) {
                section.setPassed(hasAttempts(section, enrollment));
            }
        }

        coursePojo.setCourseFeedbacks(dataManager
                .loadList(LoadContext.create(LearningFeedbackTemplate.class)
                        .setQuery(LoadContext.createQuery(
                                "select e from tsadv$LearningFeedbackTemplate e " +
                                        "where e.id in (select cft.feedbackTemplate.id from tsadv$CourseFeedbackTemplate cft where cft.course.id = :courseId and :systemDate between cft.startDate and cft.endDate) " +
                                        "and e.active = True " +
                                        "and e.employee = True " +
                                        "and e.usageType = :usageType")
                                .setParameter("courseId", courseId)
                                .setParameter("systemDate", CommonUtils.getSystemDate())
                                .setParameter("usageType", LearningFeedbackUsageType.COURSE.getId()))
                        .setView(View.LOCAL))
                .parallelStream()
                .map(this::parseEntityFeedbackToPojo)
                .collect(Collectors.toList()));
        return coursePojo;
    }

    protected SimplePojo parseEntityFeedbackToPojo(LearningFeedbackTemplate entity) {
        SimplePojo pojo = new SimplePojo();
        pojo.setId(entity.getId().toString());
        pojo.setName(entity.getName());

        return pojo;
    }

    @Override
    public void registerToCourse(UUID courseId) {
        persistence.runInTransaction(em -> {
            Optional.ofNullable(em.find(Course.class, courseId, "course.tree"))
                    .ifPresent(course -> {
                        if (!course.getSelfEnrollment()) {
                            throw new RuntimeException("Can not register to course, because course is not self enrollment");
                        }
                        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
                        Objects.requireNonNull(userPersonGroupId);

                        PersonGroupExt personGroup = dataManager.load(LoadContext.create(PersonGroupExt.class)
                                .setId(userPersonGroupId).setView(View.LOCAL));
                        Objects.requireNonNull(personGroup);

                        EnrollmentStatus status = EnrollmentStatus.APPROVED;

                        Enrollment enrollment = metadata.create(Enrollment.class);
                        enrollment.setCourse(course);
                        enrollment.setDate(new Date());
                        enrollment.setPersonGroup(personGroup);
                        enrollment.setStatus(status);
                        em.persist(enrollment);
                    });
        });
    }

    @Override
    public List<Course> loadCourses() {
        return dataManager.loadList(LoadContext.create(Course.class).setQuery(LoadContext.createQuery("" +
                "select c from tsadv$Course c where c.activeFlag = true order by c.name")).setView(View.LOCAL));
    }

    @Override
    public List<Course> loadCourses(List<ConditionPojo> conditions) {
        if (CollectionUtils.isEmpty(conditions)) {
            return loadCourses();
        }

        return persistence.callInTransaction(em -> {
            final Class<Course> courseClass = Course.class;

            String queryString = "" +
                    "SELECT * " +
                    "FROM TSADV_COURSE c " +
                    "WHERE c.ACTIVE_FLAG " +
                    "   AND c.delete_ts is null " +
                    "   %s " +
                    "ORDER BY c.name ";

            Query query = em.createNativeQuery("", Course.class);
            fillQueryParameters(conditions, query, queryString, courseClass);
            return query.getResultList();
        });
    }

    @Override
    public List<LearningObject> loadLearningObject(String contentType) {
        ContentType contentTypeEnum = ContentType.fromId(contentType.toUpperCase());
        if (contentTypeEnum != null) {
            return dataManager.loadList(LoadContext.create(LearningObject.class)
                    .setQuery(LoadContext.createQuery("" +
                            "select e " +
                            "from tsadv$LearningObject e " +
                            "where e.contentType = :contentType " +
                            "order by " +
                            "e.objectName")
                            .setParameter("contentType", contentTypeEnum))
                    .setView(View.LOCAL));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public TestPojo startAndLoadTest(UUID courseSectionObjectId, UUID enrollmentId) {
        return persistence.callInTransaction(em -> {
            Test test = getTestByCourseSectionObject(em, courseSectionObjectId, "test.course.lms");
            //TODO: почему то поиск по jpql-у не находит ничего
            Enrollment enrollment = em.createNativeQuery("" +
                    "SELECT e.* " +
                    "FROM tsadv_enrollment e " +
                    "WHERE e.id = ?1", Enrollment.class)
                    .setParameter(1, enrollmentId)
                    .getSingleResult();
            Objects.requireNonNull(enrollment);
            return getTestPojo(em, test, createAndGetAttempt(em, test, getCourseSection(courseSectionObjectId), enrollment, false).getId());
        });
    }

    @Override
    public TestPojo startAndLoadTest(UUID testId) {
        return persistence.callInTransaction(em -> {
            Test test = em.createQuery(
                    "select t " +
                            "from tsadv$CourseSectionObject cso " +
                            "   join cso.test t " +
                            "where cso.id = :csoId", Test.class)
                    .setParameter("csoId", testId).setView(Test.class, "test.course.lms").getSingleResult();
            return getTestPojo(em, test, createAndGetAttempt(em, test, testId).getId());
        });
    }

    @Override
    public List<EnrollmentPojo> loadPersonTests() {
        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        if (userPersonGroupId == null) {
            return null;
        }

        List<CourseSectionObject> courseSectionObjects = dataManager.loadList(LoadContext.create(CourseSectionObject.class)
                .setQuery(
                        LoadContext.createQuery(
                                "select cso " +
                                        "from tsadv$Enrollment e " +
                                        "   join e.course c " +
                                        "   join c.sections cs " +
                                        "   join cs.sectionObject cso " +
                                        "   join cso.test t " +
                                        "where e.personGroup.id = :personGroup " +
                                        "   and t.id is not null ")
                                .setParameter("personGroup", userPersonGroupId))
                .setView("courseSectionObject.edit"));
        List<EnrollmentPojo> enrollmentsPojo = new ArrayList<>();
        for (CourseSectionObject courseSectionObject : courseSectionObjects) {
            Course course = dataManager.load(LoadContext.create(Course.class).setQuery(LoadContext.createQuery("" +
                    "select c " +
                    "from tsadv$Course c " +
                    "   join c.sections cs " +
                    "   join cs.sectionObject cso " +
                    "where cso.id = :csoId")
                    .setParameter("csoId", courseSectionObject.getId()))
                    .setView(View.LOCAL));
            if (course == null) {
                throw new NullPointerException("Can not find course by course section object id " + courseSectionObject.getId());
            }

            EnrollmentPojo pojo = new EnrollmentPojo();
            pojo.setId(courseSectionObject.getId().toString());
            pojo.setName(course.getName() + ": " + courseSectionObject.getTest().getName());
            if (course.getLogo() != null) {
                pojo.setLogo(Base64.getEncoder().encodeToString(course.getLogo()));
            }
            enrollmentsPojo.add(pojo);
        }

        return enrollmentsPojo;
    }

    @Override
    public TestScorePojo finishTest(AnsweredTest answeredTest) {
        return persistence.callInTransaction((final EntityManager em) -> {
            CourseSectionAttempt csa = em.find(CourseSectionAttempt.class, UUID.fromString(answeredTest.getAttemptId()));
            Objects.requireNonNull(csa);

            TestScorePojo response = new TestScorePojo();

            Test test = csa.getTest();
            test.getSections().forEach(ts -> {
                TestSection testSection = dataManager.reload(ts, "testSection.with.questions");
                response.setMaxScore(response.getMaxScore() + testSection.getQuestions().size());
                testSection.getQuestions().forEach(testSectionQuestion -> {
                    AttemptQuestionPojo questionAndAnswer = answeredTest.getQuestionsAndAnswers().stream()
                            .filter(qa -> qa.getQuestionId().equals(testSectionQuestion.getQuestion().getId().toString()))
                            .findFirst()
                            .orElse(null);
                    if (questionAndAnswer != null) {
                        Question question = dataManager.load(LoadContext.create(Question.class)
                                .setId(UUID.fromString(questionAndAnswer.getQuestionId()))
                                .setView("question.edit"));
                        Objects.requireNonNull(question);

                        String answer = getAnswer(questionAndAnswer.getAnswer(), question.getType());
                        Objects.requireNonNull(answer);

                        PersonAnswer personAnswer = createPersonAnswer(em, question, csa, answer, testSection);

                        QuestionFactory qf = getQuestionFactory(question.getType());
                        Objects.requireNonNull(qf);

                        List<Answer> answers = dataManager.loadList(LoadContext.create(Answer.class).setQuery(LoadContext.createQuery("" +
                                "select a " +
                                "from tsadv$Answer a " +
                                "where a.question.id = :questionId")
                                .setParameter("questionId", question.getId())));
                        if (CollectionUtils.isEmpty(answers)) {
                            throw new NullPointerException("Can not find answers by id " + question.getId());
                        }

                        qf.checkQuestion(question, personAnswer, testSection, answers);

                        response.setScore(response.getScore() + personAnswer.getScore());
                    }
                });
                csa.setTestResult(response.getScore());
                csa.setSuccess(isSucceed(test, response.getScore()));
                csa.setActiveAttempt(true);
                em.merge(csa);

            });
            return response;
        });
    }

    private String getAnswer(List<String> userAnswer, QuestionType type) {
        switch (type) {
            case ONE: {
                Answer answer = dataManager.load(LoadContext.create(Answer.class)
                        .setId(UUID.fromString(userAnswer.get(0)))
                        .setView(View.LOCAL));
                assert answer != null;
                return Arrays.toString(new String[]{answer.getId().toString()});
            }
            case MANY: {
                List<Answer> answers = dataManager.loadList(LoadContext.create(Answer.class)
                        .setQuery(LoadContext.createQuery("" +
                                "select a " +
                                "from tsadv$Answer a " +
                                "where a.id in :ids")
                                .setParameter("ids", userAnswer.stream().map(UUID::fromString).collect(Collectors.toList())))
                        .setView(View.LOCAL));
                return Arrays.toString(answers.stream().map(answer -> answer.getId().toString()).toArray());
            }
            case NUM:
            case TEXT: {
                return userAnswer.get(0);
            }
        }
        return null;
    }

    private QuestionFactory getQuestionFactory(QuestionType type) {
        switch (type) {
            case ONE: {
                return new OneQuestion();
            }
            case MANY: {
                return new ManyQuestion();
            }
            case NUM: {
                return new NumQuestion();
            }
            case TEXT: {
                return new TextQuestion();
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public List<ProgressCoursePojo> loadPersonProgress() throws SQLException {
        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        assert userPersonGroupId != null;

        QueryRunner qr = new QueryRunner(persistence.getDataSource());
        List<ProgressCoursePojo> progress = qr.query(getPersonProgressQuery(), userPersonGroupId, (ResultSet rs) -> {
            List<ProgressCoursePojo> mappedDbResult = new ArrayList<>();
            while (rs.next()) {
                ProgressCoursePojo progressCoursePojoPojo = new ProgressCoursePojo();
                progressCoursePojoPojo.setLineNum(rs.getInt("line_num"));
                progressCoursePojoPojo.setDate(ProgressCoursePojo.dateFormatter.format(rs.getDate("attempt_date")));
                progressCoursePojoPojo.setCourseName(rs.getString("course_name"));
                progressCoursePojoPojo.setFio(rs.getString("fio"));
                progressCoursePojoPojo.setScore(rs.getInt("test_result"));
                progressCoursePojoPojo.setMaxScore(rs.getInt("max_score"));
                progressCoursePojoPojo.setCountFailed(rs.getInt("failed_tries"));
                progressCoursePojoPojo.setCertificateUrl(rs.getString("enrollment_id"));

                mappedDbResult.add(progressCoursePojoPojo);
            }
            return mappedDbResult;
        });
        return progress;
    }

    @Override
    public String getCertificate(String enrollmentId) {
        Report report = commonService.getEntity(Report.class, reportCode);
        if (report != null) {
            Enrollment enrollment = dataManager.load(LoadContext.create(Enrollment.class).setId(UUID.fromString(enrollmentId)));
            if (enrollment != null) {
                FileDescriptor fd = reportService.createAndSaveReport(report, ParamsMap.of("enrollment", enrollment), enrollmentId);
                saveCertificate(enrollment, fd);
                return fd.getId().toString();
            }
        }
        return null;
    }

    private void saveCertificate(Enrollment enrollment, FileDescriptor fd) {
        EnrollmentCertificateFile ecf = metadata.create(EnrollmentCertificateFile.class);
        ecf.setCertificateFile(fd);
        ecf.setEnrollment(enrollment);
        dataManager.commit(ecf);
    }

    @Override
    public void removeCertificate(String certificateFileId) {
        EnrollmentCertificateFile ecf = dataManager.load(LoadContext.create(EnrollmentCertificateFile.class)
                .setQuery(LoadContext.createQuery("" +
                        "select ecf " +
                        "from tsadv$EnrollmentCertificateFile ecf " +
                        "where ecf.certificateFile.id = :certificateFileId")
                        .setParameter("certificateFileId", UUID.fromString(certificateFileId)))
                .setView("enrollmentCertificateFile.with.certificateFile"));
        if (ecf != null) {
            persistence.runInTransaction(em -> {
                try {
                    fileStorageAPI.removeFile(ecf.getCertificateFile());
                    em.setSoftDeletion(false);
                    em.remove(ecf);
                } catch (FileStorageException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public Map<Date, List<CalendarMonthPojo>> personMonthEvents(Date date) {
        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        Objects.requireNonNull(userPersonGroupId);

        @SuppressWarnings("unchecked")
        List<Object[]> result = persistence.callInTransaction(em -> em.createNativeQuery(getPersonMonthEventsQuery()).setParameter(1, date).getResultList());

        return result.stream()
                .collect(Collectors.groupingBy(row -> (Date) row[0], Collectors.mapping((Object[] row) -> {
                    CalendarMonthPojo mapped = new CalendarMonthPojo();
                    mapped.setName((String) row[1]);
                    mapped.setDuration(parseResultToDuration((PGInterval) row[2]));
                    mapped.setCourseId(((UUID) row[3]).toString());
                    return mapped;
                }, Collectors.toList())));
    }

    @Override
    public List<NotificationPojo> getPersonNotifications() {
        User user = userSessionSource.getUserSession().getUser();
        Objects.requireNonNull(user);

        return dataManager.loadList(LoadContext.create(SendingNotification.class).setQuery(LoadContext.createQuery("" +
                "select n " +
                "from base$SendingNotification n " +
                "where n.user.id = :userId " +
                "order by n.createTs desc")
                .setParameter("userId", user.getId()))
                .setView("notifications.lmsp"))
                .stream()
                .map(n -> {
                    NotificationPojo notificationPojo = new NotificationPojo();
                    notificationPojo.setId(n.getId().toString());
                    notificationPojo.setCaption(n.getSendingMessage().getCaption());
//                    notificationPojo.setContentText(n.getSendingMessage().getContentText());
                    notificationPojo.setReaded(n.getReaded());
                    notificationPojo.setDate(n.getCreateTs());
                    return notificationPojo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SendingNotification getNotification(UUID notificationId) {
        return persistence.callInTransaction(em -> {
            SendingNotification sendingNotification = em.find(SendingNotification.class, notificationId, "notifications.lmsp");
            Objects.requireNonNull(sendingNotification);

            sendingNotification.setReaded(true);
            return em.merge(sendingNotification);
        });
    }

    @Override
    public ResponsePojo changePassword(String oldPassword, String newPassword) {
        User currentUser = userSessionSource.getUserSession().getUser();
        Objects.requireNonNull(currentUser);

        UUID currentUserId = currentUser.getId();

        String oldPasswordHash = passwordEncryption.getPlainHash(oldPassword);
        boolean isCorrectOldPassword = userManagementService.checkPassword(currentUserId, oldPasswordHash);
        if (!isCorrectOldPassword) {
            ResponsePojo r = new ResponsePojo();
            r.setStatus(ResponsePojo.Response.ERROR);
            r.setMessage(messages.getMainMessage("lms.password.oldPasswordIncorrect"));
            return r;
        }

        String passwordHash = passwordEncryption.getPasswordHash(currentUserId, newPassword);
        userManagementService.changeUserPassword(currentUserId, passwordHash);

        ResponsePojo r = new ResponsePojo();
        r.setStatus(ResponsePojo.Response.SUCCESS);
        return r;
    }

    @Override
    public ResponsePojo restorePassword(String userLogin) {
        ResponsePojo response = new ResponsePojo();
        if(userLogin == null && userLogin.isEmpty())
        {
            response.setStatus(ResponsePojo.Response.ERROR);
            response.setMessage("passwordRestore.noLogin.caption");
            return response;
        }
        LoadContext<UserExt> lc = LoadContext.create(UserExt.class);
        lc.setView("user.edit");
        lc.setQueryString("select u from base$UserExt u where u.loginLowerCase = :login and (u.active = true or u.active is null)")
                .setParameter("login", userLogin);
        UserExt targetUser = dataManager.load(lc);
        if (targetUser == null) {
            response.setStatus(ResponsePojo.Response.ERROR);
            response.setMessage("passwordRestore.noUser.caption");
        } else if (targetUser.getEmail() == null) {
            response.setStatus(ResponsePojo.Response.ERROR);
            response.setMessage("passwordRestore.noEmail.caption");
        } else {
            // generate new temporary password and send email
            // user must have specified e-mail in the database
            userManagementService.changePasswordsAtLogonAndSendEmails(Collections.singletonList(targetUser.getId()));
            response.setStatus(ResponsePojo.Response.SUCCESS);
        }
        return response;
    }

    @Override
    public List<CourseTrainerPojo> getCourseTrainers(UUID courseId) throws SQLException {
        return new QueryRunner(persistence.getDataSource()).query("" +
                "select concat(t.last_name, ' ', t.first_name, ' ', t.middle_name) as full_name, " +
                "       t.proffesional_data as professional_data, " +
                "       t.additional_information as additional_data " +
                "FROM tsadv_course c " +
                "       INNER JOIN tsadv_course_section cs ON c.id = cs.course_id and cs.delete_ts is null " +
                "       INNER JOIN tsadv_course_section_session css " +
                "                  ON css.course_section_id = cs.id and css.delete_ts is null " +
                "       INNER JOIN knu_trainer t ON t.id = css.trainer_knu_id and t.delete_ts is null " +
                "WHERE c.id = ?", courseId, (ResultSet rs) -> {

            List<CourseTrainerPojo> resultList = new ArrayList<>();
            while (rs.next()) {
                CourseTrainerPojo resultPojo = new CourseTrainerPojo();
                resultPojo.setFullName(rs.getString("full_name"));
                resultPojo.setProfessionalData(rs.getString("professional_data"));
                resultPojo.setAdditionalData(rs.getString("additional_data"));

                resultList.add(resultPojo);
            }
            return resultList;
        });
    }

    @Override
    public List<LearningFeedbackQuestion> loadFeedbackData(UUID feedbackTemplateId) {
        return getFeedbackQuestions(feedbackTemplateId);
    }

    @Override
    public void finishFeedback(AnsweredFeedback answeredFeedback) {
        List<LearningFeedbackQuestion> questions = getFeedbackQuestions(answeredFeedback.getTemplateId(), "question.with.answers");
        persistence.runInTransaction(em -> {
            UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
            Objects.requireNonNull(userPersonGroupId);

            PersonGroupExt personGroup = em.find(PersonGroupExt.class, userPersonGroupId);
            Objects.requireNonNull(userPersonGroupId);

            LearningFeedbackTemplate feedbackTemplate = em.find(LearningFeedbackTemplate.class, answeredFeedback.getTemplateId());
            Objects.requireNonNull(feedbackTemplate);

            Course feedbackCourse = em.find(Course.class, answeredFeedback.getCourseId());
            Objects.requireNonNull(feedbackCourse);

            CourseFeedbackPersonAnswer feedbackPersonAnswer = metadata.create(CourseFeedbackPersonAnswer.class);
            feedbackPersonAnswer.setFeedbackTemplate(feedbackTemplate);
            feedbackPersonAnswer.setCompleteDate(new Date());
            feedbackPersonAnswer.setCourse(feedbackCourse);
            feedbackPersonAnswer.setPersonGroup(personGroup);
            feedbackPersonAnswer.setResponsibleRole(FeedbackResponsibleRole.LEARNER);
            feedbackPersonAnswer.setSumScore(0L);
            feedbackPersonAnswer.setAvgScore(0D);

            List<CourseFeedbackPersonAnswerDetail> details = new ArrayList<>();

            CommitContext cc = new CommitContext();
            questions.forEach(q -> {
                AttemptQuestionPojo attemptQuestionPojo = answeredFeedback.getQuestionsAndAnswers().stream()
                        .filter(qa -> UUID.fromString(qa.getQuestionId()).equals(q.getId()))
                        .findFirst()
                        .orElse(null);
                if ((attemptQuestionPojo != null) && CollectionUtils.isNotEmpty(attemptQuestionPojo.getAnswer())) {
                    attemptQuestionPojo.getAnswer().forEach(pa -> {
                        CourseFeedbackPersonAnswerDetail detail = metadata.create(CourseFeedbackPersonAnswerDetail.class);
                        Integer questionOrder = getQuestionsOrder(em, feedbackTemplate.getId(), q.getId());
                        fillDefaultPropsAnswer(detail, feedbackCourse, feedbackPersonAnswer, personGroup, feedbackTemplate, q, questionOrder);

                        addAnswer(detail, q, pa);

                        details.add(detail);
                    });
                } else {
                    CourseFeedbackPersonAnswerDetail detail = metadata.create(CourseFeedbackPersonAnswerDetail.class);
                    Integer questionOrder = getQuestionsOrder(em, feedbackTemplate.getId(), q.getId());
                    fillDefaultPropsAnswer(detail, feedbackCourse, feedbackPersonAnswer, personGroup, feedbackTemplate, q, questionOrder);

                    details.add(detail);
                }
            });
            feedbackPersonAnswer.setSumScore(details.stream().mapToInt(CourseFeedbackPersonAnswerDetail::getScore).mapToLong(v -> (long) v).reduce(0L, Long::sum));
            feedbackPersonAnswer.setAvgScore((double) (feedbackPersonAnswer.getSumScore() / questions.size()));

            cc.addInstanceToCommit(feedbackPersonAnswer);
            details.forEach(cc::addInstanceToCommit);
            dataManager.commit(cc);
        });
    }

    @Override
    public CourseSection loadCourseSectionData(UUID enrollmentId, UUID courseSectionId) {
        return persistence.callInTransaction(em -> {
            CourseSection courseSection = dataManager.load(LoadContext.create(CourseSection.class)
                    .setId(courseSectionId)
                    .setView("course.section.with.format.session"));

            TypedQuery<Long> attempts = em.createQuery("" +
                    "select count(a) " +
                    "from tsadv$CourseSectionAttempt a " +
                    "where " +
                    "   a.enrollment.id = :enrollmentId " +
                    "   and a.courseSection.id = :courseSectionId", Long.class)
                    .setParameter("enrollmentId", enrollmentId)
                    .setParameter("courseSectionId", courseSectionId);
            if (attempts.getSingleResult() == 0 && courseSection.getSectionObject().getTest() == null) {
                //TODO: почему то поиск по jpql-у не находит ничего
                Enrollment enrollment = em.createNativeQuery("" +
                        "SELECT e.* " +
                        "FROM tsadv_enrollment e " +
                        "WHERE e.id = ?1", Enrollment.class)
                        .setParameter(1, enrollmentId)
                        .getSingleResult();
                Objects.requireNonNull(enrollment);

                createAndGetAttempt(em, null, courseSection, enrollment, true);
            }
            return courseSection;
        });
    }

    @Override
    public List<BookPojo> loadBooks() {
        return persistence.callInTransaction(em ->
                em.createQuery(booksQueryConditionQuery(), Book.class)
                        .setView(Book.class, "book.with.books")
                        .getResultList()
                        .stream()
                        .map(this::parseBookToPojo)
                        .collect(Collectors.toList())
        );
    }
    protected BookPojo parseBookToPojo(Book book) {
        BookPojo bookPojo = new BookPojo();
        bookPojo.setId(book.getId().toString());
        bookPojo.setName(book.getBookNameLang1());
        bookPojo.setLogo(book.getImage() != null ? book.getImage().getId().toString() : null);

        List<SimplePojo> fileTypes = new ArrayList<>();
        if (book.getPdf() != null) {
            fileTypes.add(new SimplePojo(book.getPdf().getId().toString(), "pdf"));
        }
        if (book.getDjvu() != null) {
            fileTypes.add(new SimplePojo(book.getDjvu().getId().toString(), "djvu"));
        }
        if (book.getEpub() != null) {
            fileTypes.add(new SimplePojo(book.getEpub().getId().toString(), "epub"));
        }
        if (book.getFb2() != null) {
            fileTypes.add(new SimplePojo(book.getFb2().getId().toString(), "fb2"));
        }
        if (book.getMobi() != null) {
            fileTypes.add(new SimplePojo(book.getMobi().getId().toString(), "mobi"));
        }
        if (book.getKf8() != null) {
            fileTypes.add(new SimplePojo(book.getKf8().getId().toString(), "kf8"));
        }
        bookPojo.setFileTypes(fileTypes);

        return bookPojo;
    }

    protected String booksQueryConditionQuery() {
        return "select b " +
                "from tsadv$Book b " +
                "   left join b.image i " +
                "   left join b.pdf p " +
                "   left join b.djvu d " +
                "   left join b.epub e " +
                "   left join b.fb2 f " +
                "   left join b.mobi m " +
                "   left join b.kf8 k " +
                "where (p is not null " +
                "   OR d is not null" +
                "   OR e is not null" +
                "   OR f is not null" +
                "   OR m is not null" +
                "   OR k is not null)";
    }

    private void addAnswer(CourseFeedbackPersonAnswerDetail detail, LearningFeedbackQuestion question, String personAnswer) {
        switch (question.getQuestionType()) {
            case NUM:
            case TEXT: {
                question.getAnswers().stream()
                        .filter(a -> a.getAnswerLangValue().trim().equalsIgnoreCase(personAnswer.trim()))
                        .findFirst().ifPresent(answer -> detail.setScore(answer.getScore()));
                detail.setTextAnswer(personAnswer);
                break;
            }
            case ONE:
            case MANY: {
                question.getAnswers().stream()
                        .filter(a -> a.getId().equals(UUID.fromString(personAnswer)))
                        .findFirst().ifPresent(answer -> {
                    detail.setScore(answer.getScore());
                    detail.setAnswer(answer);
                });
                break;
            }
        }
    }

    protected Integer getQuestionsOrder(EntityManager em, UUID feedbackTemplateId, UUID questionId) {
        return em.createQuery("" +
                "select e " +
                "from tsadv$LearningFeedbackTemplateQuestion e " +
                "where e.feedbackQuestion.id = :feedbackQuestionId " +
                "   and e.feedbackTemplate.id = :feedbackTemplateId ", LearningFeedbackTemplateQuestion.class)
                .setParameter("feedbackQuestionId", questionId)
                .setParameter("feedbackTemplateId", feedbackTemplateId)
                .getFirstResult()
                .getOrder();
    }

    protected void fillDefaultPropsAnswer(
            CourseFeedbackPersonAnswerDetail detail,
            Course course,
            CourseFeedbackPersonAnswer feedbackPersonAnswer,
            PersonGroupExt personGroup,
            LearningFeedbackTemplate feedbackTemplate,
            LearningFeedbackQuestion question,
            Integer questionOrder) {
        detail.setCourse(course);
        detail.setCourseFeedbackPersonAnswer(feedbackPersonAnswer);
        detail.setFeedbackTemplate(feedbackTemplate);
        detail.setPersonGroup(personGroup);
        detail.setQuestion(question);
        detail.setQuestionOrder(questionOrder);
        detail.setScore(0);
    }

    protected boolean equalsAnswers(LearningFeedbackAnswer entityAnswer, List<String> personAnswers, LearningFeedbackQuestionType questionType) {
        switch (questionType) {
            case NUM:
            case TEXT: {
                return personAnswers.stream()
                        .map(pa -> pa.trim().toLowerCase())
                        .anyMatch(pa -> entityAnswer.getAnswerLangValue1().equalsIgnoreCase(pa));
            }
            case ONE:
            case MANY: {
                return personAnswers.stream()
                        .map(UUID::fromString)
                        .anyMatch(pa -> entityAnswer.getId().equals(pa));
            }
            default:
                return false;
        }
    }

    protected List<LearningFeedbackQuestion> getFeedbackQuestions(UUID feedbackTemplateId, String viewName) {
        return persistence.callInTransaction(em ->
                em.createQuery(
                        "select fq " +
                                "from tsadv$LearningFeedbackTemplate lft " +
                                "   inner join lft.templateQuestions tq " +
                                "   inner join tq.feedbackQuestion fq " +
                                "where lft.active = true" +
                                "   and lft.id = :lftId " +
                                "order by " +
                                "   tq.order ")
                        .setParameter("lftId", feedbackTemplateId)
                        .setView(LearningFeedbackQuestion.class, viewName)
                        .getResultList());
    }

    protected List<LearningFeedbackQuestion> getFeedbackQuestions(UUID feedbackTemplateId) {
        return getFeedbackQuestions(feedbackTemplateId, "course.feedback");
    }

    private TimePojo parseResultToDuration(PGInterval duration) {
        TimePojo durationPojo = new TimePojo();
        try {
            BeanUtils.copyProperties(durationPojo, duration);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
        return durationPojo;
    }

    private String getPersonMonthEventsQuery() {
        return "SELECT date_trunc('day', css.start_date) start_date, " +
                "       c.name, " +
                "       css.end_date - css.start_date, " +
                "       c.id as course_id " +
                "FROM tsadv_course_section_session css " +
                "         INNER JOIN tsadv_course_section cs ON cs.id = css.course_section_id AND cs.delete_ts IS NULL " +
                "         INNER JOIN tsadv_course c ON c.id = cs.course_id AND c.delete_ts IS NULL " +
                "WHERE css.delete_ts IS NULL " +
                "   AND date_trunc('month', css.start_date) = date_trunc('month', ?1::date);";
    }

    private Boolean isSucceed(Test test, int totalScore) {
        return test.getTargetScore() == null || totalScore >= test.getTargetScore();
    }

    protected String getPersonProgressQuery() {
        return "WITH temp_person_course_section_attempt AS ( " +
                "    select csa.id                       csa_id, " +
                "           csa.test_id                  test_id, " +
                "           c.name                       course_name, " +
                "           c.id                         course_id, " +
                "           csa.attempt_date, " +
                "           csa.create_ts, " +
                "           coalesce(csa.test_result, 0) test_result, " +
                "           t.target_score, " +
                "           (case when e.certificate_number is not null then e.id else null end) as                      enrollment_id " +
                "    from tsadv_enrollment e " +
                "             left join tsadv_course_section_attempt csa " +
                "                        ON e.id = csa.enrollment_id and e.delete_ts is null and csa.delete_ts is null " +
                "             inner join tsadv_course c ON e.course_id = c.id " +
                "             inner join tsadv_test t ON csa.test_id = t.id " +
                "    where csa.delete_ts is null " +
                "      and e.person_group_id = ? " +
                "      and csa.test_id is not null " +
                "), " +
                "     temp_test_last_test AS ( " +
                "         select course_id, test_id, max(attempt_date) as attempt_date, max(create_ts) as create_ts " +
                "         from temp_person_course_section_attempt " +
                "         group by course_id, test_id " +
                "     ), " +
                "     temp_last_test_info as ( " +
                "         select temp_csa.* " +
                "         from temp_test_last_test temp_t " +
                "                  inner join temp_person_course_section_attempt temp_csa " +
                "                             ON temp_csa.test_id = temp_t.test_id and temp_csa.attempt_date = temp_t.attempt_date and " +
                "                                temp_csa.create_ts = temp_t.create_ts " +
                "     ), " +
                "     temp_course_trainers as ( " +
                "         select c.id course_id, t.id trainer_id " +
                "         FROM temp_person_course_section_attempt csa " +
                "                  INNER JOIN tsadv_course c ON c.id = csa.course_id and c.delete_ts is null " +
                "                  INNER JOIN tsadv_course_section cs ON c.id = cs.course_id and cs.delete_ts is null " +
                "                  INNER JOIN tsadv_course_section_session css ON css.course_section_id = cs.id and css.delete_ts is null " +
                "                  INNER JOIN knu_trainer t ON t.id = css.trainer_knu_id and t.delete_ts is null " +
                "         GROUP BY c.id, t.id " +
                "     ), " +
                "     temp_gorup_tests as ( " +
                "         SELECT test_id " +
                "         from temp_person_course_section_attempt " +
                "         GROUP BY test_id " +
                "     ), " +
                "     temp_max_score as ( " +
                "         select t.test_id, count(q) max_score " +
                "         from temp_gorup_tests t " +
                "                  INNER JOIN tsadv_test_section ts ON ts.test_id = t.test_id " +
                "                  INNER JOIN tsadv_question_bank qb ON qb.id = ts.question_bank_id " +
                "                  INNER JOIN tsadv_question q ON q.bank_id = qb.id " +
                "         GROUP BY t.test_id " +
                "     ), " +
                "     temp_fio as ( " +
                "         SELECT ct.course_id, string_agg(concat(last_name, ' ', first_name), ', ') fio " +
                "         FROM temp_course_trainers ct " +
                "                  INNER JOIN knu_trainer t ON ct.trainer_id = t.id " +
                "         GROUP BY ct.course_id " +
                "     ), " +
                "     temp_test_failed_tries as (select temp_person_course_section_attempt.course_id, sum(1) failed_tries " +
                "                                from temp_person_course_section_attempt " +
                "                                where coalesce(test_result, 0) < target_score " +
                "                                group by temp_person_course_section_attempt.course_id) " +
                "SELECT row_number() over ()         as line_num, " +
                "       lti.*, " +
                "       coalesce(ft.failed_tries, 0) as failed_tries, " +
                "       temp_fio.fio, " +
                "       ms.max_score " +
                "FROM temp_last_test_info lti " +
                "         LEFT join temp_test_failed_tries ft on lti.course_id = ft.course_id " +
                "         LEFT join temp_max_score ms on ms.test_id = lti.test_id " +
                "         LEFT join temp_person_course_section_attempt temp_csa ON temp_csa.csa_id = lti.csa_id " +
                "         LEFT JOIN temp_fio ON temp_fio.course_id = lti.course_id " +
                "ORDER BY " +
                "   lti.attempt_date DESC";
    }

    protected PersonAnswer createPersonAnswer(EntityManager em, Question question, CourseSectionAttempt csa, String answer, TestSection testSection) {
        PersonAnswer personAnswer = metadata.create(PersonAnswer.class);
        personAnswer.setAttempt(csa);
        personAnswer.setQuestion(question);
        personAnswer.setAnswer(answer);
        personAnswer.setTestSection(testSection);
        personAnswer.setAnswered(true);
        em.persist(personAnswer);
        return personAnswer;
    }

    private CourseSectionAttempt createAndGetAttempt(EntityManager em, Test test, UUID courseSectionObjectId) {
        Enrollment enrollment = em.createQuery("" +
                "select e " +
                "from tsadv$Enrollment e " +
                "   join e.course c " +
                "   join c.sections cs " +
                "   join cs.sectionObject cso " +
                "where e.personGroup.id = :personGroup " +
                "   and cso.id = :csoId ", Enrollment.class)
                .setParameter("csoId", courseSectionObjectId)
                .setParameter("personGroup", Objects.requireNonNull(userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID)))
                .setView(Enrollment.class, "enrollment.with.section.course.object")
                .getSingleResult();
        return createAndGetAttempt(em, test, getCourseSection(courseSectionObjectId), enrollment, true);
    }

    private TestPojo getTestPojo(EntityManager em, Test test, UUID attemptId) {
        if (test == null) {
            return null;
        }

        TestPojo testPojo = new TestPojo();
        testPojo.setAttemptId(attemptId);
        testPojo.setTimer(test.getTimer());

        testPojo.setTestSections(test.getSections().stream().map(testSection -> {
            TestSectionPojo testSectionPojo = new TestSectionPojo();
            testSectionPojo.setId(testSection.getId().toString());
            testSectionPojo.setName(testSection.getSectionName());

            testSectionPojo.setQuestionsAndAnswers(testSection.getQuestions().stream().map(questionInSection -> {
                Question question = questionInSection.getQuestion();

                QuestionPojo questionPojo = new QuestionPojo();
                questionPojo.setId(question.getId());
                questionPojo.setText(question.getText());
                questionPojo.setType(question.getType());

                if (question.getType().equals(QuestionType.ONE) || question.getType().equals(QuestionType.MANY)) {
                    List<Answer> answers = em.createQuery(
                            "select a " +
                                    "from tsadv$Question q " +
                                    "   join q.answers a " +
                                    "where q.id = :questionId ", Answer.class)
                            .setParameter("questionId", question.getId()).setView(Answer.class, View.LOCAL).getResultList();

                    questionPojo.setAnswers(answers.stream().map(answer -> {
                        AnswerPojo answerPojo = new AnswerPojo();
                        answerPojo.setId(answer.getId());
                        answerPojo.setText(answer.getAnswer());
                        return answerPojo;
                    }).collect(Collectors.toList()));
                }
                return questionPojo;
            }).collect(Collectors.toList()));
            return testSectionPojo;
        }).collect(Collectors.toList()));
        return testPojo;
    }

    private CourseSectionAttempt createAndGetAttempt(EntityManager em, Test test, CourseSection courseSection, Enrollment enrollment, boolean isSuccess) {
        final boolean isActiveAttempt = false;

        CourseSectionAttempt attempt = metadata.create(CourseSectionAttempt.class);
        attempt.setCourseSection(courseSection);
        attempt.setAttemptDate(new Date());
        attempt.setActiveAttempt(isActiveAttempt);
        attempt.setSuccess(isSuccess);
        attempt.setEnrollment(enrollment);
        attempt.setTest(test);
        em.persist(attempt);
        return attempt;
    }

    private CourseSection getCourseSection(UUID courseSectionObjectId) {
        return persistence.callInTransaction(em -> em.createNativeQuery("" +
                "SELECT cs.id " +
                "FROM tsadv_course_section_object cso " +
                "   inner join tsadv_course_section cs ON cs.section_object_id = cso.id and cs.delete_ts is null and cso.id = ?1", CourseSection.class)
                .setParameter(1, courseSectionObjectId)
                .getSingleResult());
    }

    private Test getTestByCourseSectionObject(EntityManager em, UUID courseSectionObjectId) {
        return getTestByCourseSectionObject(em, courseSectionObjectId, View.LOCAL);
    }

    private Test getTestByCourseSectionObject(EntityManager em, UUID courseSectionObjectId, String viewName) {
        return em.createQuery(
                "select t " +
                        "from tsadv$CourseSectionObject cso " +
                        "   join cso.test t " +
                        "where cso.id = :courseSectionObjectId", Test.class)
                .setParameter("courseSectionObjectId", courseSectionObjectId).setView(Test.class, viewName).getSingleResult();
    }

    private void fillQueryParameters(List<ConditionPojo> conditions, Query query, String queryString, Class<Course> courseClass) {
        StringBuilder conditionQuery = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < conditions.size(); i++) {
            ConditionPojo condition = mapper.convertValue(conditions.get(i), ConditionPojo.class);
            int parameter = i + 1;

            if (getOperator(condition.getOperator()).equalsIgnoreCase("like")) {
                fillConditionQuery(conditionQuery, condition.getProperty(), (String) condition.getValue(), condition.getOperator(), Course.class);
            } else {
                conditionQuery.append(" AND ").append(condition.getProperty()).append(" ").append(getOperator(condition.getOperator())).append(" ?").append(parameter);
                query.setParameter(parameter, condition.getValue());
            }
        }
        query.setQueryString(String.format(queryString, conditionQuery.toString()));
    }

    private boolean hasAttempts(CourseSectionPojo courseSectionPojo, Enrollment enrollment) {
        return hasAttempts(dataManager.load(LoadContext.create(CourseSection.class).setId(UUID.fromString(courseSectionPojo.getId()))), enrollment);
    }

    private boolean hasAttempts(CourseSection courseSection, Enrollment enrollment) {
        CourseSection cs = dataManager.reload(courseSection, "courseSection-checkAttempts");
        return cs.getCourseSectionAttempts() != null
                && enrollment != null
                && cs.getCourseSectionAttempts().stream().anyMatch(courseSectionAttempt -> enrollment.equals(courseSectionAttempt.getEnrollment()));
    }

    private void fillConditionQuery(StringBuilder queryBuilder, String property, String value, String operator, Class<?> conditionEntityClass) {
        Class<?> propertyClass = Arrays.stream(conditionEntityClass.getDeclaredFields()).filter(el -> el.getName().equalsIgnoreCase(property)).findFirst().get().getType();

        queryBuilder.append(" AND ").append(getPropertyWrapper(property, propertyClass))
                .append(" ")
                .append(getOperator(operator)).append(valueWrapper(value, propertyClass));
    }

    private String getPropertyWrapper(String property, Class<?> propertyClass) {
        if (propertyClass == String.class) {
            return "lower(" + property + ") ";
        } else {
            return property;
        }
    }

    protected String getOperator(String clientOperator) {
        switch (clientOperator) {
            case "contains": {
                return "like";
            }
            default: {
                return "";
            }
        }
    }

    protected String valueWrapper(String value, Class<?> propertyClass) {
        if (propertyClass == String.class) {
            return " lower('%" + value + "%')";
        } else {
            return "";
        }
    }
}