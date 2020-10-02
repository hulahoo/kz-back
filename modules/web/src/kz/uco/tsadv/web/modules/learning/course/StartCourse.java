package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningCenter;
import kz.uco.tsadv.modules.learning.enums.ContentType;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackUsageType;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplate;
import kz.uco.tsadv.service.CourseService;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

import static kz.uco.tsadv.modules.learning.dictionary.DicCourseFormat.ONLINE;
import static kz.uco.tsadv.modules.learning.dictionary.DicCourseFormat.WEBINAR;

public class StartCourse extends AbstractWindow {

    protected static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @Inject
    protected GroupBoxLayout feedbackTemplateGroupBox;
    @Inject
    protected VBoxLayout feedbackTemplatesVBox;
    @Inject
    protected Embedded courseLogo;

    @Inject
    protected CssLayout courseLogoWrapper;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected CourseService courseService;

    @Inject
    protected HBoxLayout wrapper;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Label courseName;

    @Inject
    protected Label courseSectionLabel;

    @Inject
    protected GroupBoxLayout sectionGroupBox;

    @Inject
    protected Button startSection;

    @Inject
    protected VBoxLayout testData;

    @Inject
    protected VBoxLayout additionalInfo;

    @Inject
    protected VBoxLayout courseSections;

