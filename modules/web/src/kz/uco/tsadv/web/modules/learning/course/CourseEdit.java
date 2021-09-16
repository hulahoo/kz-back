package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.web.gui.facets.NotificationFacetProvider;
import com.haulmont.reports.app.service.ReportService;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.ExtAppPropertiesConfig;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningType;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.entity.WindowProperty;
import kz.uco.uactivity.service.ActivityService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@UiController("tsadv$Course.edit")
@UiDescriptor("course-edit.xml")
@EditedEntityContainer("courseDc")
public class CourseEdit extends StandardEditor<Course> {

//    @Inject
//    protected FileUploadField imageUpload;

//    @Inject
//    protected Embedded courseImage;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected InstanceContainer<Course> courseDc;
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected InstanceLoader<Course> courseDl;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected CollectionContainer<Enrollment> enrollmentDc;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionLoader<Enrollment> enrollmentDl;
    @Inject
    protected CollectionContainer<CourseSchedule> courseScheduleDc;
    @Inject
    protected CollectionLoader<CourseSchedule> courseScheduleDl;
    @Inject
    protected Table<CourseSchedule> courseScheduleTable;
    @Inject
    protected CollectionLoader<DicLearningType> dicLearningTypesDl;
    @Inject
    protected Table<Homework> homeworkTable;
    @Inject
    protected CollectionLoader<Homework> homeworkDl;
    @Inject
    protected CollectionLoader<StudentHomework> studentHomeworkDl;
    @Inject
    protected CollectionContainer<Homework> homeworkDc;
    @Inject
    protected Table<StudentHomework> studentHomeworkTable;
    @Named("studentHomeworkTable.create")
    protected CreateActionExt studentHomeworkTableCreate;
    @Inject
    protected DataGrid<Enrollment> enrollmentsTable;
    @Inject
    protected ReportService reportService;
    @Inject
    protected CollectionLoader<CourseReview> courseReviewDl;
    @Inject
    protected GroupTable<CourseReview> courseReviewTable;
    @Inject
    protected Table<CoursePreRequisition> preRequisitionTable;
    @Inject
    protected CollectionLoader<CoursePreRequisition> preRequisitionDl;
    @Inject
    protected ExtAppPropertiesConfig extAppPropertiesConfig;
    @Inject
    protected NotificationFacetProvider notificationFacetProvider;
    @Inject
    protected NotificationFacet notification;
    @Inject
    private NotificationService notificationService;
    @Inject
    private CommonService commonService;
    @Inject
    private FrontConfig frontConfig;
    @Inject
    private GlobalConfig globalConfig;
    @Inject
    private NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    private ActivityService activityService;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        courseDl.load();
        enrollmentDl.setParameter("course", courseDc.getItem());
        enrollmentDl.load();
        courseScheduleDl.setParameter("course", courseDc.getItem());
        courseScheduleDl.load();
        dicLearningTypesDl.load();
        homeworkDl.setParameter("course", courseDc.getItem());
        homeworkDl.load();
        courseReviewDl.setParameter("course", courseDc.getItem());
        courseReviewDl.load();
        preRequisitionDl.setParameter("course", courseDc.getItem());
        preRequisitionDl.load();
    }


    @Subscribe
    protected void onInitEntity(InitEntityEvent<Course> event) {
//        PickerField.LookupAction lookupAction = partyField.getLookupAction();
//        lookupAction.setLookupScreen("base$PartyExt.browse");
//        lookupAction.setLookupScreenParams(ParamsMap.of(PartyExtBrowse.TRAINING_PROVIDER, true));

//        preRequisitionTable.addAction(new BaseAction("selectPreRequisition") {
//            @Override
//            public void actionPerform(Component component) {
//                openLookup("tsadv$Course.browse", new Window.Lookup.Handler() {
//                    @Override
//                    public void handleLookup(Collection items) {
//                        for (Course selectedCourse : (Collection<Course>) items) {
//                            CoursePreRequisition coursePreRequisition = metadata.create(CoursePreRequisition.class);
//                            coursePreRequisition.setCourse(courseDs.getItem());
//                            coursePreRequisition.setRequisitionCourse(selectedCourse);
//                            preRequisitionDs.addItem(coursePreRequisition);
//                        }
//                    }
//                }, WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
//                    StringBuilder sb = new StringBuilder("'" + courseDs.getItem().getId() + "',");
//                    for (CoursePreRequisition coursePreRequisition : preRequisitionDs.getItems()) {
//                        sb.append("'").append(coursePreRequisition.getRequisitionCourse().getId()).append("',");
//                    }
//                    put(StaticVariable.EXIST_COURSE, sb.toString().substring(0, sb.toString().length() - 1));
//                }});
//            }
//        });
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        Course course = courseDc.getItem();
        if (PersistenceHelper.isNew(course)) {
            FileDescriptor fileDescriptor = dataManager.load(FileDescriptor.class)
                    .query("select e from sys$FileDescriptor e " +
                            " where e.id = :fileId")
                    .parameter("fileId", extAppPropertiesConfig.getDefaultLogo() != null
                            && !extAppPropertiesConfig.getDefaultLogo().isEmpty()
                            ? UUID.fromString(extAppPropertiesConfig.getDefaultLogo())
                            : null)
                    .list().stream().findFirst().orElse(null);
            course.setLogo(fileDescriptor);
        }
//        Utils.getCourseImageEmbedded(course, null, courseImage);

        homeworkTable.addSelectionListener(homeworkSelectionEvent -> {
            if (homeworkSelectionEvent.getSelected().size() > 0) {
                studentHomeworkDl.setParameter("homework", homeworkTable.getSingleSelected());
                studentHomeworkDl.load();
                studentHomeworkTableCreate.setEnabled(true);
            } else {
                studentHomeworkDl.setParameter("homework", homeworkTable.getSingleSelected());
                studentHomeworkDl.load();
                studentHomeworkTableCreate.setEnabled(false);
            }
        });
    }


