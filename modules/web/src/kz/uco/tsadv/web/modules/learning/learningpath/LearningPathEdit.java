package kz.uco.tsadv.web.modules.learning.learningpath;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.learning.dictionary.diccategory.DicCategoryBrowse;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.LearningPath;
import kz.uco.tsadv.modules.learning.model.LearningPathCourse;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LearningPathEdit extends AbstractEditor<LearningPath> {

    @Inject
    private Table<Course> coursesTable;

    @Named("fieldGroup.category")
    private PickerField categoryField;

    @Named("coursesTable.create")
    private CreateAction coursesTableCreate;

    @Inject
    private CollectionDatasource<LearningPathCourse, UUID> coursesDs;

    @Inject
    private Metadata metadata;

    @Inject
    private Button addCourse;
    @Inject
    private Button editCourse;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        BaseAction action = new BaseAction("lookup") {
            @Override
            public String getIcon() {
                return "components/pickerfield/images/lookup-btn.png";
            }

            @Override
            public void actionPerform(Component component) {
//                DicCategoryBrowse regionBrowse = (DicCategoryBrowse) openLookup("tsadv$DicCategory.browse",
//                        items -> {
//                        }, // lookup handler is not used
//                        WindowManager.OpenType.DIALOG);
//
//                regionBrowse.addCloseListener(actionId -> {
//                    if (Window.SELECT_ACTION_ID.equals(actionId)) {
//                        DicCategory selected = regionBrowse.getDicCategoriesDs().getItem();
//                        categoryField.setValue(selected);
//                    }
//                });
            }
        };

        categoryField.removeAllActions();
        categoryField.addAction(action);
        categoryField.addClearAction();

        initEnabledComponent();

        coursesDs.addItemChangeListener(new Datasource.ItemChangeListener<LearningPathCourse>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<LearningPathCourse> e) {
                initEnabledComponent();
            }
        });

//        categoryField.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                if (e.getValue() == null) {
//                    Collection<UUID> list = coursesDs.getItemIds();
//
//                    for (UUID uuid : list) {
//                        coursesDs.removeItem(coursesDs.getItem(uuid));
//                    }
//                }
//                initEnabledComponent();
//            }
//        });
    }

    private void initEnabledComponent() {
        addCourse.setEnabled(categoryField.getValue() != null);
        editCourse.setEnabled(coursesDs.getItem() != null && categoryField.getValue() != null);
    }

    public void addCourse() {
        openCourseBrowse(true);
    }

    public void editCourse() {
        openCourseBrowse(false);
    }

    private void openCourseBrowse(boolean isCreate) {
        StringBuilder sb = new StringBuilder("");
        for (LearningPathCourse learningPathCourse : coursesDs.getItems()) {
            sb.append("'").append(learningPathCourse.getCourse().getId()).append("',");
        }

        Map<String, Object> params = new HashMap<>();
        if (!sb.toString().equals("")) {
            String existCourses = sb.toString().substring(0, sb.toString().length() - 1);
            params.put(StaticVariable.EXIST_COURSE, existCourses);
        }

        params.put(StaticVariable.COURSE_CATEGORY_FILTER, ((DicCategory) categoryField.getValue()).getId().toString());

        openLookup("tsadv$Course.browse", new Lookup.Handler() {
            @Override
            public void handleLookup(Collection items) {
                for (Object item : items) {
                    Course course = (Course) item;

                    if (isCreate) {
                        LearningPathCourse learningPathCourse = metadata.create(LearningPathCourse.class);
                        learningPathCourse.setLearningPath(getItem());
                        learningPathCourse.setCourse(course);
                        learningPathCourse.setOrderNumber(nextOrderNumber());
                        coursesDs.addItem(learningPathCourse);
                    } else {
                        LearningPathCourse learningPathCourse = coursesDs.getItem();
                        learningPathCourse.setCourse(course);
                        coursesDs.modifyItem(learningPathCourse);
                    }
                }
            }
        }, WindowManager.OpenType.DIALOG, params);
    }

    private int nextOrderNumber() {
        int order = 1;
        if (coursesDs.size() != 0) {
            int max = 0;

            for (LearningPathCourse item : coursesDs.getItems()) {
                if (item.getOrderNumber() > max) max = item.getOrderNumber();
            }

            order = max + 1;
        }
        return order;
    }

    @Override
    public void ready() {
        super.ready();

        coursesTable.getColumn("orderNumber").setWidth(65);
        coursesTable.getColumn("logo").setWidth(65);
    }

    public Component generateCourseLogo(LearningPathCourse learningPathCourse) {
        return Utils.getCourseImageEmbedded(learningPathCourse.getCourse(), "50px", null);
    }
}