package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.CourseService;
import kz.uco.tsadv.web.gui.components.WebRateStars;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.personal.selflearning.CourseCommon;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("all")
public class CourseCard extends CourseCommon {

    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Inject
    protected Datasource<Course> courseDs;

    @Inject
    protected CourseService courseService;

    @Inject
    protected Metadata metadata;

    @Inject
    protected VBoxLayout courseReviewVBox;

    @Inject
    protected FlowBoxLayout preRequisitionCourse;

    @Inject
    protected HtmlBoxLayout courseCardLeftMenu;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Embedded courseLogo;

    @Inject
    protected Embedded personImage;

    @Inject
    protected VBoxLayout courseBody;

    @Inject
    protected TabSheet tabSheet;

    @Inject
    protected VBoxLayout faqVBox;

    @Inject
    protected UserSession userSession;

    @Inject
    protected VBoxLayout reviewForm;

    protected WindowConfig windowConfig = AppBeans.get(WindowConfig.class);

    @WindowParam
    protected Enrollment enrollment;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("course")) {
            Course course = (Course) params.get("course");
            course = dataManager.reload(course, "course.edit");
            courseDs.setItem(course);

            Utils.getCourseImageEmbedded(course, "130px", courseLogo);

            fillBody(course);

            if (reviewForm != null && reviewForm.getComponents() != null && reviewForm.getComponents().size() == 0) {
                createReviewForm(course);
            }

            if (courseReviewVBox != null && courseReviewVBox.getComponents() != null && courseReviewVBox.getComponents().size() == 0) {
                fillCourseReview(course);
            }

            initLeftCardMenu(course);
        }

