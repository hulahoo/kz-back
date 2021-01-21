package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.service.CourseService;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CourseBrowse extends AbstractLookup {

    private static final String IMAGE_CELL_HEIGHT = "50px";

    @Inject
    protected DataManager dataManager;

    @Inject
    protected GroupDatasource<Course, UUID> coursesDs;
    @Inject
    protected Button enrollmentsBtn;
    @Inject
    protected CourseService courseService;
    @Inject
    protected Button resultsBtn;
    @Named("coursesTable.create")
    protected CreateAction coursesTableCreate;
    @Inject
    protected GroupTable<Course> coursesTable;

    @Named("coursesTable.remove")
    protected RemoveAction coursesTableRemove;
    @Inject
    protected CommonService commonService;
    @Inject
    protected UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String query = "select e from tsadv$Course e where 1 = 1";


        if (params.containsKey("fromMyCourses")) {
            UUID personGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
            if (personGroupId == null) {
                query = query + " and 1=0";
                showNotification(getMessage("msg.warning.title"), getMessage("user.hasnt.related.person"), NotificationType.TRAY);
            } else {
                query = query + String.format(" and e.id in (select ct.course.id" +
                                "                        from tsadv$CourseTrainer ct" +
                                "                       where ct.trainer.employee.id = %s)",
                        "'" + personGroupId + "'");
            }
            coursesTableCreate.setEnabled(false);
            coursesTableCreate.setVisible(false);
            coursesDs.setQuery(query);
        }

        if (params.containsKey(StaticVariable.EXIST_COURSE)) {
            query = String.format(query +
                    " and e.id not in (%s)", params.get(StaticVariable.EXIST_COURSE));

            coursesDs.setQuery(query);
        }

        if (params.containsKey(StaticVariable.COURSE_CATEGORY_FILTER)) {
            List list = courseService.getCategoryHierarchy(String.valueOf(params.get(StaticVariable.COURSE_CATEGORY_FILTER)));

            StringBuilder sb = new StringBuilder("");
            for (Object uuid : list) {
                sb.append("'").append(uuid).append("',");
            }
            String existCourses = sb.toString().substring(0, sb.toString().length() - 1);

            query = String.format(query +
                    " and e.category.id in (%s)", existCourses);

            coursesDs.setQuery(query);
        }

        coursesDs.addItemChangeListener(e -> {
            Course course = e.getItem();
            if (course != null) {
                List<Enrollment> enrollments = getEnrollments(course);
                if (!enrollments.isEmpty()) {
                    coursesTableRemove.setEnabled(false);
                } else {
                    coursesTableRemove.setEnabled(true);
                }
            }
        });
        coursesTableCreate.setAfterCommitHandler(entity -> coursesDs.refresh());
    }

    protected List<Enrollment> getEnrollments(Course course) {
        return commonService.getEntities(Enrollment.class,
                "select e from tsadv$Enrollment e where e.course.id = :courseId",
                ParamsMap.of("courseId", course.getId()), "enrollment.for.course");
    }

    @Override
    public void ready() {
        super.ready();
        enrollmentsBtn.setEnabled(coursesTable.getSingleSelected() != null);
        resultsBtn.setEnabled(coursesTable.getSingleSelected() != null);
        coursesDs.addItemChangeListener(e -> {
            itemChangeListener(e);
        });
    }

    protected void itemChangeListener(Datasource.ItemChangeEvent<Course> e) {
        enrollmentsBtn.setEnabled(coursesTable.getSingleSelected() != null);
        resultsBtn.setEnabled(coursesTable.getSingleSelected() != null);
    }

    public Component generateCourseImageCell(Course course) {
        return Utils.getCourseImageEmbedded(course, IMAGE_CELL_HEIGHT, null);
    }

    public void createEnrollments() {
        Map<String, Object> param = new HashMap<>();
        param.put("courseId", coursesTable.getSingleSelected().getId());
        openWindow("enrollments-for-course", WindowManager.OpenType.THIS_TAB, param);
    }

    public void openResults() {
        Map<String, Object> param = new HashMap<>();
        param.put("courseId", coursesTable.getSingleSelected());
        openWindow("results-for-course", WindowManager.OpenType.THIS_TAB, param);
    }

    public void editAction() {
        AbstractEditor abstractEditor = openEditor(coursesDs.getItem(), WindowManager.OpenType.THIS_TAB);
        abstractEditor.addCloseWithCommitListener(() -> coursesDs.refresh());
    }
}