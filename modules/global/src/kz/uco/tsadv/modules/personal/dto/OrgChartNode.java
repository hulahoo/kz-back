package kz.uco.tsadv.modules.personal.dto;

import java.io.Serializable;
import java.util.List;

public class OrgChartNode implements Serializable {
    private String id;

    private String personGroupId;

    private String parentId;

    private String parentPersonGroupId;

    private String fullName;

    private String image;

    private String positionGroupId;

    private String positionFullName;

    private List<OrgChartNode> children;

    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(String personGroupId) {
        this.personGroupId = personGroupId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentPersonGroupId() {
        return parentPersonGroupId;
    }

    public void setParentPersonGroupId(String parentPersonGroupId) {
        this.parentPersonGroupId = parentPersonGroupId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPositionGroupId() {
        return positionGroupId;
    }

    public void setPositionGroupId(String positionGroupId) {
        this.positionGroupId = positionGroupId;
    }

    public String getPositionFullName() {
        return positionFullName;
    }

    public void setPositionFullName(String positionFullName) {
        this.positionFullName = positionFullName;
    }

    public List<OrgChartNode> getChildren() {
        return children;
    }

    public void setChildren(List<OrgChartNode> children) {
        this.children = children;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
