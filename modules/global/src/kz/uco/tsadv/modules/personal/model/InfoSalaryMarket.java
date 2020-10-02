package kz.uco.tsadv.modules.personal.model;

import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;

import javax.persistence.*;

@Table(name = "TSADV_INFO_SALARY_MARKET")
@Entity(name = "tsadv$InfoSalaryMarket")
public class InfoSalaryMarket extends AbstractTimeBasedEntity {
    private static final long serialVersionUID = 9163509208040344967L;

    @Column(name = "MIN_", nullable = false)
    protected Integer min;

    @Column(name = "MID", nullable = false)
    protected Integer mid;

    @Column(name = "MAX_", nullable = false)
    protected Integer max;

    @Column(name = "MEDIANA")
    protected Integer mediana;

    @Column(name = "KVART1")
    protected Integer kvart1;

    @Column(name = "KVART2")
    protected Integer kvart2;

    @Column(name = "KVART3")
    protected Integer kvart3;

    @Column(name = "KVART4")
    protected Integer kvart4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID")
    protected kz.uco.tsadv.modules.personal.model.Job job;

    public void setJob(kz.uco.tsadv.modules.personal.model.Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }


    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMin() {
        return min;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMax() {
        return max;
    }

    public void setMediana(Integer mediana) {
        this.mediana = mediana;
    }

    public Integer getMediana() {
        return mediana;
    }

    public void setKvart1(Integer kvart1) {
        this.kvart1 = kvart1;
    }

    public Integer getKvart1() {
        return kvart1;
    }

    public void setKvart2(Integer kvart2) {
        this.kvart2 = kvart2;
    }

    public Integer getKvart2() {
        return kvart2;
    }

    public void setKvart3(Integer kvart3) {
        this.kvart3 = kvart3;
    }

    public Integer getKvart3() {
        return kvart3;
    }

    public void setKvart4(Integer kvart4) {
        this.kvart4 = kvart4;
    }

    public Integer getKvart4() {
        return kvart4;
    }


}