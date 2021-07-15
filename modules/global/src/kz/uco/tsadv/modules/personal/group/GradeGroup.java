package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.base.entity.abstraction.IEntityGroup;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.model.Grade;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@NamePattern("%s|grade")
@Table(name = "TSADV_GRADE_GROUP")
@Entity(name = "tsadv$GradeGroup")
public class GradeGroup extends AbstractGroup implements IEntityGroup<Grade> {
    private static final long serialVersionUID = 6379263328021886090L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<Grade> list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected DicCompany company;

    @Transient
    @MetaProperty(related = "list")
    protected Grade grade;

    @NotNull
    @Column(name = "AVAILABLE_SALARY", nullable = false)
    protected Boolean availableSalary = false;

    public Boolean getAvailableSalary() {
        return availableSalary;
    }

    public void setAvailableSalary(Boolean availableSalary) {
        this.availableSalary = availableSalary;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public List<Grade> getList() {
        return list;
    }

    public void setList(List<Grade> list) {
        this.list = list;
    }

    public Grade getGrade() {
        grade = grade != null ? grade : getGradeInDate(BaseCommonUtils.getSystemDate());
        return grade;
    }

    public Grade getGradeInDate(Date date) {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
            list.stream()
                    .filter(g -> g.getDeleteTs() == null && g.getStartDate() != null && g.getEndDate() != null
                            && !date.before(g.getStartDate())
                            && !date.after(g.getEndDate()))
                    .findFirst()
                    .ifPresent(g -> grade = g);
        }
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

}