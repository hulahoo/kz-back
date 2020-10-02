package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.timesheet.model.*;
import kz.uco.tsadv.modules.timesheet.model.TelegramCommandVariable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@NamePattern("%s|comandName")
@Table(name = "TSADV_TELEGRAM_ANSWER")
@Entity(name = "tsadv$TelegramAnswer")
public class TelegramAnswer extends StandardEntity {
    private static final long serialVersionUID = -5373948937876061640L;

    @Column(name = "COMAND_NAME", nullable = false)
    protected String comandName;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "telegramAnswer")
    protected List<kz.uco.tsadv.modules.timesheet.model.TelegramCommandVariable> variable;

    public void setVariable(List<kz.uco.tsadv.modules.timesheet.model.TelegramCommandVariable> variable) {
        this.variable = variable;
    }

    public List<TelegramCommandVariable> getVariable() {
        return variable;
    }


    public void setComandName(String comandName) {
        this.comandName = comandName;
    }

    public String getComandName() {
        return comandName;
    }


}