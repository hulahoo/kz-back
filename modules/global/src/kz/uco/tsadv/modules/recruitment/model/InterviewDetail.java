package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@NamePattern("%s|id")
@Table(name = "TSADV_INTERVIEW_DETAIL")
@Entity(name = "tsadv$InterviewDetail")
public class InterviewDetail extends AbstractParentEntity {
    private static final long serialVersionUID = 7472705035206314856L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INTERVIEW_ID")
    protected kz.uco.tsadv.modules.recruitment.model.Interview interview;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INTERVIEWER_PERSON_GROUP_ID")
    protected PersonGroupExt interviewerPersonGroup;

    public void setInterview(kz.uco.tsadv.modules.recruitment.model.Interview interview) {
        this.interview = interview;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterviewerPersonGroup(PersonGroupExt interviewerPersonGroup) {
        this.interviewerPersonGroup = interviewerPersonGroup;
    }

    public PersonGroupExt getInterviewerPersonGroup() {
        return interviewerPersonGroup;
    }


}