package kz.uco.tsadv.modules.recognition;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.dictionary.DicPersonAwardType;
import kz.uco.tsadv.modules.recognition.enums.AwardStatus;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Listeners("tsadv_PersonAwardListener")
@Table(name = "TSADV_PERSON_AWARD")
@Entity(name = "tsadv$PersonAward")
public class PersonAward extends StandardEntity {
    private static final long serialVersionUID = 3207212524834616403L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID")
    protected DicPersonAwardType type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AWARD_PROGRAM_ID")
    protected AwardProgram awardProgram;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVER_ID")
    protected PersonGroupExt receiver;

    @Length(min = 300, max = 2000, message = "{msg://PersonAward.history.min.length}")
    @NotNull
    @Column(name = "HISTORY", nullable = false, length = 2000)
    protected String history;

    @Length(min = 100, max = 2000, message = "{msg://PersonAward.why.min.length}")
    @NotNull
    @Column(name = "WHY", nullable = false, length = 2000)
    protected String why;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    public void setAwardProgram(AwardProgram awardProgram) {
        this.awardProgram = awardProgram;
    }

    public AwardProgram getAwardProgram() {
        return awardProgram;
    }


    public DicPersonAwardType getType() {
        return type;
    }

    public void setType(DicPersonAwardType type) {
        this.type = type;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setAuthor(PersonGroupExt author) {
        this.author = author;
    }

    public PersonGroupExt getAuthor() {
        return author;
    }

    public void setReceiver(PersonGroupExt receiver) {
        this.receiver = receiver;
    }

    public PersonGroupExt getReceiver() {
        return receiver;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getHistory() {
        return history;
    }

    public void setWhy(String why) {
        this.why = why;
    }

    public String getWhy() {
        return why;
    }

    public void setStatus(AwardStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public AwardStatus getStatus() {
        return status == null ? null : AwardStatus.fromId(status);
    }


}