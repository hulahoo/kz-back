package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.tsadv.modules.personal.model.GradeRuleValue;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.base.common.BaseCommonUtils;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_GRADE_RULE_VALUE_GROUP")
@Entity(name = "tsadv$GradeRuleValueGroup")
public class GradeRuleValueGroup extends AbstractGroup {
    private static final long serialVersionUID = -2069927748519273478L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<GradeRuleValue> list;

    @Transient
    @MetaProperty(related = "list")
    protected GradeRuleValue gradeRuleValue;

    public void setList(List<GradeRuleValue> list) {
        this.list = list;
    }

    public List<GradeRuleValue> getList() {
        return list;
    }

    public void setGradeRuleValue(GradeRuleValue gradeRuleValue) {
        this.gradeRuleValue = gradeRuleValue;
    }

    public GradeRuleValue getGradeRuleValue() {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
            list.stream()
                    .filter(grv -> grv.getDeleteTs() == null
                            && !BaseCommonUtils.getSystemDate().before(grv.getStartDate())
                            && !BaseCommonUtils.getSystemDate().after(grv.getEndDate()))
                    .findFirst()
                    .ifPresent(grv -> gradeRuleValue = grv);
        }
        return gradeRuleValue;
    }


}