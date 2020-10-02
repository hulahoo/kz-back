package kz.uco.tsadv.modules.learning.model;

import kz.uco.tsadv.modules.learning.enums.CertificationCalculateType;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.enums.CertificationPeriod;

import javax.persistence.*;

@Table(name = "TSADV_CERTIFICATION")
@Entity(name = "tsadv$Certification")
public class Certification extends AbstractParentEntity {
    private static final long serialVersionUID = -2459812597074978L;

    @Column(name = "NAME", nullable = false, length = 500)
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected kz.uco.tsadv.modules.learning.model.Course course;

    @Column(name = "NOTIFY_DAY", nullable = false)
    protected Integer notifyDay;

    @Column(name = "LIFE_DAY", nullable = false)
    protected Integer lifeDay;

    @Column(name = "CALCULATE_TYPE", nullable = false)
    protected Integer calculateType;

    @Column(name = "PERIOD", nullable = false)
    protected Integer period;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public CertificationPeriod getPeriod() {
        return period == null ? null : CertificationPeriod.fromId(period);
    }

    public void setPeriod(CertificationPeriod period) {
        this.period = period == null ? null : period.getId();
    }


    public void setCourse(kz.uco.tsadv.modules.learning.model.Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setNotifyDay(Integer notifyDay) {
        this.notifyDay = notifyDay;
    }

    public Integer getNotifyDay() {
        return notifyDay;
    }

    public void setLifeDay(Integer lifeDay) {
        this.lifeDay = lifeDay;
    }

    public Integer getLifeDay() {
        return lifeDay;
    }

    public void setCalculateType(CertificationCalculateType calculateType) {
        this.calculateType = calculateType == null ? null : calculateType.getId();
    }

    public CertificationCalculateType getCalculateType() {
        return calculateType == null ? null : CertificationCalculateType.fromId(calculateType);
    }


}