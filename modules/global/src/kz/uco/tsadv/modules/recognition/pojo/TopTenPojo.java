package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$TopTenPojo")
public class TopTenPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -8195385338028017936L;

    @MetaProperty
    protected Integer order;

    @MetaProperty
    protected String pId;

    @MetaProperty
    protected String pgId;

    @MetaProperty
    protected String fullName;

    @MetaProperty
    protected String organization;

    @MetaProperty
    protected String position;

    @MetaProperty
    protected String image;

    @MetaProperty
    protected String medal;

    @MetaProperty
    protected Long count;

    @MetaProperty
    protected String birthday;

    @MetaProperty
    protected String heartAward;

    @MetaProperty
    protected String employeeNumber;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMedal() {
        medal = "bl";

        if (order != null) {
            switch (order) {
                case 1: {
                    medal = "g";
                    break;
                }
                case 2: {
                    medal = "s";
                    break;
                }
                case 3: {
                    medal = "br";
                    break;
                }
            }
        }
        return medal;
    }

    public void setMedal(String medal) {
        this.medal = medal;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganization() {
        return organization;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        image = String.format("./dispatch/person_image/%s", pId);
        return image;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public String getHeartAward() {
        return heartAward;
    }

    public void setHeartAward(String heartAward) {
        this.heartAward = heartAward;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}