package kz.uco.tsadv.modules.administration.importer;


import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.administration.importer.*;
import kz.uco.tsadv.modules.administration.importer.LogRecordLevel;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * @author veronika.buksha
 */

@NamePattern("%s: %s|level,message")
@Table(name = "TSADV_IMPORT_LOG_RECORD")
@Entity(name = "tsadv$ImportLogRecord")
public class ImportLogRecord extends AbstractParentEntity {
    private static final long serialVersionUID = -2593406807214063152L;

    @Column(name = "MESSAGE", nullable = false)
    protected String message;

    @Column(name = "FULL_NAME", length = 500)
    protected String fullName;

    @Column(name = "USER_MESSAGE", length = 500)
    protected String userMessage;

    @Column(name = "SUCCESS")
    protected Boolean success;

    @Column(name = "LEVEL_", nullable = false)
    protected String level;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_", nullable = false)
    protected Date time;

    @Lob
    @Column(name = "STACKTRACE")
    protected String stacktrace;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMPORT_LOG_ID")
    protected ImportLog importLog;

    @Column(name = "LINK_SCREEN")
    protected String linkScreen;

    @Column(name = "LINK_ENTITY_NAME")
    protected String linkEntityName;

    @Column(name = "LINK_ENTITY_ID")
    protected UUID linkEntityId;

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }


    public void setLinkEntityName(String linkEntityName) {
        this.linkEntityName = linkEntityName;
    }

    public String getLinkEntityName() {
        return linkEntityName;
    }

    public void setLinkEntityId(UUID linkEntityId) {
        this.linkEntityId = linkEntityId;
    }

    public UUID getLinkEntityId() {
        return linkEntityId;
    }


    public void setLinkScreen(String linkScreen) {
        this.linkScreen = linkScreen;
    }

    public String getLinkScreen() {
        return linkScreen;
    }


    public void setImportLog(ImportLog importLog) {
        this.importLog = importLog;
    }

    public ImportLog getImportLog() {
        return importLog;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setLevel(kz.uco.tsadv.modules.administration.importer.LogRecordLevel level) {
        this.level = level == null ? null : level.getId();
    }

    public kz.uco.tsadv.modules.administration.importer.LogRecordLevel getLevel() {
        return level == null ? null : LogRecordLevel.fromId(level);
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getStacktrace() {
        return stacktrace;
    }


}