//        tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener()) {
//            public void selectedTabChanged(com.vaadin.ui.TabSheet.SelectedTabChangeEvent event) {
//                String tabId = event.getTabSheet().getSelectedTab().getId();
//                if (tabId.equalsIgnoreCase("faq")) {
//                    if (faqVBox.getComponents() != null && faqVBox.getComponents().size() == 0) {
//                        fillFaq();
//                    }
//                } else if (tabId.equalsIgnoreCase("preRequisition")) {
//                    if (params.containsKey("course")) {
//                        Course course = (Course) params.get("course");
//                        if (preRequisitionCourse.getComponents() != null && preRequisitionCourse.getComponents().size() == 0) {
//                            fillPreRequisitionCourses(course);
//                        }
//                    }
//                }
//            }
//        });`
    }

    protected void fillPreRequisitionCourses(Course course) {
        for (Course preRequisition : getPreRequisitions(course)) {
            preRequisitionCourse.add(createCourseCard(preRequisition, getWindowManager(), true, null));
        }
    }

    protected List<Course> getPreRequisitions(Course course) {
        LoadContext<Course> loadContext = LoadContext.create(Course.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$Course e " +
                        "where e.id in (select p.requisitionCourse.id from tsadv$CoursePreRequisition p where p.course.id = :courseId)")
                .setParameter("courseId", course.getId()))
                .setView("course.tree");
        return dataManager.loadList(loadContext);
    }

    @SuppressWarnings("all")
    protected void initLeftCardMenu(Course course) {
        courseCardLeftMenu.add(createLabelWithId("courseName", course.getName()));
        courseCardLeftMenu.add(createLabelWithId("courseCategory", course.getCategory().getLangValue()));

        Button button = componentsFactory.createComponent(Button.class);
        button.setId("register");
        button.setWidth("100%");

        if (enrollment == null) {
            button.setCaption(getMessage("Course.section.register"));
            button.setAction(new BaseAction("register-course") {
                @Override
                public void actionPerform(Component component) {
                    WindowInfo windowInfo = windowConfig.getWindowInfo("register-course");
                    getWindowManager().openWindow(windowInfo, WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
                        put("course", course);
                    }});
                }
            });

            if (course.getSelfEnrollment()) {
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
            }

        } else {
            if (enrollment.getStatus().equals(EnrollmentStatus.APPROVED)) {
                button.setCaption(getMessage("Course.section.start"));
                button.setAction(new BaseAction("start-course") {
                    @Override
                    public void actionPerform(Component component) {
                        WindowInfo windowInfo = windowConfig.getWindowInfo("start-course");
                        getWindowManager().openWindow(windowInfo, WindowManager.OpenType.NEW_TAB, new HashMap<String, Object>() {{
                            put("course", course);
                            put("enrollment", enrollment);
                        }});
                    }
                });
            } else {
                button.setCaption(getMessage("course.completed"));
                button.setEnabled(false);
            }

            float completePercent = courseService.completedSectionsPercent(enrollment.getId());
            double calcultaedPercent = (float) (completePercent / 100);

            courseCardLeftMenu.setTemplateName("course-card-menu-progress");
            courseCardLeftMenu.add(createH4("progressLabel", getMessage("CourseCard.course.progress") + " " + String.format("%.1f%%", completePercent), true));

            ProgressBar progressBar = componentsFactory.createComponent(ProgressBar.class);
            progressBar.setId("progressBar");
            progressBar.setWidthFull();
            progressBar.setValue(calcultaedPercent);
            courseCardLeftMenu.add(progressBar);
        }

        courseCardLeftMenu.add(button);

        StringBuilder sections = new StringBuilder("<ul>");
        int sCount = 1;
        for (CourseSection courseSection : course.getSections()) {
            sections.append(String.format("<li><a data-scroll='section-%s' class='scroll-link'>", sCount)).append(courseSection.getSectionName()).append("</a></li>");
            sCount++;
        }
        sections.append("</ul>");

        courseCardLeftMenu.add(createH4("courseSectionsLabel", getMessage("CourseCard.course.sections"), false));
        courseCardLeftMenu.add(createH4("courseRatingLabel", getMessage("CourseCard.course.rating"), true));

        Label courseSections = createLabelWithId("courseSections", sections.toString());
        courseSections.setHtmlEnabled(true);
        courseCardLeftMenu.add(courseSections);

        StringBuilder courseInfoSb = new StringBuilder("<table>");
        //Course.courseStartDate
        //Course.courseEndDate
        courseInfoSb.append("<tr><td>").append(getMessage("Course.sections.count")).append("</td><td>:</td><td>").append(course.getSections().size()).append("</td></tr>");

        Embedded courseLogo = Utils.getCourseImageEmbedded(course, "70px", null);
        courseLogo.setId("courseLogo");
        courseCardLeftMenu.add(courseLogo);

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

        courseCardLeftMenu.add(rateStarsWrap);

    }

    protected void refreshAvgRate(Course course) {
        WebRateStars rateStars = (WebRateStars) courseCardLeftMenu.getComponent("rateStars");
        if (rateStars != null) {
            RateStarsComponent rateStarsComponent = (RateStarsComponent) rateStars.getComponent();
            rateStarsComponent.setValue(course.getAvgRate());
        }

        Label rateStarsLabel = (Label) courseCardLeftMenu.getComponent("rateStarsLabel");
        if (rateStarsLabel != null) {
            rateStarsLabel.setValue("(" + String.format("%.1f", course.getAvgRate()) + ")");
        }
    }

    protected void createReviewForm(Course course) {
        PersonGroupExt personGroup = userSession.getAttribute("userPersonGroup");

        if (personGroup != null) {
            Utils.getPersonImageEmbedded(personGroup.getPerson(), "70px", personImage);

            WebRateStars rateStars = componentsFactory.createComponent(WebRateStars.class);
            RateStarsComponent rateStarsComponent = (RateStarsComponent) rateStars.getComponent();
            rateStarsComponent.setListener(rateStarsComponent::setValue);

            TextArea<String> textArea = componentsFactory.createComponent(TextArea.class);
            textArea.setRequired(false);
            textArea.setRequiredMessage(getMessage("course.review.text.error"));
            textArea.setWidth("100%");
            reviewForm.add(textArea);
            reviewForm.add(rateStars);

            HBoxLayout buttons = componentsFactory.createComponent(HBoxLayout.class);
            buttons.setSpacing(true);
            reviewForm.add(buttons);
            Button submit = componentsFactory.createComponent(Button.class);
            buttons.add(submit);
            submit.setCaption(getMessage("table.btn.save"));
            submit.setAction(new BaseAction("submit-review") {
                @Override
                public void actionPerform(Component component) {
                    textArea.setRequired(true);

                    if (validate(new ArrayList<Validatable>() {{
                        add(textArea);
                    }})) {
                        textArea.setRequired(false);

                        try {
                            CourseReview courseReview = metadata.create(CourseReview.class);
                            courseReview.setPersonGroup(personGroup);
                            courseReview.setCourse(course);
                            courseReview.setRate(rateStarsComponent.getValue());
                            courseReview.setText(textArea.getValue());
                            courseService.addCourseReview(courseReview);

                            rateStarsComponent.setValue(0);
                            textArea.setValue("");

                            courseReviewVBox.removeAll();
                            fillCourseReview(course);

                            refreshAvgRate(course);

                            showNotification(getMessage("msg.success.title"),
                                    getMessage("Course.review.add.success"),
                                    NotificationType.TRAY);
                        } catch (Exception ex) {
                            showNotification(getMessage("msg.error.title"),
                                    ex.getMessage(),
                                    NotificationType.TRAY);

                            ex.printStackTrace();
                        }
                    }
                }
            });

            Button reset = componentsFactory.createComponent(Button.class);
            buttons.add(reset);
            reset.setCaption(getMessage("table.btn.cancel"));
            reset.setAction(new BaseAction("reset-review") {
                @Override
                public void actionPerform(Component component) {
                    rateStarsComponent.setValue(0);
                    textArea.setValue("");
                    textArea.setRequired(false);
                }
            });
        }
    }

    protected void fillFaq() {
        LoadContext<Faq> loadContext = LoadContext.create(Faq.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$Faq e")).setView("faq.browse");
        List<Faq> faqList = dataManager.loadList(loadContext);

        if (!faqList.isEmpty()) {
            for (Faq faq : faqList) {
                faqVBox.add(createGroupBox(faq));
            }
        }
    }

    protected void fillCourseReview(Course course) {
        LoadContext<CourseReview> loadContext = LoadContext.create(CourseReview.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$CourseReview e where e.course.id = :courseId")
                .setParameter("courseId", course.getId()))
                .setView("courseReview.browse");
        List<CourseReview> reviewList = dataManager.loadList(loadContext);

        course.setAvgRate(0.0);

        if (!reviewList.isEmpty()) {
            double sum = 0.0;
            for (CourseReview courseReview : reviewList) {
                sum += courseReview.getRate() == null ? 0 : courseReview.getRate();
                courseReviewVBox.add(createReviewBlock(courseReview));
            }
            course.setAvgRate(sum / reviewList.size());
        }
    }

    protected HBoxLayout createReviewBlock(CourseReview courseReview) {
        PersonExt person = courseReview.getPersonGroup().getPerson();

        HBoxLayout reviewWrapper = componentsFactory.createComponent(HBoxLayout.class);
        reviewWrapper.setWidthFull();
        reviewWrapper.addStyleName("cc-review-grid");
        reviewWrapper.setSpacing(true);

        Embedded userAvatar = Utils.getPersonImageEmbedded(person, "40px", null);
        reviewWrapper.add(userAvatar);

        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        vBoxLayout.add(hBoxLayout);

        Label fullName = createLabelCss(person.getFullName(), "cc-review-person");
        fullName.setAlignment(Alignment.MIDDLE_CENTER);
        hBoxLayout.add(fullName);

        WebRateStars rateStars = componentsFactory.createComponent(WebRateStars.class);
        rateStars.setAlignment(Alignment.MIDDLE_CENTER);
        RateStarsComponent rateStarsComponent = (RateStarsComponent) rateStars.getComponent();
        rateStarsComponent.setStarWidth("15px");
        rateStarsComponent.setReadOnly(true);
        rateStarsComponent.setValue(courseReview.getRate());
        hBoxLayout.add(rateStars);

        Label comment = createLabelCss(courseReview.getText(), "cc-review-text");
        vBoxLayout.add(comment);
        reviewWrapper.add(vBoxLayout);
        reviewWrapper.expand(vBoxLayout);
        return reviewWrapper;
    }

    protected GroupBoxLayout createGroupBox(Faq faqModel) {
        GroupBoxLayout groupBox = componentsFactory.createComponent(GroupBoxLayout.class);
        groupBox.addStyleName("faq-box");
        groupBox.setCollapsable(true);
        groupBox.setCaption(faqModel.getLangValue());

        Label label = createLabel(faqModel.getContentLangValue());
        label.setHtmlEnabled(true);

        groupBox.add(label);
        return groupBox;
    }

    protected void fillBody(Course course) {
        if (!course.getSections().isEmpty()) {
            int num = 1;

            Collections.sort(course.getSections(), new Comparator<CourseSection>() {
                @Override
                public int compare(CourseSection o1, CourseSection o2) {
                    return o1.getOrder().compareTo(o2.getOrder());
                }
            });

            for (CourseSection courseSection : course.getSections()) {
                courseBody.add(createSectionBlock(courseSection, num));
                num++;
            }
        }
    }

    protected VBoxLayout createSectionBlock(CourseSection courseSection, int num) {
        VBoxLayout section = componentsFactory.createComponent(VBoxLayout.class);
        section.addStyleName("course-section-wrap section-" + num);

        Label ribbon = componentsFactory.createComponent(Label.class);
        ribbon.setHtmlEnabled(true);
        ribbon.setValue("<div class=\"ribbon\">" + getMessage("Course.section.ribbon") + num + "</div>");
        section.add(ribbon);

        section.add(createLabelCss(courseSection.getSectionName(), "course-section-name"));

        Label label = createLabel(courseSection.getDescription());
        label.setHtmlEnabled(true);
        section.add(label);
        return section;
    }
}