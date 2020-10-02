package kz.uco.tsadv.global.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.UUID;

@NamePattern("%s|name")
@MetaClass(name = "tsadv$PersonPojo")
public class PersonPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -846466271887747404L;

    @MetaProperty
    protected String personGroupId;

    @MetaProperty
    protected String personId;

    @MetaProperty
    protected String image;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected String employeeNumber;

    @Override
    public void setId(UUID id) {
        this.id = id;
        this.personGroupId = id.toString();
    }

    public String getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(String personGroupId) {
        this.personGroupId = personGroupId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        image = String.format("./dispatch/person_image/%s", personId);
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

}