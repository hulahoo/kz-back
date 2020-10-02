package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.administration.enums.HiringStepType;
import kz.uco.tsadv.modules.learning.model.Test;
import kz.uco.tsadv.modules.recruitment.enums.HS_AttemptsControlLevel;
import kz.uco.tsadv.modules.recruitment.enums.HS_Periods;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import kz.uco.tsadv.modules.recruitment.enums.HS_AttemptsControlLevel;
import kz.uco.tsadv.modules.recruitment.enums.HS_Periods;
import kz.uco.tsadv.modules.administration.enums.HiringStepType;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.learning.model.Test;
import javax.validation.constraints.NotNull;

@NamePattern("%s|stepName")
@Table(name = "TSADV_HIRING_STEP")
@Entity(name = "tsadv$HiringStep")
public class HiringStep extends AbstractParentEntity {
    private static final long serialVersionUID = -7405096709630003925L;

    @Column(name = "STEP_NAME", nullable = false)
    protected String stepName;

    @NotNull
    @Column(name = "IS_JOB_TEST", nullable = false)
    protected Boolean isJobTest = false;

    @Column(name = "TYPE_")
    protected String type;

    @Column(name = "ATTEMPTS_CONTROL_LEVEL")
    protected Integer attempts_control_level;

    @Column(name = "PERIOD")
    protected Integer period;

    @Column(name = "ATTEMPTS")
    protected Long attempts;

    @Column(name = "NUMBER_BETWEEN_ATTEMPTS")
    protected Long number_between_attempts;

    @Column(name = "STEP_DESCRIPTION", nullable = false, length = 2000)
    protected String stepDescription;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "hiringStep")
    protected List<HiringStepMember> members;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "hiringStep")
    protected List<HiringStepQuestionnaire> questionnaires;
    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEST_ID")
    protected Test test;

    @Column(name = "DEFAULT_", columnDefinition = "BOOLEAN DEFAULT FALSE")
    protected Boolean default_;

    @Column(name = "ORDER_DEFAULT")
    protected Integer orderDefault;

    public void setDefault_(Boolean default_) {
        this.default_ = default_;
    }

    public Boolean getDefault_() {
        return default_;
    }

    public void setOrderDefault(Integer orderDefault) {
        this.orderDefault = orderDefault;
    }

    public Integer getOrderDefault() {
        return orderDefault;
    }


    public void setIsJobTest(Boolean isJobTest) {
        this.isJobTest = isJobTest;
    }

    public Boolean getIsJobTest() {
        return isJobTest;
    }


    public void setTest(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }


    public void setType(HiringStepType type) {
        this.type = type == null ? null : type.getId();
    }

    public HiringStepType getType() {
        return type == null ? null : HiringStepType.fromId(type);
    }


    public List<HiringStepQuestionnaire> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(List<HiringStepQuestionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }



    public void setAttempts_control_level(HS_AttemptsControlLevel attempts_control_level) {
        this.attempts_control_level = attempts_control_level == null ? null : attempts_control_level.getId();
    }

    public HS_AttemptsControlLevel getAttempts_control_level() {
        return attempts_control_level == null ? null : HS_AttemptsControlLevel.fromId(attempts_control_level);
    }

    public void setPeriod(HS_Periods period) {
        this.period = period == null ? null : period.getId();
    }

    public HS_Periods getPeriod() {
        return period == null ? null : HS_Periods.fromId(period);
    }


    public void setAttempts(Long attempts) {
        this.attempts = attempts;
    }

    public Long getAttempts() {
        return attempts;
    }

    public void setNumber_between_attempts(Long number_between_attempts) {
        this.number_between_attempts = number_between_attempts;
    }

    public Long getNumber_between_attempts() {
        return number_between_attempts;
    }





    public void setMembers(List<HiringStepMember> members) {
        this.members = members;
    }

    public List<HiringStepMember> getMembers() {
        return members;
    }


    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    public String getStepDescription() {
        return stepDescription;
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