package kz.uco.tsadv.modules.recruitment.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|requirement")
@Table(name = "TSADV_CANDIDATE_REQUIREMENT")
@Entity(name = "tsadv$CandidateRequirement")
public class CandidateRequirement extends AbstractParentEntity {
    private static final long serialVersionUID = 102063637376468752L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUIREMENT_ID")
    protected RcQuestion requirement;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEVEL_ID")
    protected RcAnswer level;

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setRequirement(RcQuestion requirement) {
        this.requirement = requirement;
    }

    public RcQuestion getRequirement() {
        return requirement;
    }

    public void setLevel(RcAnswer level) {
        this.level = level;
    }

    public RcAnswer getLevel() {
        return level;
    }


}