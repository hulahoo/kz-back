package kz.uco.tsadv.pojo;

import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
public class OrganizationRequestSaveModel extends RequestDetailSaveModel {

    private UUID organizationGroupId;

    public UUID getOrganizationGroupId() {
        return organizationGroupId;
    }

    public void setOrganizationGroupId(UUID organizationGroupId) {
        this.organizationGroupId = organizationGroupId;
    }
}
