package kz.uco.tsadv.entity.models;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import javax.validation.constraints.NotNull;
import java.util.List;

@MetaClass(name = "tsadv_PositionHierarchy")
public class PositionHierarchy extends BaseUuidEntity {
    private static final long serialVersionUID = 5256266355019100397L;

    @MetaProperty
    private String positionName;

    @NotNull
    @MetaProperty(mandatory = true)
    private Boolean haveChildren = false;

    @MetaProperty
    private List<PositionHierarchy> children;

    @MetaProperty
    private PositionHierarchy parent;

    public PositionHierarchy getParent() {
        return parent;
    }

    public void setParent(PositionHierarchy parent) {
        this.parent = parent;
    }

    public List<PositionHierarchy> getChildren() {
        return children;
    }

    public void setChildren(List<PositionHierarchy> children) {
        this.children = children;
    }

    public Boolean getHaveChildren() {
        return haveChildren;
    }

    public void setHaveChildren(Boolean haveChildren) {
        this.haveChildren = haveChildren;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
}