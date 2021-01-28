package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.dictionary.DicGoalCategory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@NamePattern("%s|libraryName")
@Table(name = "TSADV_GOAL_LIBRARY")
@Entity(name = "tsadv$GoalLibrary")
public class GoalLibrary extends AbstractParentEntity {
    private static final long serialVersionUID = -6019055080159930232L;

    @NotNull
    @Column(name = "LIBRARY_NAME", nullable = false, length = 240)
    protected String libraryName;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected DicGoalCategory category;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @OneToMany(mappedBy = "library")
    protected List<Goal> goals;

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getLibraryName() {
        return libraryName;
    }


    public DicGoalCategory getCategory() {
        return category;
    }

    public void setCategory(DicGoalCategory category) {
        this.category = category;
    }


}