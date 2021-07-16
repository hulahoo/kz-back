package kz.uco.tsadv.pojo;

import java.util.Date;
import java.util.UUID;

/**
 * @author Alibek Berdaulet
 */
public class IncentivePojo {

    private UUID organizationGroupId;

    private Date date;

    private String organizationName;

    private Double result;

    public IncentivePojo(UUID organizationGroupId, Date date, String organizationName, Double result) {
        this.organizationGroupId = organizationGroupId;
        this.date = date;
        this.organizationName = organizationName;
        if (result != null)
            this.result = 1.0 * Math.round(result * 100) / 100;
    }

    public IncentivePojo() {
    }

    public UUID getOrganizationGroupId() {
        return organizationGroupId;
    }

    public void setOrganizationGroupId(UUID organizationGroupId) {
        this.organizationGroupId = organizationGroupId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }
}
