package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.tsadv.modules.personal.group.ContractGroup;

import javax.persistence.*;

@NamePattern("%s|id")
@Table(name = "TSADV_CONTRACT")
@Entity(name = "tsadv$Contract")
public class Contract extends AbstractParentEntity implements IGroupedEntity<ContractGroup> {
    private static final long serialVersionUID = -549768337914698856L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected ContractGroup group;

    public void setGroup(ContractGroup group) {
        this.group = group;
    }

    public ContractGroup getGroup() {
        return group;
    }


}