package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_StandardShiftListener")
@NamePattern("%s|id")
@Table(name = "TSADV_STANDARD_SHIFT")
@Entity(name = "tsadv$StandardShift")
public class StandardShift extends AbstractParentEntity {
    private static final long serialVersionUID = 351540047894202404L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STANDARD_SCHEDULE_ID")
    protected StandardSchedule standardSchedule;

    @Column(name = "NUMBER_IN_SHIFT", nullable = false)
    protected Integer numberInShift;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIFT_ID")
    protected Shift shift;

    @Column(name = "SHIFT_DISPLAY")
    protected String shiftDisplay;

    @Column(name = "SHIFT_DISPLAY_DAY", nullable = false)
    protected Integer shiftDisplayDay;


    public void setStandardSchedule(StandardSchedule standardSchedule) {
        this.standardSchedule = standardSchedule;
    }

    public StandardSchedule getStandardSchedule() {
        return standardSchedule;
    }


    public void setNumberInShift(Integer numberInShift) {
        this.numberInShift = numberInShift;
    }

    public Integer getNumberInShift() {
        return numberInShift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShiftDisplay(String shiftDisplay) {
        this.shiftDisplay = shiftDisplay;
    }

    public String getShiftDisplay() {
        return shiftDisplay;
    }

    public void setShiftDisplayDay(Integer shiftDisplayDay) {
        this.shiftDisplayDay = shiftDisplayDay;
    }

    public Integer getShiftDisplayDay() {
        return shiftDisplayDay;
    }


}