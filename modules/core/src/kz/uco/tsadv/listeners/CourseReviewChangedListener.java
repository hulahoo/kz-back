package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseReview;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component("tsadv_CourseReviewChangedListener")
public class CourseReviewChangedListener {

    @Inject
    protected DataManager dataManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<CourseReview, UUID> event) {
        Id<CourseReview, UUID> entityId = event.getEntityId();
        CourseReview courseReview = dataManager.load(entityId).view("courseReview.rate").one();
        List<CourseReview> courseReviewList = dataManager.load(CourseReview.class)
                .query("select e from tsadv$CourseReview e " +
                        " where e.course = :course " +
                        " and e.rate is not null ")
                .parameter("course", courseReview.getCourse())
                .view("courseReview.rate")
                .list();
        double allRate = courseReviewList.stream().mapToDouble(CourseReview::getRate).sum();
        BigDecimal averageScore = allRate > 0.0 && courseReviewList.size() > 0
                ? BigDecimal.valueOf(allRate / courseReviewList.size())
                : BigDecimal.ZERO;

        Course course = courseReview.getCourse();

        course.setRating(averageScore);
        List<CourseReview> allCourseReviewList = dataManager.load(CourseReview.class)
                .query("select e from tsadv$CourseReview e " +
                        " where e.course = :course ")
                .parameter("course", courseReview.getCourse())
                .view("courseReview.rate")
                .list();
        course.setCommentCount(allCourseReviewList.size());
        dataManager.commit(course);
    }
}