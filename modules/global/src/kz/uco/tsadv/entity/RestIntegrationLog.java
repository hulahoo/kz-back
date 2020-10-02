package kz.uco.tsadv.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_REST_INTEGRATION_LOG")
@Entity(name = "tsadv$RestIntegrationLog")
public class RestIntegrationLog extends StandardEntity {
    private static final long serialVersionUID = -1460818988274093154L;

    @Lob
    @Column(name = "REQUEST_ID")
    protected String requestId;

    @Column(name = "LOGIN", length = 1000)
    protected String login;

    @Lob
    @Column(name = "METHOD_NAME")
    protected String methodName;

    @Lob
    @Column(name = "PARAMS")
    protected String params;

    @Lob
    @Column(name = "MESSAGE")
    protected String message;

    @NotNull
    @Column(name = "SUCCESS", nullable = false)
    protected Boolean success = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_TIME")
    protected Date dateTime;

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }


    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }


    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }


    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }


    public void setParams(String params) {
        this.params = params;
    }

    public String getParams() {
        return params;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }


}