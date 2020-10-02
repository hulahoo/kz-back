package kz.uco.tsadv.web.modules.personal.assignment;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.performance.model.Assessment;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;

@MetaClass(name = "tsadv$AssignmentAssessmentModel")
public class AssignmentAssessmentModel extends BaseUuidEntity {
    private String name;
    private PersonExt person;
    private PositionExt position;
    private Assessment assessment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonExt getPerson() {
        return person;
    }

    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PositionExt getPosition() {
        return position;
    }

    public void setPosition(PositionExt position) {
        this.position = position;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
}
