package kz.uco.tsadv.controllers.tdc;


import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.global.*;
import kz.uco.tsadv.lms.pojo.LearningHistoryPojo;
import kz.uco.tsadv.modules.learning.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
@RequestMapping("learning-history")
public class LearningController {

    @Inject
    private DataManager dataManager;

    @Inject
    private Messages messages;

    @Inject
    private UserSessionSource userSessionSource;

    @GetMapping
    public List<LearningHistoryPojo> getLearningHistory(@RequestParam UUID personGroupId) {
        List<Enrollment> enrollments = dataManager.loadList(LoadContext.create(Enrollment.class).setQuery(LoadContext.createQuery("" +
                "select e " +
                "from tsadv$Enrollment e " +
                "where e.personGroup.id = :personGroupId ")
                .setParameter("personGroupId", personGroupId))
                .setView("learning-history"));

        List<Enrollment> completedEnrollments = enrollments.stream()
                .peek(e -> e.getCourse().getSections()
                        .forEach(s -> {
                            if (CollectionUtils.isNotEmpty(s.getCourseSectionAttempts())) {
                                CourseSectionAttempt lastCourseSectionAttempt = s.getCourseSectionAttempts()
                                        .stream()
                                        .filter(csa -> csa.getEnrollment().getId().equals(e.getId()))
                                        .max(Comparator.comparing(CourseSectionAttempt::getCreateTs))
                                        .orElse(null);
                                s.setCourseSectionAttempts(lastCourseSectionAttempt == null
                                        ? null
                                        : Collections.singletonList(dataManager.reload(lastCourseSectionAttempt, "course-section-attempt")));
                            }
                        }))
                .collect(Collectors.toList());

        List<CoursePersonNote> notes = dataManager.load(CoursePersonNote.class)
                .query("select e from tsadv_CoursePersonNote e where e.personGroup.id = :personGroupId and e.course.id in :courseId")
                .parameter("courseId", completedEnrollments.stream().map(Enrollment::getCourse).map(BaseUuidEntity::getId).collect(Collectors.toList()))
                .parameter("personGroupId", personGroupId)
                .view(new View(CoursePersonNote.class).addProperty("course").addProperty("note"))
                .list();

        return completedEnrollments.stream()
                .map(e -> {
                    List<CourseSection> sortedCourseSections = e.getCourse().getSections().stream().sorted(Comparator.comparing(CourseSection::getOrder)).collect(Collectors.toList());
                    CourseSection courseSection = sortedCourseSections.stream().filter(cs -> CollectionUtils.isNotEmpty(cs.getCourseSectionAttempts()) && cs.getCourseSectionAttempts().get(0).getTestResult() != null).findFirst().orElse(null);
                    return new LearningHistoryPojo.Builder()
                            .trainer(e.getCourse().getCourseTrainers().stream().map(t -> t.getTrainer().getTrainerFullName()).collect(Collectors.joining(" ,")))
                            .startDate(getLearningHistoryStartDate(e, sortedCourseSections).orElse(null))
                            .endDate(getLearningHistoryEndDate(e, sortedCourseSections).orElse(null))
                            .course(e.getCourse().getName())
                            .courseId(e.getCourse().getId())
                            .enrollmentStatus(messages.getMessage(e.getStatus(), userSessionSource.getLocale()))
                            .result(courseSection != null ? courseSection.getCourseSectionAttempts().get(0).getTestResultPercent() : null)
                            .certificate(CollectionUtils.isEmpty(e.getCertificateFiles()) ? null :
                                    e.getCertificateFiles().stream()
                                            .map(EnrollmentCertificateFile::getCertificateFile)
                                            .filter(Objects::nonNull)
                                            .map(BaseUuidEntity::getId)
                                            .map(UUID::toString)
                                            .findAny().orElse(null))
                            .note(notes.stream()
                                    .filter(note -> note.getCourse().equals(e.getCourse()))
                                    .map(CoursePersonNote::getNote)
                                    .filter(Objects::nonNull)
                                    .findAny()
                                    .orElse(null))
                            .build();
                })
                .collect(Collectors.toList());
    }

    protected Optional<Date> getLearningHistoryStartDate(Enrollment enrollment, List<CourseSection> sortedCourseSections) {
        try {
            Date endDate = getLearningHistoryEndDate(enrollment, sortedCourseSections).orElse(null);
            return Optional.ofNullable(enrollment.getCourseSchedule() != null
                    ? enrollment.getCourseSchedule().getStartDate()
                    : sortedCourseSections.size() > 0 && !CollectionUtils.isEmpty(sortedCourseSections.get(0).getCourseSectionAttempts())
                    ? sortedCourseSections.get(0).getCourseSectionAttempts()
                    .stream()
                    .map(CourseSectionAttempt::getAttemptDate)
                    .filter(Objects::nonNull)
                    .filter(date -> !date.after(endDate) || endDate == null)
                    .max(Date::compareTo)
                    .orElse(null)
                    : null);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    protected Optional<Date> getLearningHistoryEndDate(Enrollment enrollment, List<CourseSection> sortedCourseSections) {
        try {
            return Optional.ofNullable(enrollment.getCourseSchedule() != null
                    ? enrollment.getCourseSchedule().getStartDate()
                    : sortedCourseSections.size() > 0 && !CollectionUtils.isEmpty(sortedCourseSections.get(enrollment.getCourse().getSections().size() - 1).getCourseSectionAttempts())
                    ? sortedCourseSections.get(enrollment.getCourse().getSections().size() - 1).getCourseSectionAttempts()
                    .stream()
                    .map(CourseSectionAttempt::getAttemptDate)
                    .filter(Objects::nonNull)
                    .max(Date::compareTo)
                    .orElse(null)
                    : null);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
