package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.actions.list.RemoveAction;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.FileDescriptorResource;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Image;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@UiController("tsadv$Course.browse")
@UiDescriptor("course-browse.xml")
@LookupComponent("coursesTable")
@LoadDataBeforeShow
public class CourseBrowse extends StandardLookup<Course> {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected GroupTable<Course> coursesTable;
    @Named("coursesTable.remove")
    protected RemoveAction<Course> coursesTableRemove;
    @Inject
    protected UiComponents uiComponents;

    @Subscribe
    protected void onInit(InitEvent event) {
//        coursesTable.addGeneratedColumn(
//                "logo",
//                this::renderAvatarImageComponent
//        );
//        String query = "select e from tsadv$Course e where 1 = 1";
//
//        MapScreenOptions options = (MapScreenOptions) event.getOptions();
//        if (options.getParams().containsKey("fromMyCourses")) {
//            UUID personGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
//            if (personGroupId == null) {
//                query = query + " and 1=0";
//                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
//                        .withCaption(messageBundle.getMessage("user.hasnt.related.person")).show();
//            } else {
//                query = query + String.format(" and e.id in (select ct.course.id" +
//                                "                        from tsadv$CourseTrainer ct" +
//                                "                       where ct.trainer.employee.id = %s)",
//                        "'" + personGroupId + "'");
//            }
//            coursesTableCreate.setEnabled(false);
//            coursesTableCreate.setVisible(false);
//            coursesDl.setQuery(query);
//            coursesDl.load();
//        }
//
//        if (options.getParams().containsKey(StaticVariable.EXIST_COURSE)) {
//            query = String.format(query +
//                    " and e.id not in (%s)", options.getParams().get(StaticVariable.EXIST_COURSE));
//
//            coursesDl.setQuery(query);
//            coursesDl.load();
//        }
//
//        if (options.getParams().containsKey(StaticVariable.COURSE_CATEGORY_FILTER)) {
//            List list = courseService.getCategoryHierarchy(String.valueOf(
//                    options.getParams().get(StaticVariable.COURSE_CATEGORY_FILTER)));
//
//            StringBuilder sb = new StringBuilder();
//            for (Object uuid : list) {
//                sb.append("'").append(uuid).append("',");
//            }
//            String existCourses = sb.toString().substring(0, sb.toString().length() - 1);
//
//            query = String.format(query +
//                    " and e.category.id in (%s)", existCourses);
//
//            coursesDl.setQuery(query);
//            coursesDl.load();
//        }
        coursesTable.addSelectionListener(courseSelectionEvent -> {
            if (courseSelectionEvent != null && courseSelectionEvent.getSelected().size() > 0) {
                List<Enrollment> enrollmentList = getEnrollments(courseSelectionEvent.getSelected().iterator().next());
                if (!enrollmentList.isEmpty()) {
                    coursesTableRemove.setEnabled(false);
                } else {
                    coursesTableRemove.setEnabled(true);
                }
            }
        });
    }

    protected Component renderAvatarImageComponent(Course course) {
        FileDescriptor photoFile = course.getLogo();
        if (photoFile == null) {
            return null;
        }
        Image image = smallAvatarImage();
        image.setSource(FileDescriptorResource.class)
                .setFileDescriptor(photoFile);

        return image;
    }

    protected Image smallAvatarImage() {
        Image image = uiComponents.create(Image.class);
        image.setScaleMode(Image.ScaleMode.CONTAIN);
        image.setHeight("50");
        image.setWidth("100");
        image.setStyleName("avatar-icon-small");
        return image;
    }


//    @Override
//    public void init(Map<String, Object> params) {
//        super.init(params);
//
//        String query = "select e from tsadv$Course e where 1 = 1";
//
//
//        if (params.containsKey("fromMyCourses")) {
//            UUID personGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
//            if (personGroupId == null) {
//                query = query + " and 1=0";
//                showNotification(getMessage("msg.warning.title"), getMessage("user.hasnt.related.person"), NotificationType.TRAY);
//            } else {
//                query = query + String.format(" and e.id in (select ct.course.id" +
//                                "                        from tsadv$CourseTrainer ct" +
//                                "                       where ct.trainer.employee.id = %s)",
//                        "'" + personGroupId + "'");
//            }
//            coursesTableCreate.setEnabled(false);
//            coursesTableCreate.setVisible(false);
//            coursesDs.setQuery(query);
//        }
//
//        if (params.containsKey(StaticVariable.EXIST_COURSE)) {
//            query = String.format(query +
//                    " and e.id not in (%s)", params.get(StaticVariable.EXIST_COURSE));
//
//            coursesDs.setQuery(query);
//        }
//
//        if (params.containsKey(StaticVariable.COURSE_CATEGORY_FILTER)) {
//            List list = courseService.getCategoryHierarchy(String.valueOf(params.get(StaticVariable.COURSE_CATEGORY_FILTER)));
//
//            StringBuilder sb = new StringBuilder("");
//            for (Object uuid : list) {
//                sb.append("'").append(uuid).append("',");
//            }
//            String existCourses = sb.toString().substring(0, sb.toString().length() - 1);
//
//            query = String.format(query +
//                    " and e.category.id in (%s)", existCourses);
//
//            coursesDs.setQuery(query);
//        }
//
//        coursesDs.addItemChangeListener(e -> {
//            Course course = e.getItem();
//            if (course != null) {
//                List<Enrollment> enrollments = getEnrollments(course);
//                if (!enrollments.isEmpty()) {
//                    coursesTableRemove.setEnabled(false);
//                } else {
//                    coursesTableRemove.setEnabled(true);
//                }
//            }
//        });
//        coursesTableCreate.setAfterCommitHandler(entity -> coursesDs.refresh());
//    }

    protected List<Enrollment> getEnrollments(Course course) {
        return dataManager.load(Enrollment.class)
                .query("select e from tsadv$Enrollment e where e.course.id = :courseId")
                .parameter("courseId", course.getId())
                .view("enrollment.for.course")
                .list();
    }

//    public void createEnrollments() {
//        Map<String, Object> param = new HashMap<>();
//        param.put("courseId", coursesTable.getSingleSelected().getId());
//        openWindow("enrollments-for-course", WindowManager.OpenType.THIS_TAB, param);
//    }

//    public void openResults() {
//        Map<String, Object> param = new HashMap<>();
//        param.put("courseId", coursesTable.getSingleSelected());
//        openWindow("results-for-course", WindowManager.OpenType.THIS_TAB, param);
//    }

//    public void editAction() {
//        AbstractEditor abstractEditor = openEditor(coursesDs.getItem(), WindowManager.OpenType.THIS_TAB);
//        abstractEditor.addCloseWithCommitListener(() -> coursesDs.refresh());
//    }
}