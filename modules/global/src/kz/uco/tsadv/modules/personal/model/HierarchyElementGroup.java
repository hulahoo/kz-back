package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.base.entity.abstraction.IEntityGroup;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.tsadv.global.common.CommonUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@NamePattern("%s|name")
@Table(name = "TSADV_HIERARCHY_ELEMENT_GROUP")
@Entity(name = "tsadv_HierarchyElementGroup")
public class HierarchyElementGroup extends AbstractGroup implements IEntityGroup<HierarchyElementExt> {
    private static final long serialVersionUID = 1691925602631056688L;

    @OneToMany(mappedBy = "group")
    protected List<HierarchyElementExt> list;

    @Transient
    @MetaProperty(related = "list")
    protected HierarchyElementExt hierarchyElement;

    @Transient
    @MetaProperty(related = "list")
    protected String name;

    public String getName() {
        if (hierarchyElement == null) {
            hierarchyElement = getHierarchyElement();
        }
        return hierarchyElement != null ? hierarchyElement.getName() : null;
    }

    public HierarchyElementExt getHierarchyElement() {
        if (hierarchyElement == null &&
                PersistenceHelper.isLoaded(this, "list") &&
                !CollectionUtils.isEmpty(list)) {
            Date systemDate = CommonUtils.getSystemDate();
            list.stream().filter(hierarchyElement ->
                    !hierarchyElement.getStartDate().after(systemDate)
                            && !hierarchyElement.getEndDate().before(systemDate))
                    .findAny()
                    .ifPresent(hierarchyElement -> this.hierarchyElement = hierarchyElement);
        }
        return hierarchyElement;
    }

    public void setHierarchyElement(HierarchyElementExt hierarchyElement) {
        this.hierarchyElement = hierarchyElement;
    }

    public List<HierarchyElementExt> getList() {
        return list;
    }

    public void setList(List<HierarchyElementExt> list) {
        this.list = list;
    }
}