//    @Override
//    public void init(Map<String, Object> params) {
//        super.init(params);
//
//        PickerField.LookupAction lookupAction = partyField.getLookupAction();
//        lookupAction.setLookupScreen("base$PartyExt.browse");
//        lookupAction.setLookupScreenParams(ParamsMap.of(PartyExtBrowse.TRAINING_PROVIDER, true));
//
//
//        imageUpload.setUploadButtonCaption("");
//        imageUpload.setClearButtonCaption("");
//
//        imageUpload.addFileUploadSucceedListener(this::fileUploadSucceed);
//
//        imageUpload.addBeforeValueClearListener(this::beforeValueClearPerformed);
//
//        preRequisitionTable.addAction(new BaseAction("selectPreRequisition") {
//            @Override
//            public void actionPerform(Component component) {
//                openLookup("tsadv$Course.browse", new Lookup.Handler() {
//                    @Override
//                    public void handleLookup(Collection items) {
//                        for (Course selectedCourse : (Collection<Course>) items) {
//                            CoursePreRequisition coursePreRequisition = metadata.create(CoursePreRequisition.class);
//                            coursePreRequisition.setCourse(courseDs.getItem());
//                            coursePreRequisition.setRequisitionCourse(selectedCourse);
//                            preRequisitionDs.addItem(coursePreRequisition);
//                        }
//                    }
//                }, WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
//                    StringBuilder sb = new StringBuilder("'" + courseDs.getItem().getId() + "',");
//                    for (CoursePreRequisition coursePreRequisition : preRequisitionDs.getItems()) {
//                        sb.append("'").append(coursePreRequisition.getRequisitionCourse().getId()).append("',");
//                    }
//                    put(StaticVariable.EXIST_COURSE, sb.toString().substring(0, sb.toString().length() - 1));
//                }});
//            }
//        });
//    }

//    @Override
//    protected void initNewItem(Course item) {
//        super.initNewItem(item);
//        item.setSelfEnrollment(false);
//    }

