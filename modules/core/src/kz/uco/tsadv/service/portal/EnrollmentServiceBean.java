package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(EnrollmentService.NAME)
public class EnrollmentServiceBean implements EnrollmentService {

    @Inject
    private Persistence persistence;

    @Inject
    private DataManager dataManager;

    @Override
    public List<DicCategory> searchEnrollment(UUID userId) {
        return persistence.callInTransaction(em -> {
            List<DicCategory> enrollmentCategory = dataManager.loadList(LoadContext.create(DicCategory.class).setQuery(LoadContext.createQuery("" +
                    "select distinct c " +
                    "            from tsadv$DicCategory c " +
                    "            join c.courses cc " +
                    "            join cc.enrollments e " +
                    "where e.personGroup.id = :userId")
                    .setParameter("userId", userId))
                    .setView("category-enrollment"));
            return searchEnrollmentFilter(enrollmentCategory, userId);
        });
    }

    @Override
    public List<DicCategory> searchEnrollment(String courseName, UUID userId) {
        return persistence.callInTransaction(em -> {
            List<DicCategory> enrollmentCategory = em.createQuery("" +
                    "select distinct c " +
                    "            from tsadv$DicCategory c " +
                    "            join c.courses cc " +
                    "            join cc.enrollments e " +
                    "            where (lower (cc.name) like lower (concat(concat('%', :courseName), '%'))) " +
                    "                and e.personGroup.id = :userId", DicCategory.class)
                    .setParameter("courseName", courseName)
                    .setParameter("userId", userId)
                    .setView(DicCategory.class, "category-enrollment")
                    .getResultList()
                    .stream()
                    .peek(c -> c.setCourses(c.getCourses().stream().filter(course ->
                            course.getName().toLowerCase().contains(courseName.toLowerCase())).collect(Collectors.toList())))
                    .collect(Collectors.toList());
            return searchEnrollmentFilter(enrollmentCategory, userId);
        });
    }

    protected List<DicCategory> searchEnrollmentFilter(List<DicCategory> enrollmentCategory, UUID userId) {
        return enrollmentCategory
                .stream()
                .filter(c -> c.getCourses()
                        .stream()
                        .anyMatch(course -> course.getEnrollments().stream().anyMatch(e -> e.getPersonGroup().getId().equals(userId))))
                .peek(category -> {
                    category.setCourses(category.getCourses()
                            .stream()
                            .filter(course -> course.getEnrollments()
                                    .stream().anyMatch(enrollment -> enrollment.getPersonGroup().getId().equals(userId)))
                            .collect(Collectors.toList()));
                })
                .peek(category -> category.getCourses()
                        .forEach(course -> course.setEnrollments(course.getEnrollments()
                                .stream()
                                .filter(enrollment -> Objects.equals(enrollment.getPersonGroup().getId(), userId))
                                .collect(Collectors.toList()))))
                .collect(Collectors.toList());
    }
}