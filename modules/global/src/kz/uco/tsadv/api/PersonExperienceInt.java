package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

/**
 * Сведения об опыте работы (интеграционный обьект)
 */
@MetaClass(name = "tsadv$PersonExperienceInt")
public class PersonExperienceInt extends AbstractEntityInt {
    private static final long serialVersionUID = 5437186618019730679L;

    /**
     * Компания
     */
    @MetaProperty
    protected String company;

    /**
     * По настоящее время (true / false)
     */
    @MetaProperty
    protected Boolean untilNow;

    /**
     * Должность
     */
    @MetaProperty
    protected String job;

    /**
     * Год, Месяц начала работы
     */
    @MetaProperty
    protected String startMonth;

    /**
     * Год, Месяц окончания работы
     */
    @MetaProperty
    protected String endMonth;

    /**
     * Примечание
     */
    @MetaProperty
    protected String description;



    public void setUntilNow(Boolean untilNow) {
        this.untilNow = untilNow;
    }

    public Boolean getUntilNow() {
        return untilNow;
    }


    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public String getEndMonth() {
        return endMonth;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}