package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@NamePattern("%s|description")
@MetaClass(name = "tsadv$NomineePojo")
public class NomineePojo extends BaseUuidEntity {
    private static final long serialVersionUID = -3183765137487009184L;

    @MetaProperty
    protected String pId;

    @MetaProperty
    protected String pgId;

    @MetaProperty
    protected String image;

    @MetaProperty
    protected String fullName;

    @MetaProperty
    protected String position;

    @MetaProperty
    protected String organization;

    @MetaProperty
    protected String program;

    @MetaProperty
    protected Integer year;

    @MetaProperty
    protected String description;

    @MetaProperty
    protected String personAwardId;

    @MetaProperty
    protected String employeeNumber;

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getPersonAwardId() {
        return personAwardId;
    }

    public void setPersonAwardId(String personAwardId) {
        this.personAwardId = personAwardId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getPId() {
        return pId;
    }

    public void setPgId(String pgId) {
        this.pgId = pgId;
    }

    public String getPgId() {
        return pgId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        image = String.format("./dispatch/person_image/%s", pId);
        return image;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganization() {
        return organization;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}