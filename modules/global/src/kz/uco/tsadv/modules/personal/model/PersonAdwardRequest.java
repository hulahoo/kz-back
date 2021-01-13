package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_ADWARD_REQUEST")
@Entity(name = "tsadv_PersonAdwardRequest")
public class PersonAdwardRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 8390924626160011203L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    private DicRequestStatus requestStatus;

    @Column(name = "ACADEMIC_DEGREE", length = 2000)
    protected String academicDegree;

    @Column(name = "SCIENTIFIC_WORKS_IVENTIONS", length = 2000)
    protected String scientificWorksIventions;

    @Column(name = "STATE_AWARDS", length = 2000)
    protected String stateAwards;

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public String getStateAwards() {
        return stateAwards;
    }

    public void setStateAwards(String stateAwards) {
        this.stateAwards = stateAwards;
    }

    public String getScientificWorksIventions() {
        return scientificWorksIventions;
    }

    public void setScientificWorksIventions(String scientificWorksIventions) {
        this.scientificWorksIventions = scientificWorksIventions;
    }

    public String getAcademicDegree() {
        return academicDegree;
    }

    public void setAcademicDegree(String academicDegree) {
        this.academicDegree = academicDegree;
    }
}