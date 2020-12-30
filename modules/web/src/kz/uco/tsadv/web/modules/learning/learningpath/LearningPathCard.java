package kz.uco.tsadv.web.modules.learning.learningpath;

import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.config.WindowInfo;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.web.gui.components.WebRateStars;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.personal.selflearning.CourseCommon;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.*;

@SuppressWarnings("all")
public class LearningPathCard extends CourseCommon {

    @WindowParam
    protected LearningPath learningPath;

    @Inject
    protected HtmlBoxLayout lpCardLeftMenu;

    @Inject
    protected VBoxLayout learningPathVBox;

    @Inject
    protected HBoxLayout colourPicker;

    @Inject
    protected LinkButton timeLineMode;

    @Inject
    protected LinkButton defaultMode;

    @Inject
    protected VBoxLayout reviewForm;

    @Inject
    protected VBoxLayout courseReviewVBox;

    @Inject
    protected CssLayout timeLine;

    @Inject
    protected Embedded personImage;

    protected int completedCount = 0;

    protected List<LearningPathCourse> learningPathCourses;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        UUID personGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);

        if (learningPath != null && personGroupId != null) {
            if (reviewForm != null && reviewForm.getComponents() != null && reviewForm.getComponents().size() == 0) {
                createReviewForm();
            }

            if (courseReviewVBox != null && courseReviewVBox.getComponents() != null && courseReviewVBox.getComponents().size() == 0) {
                fillCourseReview();
            }

            learningPathCourses = getCourses(personGroupId);

            initLeftCardMenu(learningPathCourses, personGroupId);

            renderCourseCards(learningPathCourses, personGroupId);

            initMode();
            modeTumbler(true);
        }
    }

    protected void initMode() {
        defaultMode.setAction(new BaseAction("default-mode") {
            @Override
            public void actionPerform(Component component) {
                modeTumbler(true);
            }
        });

        timeLineMode.setAction(new BaseAction("time-line-mode") {
            @Override
            public void actionPerform(Component component) {
                modeTumbler(false);
            }
        });
    }

    protected void modeTumbler(boolean isDefault) {
        String modeActiveCss = "tl-mode-active";
        defaultMode.removeStyleName(modeActiveCss);
        timeLineMode.removeStyleName(modeActiveCss);

        if (!isDefault) {
            if (learningPathCourses != null) {
                timeLine.removeAll();
                fillTimeLine();
            }

            timeLineMode.addStyleName(modeActiveCss);
        } else {
            defaultMode.addStyleName(modeActiveCss);
        }

        learningPathVBox.setVisible(isDefault);
        timeLine.setVisible(!isDefault);
        colourPicker.setVisible(!isDefault);
    }

    public void darkTimeLine() {
        timeLine.removeStyleName("light-timeline");
        timeLine.addStyleName("dark-timeline");
    }

    public void lightTimeLine() {
        timeLine.removeStyleName("dark-timeline");
        timeLine.addStyleName("light-timeline");
    }

    public void fillTimeLine() {
        int count = 1;
        for (LearningPathCourse learningPathCourse : learningPathCourses) {
            createTimeLineBlock(learningPathCourse, count);
            count++;
        }
    }

    protected void createTimeLineBlock(LearningPathCourse pathCourse, int counter) {
        Course course = pathCourse.getCourse();

        HtmlBoxLayout htmlBoxLayout = componentsFactory.createComponent(HtmlBoxLayout.class);
        htmlBoxLayout.setWidthAuto();
        htmlBoxLayout.setTemplateName("tl-course");
        htmlBoxLayout.setStyleName("tl-container");
        htmlBoxLayout.addStyleName(counter % 2 != 0 ? "tl-left" : "tl-right");
        if (BooleanUtils.isTrue(course.getCompleted())) {
            htmlBoxLayout.addStyleName("tl-complete");
        }


        LinkButton courseTitle = componentsFactory.createComponent(LinkButton.class);
        courseTitle.setId("title");
        courseTitle.setCaption(course.getName());
        courseTitle.setAction(new BaseAction("redirect-course") {
            @Override
            public void actionPerform(Component component) {
                Course filledCourse = getCourse(course.getId());
                WindowInfo windowInfo = windowConfig.getWindowInfo("course-card");
                getWindowManager().openWindow(windowInfo, WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
                    put("course", filledCourse);
                }});
            }
        });

        htmlBoxLayout.add(courseTitle);
        htmlBoxLayout.add(createLabel("category", course.getCategory().getLangValue()));

        Embedded embedded = Utils.getCourseImageEmbedded(course, "90px", null);
        if (embedded != null) {
            embedded.setHeight("70px");
            embedded.setId("image");
            embedded.setStyleName(null);
            htmlBoxLayout.add(embedded);
        }

        htmlBoxLayout.add(createLabel("description", course.getShortDescription()));
        timeLine.add(htmlBoxLayout);
    }

    protected Label createLabel(String id, String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setId(id);
        label.setValue(value);
        return label;
    }

    protected void createReviewForm() {
        PersonGroupExt personGroup = userSession.getAttribute("userPersonGroup");

        if (personGroup != null) {
            Utils.getPersonImageEmbedded(personGroup.getPerson(), "70px", personImage);

            WebRateStars rateStars = componentsFactory.createComponent(WebRateStars.class);
            RateStarsComponent rateStarsComponent = (RateStarsComponent) rateStars.getComponent();
            rateStarsComponent.setListener(rateStarsComponent::setValue);

            TextArea textArea = componentsFactory.createComponent(TextArea.class);
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
                            LearningPathReview courseReview = metadata.create(LearningPathReview.class);
                            courseReview.setPersonGroup(personGroup);
                            courseReview.setLearningPath(learningPath);
                            courseReview.setRate(rateStarsComponent.getValue());
                            courseReview.setText((String) textArea.getValue());
                            courseService.addLearningPathReview(courseReview);

                            rateStarsComponent.setValue(0);
                            textArea.setValue("");

                            courseReviewVBox.removeAll();
                            fillCourseReview();

                            refreshAvgRate();

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

    protected void refreshAvgRate() {
        WebRateStars rateStars = (WebRateStars) lpCardLeftMenu.getComponent("rateStars");
        if (rateStars != null) {
            RateStarsComponent rateStarsComponent = (RateStarsComponent) rateStars.getComponent();
            rateStarsComponent.setValue(learningPath.getAvgRate());
        }

        Label rateStarsLabel = (Label) lpCardLeftMenu.getComponent("rateStarsLabel");
        if (rateStarsLabel != null) {
            rateStarsLabel.setValue("(" + String.format("%.1f", learningPath.getAvgRate()) + ")");
        }
    }

    protected void fillCourseReview() {
        LoadContext<LearningPathReview> loadContext = LoadContext.create(LearningPathReview.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$LearningPathReview e where e.learningPath.id = :lpId")
                .setParameter("lpId", learningPath.getId()))
                .setView("learningPathReview.browse");
        List<LearningPathReview> reviewList = dataManager.loadList(loadContext);

        learningPath.setAvgRate(0.0);

        if (!reviewList.isEmpty()) {
            double sum = 0.0;
            for (LearningPathReview courseReview : reviewList) {
                sum += courseReview.getRate() == null ? 0 : courseReview.getRate();
                courseReviewVBox.add(createReviewBlock(courseReview));
            }
            learningPath.setAvgRate(sum / reviewList.size());
        }
    }

    protected HBoxLayout createReviewBlock(LearningPathReview learningPathReview) {
        PersonExt person = learningPathReview.getPersonGroup().getPerson();

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
        rateStarsComponent.setValue(learningPathReview.getRate());
        hBoxLayout.add(rateStars);

        Label comment = createLabelCss(learningPathReview.getText(), "cc-review-text");
        vBoxLayout.add(comment);
        reviewWrapper.add(vBoxLayout);
        reviewWrapper.expand(vBoxLayout);
        return reviewWrapper;
    }

    protected void initLeftCardMenu(List<LearningPathCourse> learningPathCourses, UUID personGroupId) {
        lpCardLeftMenu.add(createLabelWithId("lpName", learningPath.getName()));
        lpCardLeftMenu.add(createLabelWithId("lpCategory", learningPath.getCategory().getLangValue()));

        renderButton("register", personGroupId, true);

        StringBuilder sections = new StringBuilder("<ul>");
        int sCount = 1;
        for (LearningPathCourse learningPathCourse : learningPathCourses) {
            sections.append(String.format("<li><a data-scroll='section-%s' class='scroll-link'>", sCount)).append(learningPathCourse.getCourse().getName()).append("</a></li>");
            sCount++;
        }
        sections.append("</ul>");

        lpCardLeftMenu.add(createH4("courseSectionsLabel", getMessage("learning.path.courses"), false));
        lpCardLeftMenu.add(createH4("courseRatingLabel", getMessage("CourseCard.course.rating"), true));

        Label courseSections = createLabelWithId("courseSections", sections.toString());
        courseSections.setHtmlEnabled(true);
        lpCardLeftMenu.add(courseSections);

        Embedded categoryImage = Utils.getCourseCategoryImageEmbedded(learningPath.getCategory(), "70px", null);
        categoryImage.setId("categoryImage");
        lpCardLeftMenu.add(categoryImage);

        HBoxLayout rateStarsWrap = componentsFactory.createComponent(HBoxLayout.class);
        rateStarsWrap.setId("rateStarsWrap");

        WebRateStars rateStars = componentsFactory.createComponent(WebRateStars.class);
        rateStars.setId("rateStars");
        RateStarsComponent rateStarsComponent = (RateStarsComponent) rateStars.getComponent();
        rateStarsComponent.setListener(rateStarsComponent::setValue);
        rateStarsComponent.setReadOnly(true);
        rateStarsComponent.setValue(learningPath.getAvgRate());
        rateStarsWrap.add(rateStars);

        Label rateStarsLabel = createLabelCss("(" + String.format("%.1f", learningPath.getAvgRate()) + ")", "cc-rate-star-lbl");
        rateStarsLabel.setId("rateStarsLabel");
        rateStarsWrap.add(rateStarsLabel);

        lpCardLeftMenu.add(rateStarsWrap);
    }

    protected void renderButton(String id, UUID personGroupId, boolean isNew) {
        Button button = isNew ? componentsFactory.createComponent(Button.class) : (Button) lpCardLeftMenu.getOwnComponent(id);
        if (isNew) {
            button.setId(id);
            button.setWidth("100%");
            lpCardLeftMenu.add(button);
        }

        boolean isFavorite = isFavorite(personGroupId);
        if (isFavorite) {
            button.setCaption(getMessage("remove.favorite.btn"));
            button.setAction(new BaseAction("remove-favorite") {
                @Override
                public void actionPerform(Component component) {
                    if (courseService.deleteFavorite(learningPath.getId(), personGroupId) > 0) {
                        showNotification(
                                getMessage("msg.success.title"),
                                getMessage("delete.favorite.success"),
                                NotificationType.TRAY);
                        renderButton(button.getId(), personGroupId, false);
                    } else {
                        showNotification("favorite.error");
                    }
                }
            });
        } else {
            button.setCaption(getMessage("add.favorite.btn"));
            button.setAction(new BaseAction("add-favorite") {
                @Override
                public void actionPerform(Component component) {
                    if (courseService.addFavorite(learningPath.getId(), personGroupId) > 0) {
                        showNotification(
                                getMessage("msg.success.title"),
                                getMessage("add.favorite.success"),
                                NotificationType.TRAY);

                        renderButton(button.getId(), personGroupId, false);
                    } else {
                        showNotification("favorite.error");
                    }
                }
            });
        }
    }

    protected boolean isFavorite(UUID personGroupId) {
        LoadContext<PersonLearningPath> loadContext = LoadContext.create(PersonLearningPath.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$PersonLearningPath e " +
                        "where e.personGroupId.id = :pId and e.learningPath.id = :lpId")
                .setParameter("pId", personGroupId)
                .setParameter("lpId", learningPath.getId()));
        return dataManager.getCount(loadContext) > 0;
    }

    protected void renderCourseCards(List<LearningPathCourse> learningPathCourses, UUID personGroupId) {
        float calcultaedPercent = 0f;

        if (!learningPathCourses.isEmpty()) {
            learningPathVBox.removeAll();

            Label learningPathDescription = createLabel(learningPath.getDescription());
            learningPathDescription.setStyleName("reset-label");
            learningPathDescription.setHtmlEnabled(true);
            learningPathVBox.add(learningPathDescription);

            int num = 1;
            for (LearningPathCourse learningPathCourse : learningPathCourses) {
                learningPathVBox.add(createCourseBlock(learningPathCourse.getCourse(), num, personGroupId));
                num++;
            }

            calcultaedPercent = (float) completedCount * (float) 100 / (float) learningPathCourses.size();
        }


        lpCardLeftMenu.add(createH4("progressLabel", getMessage("CourseCard.course.progress") + " " + String.format("%.1f%%", calcultaedPercent), true));

        ProgressBar progressBar = componentsFactory.createComponent(ProgressBar.class);
        progressBar.setId("progressBar");
        progressBar.setWidthFull();
        progressBar.setValue((double) (calcultaedPercent / 100));
        lpCardLeftMenu.add(progressBar);
    }

    protected List<LearningPathCourse> getCourses(UUID personGroupId) {
        LoadContext<LearningPathCourse> loadContext = LoadContext.create(LearningPathCourse.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select c " +
                        "from tsadv$LearningPathCourse c " +
                        "where c.learningPath.id = :lpId " +
                        "order by c.orderNumber");

        query.setParameter("lpId", learningPath.getId());
        loadContext.setView("learningPathCourse.card");
        loadContext.setQuery(query);

        List<LearningPathCourse> pathCourseList = dataManager.loadList(loadContext);
        for (LearningPathCourse pathCourse : pathCourseList) {
            Course course = pathCourse.getCourse();
            boolean completed = isCompleteCourse(course, personGroupId);
            course.setCompleted(completed);
        }

        return pathCourseList;
    }

    protected VBoxLayout createCourseBlock(Course course, int num, UUID personGroupId) {
        VBoxLayout section = componentsFactory.createComponent(VBoxLayout.class);
        section.addStyleName("course-section-wrap section-" + num);

        boolean isCompleted = BooleanUtils.isTrue(course.getCompleted());

        completedCount += isCompleted ? 1 : 0;

        Label ribbon = componentsFactory.createComponent(Label.class);
        ribbon.setHtmlEnabled(true);
        ribbon.setValue("<div class=\"ribbon\">" + getMessage("learning.path.ribbon") + num + (isCompleted ? " <i class=\"fa fa-certificate\"></i>" : "") + "</div>");
        section.add(ribbon);

        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(course.getName());
        linkButton.setStyleName("course-section-name");
        linkButton.setAction(new BaseAction("redirect-course") {
            @Override
            public void actionPerform(Component component) {
                Course filledCourse = getCourse(course.getId());
                WindowInfo windowInfo = windowConfig.getWindowInfo("course-card");
                getWindowManager().openWindow(windowInfo, WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
                    put("course", filledCourse);
                }});
            }
        });
        section.add(linkButton);

        Label label = createLabel(course.getDescription());
        label.setHtmlEnabled(true);
        section.add(label);
        return section;
    }

    protected Course getCourse(UUID courseId) {
        LoadContext<Course> loadContext = LoadContext.create(Course.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select c " +
                        "from tsadv$Course c " +
                        "where c.id =:cId");
        query.setParameter("cId", courseId);
        loadContext.setView("course.tree");
        loadContext.setQuery(query);
        return dataManager.load(loadContext);
    }

    protected boolean isCompleteCourse(Course course, UUID personGroupId) {
        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$Enrollment e " +
                        "where e.status = 5 " +
                        "and e.personGroupId.id = :pId " +
                        "and e.course.id = :cId")
                .setParameter("pId", personGroupId)
                .setParameter("cId", course.getId()));
        return dataManager.getCount(loadContext) > 0;
    }
}