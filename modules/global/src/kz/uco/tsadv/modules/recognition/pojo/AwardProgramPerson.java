package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.recognition.AwardProgram;

@MetaClass(name = "tsadv$AwardProgramPerson")
public class AwardProgramPerson extends BaseUuidEntity {
    private static final long serialVersionUID = -2974965479337634161L;

    @MetaProperty
    protected PersonExt person;

    @MetaProperty
    protected AwardProgram awardProgram;

    @MetaProperty
    protected Long count;

    public AwardProgram getAwardProgram() {
        return awardProgram;
    }

    public void setAwardProgram(AwardProgram awardProgram) {
        this.awardProgram = awardProgram;
    }

    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }


}