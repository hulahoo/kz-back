package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_RECOGNITION_LOGIN_LOG")
@Entity(name = "tsadv$RecognitionLoginLog")
public class RecognitionLoginLog extends AbstractParentEntity {
    private static final long serialVersionUID = 8753655007722739020L;

    @NotNull
    @Column(name = "LOGIN", nullable = false)
    protected String login;

    @NotNull
    @Column(name = "SESSION_ID", nullable = false)
    protected UUID sessionId;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "DATE_TIME", nullable = false)
    protected Date dateTime;

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }


}