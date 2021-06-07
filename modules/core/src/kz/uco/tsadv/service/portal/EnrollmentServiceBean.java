package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.pojo.CategoryCoursePojo;
import kz.uco.tsadv.service.CourseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(EnrollmentService.NAME)
public class EnrollmentServiceBean implements EnrollmentService {

    @Inject
    private DataManager dataManager;

    @Inject
    private CourseService courseService;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public List<CategoryCoursePojo> searchEnrollment(UUID personGroupId) {
        return courseService.mapCoursesToCategory(dataManager.loadList(LoadContext.create(Course.class).setQuery(LoadContext.createQuery("" +
                "select c " +
                "from tsadv$Course c " +
                "   join c.category ca " +
                "   join c.enrollments e " +
                "           on e.personGroup.id = :personGroupId " +
                "where c.activeFlag = true")
                .setParameter("personGroupId", personGroupId))
                .setView("course.list"))
                .stream(), personGroupId);
    }

    @Override
    public List<CategoryCoursePojo> searchEnrollment(UUID personGroupId, String courseName) {
        return courseService.mapCoursesToCategory(dataManager.loadList(LoadContext.create(Course.class).setQuery(LoadContext.createQuery("" +
                "select c " +
                "from tsadv$Course c " +
                "   join c.category ca " +
                "   join c.enrollments e " +
                "       on e.personGroup.id = :personGroupId " +
                "where (lower (c.name) like lower (concat(concat('%', :courseName), '%'))) " +
                "   and c.activeFlag = true")
                .setParameter("personGroupId", personGroupId)
                .setParameter("courseName", courseName))
                .setView("course.list"))
                .stream()
                .filter(course -> course.getName().toLowerCase().contains(courseName.toLowerCase())), personGroupId);

    }
}