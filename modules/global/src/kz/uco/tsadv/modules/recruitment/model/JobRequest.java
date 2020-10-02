package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.dictionary.DicJobRequestReason;
import kz.uco.tsadv.modules.recruitment.dictionary.DicSource;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Отклик на Вакансию
 */
@Listeners("tsadv_JobRequestListener")
@NamePattern("%s|id")
@Table(name = "TSADV_JOB_REQUEST", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_TSADV_JOB_REQUEST_UNQ", columnNames = {"CANDIDATE_PERSON_GROUP_ID", "REQUISITION_ID"})
})
@Entity(name = "tsadv$JobRequest")
public class JobRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 915759745655556065L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID")
    protected Requisition requisition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_ID")
    protected DicSource source;

    @Column(name = "OTHER_SOURCE")
    protected String otherSource;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CANDIDATE_PERSON_GROUP_ID")
    protected PersonGroupExt candidatePersonGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "REQUEST_DATE", nullable = false)
    protected Date requestDate;

    @Column(name = "REQUEST_STATUS", nullable = false)
    protected Integer requestStatus;

    @Transient
    @MetaProperty
    protected Double competenceMatchPercent;

    @Transient
    @MetaProperty
    protected Double questionnaireMatchPercent;

    @Transient
    @MetaProperty
    protected Integer passedInterviews;

    @Transient
    @MetaProperty
    protected Integer totalInterviews;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "jobRequest")
    protected List<Interview> interviews;

    @Transient
    @MetaProperty(related = "interviews")
    protected Interview interview;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_REQUEST_REASON_ID")
    protected DicJobRequestReason jobRequestReason;

    @Column(name = "REASON", length = 200)
    protected String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VIDEO_FILE_ID")
    protected FileDescriptor videoFile;


    @Transient
    @MetaProperty(mandatory = true, related = "viewLaters")
    protected Boolean viewLater = false;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "jobRequest")
    protected List<UserExtJobRequestSeting> viewLaters;

    @Column(name = "IS_RESERVED", nullable = false)
    protected Boolean isReserved = false;

    @Transient
    @MetaProperty
    protected String competenceMatchString;

    @NotNull
    @Column(name = "SELECTED_BY_MANAGER", nullable = false)
    protected Boolean selectedByManager = false;

    @NotNull
    @Column(name = "SENT", nullable = false)
    protected Boolean sent = false;

    @Transient
    protected Map<UUID, Double> competenceQuestionnaireTotals;

    @Transient
    protected Map<UUID, Map<UUID, Double>> testResultsQuestionnaireQuestions;
    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public Boolean getSent() {
        return sent;
    }


    public void setSelectedByManager(Boolean selectedByManager) {
        this.selectedByManager = selectedByManager;
    }

    public Boolean getSelectedByManager() {
        return selectedByManager;
    }


    public Requisition getRequisition() {
        return requisition;
    }

    public void setRequisition(Requisition requisition) {
        this.requisition = requisition;
    }



    public void setSource(DicSource source) {
        this.source = source;
    }

    public DicSource getSource() {
        return source;
    }

    public void setOtherSource(String otherSource) {
        this.otherSource = otherSource;
    }

    public String getOtherSource() {
        return otherSource;
    }


    public Map<UUID, Double> getCompetenceQuestionnaireTotals() {
        return competenceQuestionnaireTotals;
    }

    public void setCompetenceQuestionnaireTotals(Map<UUID, Double> competenceQuestionnaireTotals) {
        this.competenceQuestionnaireTotals = competenceQuestionnaireTotals;
    }

    public Map<UUID, Map<UUID, Double>> getTestResultsQuestionnaireQuestions() {
        return testResultsQuestionnaireQuestions;
    }

    public void setTestResultsQuestionnaireQuestions(Map<UUID, Map<UUID, Double>> testResultsQuestionnaireQuestions) {
        this.testResultsQuestionnaireQuestions = testResultsQuestionnaireQuestions;
    }

    public void setCompetenceMatchString(String competenceMatchString) {
        this.competenceMatchString = competenceMatchString;
    }

    public String getCompetenceMatchString() {
        return competenceMatchString;
    }


    public void setIsReserved(Boolean isReserved) {
        this.isReserved = isReserved;
    }

    public Boolean getIsReserved() {
        return isReserved;
    }


    public void setViewLaters(List<UserExtJobRequestSeting> viewLaters) {
        this.viewLaters = viewLaters;
    }

    public List<UserExtJobRequestSeting> getViewLaters() {
        return viewLaters;
    }


    public void setViewLater(Boolean viewLater) {
        this.viewLater = viewLater;
    }

    public Boolean getViewLater() {
        if (PersistenceHelper.isLoaded(this, "viewLaters") && getViewLaters() != null) {
            UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");

            UserExtJobRequestSeting userExtJobRequestSeting = getViewLaters()
                    .stream()
                    .filter(i -> i.getDeleteTs() == null && i.getUserExt().getId().equals(userSessionSource.getUserSession().getAttribute(StaticVariable.USER_EXT_ID)))
                    .findAny().orElse(null);
            if (userExtJobRequestSeting == null)
                this.viewLater = false;
            else
                this.viewLater = userExtJobRequestSeting.getViewLater();
        }
        return this.viewLater;
    }


    public void setVideoFile(FileDescriptor videoFile) {
        this.videoFile = videoFile;
    }

    public FileDescriptor getVideoFile() {
        return videoFile;
    }


    public void setJobRequestReason(DicJobRequestReason jobRequestReason) {
        this.jobRequestReason = jobRequestReason;
    }

    public DicJobRequestReason getJobRequestReason() {
        return jobRequestReason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public void setInterviews(List<Interview> interviews) {
        this.interviews = interviews;
    }

    public List<Interview> getInterviews() {
        return interviews;
    }


    public void setPassedInterviews(Integer passedInterviews) {
        this.passedInterviews = passedInterviews;
    }

    public Integer getPassedInterviews() {
        return passedInterviews;
    }

    public void setTotalInterviews(Integer totalInterviews) {
        this.totalInterviews = totalInterviews;
    }

    public Integer getTotalInterviews() {
        return totalInterviews;
    }


    public void setQuestionnaireMatchPercent(Double questionnaireMatchPercent) {
        this.questionnaireMatchPercent = questionnaireMatchPercent;
    }

    public Double getQuestionnaireMatchPercent() {
        return questionnaireMatchPercent;
    }


    public JobRequestStatus getRequestStatus() {
        return requestStatus == null ? null : JobRequestStatus.fromId(requestStatus);
    }

    public void setRequestStatus(JobRequestStatus requestStatus) {
        this.requestStatus = requestStatus == null ? null : requestStatus.getId();
    }


    public void setCompetenceMatchPercent(Double competenceMatchPercent) {
        this.competenceMatchPercent = competenceMatchPercent;
    }

    public Double getCompetenceMatchPercent() {
//        int avg = 0;
//        int count = 0;
//        for (RequisitionCompetence requisitionCompetence : requisition.getCompetences()) {
//            if (requisitionCompetence.getScaleLevel().getLevelNumber() <=
//                    candidatePersonGroup.getCompetenceElements().stream()
//                            .filter(competenceElement -> competenceElement.getCompetenceGroup().getId()
//                                    .equals(requisitionCompetence.getCompetenceGroup().getId())).findFirst().get().getScaleLevel().getLevelNumber()) {
//                avg+=1;
//
//            }
//            count+=1;
//        }
//        return avg * 1.0/count;
//        TODO check perfomace of this method

        return competenceMatchPercent;
    }




    public void setCandidatePersonGroup(PersonGroupExt candidatePersonGroup) {
        this.candidatePersonGroup = candidatePersonGroup;
    }

    public PersonGroupExt getCandidatePersonGroup() {
        return candidatePersonGroup;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    @MetaProperty
    public String getName() {
        StringBuilder sb = new StringBuilder();

        if (getRequisition() != null)
            sb.append(getRequisition().getCode());

        if (getCandidatePersonGroup() != null && getCandidatePersonGroup().getPerson() != null) {
            sb.append(" (");
            sb.append(getCandidatePersonGroup().getPerson().getFullName());
            sb.append(")");
        }

        return sb.toString();
    }


    public Interview getInterview() {
        if (this.interview == null && PersistenceHelper.isLoaded(this, "interviews") && getInterviews() != null) {
            getInterviews()
                    .stream()
                    .filter(i -> i.getDeleteTs() == null && i.getInterviewStatus() != InterviewStatus.CANCELLED)
                    .sorted((i1, i2) -> {
                        if (i2.getRequisitionHiringStep() != null && i1.getRequisitionHiringStep() != null
                                && i2.getRequisitionHiringStep().getOrder() != null && i1.getRequisitionHiringStep().getOrder() != null) {
                            return i2.getRequisitionHiringStep().getOrder() - i1.getRequisitionHiringStep().getOrder();
                        } else {
                            if (i2.getRequisitionHiringStep() == null) {
                                return 1;
                            } else if (i1.getRequisitionHiringStep() == null) {
                                return -1;
                            }
                        }
                        return 1;
                    })
                    .findFirst()
                    .ifPresent(i -> this.interview = i);
        }
        return this.interview;
    }
}