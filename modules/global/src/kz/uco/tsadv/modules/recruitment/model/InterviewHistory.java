package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@NamePattern("%s|id")
@Table(name = "TSADV_INTERVIEW_HISTORY")
@Entity(name = "tsadv$InterviewHistory")
public class InterviewHistory extends AbstractParentEntity {
    private static final long serialVersionUID = -1330113670773726023L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INTERVIEW_ID")
    protected Interview interview;

    @Column(name = "INTERVIEW_STATUS", nullable = false)
    protected Integer interviewStatus;

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterviewStatus(InterviewStatus interviewStatus) {
        this.interviewStatus = interviewStatus == null ? null : interviewStatus.getId();
    }

    public InterviewStatus getInterviewStatus() {
        return interviewStatus == null ? null : InterviewStatus.fromId(interviewStatus);
    }


}