//    protected void checkAccessEdit(Course course) {
        /*boolean access = course != null && !hasEnrollments(course) && !hasCertificationEnrollments(course);

        richTextArea.setEditable(access);
        fieldGroup.setEditable(access);
        imageUpload.setVisible(access);

        if (access) {
            Boolean activeFlag = getItem().getActiveFlag();
            enableDisableActions(activeFlag == null || !activeFlag);
        } else {
            enableDisableActions(false);
        }*/

//        enableDisableActions(true);
//    }

//    protected void enableDisableActions(boolean enable) {
//        ComponentsHelper.walkComponents(tabSheet, new ComponentVisitor() {
//            @Override
//            public void visit(Component component, String name) {
//                if (component.getClass().getSimpleName().equalsIgnoreCase("WebTable")) {
//                    Table table = ((Table) component);
//                    table.getActions().forEach(new Consumer<Action>() {
//                        @Override
//                        public void accept(Action action) {
//                            action.setEnabled(enable);
//                        }
//                    });
//
//                    table.getButtonsPanel().setVisible(enable);
//                }
//            }
//        });
//    }

//    protected boolean hasEnrollments(Course course) {
//        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);
//        LoadContext.Query query = LoadContext.createQuery(
//                "select e from tsadv$Enrollment e " +
//                        "where e.course.id = :cId");
//        query.setParameter("cId", course.getId());
//        loadContext.setQuery(query);
//        return dataManager.getCount(loadContext) > 0;
//    }

//    protected boolean hasCertificationEnrollments(Course course) {
//        LoadContext<CertificationEnrollment> loadContext = LoadContext.create(CertificationEnrollment.class);
//        LoadContext.Query query = LoadContext.createQuery(
//                "select e from tsadv$CertificationEnrollment e join e.certification c " +
//                        "where c.course.id = :cId");
//        query.setParameter("cId", course.getId());
//        loadContext.setQuery(query);
//        return dataManager.getCount(loadContext) > 0;
//    }

//    protected void fileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
//        Course course = courseDc.getItem();
//        if (imageUpload.getFileContent() != null) {
//            try {
//                course.setLogo(CommonUtils.resize(imageUpload.getFileContent(), IMAGE_SIZE.XSS));
//                Utils.getCourseImageEmbedded(course, null, courseImage);
//            } catch (IOException e) {
//                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
//                        .withCaption(messageBundle.getMessage("fileUploadErrorMessage")).show();
//            }
//        }
//        imageUpload.setValue(null);
//    }

