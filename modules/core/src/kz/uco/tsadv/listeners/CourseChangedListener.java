package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.global.ViewBuilder;
import kz.uco.tsadv.entity.ImageSize;
import kz.uco.tsadv.entity.ResizedImage;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.service.ImageResizeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.UUID;

@Component("tsadv_CourseChangedListener")
public class CourseChangedListener {

    @Inject
    private DataManager dataManager;

    @Inject
    private ImageResizeService imageResizeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<Course, UUID> event) {
        if (event.getChanges().isChanged("logo")) {
            final Course course = dataManager.load(event.getEntityId())
                    .view(ViewBuilder.of(Course.class)
                            .add("logo", View.MINIMAL)
                            .build())
                    .one();
            if (course.getLogo() != null) {
                dataManager.loadList(LoadContext.create(ImageSize.class)
                        .setView(View.LOCAL))
                        .forEach(is -> imageResizeService.resizeImage(is, course.getLogo()));
            }
        }
    }
}