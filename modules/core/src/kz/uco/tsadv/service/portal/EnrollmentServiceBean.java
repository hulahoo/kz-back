package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.Persistence;
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

    @Override
    public List<DicCategory> searchEnrollment(UUID userId) {
        return persistence.callInTransaction(em -> {
            List<DicCategory> enrollmentCategory = em.createQuery("" +
                    "select c " +
                    "            from tsadv$DicCategory c " +
                    "            join c.courses cc " +
                    "            join cc.enrollments e " +
                    "            where e.personGroup.id = :userId", DicCategory.class)
                    .setParameter("userId", userId)
                    .setView(DicCategory.class, "category-enrollment")
                    .getResultList();
            return searchEnrollmentFilter(enrollmentCategory, userId);
        });
    }

    @Override
    public List<DicCategory> searchEnrollment(String courseName, UUID userId) {
        return persistence.callInTransaction(em -> {
            List<DicCategory> enrollmentCategory = em.createQuery("" +
                    "select c " +
                    "            from tsadv$DicCategory c " +
                    "            join c.courses cc " +
                    "            join cc.enrollments e " +
                    "            where (lower ( cc.name) like :courseName) " +
                    "                and e.personGroup.id = :userId", DicCategory.class)
                    .setParameter("courseName", courseName)
                    .setParameter("userId", userId)
                    .setView(DicCategory.class, "category-enrollment")
                    .getResultList();
            return searchEnrollmentFilter(enrollmentCategory, userId);
        });
    }

    protected List<DicCategory> searchEnrollmentFilter(List<DicCategory> enrollmentCategory, UUID userId) {
        return enrollmentCategory
                .stream()
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