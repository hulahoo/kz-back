package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.tsadv.modules.personal.model.Contract;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_CONTRACT_GROUP")
@Entity(name = "tsadv$ContractGroup")
public class ContractGroup extends AbstractGroup {
    private static final long serialVersionUID = 290963597767923537L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<Contract> list;

    @Transient
    @MetaProperty(related = "list")
    protected Contract contract;

    public List<Contract> getList() {
        return list;
    }

    public void setList(List<Contract> list) {
        this.list = list;
    }

    public Contract getContract() {
        if (list != null && !list.isEmpty()) contract = list.get(0);
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }


}