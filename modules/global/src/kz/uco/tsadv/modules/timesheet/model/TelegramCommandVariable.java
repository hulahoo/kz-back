package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.timesheet.model.TelegramAnswer;

import javax.persistence.*;

@NamePattern("%s|nameVariable")
@Table(name = "TSADV_TELEGRAM_COMMAND_VARIABLE")
@Entity(name = "tsadv$TelegramCommandVariable")
public class TelegramCommandVariable extends StandardEntity {
    private static final long serialVersionUID = -4161381890291840293L;

    @Column(name = "NAME_VARIABLE")
    protected String nameVariable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TELEGRAM_ANSWER_ID")
    protected TelegramAnswer telegramAnswer;

    public void setTelegramAnswer(TelegramAnswer telegramAnswer) {
        this.telegramAnswer = telegramAnswer;
    }

    public TelegramAnswer getTelegramAnswer() {
        return telegramAnswer;
    }


    public void setNameVariable(String nameVariable) {
        this.nameVariable = nameVariable;
    }

    public String getNameVariable() {
        return nameVariable;
    }


}