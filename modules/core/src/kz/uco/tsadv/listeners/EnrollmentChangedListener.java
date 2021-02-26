package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.events.AttributeChanges;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.notification.NotificationSenderAPI;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.performance.model.CourseTrainer;
import kz.uco.tsadv.service.LmsService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component("tsadv_EnrollmentChangedListener")
public class EnrollmentChangedListener {
    @Inject
    protected NotificationSenderAPI notificationSender;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected LmsService lmsService;
    @Inject
    protected FileStorageAPI fileStorageAPI;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<Enrollment, UUID> event) {
        AttributeChanges changes = event.getChanges();
        Id<Enrollment, UUID> entityId = event.getEntityId();
        Enrollment enrollment = dataManager.load(entityId).view("enrollment.for.course").one();
        if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {

            for (String attribute : changes.getAttributes()) {

                if (!attribute.equals("status")) continue;

                Integer oldValue = changes.getOldValue(attribute);
                if (!EnrollmentStatus.APPROVED.equals(EnrollmentStatus.fromId(oldValue))
                        && EnrollmentStatus.APPROVED.equals(enrollment.getStatus())) {

                    sendNotification(enrollment, "tdc.student.enrollmentApproved");
                } else if (!EnrollmentStatus.COMPLETED.equals(EnrollmentStatus.fromId(oldValue))
                        && EnrollmentStatus.COMPLETED.equals(enrollment.getStatus())) {

                    sendNotificationCompleted(enrollment);
                }
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {

            if (EnrollmentStatus.APPROVED.equals(enrollment.getStatus())) {

                sendNotification(enrollment, "tdc.student.enrollmentApproved");
            } else {
                sendNotification(enrollment, "tdc.trainer.newEnrollment");
            }
        }
    }

    private void sendNotification(Enrollment enrollment, String notificationCode) {
        List<CourseTrainer> courseTrainers = dataManager.load(CourseTrainer.class)
                .query("select e from tsadv$CourseTrainer e " +
                        " where e.course = :course " +
                        " and current_date between e.dateFrom and e.dateTo " +
                        " and e.trainer.employee is not null")
                .parameter("course", enrollment.getCourse())
                .view("courseTrainer.edit")
                .list();

        if (!courseTrainers.isEmpty()) {
            courseTrainers.forEach(courseTrainer -> {
                TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e " +
                                " where e.personGroup = :personGroup")
                        .parameter("personGroup", courseTrainer.getTrainer().getEmployee())
                        .list().stream().findFirst().orElse(null);
                if (tsadvUser != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseName", enrollment.getCourse().getName());
                    map.put("personFullName", enrollment.getPersonGroup().getFullName());
                    notificationSender.sendParametrizedNotification(notificationCode,
                            tsadvUser, map);
                }
            });
        }
    }

    private void sendNotificationCompleted(Enrollment enrollment) {
        TsadvUser user = dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e " +
                        " where e.personGroup = :personGroup")
                .parameter("personGroup", enrollment.getPersonGroup())
                .list().stream().findFirst().orElse(null);
        if (user != null) {

            UUID fileDescriptorId = UUID.fromString(lmsService.getCertificate(String.valueOf(enrollment.getId())));

            FileDescriptor fileDescriptor = dataManager.load(FileDescriptor.class)
                    .query("select e from SYS_FILE e " +
                            " where e.id = :fileDescriptorId")
                    .parameter("fileDescriptorId", fileDescriptorId)
                    .list().stream().findFirst().orElse(null);

            Map<String, Object> map = new HashMap<>();
            map.put("courseName", enrollment.getCourse().getName());

            if (fileDescriptor != null) {
                EmailAttachment[] emailAttachments = new EmailAttachment[0];
                emailAttachments = getEmailAttachments(fileDescriptor, emailAttachments);
                notificationSender.sendParametrizedNotification("tdc.student.enrollmentClosed",
                        user, map, emailAttachments);
            } else {
                notificationSender.sendParametrizedNotification("tdc.student.enrollmentClosed",
                        user, map);
            }
        }
    }

    private EmailAttachment[] getEmailAttachments(FileDescriptor fileDescriptor, EmailAttachment[] emailAttachments) {
        try {
            EmailAttachment emailAttachment = new EmailAttachment(fileStorageAPI.loadFile(fileDescriptor), "Сертификат");
            emailAttachments = (EmailAttachment[]) ArrayUtils.add(emailAttachments, emailAttachment);
        } catch (FileStorageException e) {
            e.printStackTrace();
        }
        return emailAttachments;
    }
}