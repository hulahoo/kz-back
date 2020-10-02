package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;

public class SimplePojo implements Serializable {
    public String id;
    public String name;

    public SimplePojo() {

    }
    public SimplePojo(String id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
