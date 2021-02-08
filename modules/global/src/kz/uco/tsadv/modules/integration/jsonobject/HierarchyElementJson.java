package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class HierarchyElementJson implements Serializable {

    private String legacyId;

    private String parentOrganizationId;

    private String subordinateOrganizationId;

    private String startDate;

    private String endDate;

    private String companyCode;

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public String getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(String parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    public String getSubordinateOrganizationId() {
        return subordinateOrganizationId;
    }

    public void setSubordinateOrganizationId(String subordinateOrganizationId) {
        this.subordinateOrganizationId = subordinateOrganizationId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
