package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;

public class MaterialPojo implements Serializable {
    protected String id;
    protected String logo;
    protected String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
