package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.dictionary.DicHiringMemberType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NamePattern("%s|id")
@Table(name = "TSADV_HIRING_STEP_MEMBER")
@Entity(name = "tsadv$HiringStepMember")
public class HiringStepMember extends AbstractParentEntity {
    private static final long serialVersionUID = -7079981157253542180L;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HIRING_STEP_ID")
    protected HiringStep hiringStep;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HIRING_MEMBER_TYPE_ID")
    protected DicHiringMemberType hiringMemberType;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    protected DicHrRole role;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PERSON_GROUP_ID")
    protected PersonGroupExt userPersonGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;
    @Column(name = "MAIN_INTERVIEWER", nullable = false)
    protected Boolean mainInterviewer = false;

    public void setMainInterviewer(Boolean mainInterviewer) {
        this.mainInterviewer = mainInterviewer;
    }

    public Boolean getMainInterviewer() {
        return mainInterviewer;
    }


    public DicHrRole getRole() {
        return role;
    }

    public void setRole(DicHrRole role) {
        this.role = role;
    }


    public void setHiringStep(HiringStep hiringStep) {
        this.hiringStep = hiringStep;
    }

    public HiringStep getHiringStep() {
        return hiringStep;
    }

    public void setHiringMemberType(DicHiringMemberType hiringMemberType) {
        this.hiringMemberType = hiringMemberType;
    }

    public DicHiringMemberType getHiringMemberType() {
        return hiringMemberType;
    }


    public void setUserPersonGroup(PersonGroupExt userPersonGroup) {
        this.userPersonGroup = userPersonGroup;
    }

    public PersonGroupExt getUserPersonGroup() {
        return userPersonGroup;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}