//    protected void beforeValueClearPerformed(FileUploadField.BeforeValueClearEvent beforeEvent) {
//        beforeEvent.preventClearAction();
//        dialogs.createOptionDialog()
//                .withCaption(messageBundle.getMessage("confirmation"))
//                .withMessage(messageBundle.getMessage("aUSure"))
//                .withType(Dialogs.MessageType.CONFIRMATION)
//                .withActions(
//                        new DialogAction(DialogAction.Type.YES)
//                                .withHandler(actionPerformedEvent -> {
//                                            courseDc.getItem().setLogo(null);
//                                            courseImage.resetSource();
//                                        }
//                                ),
//                        new DialogAction(DialogAction.Type.NO))
//                .show();
//    }

    @Subscribe("enrollmentsTable.create")
    protected void onEnrollmentsTableCreate(Action.ActionPerformedEvent event) {
        List<Enrollment> changedEnrollmentList = new ArrayList<>();
        CommitContext newCommitContext = new CommitContext();
        screenBuilders.lookup(PersonExt.class, this)
                .withScreenId("base$PersonForKpiCard.browse")
                .withSelectHandler(personList -> {
                    personList.forEach(personExt -> {
                        boolean isNew = true;
                        List<Enrollment> enrollmentList = dataManager.load(Enrollment.class)
                                .query("select e from tsadv$Enrollment e " +
                                        " where e.course = :course")
                                .parameter("course", courseDc.getItem())
                                .view("enrollment.for.course")
                                .list();
                        if (!enrollmentList.isEmpty()) {
                            for (Enrollment enrollment : enrollmentList) {
                                if (enrollment.getPersonGroup().equals(personExt.getGroup())) {
                                    isNew = false;
                                    if (courseScheduleDc.getItems().size() == 1) {
                                        enrollment.setCourseSchedule(courseScheduleDc.getItems().get(0));
                                    } else if (courseScheduleDc.getItems().size() == 0) {
                                        enrollment.setCourseSchedule(null);
                                    }
                                    newCommitContext.addInstanceToCommit(enrollment);
                                    changedEnrollmentList.add(enrollment);
                                    break;
                                }
                            }
                        }
                        if (isNew) {
                            Enrollment enrollment = metadata.create(Enrollment.class);
                            enrollment.setCourse(courseDc.getItem());
                            enrollment.setPersonGroup(personExt.getGroup());
                            enrollment.setStatus(EnrollmentStatus.APPROVED);
                            enrollment.setDate(BaseCommonUtils.getSystemDate());
                            if (courseScheduleDc.getItems().size() == 1) {
                                enrollment.setCourseSchedule(courseScheduleDc.getItems().get(0));
                            }
                            changedEnrollmentList.add(enrollment);
                            newCommitContext.addInstanceToCommit(enrollment);
                        }
                    });
                    if (courseScheduleDc.getItems().size() == 1 || courseScheduleDc.getItems().size() == 0) {
                        dataManager.commit(newCommitContext);
                        enrollmentDl.load();
                    }
                }).build().show()
                .addAfterCloseListener(afterCloseEvent -> {
                    if (courseScheduleDc.getItems().size() > 1) {
                        screenBuilders.lookup(CourseSchedule.class, this)
                                .withScreenId("tsadv_CourseSchedule.browse")
                                .withOptions(new MapScreenOptions(ParamsMap.of("course", courseDc.getItem())))
                                .withSelectHandler(courseSchedules -> {
                                    courseSchedules.forEach(courseSchedule ->
                                            changedEnrollmentList.forEach(enrollment -> {
                                                enrollment.setCourseSchedule(courseSchedule);
                                                newCommitContext.addInstanceToCommit(enrollment);
                                            }));
                                    dataManager.commit(newCommitContext);
                                    enrollmentDl.load();
                                })
                                .build().show();
                    } else if (courseScheduleDc.getItems().size() == 0) {
//                        notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
//                                .withCaption(messageBundle.getMessage("notCourseSchedule")
//                                        + " " + courseDc.getItem().getName())
//                                .show();
                    }
                });
    }

    @Subscribe("courseScheduleTable.create")
    protected void onCourseScheduleTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(courseScheduleTable)
                .newEntity()
                .withInitializer(courseSchedule -> courseSchedule.setCourse(courseDc.getItem())).build().show()
                .addAfterCloseListener(afterCloseEvent -> courseScheduleDl.load());
    }

    @Subscribe("homeworkTable.create")
    protected void onHomeworkTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(homeworkTable)
                .newEntity()
                .withInitializer(homework -> homework.setCourse(courseDc.getItem())).build().show()
                .addAfterCloseListener(afterCloseEvent -> homeworkDl.load());
    }

    @Subscribe("studentHomeworkTable.create")
    protected void onStudentHomeworkTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(studentHomeworkTable)
                .newEntity()
                .withInitializer(studentHomework -> studentHomework.setHomework(homeworkTable.getSingleSelected()))
                .build().show()
                .addAfterCloseListener(afterCloseEvent -> studentHomeworkDl.load());
    }

    @Subscribe("enrollmentsTable.generateCertificate")
    protected void onEnrollmentsTableGenerateCertificate(Action.ActionPerformedEvent event) {
        List<Enrollment> completedEnrollment = new ArrayList<>();
        List<Enrollment> unCompletedEnrollment = new ArrayList<>();
        CommitContext commitContext = new CommitContext();
        enrollmentsTable.getSelected().forEach(enrollment -> {
            if (EnrollmentStatus.COMPLETED.equals(enrollment.getStatus())) {
                completedEnrollment.add(enrollment);
                CourseCertificate courseCertificate = enrollment.getCourse().getCertificate() != null
                        && !enrollment.getCourse().getCertificate().isEmpty()
                        ? enrollment.getCourse().getCertificate().get(0)
                        : null;
                if (courseCertificate != null) {

                    FileDescriptor fd = reportService.createAndSaveReport(courseCertificate.getCertificate(),
                            ParamsMap.of("enrollment", enrollment), enrollment.getCourse().getName());

                    if (fd != null) {
                        List<EnrollmentCertificateFile> ecfList = dataManager.load(EnrollmentCertificateFile.class)
                                .query("select e from tsadv$EnrollmentCertificateFile e " +
                                        " where e.enrollment = :enrollment ")
                                .parameter("enrollment", enrollment)
                                .view("enrollmentCertificateFile.with.certificateFile")
                                .list();
                        ecfList.forEach(commitContext::addInstanceToRemove);

                        EnrollmentCertificateFile ecf = metadata.create(EnrollmentCertificateFile.class);
                        ecf.setCertificateFile(fd);
                        ecf.setEnrollment(enrollment);
                        commitContext.addInstanceToCommit(ecf);
                    }
                }
            } else {
                unCompletedEnrollment.add(enrollment);
            }
        });
        dataManager.commit(commitContext);
        if (!completedEnrollment.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for (Enrollment enrollment : completedEnrollment) {
                result.append(enrollment.getPersonGroup().getFullName()).append(", ");

            }
            result.append(";");
            notification.setCaption(String.format(messageBundle.getMessage("certificateGenerated"),
                    result.toString().replaceAll(", ;", "")));
            notification.setType(Notifications.NotificationType.HUMANIZED);
            notification.setPosition(Notifications.Position.BOTTOM_RIGHT);
            notification.show();
        }
        if (!unCompletedEnrollment.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for (Enrollment enrollment : unCompletedEnrollment) {
                result.append(enrollment.getPersonGroup().getFullName()).append(", ");

            }
            result.append(";");
            notification.setCaption(String.format(messageBundle.getMessage("uncompletedEnrollmentedPerson"),
                    result.toString().replaceAll(", ;", "")));
            notification.setType(Notifications.NotificationType.WARNING);
            notification.setPosition(Notifications.Position.MIDDLE_CENTER);
            notification.show();
        }
    }

