package kz.uco.tsadv.web.modules.personal.selflearning;

import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.LearningPath;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SelfLearning extends CourseCommon {

    protected static final String ACTIVE_TAB = "active";

    @Inject
    protected FlowBoxLayout activeCourses;

    @Inject
    protected FlowBoxLayout completedCourses;

    @Inject
    protected TabSheet tabSheet;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected VBoxLayout learningPaths;

    protected UUID personGroupId = null;

    protected void startCourse(Enrollment enrollment1, WindowManager windowManager) {
        WindowInfo windowInfo = windowConfig.getWindowInfo("start-course");
        Window window = windowManager.openWindow(windowInfo, WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
            put("enrollment", enrollment1);
        }});
        window.addCloseListener(actionId -> {
            if (actionId.equals("finish")) {
                close("");
                Window window1 = openWindow("self-learning", WindowManager.OpenType.THIS_TAB);
            }
        });
    }

    @Override
    public void ready() {
        super.ready();
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        personGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);

//        tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
//            @Override
//            public void selectedTabChanged(TabSheet.SelectedTabChangeEvent event) {
//                fillData(event.getSelectedTab().getName());
//            }
//        });

        tabSheet.setTab(ACTIVE_TAB);

        fillData(tabSheet.getTab().getName());
    }

    protected void fillData(String tabName) {
        if (personGroupId != null) {
            if (tabName.equalsIgnoreCase("learningPath")) {
                fillLearningPaths();
            } else if (tabName.equalsIgnoreCase("certification")) {
                //
            } else {
                fillCourses(tabName.equals(ACTIVE_TAB));
            }
        }
    }

    protected void fillLearningPaths() {
        learningPaths.removeAll();

        LoadContext<LearningPath> loadContext = LoadContext.create(LearningPath.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$LearningPath e " +
                        "where e.id in (" +
                        "   select e.learningPath.id " +
                        "   from tsadv$PersonLearningPath e " +
                        "   where e.personGroupId.id = :pId)")
                .setParameter("pId", personGroupId));
        loadContext.setView("learningPath.browse");

        List<LearningPath> list = dataManager.loadList(loadContext);
        for (LearningPath learningPath : list) {
            setAvgRate(learningPath);
            setCourseCount(learningPath);
            learningPaths.add(generateLPath(learningPath));
        }
    }

    protected void fillCourses(boolean isActive) {
        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);

        LoadContext.Query query = LoadContext.createQuery(
                "select e " +
                        "from tsadv$Enrollment e " +
                        "where e.personGroupId.id = :personGroupId " +
                        "and e.status = :status");

        query.setParameter("personGroup", personGroupId);
        query.setParameter("status", isActive ? EnrollmentStatus.APPROVED : EnrollmentStatus.COMPLETED);

        loadContext.setQuery(query);
        loadContext.setView("enrollment.for.course.card");

        List<Enrollment> enrollments = dataManager.loadList(loadContext);
        if (!enrollments.isEmpty()) {
            activeCourses.removeAll();
            completedCourses.removeAll();

            for (Enrollment enrollment : enrollments) {
                if (isActive && getCompletedSectionsPercent(enrollment) < 100) {
                    activeCourses.add(createCourseCard(enrollment, getWindowManager(), false));
                } else {
                    completedCourses.add(createCourseCard(enrollment, getWindowManager(), false));
                }
            }
        } else {
            if (isActive) {
                activeCourses.removeAll();
                activeCourses.add(createEmptyCourse());
            }
        }
    }

    protected CssLayout createEmptyCourse() {
        CssLayout cssLayout = componentsFactory.createComponent(CssLayout.class);
        cssLayout.setWidth("260px");
        cssLayout.setHeight("360px");
        cssLayout.setStyleName("add-course-empty");
        Label label = createLabelCss(getMessage("add.new.course"), "add-course-label");
        label.setIcon("font-icon:PLUS_CIRCLE");
        cssLayout.add(label);
        cssLayout.addLayoutClickListener(layoutClickEvent -> {
            openWindow("all-courses", WindowManager.OpenType.NEW_TAB);
        });
        return cssLayout;
    }
}