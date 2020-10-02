package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$PositionVacancyChart")
public class PositionVacancyChart extends BaseUuidEntity {
    private static final long serialVersionUID = 7936395141648474178L;

    @MetaProperty
    protected String positionName;

    @MetaProperty
    protected Integer maxCount;

    @MetaProperty
    protected Integer fillCount;

    @MetaProperty
    protected Integer vacancyCount;

    public Integer getVacancyCount() {
        return vacancyCount;
    }

    public void setVacancyCount(Integer vacancyCount) {
        this.vacancyCount = vacancyCount;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setFillCount(Integer fillCount) {
        this.fillCount = fillCount;
    }

    public Integer getFillCount() {
        return fillCount;
    }


}