    protected CourseSection selectedCourseSection;
    protected LearningFeedbackTemplate feedbackTemplate;
    protected Enrollment enrollment;
    @Inject
    protected CommonService commonService;
    @Inject
    protected UuidSource uuidSource;
    @Inject
    protected CommonConfig commonConfig;
    @Inject
    protected ExportDisplay exportDisplay;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("enrollment")) {
            this.enrollment = (Enrollment) params.get("enrollment");

            if (enrollment != null) {
                enrollment = dataManager.reload(this.enrollment, "enrollment.for.start.test");
                Course course = this.enrollment.getCourse();

                Utils.getCourseImageEmbedded(course, "60px", courseLogo);

                courseName.setValue(course.getName());

                fillCourseSections(course);

                fillCourse(course);

//                courseLogoWrapper.addLayoutClickListener(new LayoutClickListener() {
//                    @Override
//                    public void layoutClick(LayoutClickEvent event) {
//                        fillCourse(course);
//                    }
//                });
            }
        }
    }

    protected void fillCourse(Course course) {
        wrapper.removeAll();
        wrapper.add(createLabel(course.getName(), "course-section-name"));
        courseSectionLabel.setValue("");

        Label content = createLabel(course.getDescription(), "course-section-description");
        content.setHtmlEnabled(true);
        additionalInfo.removeAll();
        testData.removeAll();
        testData.add(content);
    }

    public void startSection() {
        if (selectedCourseSection != null) {
            if ("knu".equals(commonConfig.getMainApp())) {
                LearningObject learningObjectKnu = null;
                try {
                    learningObjectKnu = selectedCourseSection
                            .getSectionObject().getContent();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (learningObjectKnu != null) {
                    learningObjectKnu = dataManager.reload(learningObjectKnu, "learningObject.browse");
                    if (learningObjectKnu.getContentType() != null) {
                        if (ContentType.FILE.equals(learningObjectKnu.getContentType())){
                            exportDisplay.show(learningObjectKnu.getFile());
                            createCourseSectionAttempt();
                            return;

                        } else if (!ContentType.TEXT.equals(learningObjectKnu.getContentType())){
                            AbstractWindow item = openWindow(
                                    "learning-object-player",
                                    WindowManager.OpenType.DIALOG,
                                    ParamsMap.of("item", learningObjectKnu));

                            item.addCloseListener(actionId -> createCourseSectionAttempt());
                            return;
                        }
                    }
                }
            }
            List<CourseSection> notPassedCourseSectionList = notPassedCourseSection(selectedCourseSection);
            if (notPassedCourseSectionList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (CourseSection courseSection : notPassedCourseSectionList) {
                    sb.append(courseSection.getSectionName());
                    sb.append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                String notPassedCourseSection = sb.toString();
                showNotification(getMessage("msg.warning.title"),
                        String.format(getMessage("section.order.error"), notPassedCourseSection),
                        NotificationType.TRAY);
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("courseSection", this.selectedCourseSection);
            params.put("enrollment", enrollment);

            CourseSectionObject object = selectedCourseSection.getSectionObject();
            String formatCode = selectedCourseSection.getFormat().getCode();

            if (formatCode != null &&
                    (formatCode.equalsIgnoreCase(ONLINE) ||
                            formatCode.equalsIgnoreCase(WEBINAR)
                    )) {
                if (object.getObjectType().getCode().equalsIgnoreCase("test")) {
                    Test test = object.getTest();

                    /**
                     * Get last success attempt for calculate days between
                     * */
                    CourseSectionAttempt lastAttempt = getLastActiveAttempt(selectedCourseSection);
                    if (lastAttempt != null) {
                        Date currentDate = new Date();
                        long diff = currentDate.getTime() - lastAttempt.getAttemptDate().getTime();

                        long diffDays = diff / (24 * 60 * 60 * 1000);

                        if (diffDays < test.getDaysBetweenAttempts()) {
                            showNotification(
                                    getMessage("msg.warning.title"),
                                    String.format(getMessage("test.must.start.after"), test.getDaysBetweenAttempts()),
                                    NotificationType.TRAY);
                            return;
                        }
                    }

                    if (!checkAttempt(test, selectedCourseSection, lastAttempt)) {
                        showNotification(getMessage("attempt.full"));
                        return;
                    }

                    //TODO: params.put("lastAttempt", lastAttempt);
                }

                openOnlineSection(params, "start-online-section");
            } else {
                showNotification("Не реализован в ДЕМО!");
            }
        } else if (feedbackTemplate != null) {
            openWindow("start-fill-feedback",
                    WindowManager.OpenType.THIS_TAB,
                    ParamsMap.of(StartFillFeedback.ENROLLMENT, enrollment,
                            StartFillFeedback.FEEDBACK_TEMPLATE, feedbackTemplate));
        }
    }

    protected void createCourseSectionAttempt() {
        CourseSectionAttempt newCourseSectionAttempt = metadata.create(CourseSectionAttempt.class);
        newCourseSectionAttempt.setId(uuidSource.createUuid());
        newCourseSectionAttempt.setEnrollment(enrollment);
        newCourseSectionAttempt.setAttemptDate(CommonUtils.getSystemDate());
        newCourseSectionAttempt.setSuccess(true);
        newCourseSectionAttempt.setActiveAttempt(true);
        newCourseSectionAttempt.setCourseSection(selectedCourseSection);
        dataManager.commit(newCourseSectionAttempt);
        fillCourseSections(enrollment.getCourse());
    }


    protected void openOnlineSection(Map<String, Object> params, String windowAlias) {
        Window window = openWindow(windowAlias, WindowManager.OpenType.THIS_TAB, params);
        window.addCloseListener(this::onClosed);
    }

    protected void onClosed(String actionId) {
        if (actionId.equals("finish")) {
            close("finish");
        }
    }

    protected List<CourseSection> notPassedCourseSection(CourseSection courseSection) {
        List<CourseSection> notPassedCourseSection = new ArrayList<>();
        if (enrollment == null) return notPassedCourseSection;
        List<CourseSection> beforeByOrderCourseSectionList =
                commonService.getEntities(CourseSection.class,
                        "select cs from tsadv$CourseSection cs" +
                                "    where cs.order < :currentCourseSectionOrder" +
                                "    and cs.mandatory = true" +
                                "    and cs.course.id = :currentCourseId" +
                                "    order by cs.order",
                        ParamsMap.of("currentCourseSectionOrder", courseSection.getOrder(),
                                "currentCourseId", courseSection.getCourse().getId()),
                        "_local");
        if (beforeByOrderCourseSectionList.size() > 0) {
            List<CourseSectionAttempt> beforeByOrderCourseSectionAttemptList =
                    commonService.getEntities(CourseSectionAttempt.class,
                            "select e from tsadv$CourseSectionAttempt e" +
                                    " where e.courseSection.id in :courseSectionsIds" +
                                    " and e.activeAttempt = true" +
                                    " and e.enrollment.id = :enrollmentId",
                            ParamsMap.of("courseSectionsIds", beforeByOrderCourseSectionList,
                                    "enrollmentId", enrollment.getId()),
                            "courseSectionAttempt.browse");
            for (CourseSection section : beforeByOrderCourseSectionList) {
                boolean hasSuccessAttempt = false;
                for (CourseSectionAttempt courseSectionAttempt : beforeByOrderCourseSectionAttemptList) {
                    if (section.getId().equals(courseSectionAttempt.getCourseSection().getId())
                            && courseSectionAttempt.getSuccess()) {
                        hasSuccessAttempt = true;
                    }
                }
                if (!hasSuccessAttempt) notPassedCourseSection.add(section);
            }
        }
        return notPassedCourseSection;
    }

    protected boolean checkAttempt(Test test, CourseSection courseSection,
                                   CourseSectionAttempt lastAttemptCurrentDate) {
        LoadContext<CourseSectionAttempt> loadContext = LoadContext.create(CourseSectionAttempt.class);
        loadContext.setQuery(LoadContext.createQuery(String.format(
                "select e from tsadv$CourseSectionAttempt e " +
                        "where e.activeAttempt = :activeAttempt " +
                        "and e.courseSection.id = :courseSectionId " +
                        "and e.enrollment.id = :enrollmentId %s",
                lastAttemptCurrentDate != null ? String.format(" and e.id <> '%s'", lastAttemptCurrentDate.getId()) : ""))
                .setParameter("courseSectionId", courseSection.getId())
                .setParameter("activeAttempt", Boolean.TRUE)
                .setParameter("enrollmentId", enrollment.getId()));
        long attemptCount = dataManager.getCount(loadContext);
        return attemptCount < test.getMaxAttempt();
    }

    protected CourseSectionAttempt getLastActiveAttempt(CourseSection courseSection) {
        LoadContext<CourseSectionAttempt> loadContext = LoadContext.create(CourseSectionAttempt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$CourseSectionAttempt e " +
                        "where e.activeAttempt = :activeAttempt " +
                        "and e.courseSection.id = :courseSectionId " +
                        "and e.enrollment.id = :enrollmentId " +
                        "order by e.attemptDate desc");

        query.setParameter("activeAttempt", Boolean.TRUE);
        query.setParameter("courseSectionId", courseSection.getId());
        query.setParameter("enrollmentId", enrollment.getId());

        loadContext.setQuery(query);
        loadContext.setView("courseSectionAttempt.edit");

        List<CourseSectionAttempt> attemptList = dataManager.loadList(loadContext);
        if (!attemptList.isEmpty()) return attemptList.get(0);

        return null;
    }

    protected CourseSectionAttempt getLastAttempt(CourseSection courseSection, boolean isActiveAttempt,
                                                  boolean isSuccess) {
        LoadContext<CourseSectionAttempt> loadContext = LoadContext.create(CourseSectionAttempt.class);
        LoadContext.Query query = LoadContext.createQuery(String.format(
                "select e from tsadv$CourseSectionAttempt e " +
                        "where e.activeAttempt = :activeAttempt " +
                        "and e.success = :success " +
                        "and e.courseSection.id = :courseSectionId " +
                        "and e.enrollment.id = :enrollmentId %s " +
                        "order by e.attemptDate desc", !isActiveAttempt ? " and e.attemptDate = CURRENT_DATE" : ""));

        query.setParameter("activeAttempt", isActiveAttempt);
        query.setParameter("success", isSuccess);
        query.setParameter("courseSectionId", courseSection.getId());
        query.setParameter("enrollmentId", enrollment.getId());

        loadContext.setQuery(query);
        loadContext.setView("courseSectionAttempt.edit");

        List<CourseSectionAttempt> attemptList = dataManager.loadList(loadContext);
        if (!attemptList.isEmpty()) return attemptList.get(0);

        return null;
    }

    protected void footerButtonsVisible(boolean isSectionStarted, boolean isTest) {
        startSection.setVisible(!isSectionStarted);
        sectionGroupBox.setVisible(!isSectionStarted);
        feedbackTemplateGroupBox.setVisible(!isSectionStarted);
    }

    protected void fillCourseSections(Course course) {
        course.getSections().sort(Comparator.comparing(CourseSection::getOrder));

        for (CourseSection courseSection : course.getSections()) {
            courseSections.add(createCourseSectionList(courseSection));
        }

        List<LearningFeedbackTemplate> feedbackTemplates = loadFeedbackTemplates(course);
        if (feedbackTemplates != null && !feedbackTemplates.isEmpty()) {
            for (LearningFeedbackTemplate feedbackTemplate : feedbackTemplates) {
                feedbackTemplatesVBox.add(createFeedbackTemplate(feedbackTemplate));
            }
        }
    }

    protected List<LearningFeedbackTemplate> loadFeedbackTemplates(Course course) {
        LoadContext<LearningFeedbackTemplate> loadContext = LoadContext.create(LearningFeedbackTemplate.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$LearningFeedbackTemplate e " +
                        "where e.id in (select cft.feedbackTemplate.id from tsadv$CourseFeedbackTemplate cft where cft.course.id = :courseId and :systemDate between cft.startDate and cft.endDate) " +
                        "and e.active = True " +
                        "and e.employee = True " +
                        "and e.usageType = :usageType");

        query.setParameter("courseId", course.getId());
        query.setParameter("systemDate", CommonUtils.getSystemDate());
        query.setParameter("usageType", LearningFeedbackUsageType.COURSE.getId());
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.loadList(loadContext);
    }

    protected VBoxLayout createCourseSectionList(CourseSection courseSection) {
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        vBoxLayout.setWidth("100%");
        vBoxLayout.addStyleName("course-section-list");
        vBoxLayout.add(getSectionNameComponent(courseSection));
        vBoxLayout.add(createLabel(courseSection.getFormat().getLangValue(), "section-format"));

//        vBoxLayout.addLayoutClickListener(new LayoutClickListener() {
//            @Override
//            public void layoutClick(LayoutClickEvent event) {
//                loadData(courseSection);
//            }
//        });
        return vBoxLayout;
    }

    protected Component getSectionNameComponent(CourseSection courseSection) {
        return createLabel(courseSection.getSectionName(), "section-name");
    }

    protected VBoxLayout createFeedbackTemplate(LearningFeedbackTemplate feedbackTemplate) {
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        vBoxLayout.setWidth("100%");
        vBoxLayout.addStyleName("course-section-list");
        vBoxLayout.add(createLabel(feedbackTemplate.getName(), "section-name"));
        //vBoxLayout.add(createLabel(courseSection.getFormat().getLangValue(), "section-format"));

//        vBoxLayout.addLayoutClickListener(new LayoutClickListener() {
//            @Override
//            public void layoutClick(LayoutClickEvent event) {
//                loadData(feedbackTemplate);
//            }
//        });
        return vBoxLayout;
    }

    protected void loadData(LearningFeedbackTemplate feedbackTemplate) {
        String name = feedbackTemplate.getName();
        wrapper.removeAll();
        wrapper.add(createLabel(name, "course-section-name"));
        courseSectionLabel.setValue(name);
        this.selectedCourseSection = null;
        this.feedbackTemplate = feedbackTemplate;

        additionalInfo.removeAll();
        testData.removeAll();
        startSection.setVisible(true);

        footerButtonsVisible(false, false);
        startSection.setVisible(true);

        Label content = createLabel(feedbackTemplate.getDescription(), "course-section-description");
        content.setHtmlEnabled(true);
        testData.add(content);
    }

    protected void loadData(CourseSection courseSection) {
        wrapper.removeAll();
        wrapper.add(createLabel(courseSection.getSectionName(), "course-section-name"));
        courseSectionLabel.setValue(courseSection.getSectionName());
        this.selectedCourseSection = courseSection;

        additionalInfo.removeAll();
        testData.removeAll();

        String formatCode = courseSection.getFormat().getCode();

        startSection.setVisible(true);

        if (formatCode.equalsIgnoreCase("offline")) {
            List<CourseSectionSession> sectionSessions = courseSection.getSession();
            if (sectionSessions != null && !sectionSessions.isEmpty()) {
                CourseSectionSession sectionSession = sectionSessions.get(0);

                DicLearningCenter dicLearningCenter = sectionSession.getLearningCenter();

                if (dicLearningCenter != null) {
                    GridLayout gridLayout = componentsFactory.createComponent(GridLayout.class);
                    gridLayout.addStyleName("offline-info-grid");
                    gridLayout.setSpacing(true);
                    gridLayout.setColumns(3);
                    gridLayout.setRows(3);

                    gridLayout.add(createLabel(getMessage("start.course.address")), 0, 0);
                    gridLayout.add(createLabel(":"), 1, 0);
                    gridLayout.add(createLabel(dicLearningCenter.getAddress()), 2, 0);

                    gridLayout.add(createLabel(getMessage("start.course.date.start")), 0, 1);
                    gridLayout.add(createLabel(":"), 1, 1);
                    gridLayout.add(createLabel(dateTimeFormat.format(sectionSession.getStartDate())), 2, 1);

                    gridLayout.add(createLabel(getMessage("start.course.date.end")), 0, 2);
                    gridLayout.add(createLabel(":"), 1, 2);
                    gridLayout.add(createLabel(dateTimeFormat.format(sectionSession.getEndDate())), 2, 2);

                    additionalInfo.add(gridLayout);

                    if (dicLearningCenter.getLongitude() != null && dicLearningCenter.getLatitude() != null) {
                        GroupBoxLayout mapGroupBox = componentsFactory.createComponent(GroupBoxLayout.class);
                        mapGroupBox.setWidth("600px");
                        mapGroupBox.setHeight("400px");
                        mapGroupBox.setCaption(getMessage("start.course.map"));

                        MapViewer map = componentsFactory.createComponent(MapViewer.class);
                        map.setZoom(14);
                        map.setDraggable(true);
                        map.setWidth("100%");
                        map.setHeight("100%");

                        GeoPoint geoPoint = map.createGeoPoint(dicLearningCenter.getLatitude(), dicLearningCenter.getLongitude());
                        map.setCenter(geoPoint);
                        map.addMarker(dicLearningCenter.getLangValue(), geoPoint, false, null);

                        mapGroupBox.add(map);

                        additionalInfo.add(mapGroupBox);
                    }
                }

            }

            footerButtonsVisible(false, false);
            startSection.setVisible(false);
        }

        Label content = createLabel(courseSection.getDescription(), "course-section-description");
        content.setHtmlEnabled(true);
        testData.add(content);
    }

    protected Label createLabel(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }

    @SuppressWarnings("all")
    protected Label createLabel(String value, String cssClass) {
        Label label = createLabel(value);
        label.addStyleName(cssClass);
        return label;
    }
}