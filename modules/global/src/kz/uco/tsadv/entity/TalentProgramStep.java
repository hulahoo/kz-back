package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.core.notification.NotificationTemplate;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramStep;

import javax.persistence.*;

@NamePattern("%s|step")
@Table(name = "TSADV_TALENT_PROGRAM_STEP")
@Entity(name = "tsadv$TalentProgramStep")
public class TalentProgramStep extends StandardEntity {
    private static final long serialVersionUID = 968506749475895116L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TALENT_PROGRAM_ID")
    protected TalentProgram talentProgram;

    @Column(name = "ORDER_NUM")
    protected Integer orderNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STEP_ID")
    protected DicTalentProgramStep step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTIFICATION_ID")
    protected NotificationTemplate notification;

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setTalentProgram(TalentProgram talentProgram) {
        this.talentProgram = talentProgram;
    }

    public TalentProgram getTalentProgram() {
        return talentProgram;
    }

    public void setStep(DicTalentProgramStep step) {
        this.step = step;
    }

    public DicTalentProgramStep getStep() {
        return step;
    }

    public void setNotification(NotificationTemplate notification) {
        this.notification = notification;
    }

    public NotificationTemplate getNotification() {
        return notification;
    }

}