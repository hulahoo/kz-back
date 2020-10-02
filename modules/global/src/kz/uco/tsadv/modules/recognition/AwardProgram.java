package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@NamePattern("%s|name")
@Table(name = "TSADV_AWARD_PROGRAM")
@Entity(name = "tsadv$AwardProgram")
public class AwardProgram extends StandardEntity {
    private static final long serialVersionUID = 8801255644698946420L;

    @NotNull
    @Column(name = "NAME", nullable = false, length = 500)
    protected String name;

    @NotNull
    @Column(name = "YEAR_", nullable = false)
    protected Integer year;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "awardProgram")
    protected List<PersonAward> personAwards;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    protected Boolean active = false;

    @NotNull
    @Column(name = "ORDER_", nullable = false)
    protected Integer order;

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getYear() {
        return year;
    }


    public List<PersonAward> getPersonAwards() {
        return personAwards;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


}