package kz.uco.tsadv.listeners;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.events.AttributeChanges;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import com.haulmont.reports.app.service.ReportService;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.tsadv.beans.validation.tdc.enrollment.EnrollmentValidation;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
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
    protected NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected LmsService lmsService;
    @Inject
    protected FileStorageAPI fileStorageAPI;
    @Inject
    protected ReportService reportService;
    @Inject
    protected Metadata metadata;
    @Inject
    private TransactionalDataManager transactionalDataManager;
    @Inject
    private List<EnrollmentValidation> enrollmentValidations;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<Enrollment, UUID> event) {
        if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            if (event.getChanges().isChanged("status")) {
                Enrollment enrollment = transactionalDataManager.load(event.getEntityId())
                        .view("enrollment.validation.status")
                        .one();
                for (EnrollmentValidation enrollmentValidation : enrollmentValidations) {
                    enrollmentValidation.validate(enrollment);
                }
            }
        }
    }

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

                    TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                            .query("select e from tsadv$UserExt e " +
                                    " where e.personGroup = :personGroup")
                            .parameter("personGroup", enrollment.getPersonGroup())
                            .list().stream().findFirst().orElse(null);
                    if (tsadvUser != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("courseName", enrollment.getCourse().getName());
                        map.put("personFullName", enrollment.getPersonGroup().getFullName());
                        notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentApproved",
                                tsadvUser, map);
                    }

                } else if (!EnrollmentStatus.COMPLETED.equals(EnrollmentStatus.fromId(oldValue))
                        && EnrollmentStatus.COMPLETED.equals(enrollment.getStatus())) {

                    TsadvUser user = dataManager.load(TsadvUser.class)
                            .query("select e from tsadv$UserExt e " +
                                    " where e.personGroup = :personGroup")
                            .parameter("personGroup", enrollment.getPersonGroup())
                            .list().stream().findFirst().orElse(null);
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseName", enrollment.getCourse().getName());

//                    sendNotificationCompleted(enrollment);
                    if (generateCertificate(enrollment)) {
                        CommitContext commitContext = new CommitContext();
                        CourseCertificate courseCertificate = enrollment.getCourse().getCertificate() != null
                                && !enrollment.getCourse().getCertificate().isEmpty()
                                ? enrollment.getCourse().getCertificate().get(0)
                                : null;
                        if (courseCertificate != null) {

                            FileDescriptor fd = reportService.createAndSaveReport(courseCertificate.getCertificate(),
                                    ParamsMap.of("enrollment", enrollment), enrollment.getCourse().getName());


                            if (fd != null) {
                                List<EnrollmentCertificateFile> ecfList = dataManager.load(EnrollmentCertificateFile.class)
                                        .query("select e from tsadv$EnrollmentCertificateFile e " +
                                                " where e.enrollment = :enrollment ")
                                        .parameter("enrollment", enrollment)
                                        .view("enrollmentCertificateFile.with.certificateFile")
                                        .list();
                                ecfList.forEach(commitContext::addInstanceToRemove);

                                EnrollmentCertificateFile ecf = metadata.create(EnrollmentCertificateFile.class);
                                ecf.setCertificateFile(fd);
                                ecf.setEnrollment(enrollment);
                                commitContext.addInstanceToCommit(ecf);

                                dataManager.commit(commitContext);

                                EmailAttachment[] emailAttachments = new EmailAttachment[0];
                                emailAttachments = getEmailAttachments(fd, emailAttachments);
                                notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
                                        user, map, emailAttachments);
                            } else {
                                notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
                                        user, map);
                            }
                        } else {
                            notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
                                    user, map);
                        }
                    } else {
                        notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
                                user, map);
                    }
                }
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {

            if (EnrollmentStatus.APPROVED.equals(enrollment.getStatus())) {

                TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e " +
                                " where e.personGroup = :personGroup")
                        .parameter("personGroup", enrollment.getPersonGroup())
                        .list().stream().findFirst().orElse(null);
                if (tsadvUser != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseName", enrollment.getCourse().getName());
                    map.put("personFullName", enrollment.getPersonGroup().getFullName());
                    notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentApproved",
                            tsadvUser, map);
                }
            } else {
                sendNotification(enrollment, "tdc.trainer.newEnrollment");
            }
        }

    }

    protected boolean generateCertificate(Enrollment enrollment) {
        boolean success = true;
        if (enrollment.getCourse().getSelfEnrollment()) {
            for (CourseSection section : enrollment.getCourse().getSections()) {
                List<CourseSectionAttempt> courseSectionAttemptList = dataManager.load(CourseSectionAttempt.class)
                        .query("select e from tsadv$CourseSectionAttempt e " +
                                " where e.success = true " +
                                " and e.courseSection = :section")
                        .parameter("section", section)
                        .view("courseSectionAttempt.edit")
                        .list();
                if (courseSectionAttemptList.size() < 1) {
                    success = false;
                }
            }
        }
        return success;
    }

    protected void sendNotification(Enrollment enrollment, String notificationCode) {
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
                    notificationSenderAPIService.sendParametrizedNotification(notificationCode,
                            tsadvUser, map);
                }
            });
        }
    }

//    protected void sendNotificationCompleted(Enrollment enrollment) {
//        TsadvUser user = dataManager.load(TsadvUser.class)
//                .query("select e from tsadv$UserExt e " +
//                        " where e.personGroup = :personGroup")
//                .parameter("personGroup", enrollment.getPersonGroup())
//                .list().stream().findFirst().orElse(null);
//        if (user != null) {
//
//            UUID fileDescriptorId = UUID.fromString(lmsService.getCertificate(String.valueOf(enrollment.getId())));
//
//            FileDescriptor fileDescriptor = dataManager.load(FileDescriptor.class)
//                    .query("select e from SYS_FILE e " +
//                            " where e.id = :fileDescriptorId")
//                    .parameter("fileDescriptorId", fileDescriptorId)
//                    .list().stream().findFirst().orElse(null);
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("courseName", enrollment.getCourse().getName());
//
//            if (fileDescriptor != null) {
//                EmailAttachment[] emailAttachments = new EmailAttachment[0];
//                emailAttachments = getEmailAttachments(fileDescriptor, emailAttachments);
//                notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
//                        user, map, emailAttachments);
//            } else {
//                notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
//                        user, map);
//            }
//        }
//    }

    protected EmailAttachment[] getEmailAttachments(FileDescriptor fileDescriptor, EmailAttachment[] emailAttachments) {
        try {
            EmailAttachment emailAttachment = new EmailAttachment(fileStorageAPI.loadFile(fileDescriptor), "Сертификат");
            emailAttachments = (EmailAttachment[]) ArrayUtils.add(emailAttachments, emailAttachment);
        } catch (FileStorageException e) {
            e.printStackTrace();
        }
        return emailAttachments;
    }
}