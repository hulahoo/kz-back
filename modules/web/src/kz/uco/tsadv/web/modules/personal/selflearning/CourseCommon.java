package kz.uco.tsadv.web.modules.personal.selflearning;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.gui.components.WebRateStars;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;
import kz.uco.tsadv.service.CourseService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Adilbekov Yernar
 */

@SuppressWarnings("all")
public class CourseCommon extends AbstractWindow {

    protected static Log logger = LogFactory.getLog(CourseCommon.class);

    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    protected ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.class);

    protected WindowConfig windowConfig = AppBeans.get(WindowConfig.class);

    protected Metadata metadata = AppBeans.get(Metadata.class);

    @Inject
    protected DataManager dataManager;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected UserSession userSession;

    @Inject
    protected CourseService courseService;

    protected PersonExt person;

    protected Enrollment previosEnrollment;
    protected float previosCompletedValue;

    @Override
    public void init(Map<String, Object> params) {
        person = userSessionSource.getUserSession().getAttribute("userPerson");
    }

    protected Component generateLPath(LearningPath learningPath) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setStyleName("learning-path-wrap");
        hBoxLayout.setSpacing(true);

        Embedded categoryImage = Utils.getCourseCategoryImageEmbedded(learningPath.getCategory(), "40px", null);
        hBoxLayout.add(categoryImage);

        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);

        /**
         * Name row
         * */
        LinkButton name = componentsFactory.createComponent(LinkButton.class);
        name.setStyleName("lp-course-link");
        name.setCaption(learningPath.getName());
        name.setAction(new BaseAction("lp-name") {
            @Override
            public void actionPerform(Component component) {
                /*openWindow("learning-path-time-line",
                        WindowManager.OpenType.THIS_TAB,
                        ParamsMap.of("learningPath", learningPath));*/

                openWindow("learning-path-card",
                        WindowManager.OpenType.THIS_TAB,
                        ParamsMap.of("learningPath", learningPath));
            }
        });
        vBoxLayout.add(name);

        /**
         * Rating, CourseCount, ReviewCount row
         * */
        HBoxLayout hBox1 = componentsFactory.createComponent(HBoxLayout.class);
        hBox1.add(createLabelCss(getMessage("learning.path.course.count"), "lp-label"));
        hBox1.add(createLabelCss(":", "lp-dot"));
        hBox1.add(createLabelCss(String.valueOf(learningPath.getCourseCount()), "lp-value"));

        hBox1.add(createDivider());

        hBox1.add(createLabelCss(getMessage("CourseCard.course.rating"), "lp-label"));
        hBox1.add(createLabelCss(":", "lp-dot"));

        WebRateStars webRateStars = componentsFactory.createComponent(WebRateStars.class);
        webRateStars.setStyleName("lp-rate-stars");
        RateStarsComponent rateStars = (RateStarsComponent) webRateStars.getComponent();
        rateStars.setListener(rateStars::setValue);
        rateStars.setReadOnly(true);
        rateStars.setValue(learningPath.getAvgRate());
        rateStars.setStarWidth("13px");
        hBox1.add(webRateStars);
        vBoxLayout.add(hBox1);

        hBox1.add(createDivider());

        hBox1.add(createLabelCss(getMessage("learning.path.review.count"), "lp-label"));
        hBox1.add(createLabelCss(":", "lp-dot"));
        hBox1.add(createLabelCss(String.valueOf(learningPath.getReviewCount()), "lp-value"));

        hBoxLayout.add(vBoxLayout);
        return hBoxLayout;
    }

    protected Label createDivider() {
        Label divider = createLabelCss("<i class=\"fa fa-circle-o\"></i>", "lp-divider");
        divider.setHtmlEnabled(true);
        return divider;
    }

    protected void setAvgRate(LearningPath learningPath) {
        LoadContext<LearningPathReview> loadContext = LoadContext.create(LearningPathReview.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$LearningPathReview e where e.learningPath.id = :lpId")
                .setParameter("lpId", learningPath.getId()))
                .setView("learningPathReview.rate");
        List<LearningPathReview> reviewList = dataManager.loadList(loadContext);

        learningPath.setAvgRate(0.0);
        learningPath.setReviewCount((long) reviewList.size());

        if (!reviewList.isEmpty()) {
            double sum = 0.0;
            for (LearningPathReview courseReview : reviewList) {
                sum += courseReview.getRate() == null ? 0 : courseReview.getRate();
            }
            learningPath.setAvgRate(sum / reviewList.size());
        }
    }

    protected void setCourseCount(LearningPath learningPath) {
        LoadContext<LearningPathCourse> loadContext = LoadContext.create(LearningPathCourse.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$LearningPathCourse e " +
                        "where e.learningPath.id = :lpId").setParameter("lpId", learningPath.getId()));
        learningPath.setCourseCount(dataManager.getCount(loadContext));
    }

    protected String getCategoryHierarchy(UUID categoryId) {
        List list = courseService.getCategoryHierarchy(String.valueOf(categoryId));

        StringBuilder sb = new StringBuilder("");
        for (Object uuid : list) {
            sb.append("'").append(uuid).append("',");
        }
        String categories = sb.toString().substring(0, sb.toString().length() - 1);
        //logger.info(categories);
        return categories;
    }

    protected HtmlBoxLayout createCourseCard(Enrollment enrollment, WindowManager windowManager, boolean isNewCourse) {
        return createCourseCard(enrollment.getCourse(), windowManager, isNewCourse, enrollment);
    }

    protected HtmlBoxLayout createCourseCard(Course course, WindowManager windowManager, boolean isNewCourse, Enrollment enrollment) {
        HtmlBoxLayout htmlBoxLayout = componentsFactory.createComponent(HtmlBoxLayout.class);
        htmlBoxLayout.addStyleName("sl-course-wrapper");
        htmlBoxLayout.setTemplateName("course-card");
        htmlBoxLayout.add(createLabel("courseName", course.getName()));
        htmlBoxLayout.add(createLabel("courseCategory", course.getCategory().getLangValue()));
        htmlBoxLayout.add(createLabel("shortDescription", course.getShortDescription()));

        Button showBtn = componentsFactory.createComponent(Button.class);
        showBtn.setId("showCard");
        showBtn.setWidth("100%");
        showBtn.setCaption(getMessage("Course.full.info"));
        showBtn.setAction(new BaseAction("show-card") {
            @Override
            public void actionPerform(Component component) {
                WindowInfo windowInfo = windowConfig.getWindowInfo("course-card");
                windowManager.openWindow(windowInfo, WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
                    put("course", course);
                    if (enrollment != null) put("enrollment", enrollment);
                }});
            }
        });
        htmlBoxLayout.add(showBtn);

        Button button = componentsFactory.createComponent(Button.class);
        button.setId("register");
        button.setWidth("100%");
        button.setCaption(getMessage(isNewCourse ? "Course.section.register" : "Course.section.start"));
        button.setAction(new BaseAction(button.getCaption()) {
            @Override
            public void actionPerform(Component component) {
                if (isNewCourse) {
                    WindowInfo windowInfo = windowConfig.getWindowInfo("register-course");
                    windowManager.openWindow(windowInfo, WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
                        put("course", course);
                    }});
                } else {
                    startCourse(enrollment, windowManager);

                }
            }
        });

        if (enrollment != null) {
            if (enrollment.getStatus().equals(EnrollmentStatus.COMPLETED)) {
                button.setCaption(getMessage("course.completed"));
                button.setEnabled(true);
            } else {
                htmlBoxLayout.setTemplateName("course-card-progress");
                float completePercent = getCompletedSectionsPercent(enrollment);
                double calcultaedPercent = (float) (completePercent / 100);

                htmlBoxLayout.add(createLabel("progressLabel", getMessage("CourseCard.course.progress") + " " + String.format("%.1f%%", completePercent)));

                ProgressBar progressBar = componentsFactory.createComponent(ProgressBar.class);
                progressBar.setId("progressBar");
                progressBar.setWidthFull();
                progressBar.setValue(calcultaedPercent);
                htmlBoxLayout.add(progressBar);
            }
        }

        htmlBoxLayout.add(button);

        StringBuilder sections = new StringBuilder("<ul>");

        course.getSections().sort(new Comparator<CourseSection>() {
            @Override
            public int compare(CourseSection o1, CourseSection o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        for (CourseSection courseSection : course.getSections()) {
            sections.append("<li>").append(courseSection.getSectionName()).append("</li>");
        }
        sections.append("</ul>");

        StringBuilder courseInfoSb = new StringBuilder("<table>");
        courseInfoSb.append("<tr><td>").append(getMessage("Course.sections.count")).append("</td><td>:</td><td>").append(course.getSections().size()).append("</td></tr>");

        //htmlBoxLayout.add(createH4("courseInfoLabel", getMessage("CourseCard.course.info"), true));

        /*Label courseInfo = createLabel("courseInfo", courseInfoSb.toString());
        courseInfo.setHtmlEnabled(true);
        htmlBoxLayout.add(courseInfo);*/

        htmlBoxLayout.add(createH4("courseSectionsLabel", getMessage("CourseCard.course.sections"), false));

        Label courseSections = createLabel("courseSections", sections.toString());
        courseSections.setHtmlEnabled(true);
        htmlBoxLayout.add(courseSections);

        /*Embedded categoryImage = Utils.getCourseCategoryImageEmbedded(course.getCategory(), null, null);
        categoryImage.setResponsive(true);
        categoryImage.setId("categoryImage");
        htmlBoxLayout.add(categoryImage);*/

        Embedded courseLogo = Utils.getCourseImageEmbedded(course, "100px", null);
        courseLogo.setId("courseLogo");
        htmlBoxLayout.add(courseLogo);

        fillAvgRate(course);

        htmlBoxLayout.add(createH4("rateStarsLabel", getMessage("CourseCard.course.rating"), true));

        HBoxLayout rateStarsWrap = componentsFactory.createComponent(HBoxLayout.class);
        rateStarsWrap.setId("rateStarsWrap");

        WebRateStars rateStars = componentsFactory.createComponent(WebRateStars.class);
        rateStars.setId("rateStars");
        RateStarsComponent rateStarsComponent = (RateStarsComponent) rateStars.getComponent();
        rateStarsComponent.setListener(rateStarsComponent::setValue);
        rateStarsComponent.setReadOnly(true);
        rateStarsComponent.setValue(course.getAvgRate());
        rateStarsWrap.add(rateStars);

        Label rateStarsLabel = createLabelCss("(" + String.format("%.1f", course.getAvgRate()) + ")", "cc-rate-star-lbl");
        rateStarsLabel.setId("rateStarsLabel");
        rateStarsWrap.add(rateStarsLabel);

        htmlBoxLayout.add(rateStarsWrap);

        return htmlBoxLayout;
    }

    protected HtmlBoxLayout createCourseCardAllCourses(Course course, WindowManager windowManager, boolean isNewCourse, Enrollment enrollment) {
        HtmlBoxLayout htmlBoxLayout = componentsFactory.createComponent(HtmlBoxLayout.class);
        htmlBoxLayout.addStyleName("sl-course-wrapper");
        htmlBoxLayout.setTemplateName("course-card");
        htmlBoxLayout.add(createLabel("courseName", course.getName()));
        htmlBoxLayout.add(createLabel("courseCategory", course.getCategory().getLangValue()));
        htmlBoxLayout.add(createLabel("shortDescription", course.getShortDescription()));

        Button showBtn = componentsFactory.createComponent(Button.class);
        showBtn.setId("showCard");
        showBtn.setWidth("100%");
        showBtn.setCaption(getMessage("Course.full.info"));
        showBtn.setAction(new BaseAction("show-card") {
            @Override
            public void actionPerform(Component component) {
                WindowInfo windowInfo = windowConfig.getWindowInfo("course-card");
                windowManager.openWindow(windowInfo, WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
                    put("course", course);
                    if (enrollment != null) put("enrollment", enrollment);
                }});
            }
        });
        htmlBoxLayout.add(showBtn);

        Button button = componentsFactory.createComponent(Button.class);
        button.setId("register");
        button.setWidth("100%");
        button.setCaption(getMessage(isNewCourse ? "Course.section.register" : "Course.section.start"));
        button.setAction(new BaseAction(button.getCaption()) {
            @Override
            public void actionPerform(Component component) {
                if (isNewCourse) {
                    WindowInfo windowInfo = windowConfig.getWindowInfo("register-course");
                    windowManager.openWindow(windowInfo, WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
                        put("course", course);
                    }});
                } else {
                    startCourse(enrollment, windowManager);

                }
            }
        });

        if (course.getSelfEnrollment()) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }

        if (enrollment != null) {
            if (enrollment.getStatus().equals(EnrollmentStatus.COMPLETED)) {
                button.setCaption(getMessage("course.completed"));
                button.setEnabled(false);
            } else {
                htmlBoxLayout.setTemplateName("course-card-progress");
                float completePercent = getCompletedSectionsPercent(enrollment);
                double calcultaedPercent = (float) (completePercent / 100);

                htmlBoxLayout.add(createLabel("progressLabel", getMessage("CourseCard.course.progress") + " " + String.format("%.1f%%", completePercent)));

                ProgressBar progressBar = componentsFactory.createComponent(ProgressBar.class);
                progressBar.setId("progressBar");
                progressBar.setWidthFull();
                progressBar.setValue(calcultaedPercent);
                htmlBoxLayout.add(progressBar);
            }
        }

        htmlBoxLayout.add(button);

        StringBuilder sections = new StringBuilder("<ul>");

        course.getSections().sort(new Comparator<CourseSection>() {
            @Override
            public int compare(CourseSection o1, CourseSection o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        for (CourseSection courseSection : course.getSections()) {
            sections.append("<li>").append(courseSection.getSectionName()).append("</li>");
        }
        sections.append("</ul>");

        StringBuilder courseInfoSb = new StringBuilder("<table>");
        courseInfoSb.append("<tr><td>").append(getMessage("Course.sections.count")).append("</td><td>:</td><td>").append(course.getSections().size()).append("</td></tr>");

        //htmlBoxLayout.add(createH4("courseInfoLabel", getMessage("CourseCard.course.info"), true));

        /*Label courseInfo = createLabel("courseInfo", courseInfoSb.toString());
        courseInfo.setHtmlEnabled(true);
        htmlBoxLayout.add(courseInfo);*/

        htmlBoxLayout.add(createH4("courseSectionsLabel", getMessage("CourseCard.course.sections"), false));

        Label courseSections = createLabel("courseSections", sections.toString());
        courseSections.setHtmlEnabled(true);
        htmlBoxLayout.add(courseSections);

        /*Embedded categoryImage = Utils.getCourseCategoryImageEmbedded(course.getCategory(), null, null);
        categoryImage.setResponsive(true);
        categoryImage.setId("categoryImage");
        htmlBoxLayout.add(categoryImage);*/

        Embedded courseLogo = Utils.getCourseImageEmbedded(course, "100px", null);
        courseLogo.setId("courseLogo");
        htmlBoxLayout.add(courseLogo);

        fillAvgRate(course);

        htmlBoxLayout.add(createH4("rateStarsLabel", getMessage("CourseCard.course.rating"), true));

        HBoxLayout rateStarsWrap = componentsFactory.createComponent(HBoxLayout.class);
        rateStarsWrap.setId("rateStarsWrap");

        WebRateStars rateStars = componentsFactory.createComponent(WebRateStars.class);
        rateStars.setId("rateStars");
        RateStarsComponent rateStarsComponent = (RateStarsComponent) rateStars.getComponent();
        rateStarsComponent.setListener(rateStarsComponent::setValue);
        rateStarsComponent.setReadOnly(true);
        rateStarsComponent.setValue(course.getAvgRate());
        rateStarsWrap.add(rateStars);

        Label rateStarsLabel = createLabelCss("(" + String.format("%.1f", course.getAvgRate()) + ")", "cc-rate-star-lbl");
        rateStarsLabel.setId("rateStarsLabel");
        rateStarsWrap.add(rateStarsLabel);

        htmlBoxLayout.add(rateStarsWrap);

        return htmlBoxLayout;
    }

    protected void startCourse(Enrollment enrollment1, WindowManager windowManager) {
        WindowInfo windowInfo = windowConfig.getWindowInfo("start-course");
        Window window = windowManager.openWindow(windowInfo, WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
            put("enrollment", enrollment1);
        }});
    }

    protected void fillAvgRate(Course course) {
        LoadContext<CourseReview> loadContext = LoadContext.create(CourseReview.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$CourseReview e where e.course.id = :courseId")
                .setParameter("courseId", course.getId()))
                .setView("courseReview.rate");
        List<CourseReview> reviewList = dataManager.loadList(loadContext);

        double sum = 0.0;

        course.setAvgRate(0.0);

        if (!reviewList.isEmpty()) {
            for (CourseReview courseReview : reviewList) {
                sum += courseReview.getRate() == null ? 0 : courseReview.getRate();
            }
            course.setAvgRate(sum / reviewList.size());
        }
    }

    protected Label createH4(String id, String message, boolean isBordered) {
        Label redirectLabel = createLabel(id,
                String.format("<h4 class=\"%s\">%s</h4>", isBordered ? " border-top" : "", message));
        redirectLabel.setHtmlEnabled(true);
        redirectLabel.setWidth("100%");
        return redirectLabel;
    }

    protected Label createLabel(String id, String caption) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setId(id);
        label.setValue(caption);
        return label;
    }

    protected Label createLabelWithId(String id, String value) {
        return createLabel(id, value);
    }

    protected Label createLabel(String caption) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(caption);
        return label;
    }

    protected Label createLabelCss(String caption, String css) {
        Label label = createLabel(caption);
        label.setStyleName(css);
        return label;
    }

    public float getCompletedSectionsPercent(Enrollment enrollment) {
        if (previosEnrollment != null && enrollment.getId().equals(previosEnrollment.getId())) {
            return previosCompletedValue;
        }
        previosCompletedValue = courseService.completedSectionsPercent(enrollment.getId());
        return previosCompletedValue;
    }
}
