package kz.uco.tsadv.modules.administration.importer;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Lob;

@Table(name = "TSADV_IMPORT_HISTORY_LOG")
@Entity(name = "tsadv$ImportHistoryLog")
public class ImportHistoryLog extends StandardEntity {
    private static final long serialVersionUID = -3605734630709246187L;

    @Column(name = "MESSAGE")
    protected String message;

    @Lob
    @Column(name = "STACKTRACE")
    protected String stacktrace;

    @Column(name = "LOGIN")
    protected String login;

    @Lob
    @Column(name = "PARAMS")
    protected String params;

    @NotNull
    @Column(name = "SUCCESS", nullable = false)
    protected Boolean success = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_TIME")
    protected Date dateTime;

    @Column(name = "ENTITIES_PROCESSED")
    protected Integer entitiesProcessed;

    @Column(name = "LEVEL_")
    protected String level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMPORT_HISTORY_ID")
    protected ImportHistory importHistory;

    public ImportHistory getImportHistory() {
        return importHistory;
    }

    public void setImportHistory(ImportHistory importHistory) {
        this.importHistory = importHistory;
    }




    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getStacktrace() {
        return stacktrace;
    }


    public void setLevel(ImportLogLevel level) {
        this.level = level == null ? null : level.getId();
    }

    public ImportLogLevel getLevel() {
        return level == null ? null : ImportLogLevel.fromId(level);
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getParams() {
        return params;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setEntitiesProcessed(Integer entitiesProcessed) {
        this.entitiesProcessed = entitiesProcessed;
    }

    public Integer getEntitiesProcessed() {
        return entitiesProcessed;
    }


}