package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.performance.model.CompetenceTemplateDetail;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NamePattern("%s|competenceTemplateName")
@Table(name = "TSADV_COMPETENCE_TEMPLATE")
@Entity(name = "tsadv$CompetenceTemplate")
public class CompetenceTemplate extends AbstractParentEntity {
    private static final long serialVersionUID = 353718681990079232L;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;


    @Column(name = "USE_POSITION_COMPETENCE", nullable = false)
    protected Boolean usePositionCompetence = false;

    @Column(name = "POSITION_COMPETENCE_WEIGHT", nullable = false)
    protected Integer positionCompetenceWeight;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "competenceTemplate")
    protected List<CompetenceTemplateDetail> competenceTemplateDetail;

    @Column(name = "COMPETENCE_TEMPLATE_NAME", nullable = false)
    protected String competenceTemplateName;

    public void setUsePositionCompetence(Boolean usePositionCompetence) {
        this.usePositionCompetence = usePositionCompetence;
    }

    public Boolean getUsePositionCompetence() {
        return usePositionCompetence;
    }


    public void setCompetenceTemplateName(String competenceTemplateName) {
        this.competenceTemplateName = competenceTemplateName;
    }

    public String getCompetenceTemplateName() {
        return competenceTemplateName;
    }


    public void setCompetenceTemplateDetail(List<CompetenceTemplateDetail> competenceTemplateDetail) {
        this.competenceTemplateDetail = competenceTemplateDetail;
    }

    public List<CompetenceTemplateDetail> getCompetenceTemplateDetail() {
        return competenceTemplateDetail;
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


    public void setPositionCompetenceWeight(Integer positionCompetenceWeight) {
        this.positionCompetenceWeight = positionCompetenceWeight;
    }

    public Integer getPositionCompetenceWeight() {
        return positionCompetenceWeight;
    }


}