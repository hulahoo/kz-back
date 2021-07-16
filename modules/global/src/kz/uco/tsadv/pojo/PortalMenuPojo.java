package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Alibek Berdaulet
 */
public class PortalMenuPojo implements Serializable {

    protected String id;
    protected String ru;
    protected String en;
    protected String type;
    protected PortalMenuPojo parent;
    protected List<PortalMenuPojo> items;

    public PortalMenuPojo getParent() {
        return parent;
    }

    public void setParent(PortalMenuPojo parent) {
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public List<PortalMenuPojo> getItems() {
        return items;
    }

    public void setItems(List<PortalMenuPojo> items) {
        this.items = items;
    }
}