//    @Subscribe("imageUpload")
//    protected void onImageUploadFileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
//        fileUploadSucceed(event);
//    }
//
//    @Subscribe("imageUpload")
//    protected void onImageUploadBeforeValueClear(FileUploadField.BeforeValueClearEvent event) {
//        beforeValueClearPerformed(event);
//    }

    @Subscribe("courseReviewTable.create")
    protected void onCourseReviewTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(courseReviewTable)
                .newEntity()
                .withInitializer(courseReview -> courseReview.setCourse(courseDc.getItem())).build().show()
                .addAfterCloseListener(afterCloseEvent -> courseReviewDl.load());
    }

    @Subscribe("preRequisitionTable.create")
    protected void onPreRequisitionTableCreate(Action.ActionPerformedEvent event) {
        CommitContext commitContext = new CommitContext();
        screenBuilders.lookup(Course.class, this)
                .withSelectHandler(courses -> {
                    courses.forEach(course -> {
                        CoursePreRequisition coursePreRequisition = metadata.create(CoursePreRequisition.class);
                        coursePreRequisition.setCourse(courseDc.getItem());
                        coursePreRequisition.setRequisitionCourse(course);
                        commitContext.addInstanceToCommit(coursePreRequisition);
                    });
                    dataManager.commit(commitContext);
                    preRequisitionDl.load();
                }).build().show()
                .addAfterCloseListener(afterCloseEvent -> preRequisitionDl.load());
    }

    protected ActivityType getActivityType() {
        return dataManager.load(ActivityType.class)
                .query("select e from uactivity$ActivityType e where e.code = 'NOTIFICATION'")
                .view(new View(ActivityType.class)
                        .addProperty("code")
                        .addProperty("windowProperty",
                                new View(WindowProperty.class).addProperty("entityName").addProperty("screenName")))
                .one();
    }

    @Subscribe("enrollmentsTable.addAttempt")
    public void onEnrollmentsTableAddAttempt(Action.ActionPerformedEvent event) {
        Map<String, Object> userParams = new HashMap<>();
        CommitContext commitContext = new CommitContext();
        for (Enrollment enrollment : enrollmentsTable.getSelected()) {
            for (CourseSection section : courseDc.getItem().getSections()) {
                if (section.getSectionObject() != null && section.getSectionObject().getObjectType() != null
                        && "TEST".equals(section.getSectionObject().getObjectType().getCode())) {
                    Test test = section.getSectionObject().getTest();
                    CourseSectionAttempt courseSectionAttempt = dataManager.load(CourseSectionAttempt.class)
                            .query("select e from tsadv$CourseSectionAttempt e " +
                                    " where e.enrollment = :enrollment " +
                                    " and e.test = :test " +
                                    " and e.activeAttempt = true ")
                            .parameter("enrollment", enrollment)
                            .parameter("test", test)
                            .view("courseSectionAttempt.edit")
                            .list().stream().findFirst().orElse(null);
                    if (courseSectionAttempt != null) {
                        courseSectionAttempt.setActiveAttempt(false);
                        commitContext.addInstanceToCommit(courseSectionAttempt);
                    }
                }
            }
            TsadvUser user = commonService.getEntity(TsadvUser.class,
                    "select distinct su " +
                            "from tsadv$UserExt su " +
                            "where su.personGroup.id = :id ",
                    ParamsMap.of("id", enrollment.getPersonGroup().getId()),
                    "_base");
            String courseLink = "<a href=\"" + globalConfig.getWebAppUrl() + "/open?screen=" +
                    "tsadv$Course.edit" +
                    "&item=" + "tsadv$Course" + "-" + enrollment.getCourse().getId() +
                    "\" target=\"_blank\">%s " + "</a>";
            String myCourseLink = "<a href=\"" + frontConfig.getFrontAppUrl()
                    + "/course/" +enrollment.getCourse().getId()
                    + "\" target=\"_blank\">%s " + "</a>";
            userParams.put("personFullNameRu", enrollment.getPersonGroup().getPerson().getFirstName() + " " + enrollment.getPersonGroup().getPerson().getLastName());
            userParams.put("personFullNameEn", enrollment.getPersonGroup().getPerson().getFirstNameLatin() != null
                    && !enrollment.getPersonGroup().getPerson().getFirstNameLatin().isEmpty() &&
                    enrollment.getPersonGroup().getPerson().getLastNameLatin() != null
                    && !enrollment.getPersonGroup().getPerson().getLastNameLatin().isEmpty()
                    ? enrollment.getPersonGroup().getPerson().getFirstNameLatin() + " " + enrollment.getPersonGroup().getPerson().getLastNameLatin()
                    : enrollment.getPersonGroup().getPerson().getFirstName() + " " + enrollment.getPersonGroup().getPerson().getLastName());
            userParams.put("courseName", String.format(myCourseLink, enrollment.getCourse().getName()));
            activityService.createActivity(
                    user,
                    user,
                    getActivityType(),
                    StatusEnum.active,
                    "description",
                    null,
                    new Date(),
                    null,
                    null,
                    user.getId(),
                    "tdc.additional.attempt",
                    userParams);
            notificationSenderAPIService.sendParametrizedNotification("tdc.additional.attempt", user, userParams);
        }
        dataManager.commit(commitContext);
        notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                .withCaption(messageBundle.getMessage("attemptAdded")).show();
    }
}