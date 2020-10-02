package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;

public class EnrollmentPojo implements Serializable {
    protected String id;

    protected String name;

    protected String logo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
