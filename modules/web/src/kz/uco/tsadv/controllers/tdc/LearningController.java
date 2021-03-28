package kz.uco.tsadv.controllers.tdc;


import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import kz.uco.tsadv.lms.pojo.LearningHistoryPojo;
import kz.uco.tsadv.modules.learning.model.CourseSection;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.EnrollmentCertificateFile;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("learning-history")
public class LearningController {

    @Inject
    private DataManager dataManager;

    @GetMapping
    public List<LearningHistoryPojo> getLearningHistory(String personGroupId) {
        List<Enrollment> enrollments = dataManager.loadList(LoadContext.create(Enrollment.class).setQuery(LoadContext.createQuery("" +
                "select e " +
                "from tsadv$Enrollment e " +
                "where e.personGroup.id = :personGroupId ")
                .setParameter("personGroupId", UUID.fromString(personGroupId)))
                .setView("learning-history"));

        List<Enrollment> completedEnrollments = enrollments.stream()
                .peek(e -> e.getCourse().getSections()
                        .forEach(s -> {
                            CourseSectionAttempt lastCourseSectionAttempt = s.getCourseSectionAttempts()
                                    .stream()
                                    .filter(csa -> csa.getEnrollment().getId().equals(e.getId()))
                                    .max(Comparator.comparing(CourseSectionAttempt::getAttemptDate))
                                    .orElse(null);
                            s.setCourseSectionAttempts(lastCourseSectionAttempt == null
                                    ? null
                                    : Collections.singletonList(dataManager.reload(lastCourseSectionAttempt, "course-section-attempt")));
                        }))
                .collect(Collectors.toList());

        return completedEnrollments.stream()
                .map(e -> {
                    List<CourseSection> sortedCourseSections = e.getCourse().getSections().stream().sorted((cs1, cs2) -> cs2.getOrder().compareTo(cs1.getOrder())).collect(Collectors.toList());
                    CourseSection courseSection = sortedCourseSections.stream().filter(cs -> CollectionUtils.isNotEmpty(cs.getCourseSectionAttempts()) && cs.getCourseSectionAttempts().get(0).getTestResult() != null).findFirst().orElse(null);
                    return new LearningHistoryPojo.Builder()
                            .trainer(e.getCourse().getCourseTrainers().stream().map(t -> t.getTrainer().getTrainerFullName()).collect(Collectors.joining(" ,")))
                            .startDate(getLearningHistoryStartDate(e, sortedCourseSections).orElse(null))
                            .endDate(getLearningHistoryEndDate(e, sortedCourseSections).orElse(null))
                            .course(e.getCourse().getName())
                            .result(courseSection != null ? courseSection.getCourseSectionAttempts().get(0).getTestResult() : null)
                            .certificate(CollectionUtils.isEmpty(e.getCertificateFiles()) ? null :
                                    e.getCertificateFiles().stream()
                                            .map(EnrollmentCertificateFile::getCertificateFile)
                                            .filter(Objects::nonNull)
                                            .map(BaseUuidEntity::getId)
                                            .map(UUID::toString)
                                            .findAny().orElse(null))
                            .build();
                })
                .collect(Collectors.toList());
    }


    protected Optional<Date> getLearningHistoryStartDate(Enrollment enrollment, List<CourseSection> sortedCourseSections) {
        try {
            return Optional.ofNullable(enrollment.getCourseSchedule() != null
                    ? enrollment.getCourseSchedule().getStartDate()
                    : sortedCourseSections.get(0).getCourseSectionAttempts().get(0).getAttemptDate());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    protected Optional<Date> getLearningHistoryEndDate(Enrollment enrollment, List<CourseSection> sortedCourseSections) {
        try {
            return Optional.ofNullable(enrollment.getCourseSchedule() != null
                    ? enrollment.getCourseSchedule().getStartDate()
                    : sortedCourseSections.get(enrollment.getCourse().getSections().size() - 1).getCourseSectionAttempts().get(0).getAttemptDate());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}