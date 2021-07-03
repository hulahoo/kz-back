package kz.uco.tsadv.modules.personal.dto;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Alibek Berdaulet
 */
public class PersonDto implements Serializable {

    private UUID id;

    private UUID groupId;

    private UUID positionGroupId;

    private String fullName;

    private String positionName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getPositionGroupId() {
        return positionGroupId;
    }

    public void setPositionGroupId(UUID positionGroupId) {
        this.positionGroupId = positionGroupId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
}
