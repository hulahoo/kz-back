package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.modules.personal.model.Competence;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_COMPETENCE_GROUP")
@Entity(name = "tsadv$CompetenceGroup")
public class CompetenceGroup extends AbstractGroup {
    private static final long serialVersionUID = 6888189997981508678L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<Competence> list;

    @Transient
    @MetaProperty(related = "list")
    protected Competence competence;

    public List<Competence> getList() {
        return list;
    }

    public void setList(List<Competence> list) {
        this.list = list;
    }

    public Competence getCompetence() {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
            list.stream().filter(c -> c.getDeleteTs() == null
                    && !BaseCommonUtils.getSystemDate().before(c.getStartDate())
                    && !BaseCommonUtils.getSystemDate().after(c.getEndDate()))
                    .findFirst()
                    .ifPresent(c -> competence = c);
        }
        return competence;
    }

    public void setCompetence(Competence competence) {
        this.competence = competence;
    }


}