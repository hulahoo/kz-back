package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.dictionary.DicInterviewReason;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import com.haulmont.cuba.core.entity.FileDescriptor;

@Listeners("tsadv_InterviewListener")
@NamePattern("%s|id")
@Table(name = "TSADV_INTERVIEW")
@Entity(name = "tsadv$Interview")
public class Interview extends AbstractParentEntity {
    private static final long serialVersionUID = 8614631575611860213L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_REQUEST_ID")
    protected JobRequest jobRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_HIRING_STEP_ID")
    protected RequisitionHiringStep requisitionHiringStep;

    @Temporal(TemporalType.DATE)
    @Column(name = "INTERVIEW_DATE")
    protected Date interviewDate;

    @Column(name = "INTERVIEW_STATUS", nullable = false)
    protected Integer interviewStatus;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "interview")
    protected List<InterviewDetail> interviewDetails;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "interview")
    protected List<InterviewQuestionnaire> questionnaires;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAIN_INTERVIEWER_PERSON_GROUP_ID")
    protected PersonGroupExt mainInterviewerPersonGroup;

    @Temporal(TemporalType.TIME)
    @Column(name = "TIME_FROM")
    protected Date timeFrom;

    @Temporal(TemporalType.TIME)
    @Column(name = "TIME_TO")
    protected Date timeTo;

    @Column(name = "SEND_INVITATION_TO_CANDIDATE")
    protected Boolean sendInvitationToCandidate;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERVIEW_REASON_ID")
    protected DicInterviewReason interviewReason;

    @Column(name = "REASON", length = 2000)
    protected String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLACE_ID")
    protected DicLocation place;

    @Column(name = "IS_SCHEDULED", nullable = false)
    protected Boolean isScheduled = false;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUISITION_ID")
    protected Requisition requisition;

    @Column(name = "MAX_CANDIDATES_COUNT")
    protected Integer maxCandidatesCount;
    @Lob
    @Column(name = "COMMENT_")
    protected String comment;

    @Transient
    @MetaProperty
    protected Double questionnaireMatchPercent;

    @Column(name = "IS_GROUP", nullable = false)
    protected Boolean isGroup = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_INTERVIEW_ID")
    protected Interview groupInterview;

    @Transient
    @MetaProperty
    protected Integer groupDraftCount;

    @Transient
    @MetaProperty
    protected Integer groupOnApprovalCount;

    @Transient
    @MetaProperty
    protected Integer groupPlannedCount;

    @Transient
    @MetaProperty
    protected Integer groupCompletedCount;

    @Transient
    @MetaProperty
    protected Integer groupFailedCount;

    @Transient
    @MetaProperty
    protected Integer groupCancelledCount;

    @Transient
    @MetaProperty
    protected Integer groupOnCancellationCount;

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }


    public Requisition getRequisition() {
        return requisition;
    }

    public void setRequisition(Requisition requisition) {
        this.requisition = requisition;
    }

    public Interview getGroupInterview() {
        return groupInterview;
    }

    public void setGroupInterview(Interview groupInterview) {
        this.groupInterview = groupInterview;
    }



    public void setGroupDraftCount(Integer groupDraftCount) {
        this.groupDraftCount = groupDraftCount;
    }

    public Integer getGroupDraftCount() {
        return groupDraftCount;
    }

    public void setGroupOnApprovalCount(Integer groupOnApprovalCount) {
        this.groupOnApprovalCount = groupOnApprovalCount;
    }

    public Integer getGroupOnApprovalCount() {
        return groupOnApprovalCount;
    }

    public void setGroupPlannedCount(Integer groupPlannedCount) {
        this.groupPlannedCount = groupPlannedCount;
    }

    public Integer getGroupPlannedCount() {
        return groupPlannedCount;
    }

    public void setGroupCompletedCount(Integer groupCompletedCount) {
        this.groupCompletedCount = groupCompletedCount;
    }

    public Integer getGroupCompletedCount() {
        return groupCompletedCount;
    }

    public void setGroupFailedCount(Integer groupFailedCount) {
        this.groupFailedCount = groupFailedCount;
    }

    public Integer getGroupFailedCount() {
        return groupFailedCount;
    }

    public void setGroupCancelledCount(Integer groupCancelledCount) {
        this.groupCancelledCount = groupCancelledCount;
    }

    public Integer getGroupCancelledCount() {
        return groupCancelledCount;
    }

    public void setGroupOnCancellationCount(Integer groupOnCancellationCount) {
        this.groupOnCancellationCount = groupOnCancellationCount;
    }

    public Integer getGroupOnCancellationCount() {
        return groupOnCancellationCount;
    }


    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }




    public void setQuestionnaireMatchPercent(Double questionnaireMatchPercent) {
        this.questionnaireMatchPercent = questionnaireMatchPercent;
    }

    public Double getQuestionnaireMatchPercent() {
        return questionnaireMatchPercent;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


    public DicLocation getPlace() {
        return place;
    }

    public void setPlace(DicLocation place) {
        this.place = place;
    }


    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }




    public void setMaxCandidatesCount(Integer maxCandidatesCount) {
        this.maxCandidatesCount = maxCandidatesCount;
    }

    public Integer getMaxCandidatesCount() {
        return maxCandidatesCount;
    }


    public void setInterviewReason(DicInterviewReason interviewReason) {
        this.interviewReason = interviewReason;
    }

    public DicInterviewReason getInterviewReason() {
        return interviewReason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


    public InterviewStatus getInterviewStatus() {
        return interviewStatus == null ? null : InterviewStatus.fromId(interviewStatus);
    }

    public void setInterviewStatus(InterviewStatus interviewStatus) {
        this.interviewStatus = interviewStatus == null ? null : interviewStatus.getId();
    }


    public void setSendInvitationToCandidate(Boolean sendInvitationToCandidate) {
        this.sendInvitationToCandidate = sendInvitationToCandidate;
    }

    public Boolean getSendInvitationToCandidate() {
        return sendInvitationToCandidate;
    }


    public void setMainInterviewerPersonGroup(PersonGroupExt mainInterviewerPersonGroup) {
        this.mainInterviewerPersonGroup = mainInterviewerPersonGroup;
    }

    public PersonGroupExt getMainInterviewerPersonGroup() {
        return mainInterviewerPersonGroup;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public Date getTimeTo() {
        return timeTo;
    }


    public void setQuestionnaires(List<InterviewQuestionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }

    public List<InterviewQuestionnaire> getQuestionnaires() {
        return questionnaires;
    }


    public void setInterviewDetails(List<InterviewDetail> interviewDetails) {
        this.interviewDetails = interviewDetails;
    }

    public List<InterviewDetail> getInterviewDetails() {
        return interviewDetails;
    }


    public void setJobRequest(JobRequest jobRequest) {
        this.jobRequest = jobRequest;
    }

    public JobRequest getJobRequest() {
        return jobRequest;
    }

    public void setRequisitionHiringStep(RequisitionHiringStep requisitionHiringStep) {
        this.requisitionHiringStep = requisitionHiringStep;
    }

    public RequisitionHiringStep getRequisitionHiringStep() {
        return requisitionHiringStep;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }

    public Date getInterviewDate() {
        return interviewDate;
    }


}