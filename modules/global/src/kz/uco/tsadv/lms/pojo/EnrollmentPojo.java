package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.UUID;

public class EnrollmentPojo implements Serializable {
    protected String id;

    protected String name;

    protected UUID logo;

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

    public UUID getLogo() {
        return logo;
    }

    public void setLogo(UUID logo) {
        this.logo = logo;
    }
}
