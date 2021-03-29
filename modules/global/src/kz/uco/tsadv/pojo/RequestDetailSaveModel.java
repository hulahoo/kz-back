package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
public class RequestDetailSaveModel implements Serializable {

    protected UUID rId;
    protected UUID rdId;
    protected UUID parentRdId;
    protected String nameRu;
    protected String nameEn;
    protected UUID parentOrganizationGroupId;

    public UUID getrId() {
        return rId;
    }

    public void setrId(UUID rId) {
        this.rId = rId;
    }

    public UUID getRdId() {
        return rdId;
    }

    public void setRdId(UUID rdId) {
        this.rdId = rdId;
    }

    public UUID getParentRdId() {
        return parentRdId;
    }

    public void setParentRdId(UUID parentRdId) {
        this.parentRdId = parentRdId;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public UUID getParentOrganizationGroupId() {
        return parentOrganizationGroupId;
    }

    public void setParentOrganizationGroupId(UUID parentOrganizationGroupId) {
        this.parentOrganizationGroupId = parentOrganizationGroupId;
    }
}
