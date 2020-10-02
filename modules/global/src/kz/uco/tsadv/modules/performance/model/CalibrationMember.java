package kz.uco.tsadv.modules.performance.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_CALIBRATION_MEMBER")
@Entity(name = "tsadv$CalibrationMember")
public class CalibrationMember extends AbstractParentEntity {
    private static final long serialVersionUID = 4196905360498384009L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonGroupExt person;

    @Column(name = "POTENCIAL")
    protected Integer potencial;

    @Column(name = "PERFORMANCE")
    protected Integer performance;

    @Column(name = "RISK_OF_LOSS")
    protected Integer riskOfLoss;

    @Column(name = "IMPACT_OF_LOSS")
    protected Integer impactOfLoss;

    @Column(name = "COMPETENCE_OVERALL")
    protected Integer competenceOverall;

    @Column(name = "GOAL_OVERALL")
    protected Integer goalOverall;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SESSION_ID")
    protected CalibrationSession session;

    public void setSession(CalibrationSession session) {
        this.session = session;
    }

    public CalibrationSession getSession() {
        return session;
    }


    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Integer getPerformance() {
        return performance;
    }


    public void setPerson(PersonGroupExt person) {
        this.person = person;
    }

    public PersonGroupExt getPerson() {
        return person;
    }

    public void setPotencial(Integer potencial) {
        this.potencial = potencial;
    }

    public Integer getPotencial() {
        return potencial;
    }

    public void setRiskOfLoss(Integer riskOfLoss) {
        this.riskOfLoss = riskOfLoss;
    }

    public Integer getRiskOfLoss() {
        return riskOfLoss;
    }

    public void setImpactOfLoss(Integer impactOfLoss) {
        this.impactOfLoss = impactOfLoss;
    }

    public Integer getImpactOfLoss() {
        return impactOfLoss;
    }

    public void setCompetenceOverall(Integer competenceOverall) {
        this.competenceOverall = competenceOverall;
    }

    public Integer getCompetenceOverall() {
        return competenceOverall;
    }

    public void setGoalOverall(Integer goalOverall) {
        this.goalOverall = goalOverall;
    }

    public Integer getGoalOverall() {
        return goalOverall;
    }


}