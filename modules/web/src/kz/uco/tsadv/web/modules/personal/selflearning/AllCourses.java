package kz.uco.tsadv.web.modules.personal.selflearning;

import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.LearningPath;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AllCourses extends CourseCommon {

    @Inject
    protected Tree<DicCategory> categoryTree;

    @Inject
    protected FlowBoxLayout categoryCourses;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected HierarchicalDatasource<DicCategory, UUID> categoryDs;

    @Inject
    protected VBoxLayout learningPaths;

    @Inject
    protected TabSheet tabSheet;

    @Inject
    protected CollectionDatasource<Course, UUID> coursesDs;

    @Inject
    protected Table<Course> coursesTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        coursesDs.addCollectionChangeListener(this::collectionChanged);
        categoryDs.addItemChangeListener(this::itemChanged);


        tabSheet.addSelectedTabChangeListener(this::selectedTabChanged);

        fillLearningPaths(categoryDs.getItem());
    }

    protected void fillLearningPaths(DicCategory dicCategory) {
        learningPaths.removeAll();

        if (dicCategory != null) {
            String categories = getCategoryHierarchy(dicCategory.getId());

            LoadContext<LearningPath> loadContext = LoadContext.create(LearningPath.class);
            loadContext.setQuery(LoadContext.createQuery(String.format(
                    "select e from tsadv$LearningPath e " +
                            "where e.category.id in (%s)", categories)));
            loadContext.setView("learningPath.browse");

            List<LearningPath> list = dataManager.loadList(loadContext);
            for (LearningPath learningPath : list) {
                setAvgRate(learningPath);
                setCourseCount(learningPath);
                learningPaths.add(generateLPath(learningPath));
            }
        }
    }

    protected void collectionChanged(CollectionDatasource.CollectionChangeEvent<Course, UUID> e) {
        List<Course> courseList = e.getDs().getItems().stream().collect(Collectors.toList());
        categoryCourses.removeAll();
        if (!courseList.isEmpty()) {
            for (Course course : courseList) {
                categoryCourses.add(createCourseCardAllCourses(course, getWindowManager(), true, null));
            }
        }
        fillLearningPaths(categoryDs.getItem());
    }

    protected void itemChanged(Datasource.ItemChangeEvent<DicCategory> e) {
        if (e.getItem() != null) {
            String categories = getCategoryHierarchy(e.getItem().getId());
            coursesDs.setQuery(String.format("select e " +
                    "from tsadv$Course e " +
                    "where e.category.id in (%s) " +
                    "and e.activeFlag = true order by e.name", categories));
            ((CollectionDatasource.SupportsPaging) coursesDs).setFirstResult(0);
            coursesDs.refresh();
        } else {
            coursesDs.setQuery("select e from tsadv$Course e where 1<>1 ");
            ((CollectionDatasource.SupportsPaging) coursesDs).setFirstResult(0);
            coursesDs.refresh();
        }
    }

    protected void selectedTabChanged(TabSheet.SelectedTabChangeEvent event) {
        if (event.getSelectedTab().getName().equalsIgnoreCase("learningPath")) {
            fillLearningPaths(categoryDs.getItem());
        }
    }
}