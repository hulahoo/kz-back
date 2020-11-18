package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.tsadv.modules.personal.group.GlobalValueGroup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_GLOBAL_VALUE")
@Entity(name = "tsadv$GlobalValue")
public class GlobalValue extends AbstractTimeBasedEntity implements IGroupedEntity<GlobalValueGroup> {
    private static final long serialVersionUID = 263611082528099665L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "VALUE_")
    protected String value;

    @NotNull
    @Column(name = "CODE", nullable = false, unique = true)
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected GlobalValueGroup group;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


    public GlobalValueGroup getGroup() {
        return group;
    }

    public void setGroup(GlobalValueGroup group) {
        this.group = group;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}