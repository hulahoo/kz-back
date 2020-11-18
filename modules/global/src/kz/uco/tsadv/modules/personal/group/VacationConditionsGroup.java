package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.base.entity.abstraction.IEntityGroup;
import kz.uco.tsadv.modules.personal.model.VacationConditions;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Do not use this entity. It will be removed
 */
@Table(name = "TSADV_VACATION_CONDITIONS_GROUP")
@Entity(name = "tsadv$VacationConditionsGroup")
@Deprecated
public class VacationConditionsGroup extends AbstractGroup implements IEntityGroup<VacationConditions> {
    private static final long serialVersionUID = 8570238650840302282L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<VacationConditions> list;

    @Transient
    @MetaProperty
    protected VacationConditions vacationConditions;

    public void setVacationConditions(VacationConditions vacationConditions) {
        this.vacationConditions = vacationConditions;
    }

    public VacationConditions getVacationConditions() {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
            list.stream()
                    .filter(p -> p.getDeleteTs() == null
                            && !BaseCommonUtils.getSystemDate().before(p.getStartDate())
                            && !BaseCommonUtils.getSystemDate().after(p.getEndDate()))
                    .findFirst()
                    .ifPresent(p -> vacationConditions = p);
        }
        return vacationConditions;
    }


    public List<VacationConditions> getList() {
        return list;
    }

    public void setList(List<VacationConditions> list) {
        this.list = list;
    }


}