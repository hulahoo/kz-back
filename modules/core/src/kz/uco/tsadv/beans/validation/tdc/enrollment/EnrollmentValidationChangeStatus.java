package kz.uco.tsadv.beans.validation.tdc.enrollment;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswer;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackTemplate;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.ValidationException;

@Component
public class EnrollmentValidationChangeStatus implements EnrollmentValidation {

    @Inject
    private TransactionalDataManager tdm;

    @Inject
    private DataManager dataManager;

    @Transactional
    @Override
    public void validate(Enrollment enrollment) throws ValidationException {
        if (enrollment.getStatus().equals(EnrollmentStatus.COMPLETED)) {
            long usersAnsweredFeedbacks = dataManager.getCount(LoadContext.create(LearningFeedbackTemplate.class).setQuery(LoadContext.createQuery("" +
                    "select distinct cfpa.feedbackTemplate " +
                    "from tsadv$CourseFeedbackPersonAnswer cfpa " +
                    "   join cfpa.course c " +
                    "where cfpa.personGroup.id = :personGroupId " +
                    "   and c.id = :courseId ")
                    .setParameter("courseId", enrollment.getCourse().getId())
                    .setParameter("personGroupId", enrollment.getPersonGroup().getId())));
            long totalFeedbackCount = enrollment.getCourse().getFeedbackTemplates().size();
            if (usersAnsweredFeedbacks != totalFeedbackCount) {
                throw new ValidationException("Пользователь ответил не на все обратные связи");
            }
        }
    }
}
