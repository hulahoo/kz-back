package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.performance.model.CompetenceTemplate;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@NamePattern("%s|competenceGroup")
@Table(name = "TSADV_COMPETENCE_TEMPLATE_DETAIL")
@Entity(name = "tsadv$CompetenceTemplateDetail")
public class CompetenceTemplateDetail extends AbstractParentEntity {
    private static final long serialVersionUID = 592640439283091548L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPETENCE_TEMPLATE_ID")
    protected kz.uco.tsadv.modules.performance.model.CompetenceTemplate competenceTemplate;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPETENCE_GROUP_ID")
    protected CompetenceGroup competenceGroup;

    @Column(name = "WEIGHT", nullable = false)
    protected Integer weight;


    public void setCompetenceGroup(CompetenceGroup competenceGroup) {
        this.competenceGroup = competenceGroup;
    }

    public CompetenceGroup getCompetenceGroup() {
        return competenceGroup;
    }


    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }


    public void setCompetenceTemplate(kz.uco.tsadv.modules.performance.model.CompetenceTemplate competenceTemplate) {
        this.competenceTemplate = competenceTemplate;
    }

    public CompetenceTemplate getCompetenceTemplate() {
        return competenceTemplate;
    }


}