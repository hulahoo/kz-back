package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.AttributeChanges;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.model.Job;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component("tsadv_JobChangedListener")
public class JobChangedListener {

    @Inject
    private TransactionalDataManager txDataManager;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<Job, UUID> event) {
        Id<Job, UUID> entityId = event.getEntityId();

        Job job = txDataManager.load(entityId).view("job.edit").one();

        JobGroup jobGroup = txDataManager.load(JobGroup.class)
                .query("select j from tsadv$JobGroup j where j.id = :id")
                .parameter("id", job.getGroup().getId())
                .view("jobGroup.browse")
                .one();

        if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            if (job.getEndDate().compareTo(BaseCommonUtils.getSystemDate()) >= 0
                    && job.getStartDate().compareTo(BaseCommonUtils.getSystemDate()) <= 0){

                jobGroup.setJobNameLang1(job.getJobNameLang1());
                jobGroup.setJobNameLang2(job.getJobNameLang2());
                jobGroup.setJobNameLang3(job.getJobNameLang3());
                jobGroup.setJobNameLang4(job.getJobNameLang4());
                jobGroup.setJobNameLang5(job.getJobNameLang5());
                txDataManager.save(jobGroup);
            }
        }

        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
            jobGroup.setJobNameLang1(job.getJobNameLang1());
            jobGroup.setJobNameLang2(job.getJobNameLang2());
            jobGroup.setJobNameLang3(job.getJobNameLang3());
            jobGroup.setJobNameLang4(job.getJobNameLang4());
            jobGroup.setJobNameLang5(job.getJobNameLang5());
            txDataManager.save(jobGroup);
        }
    }
}