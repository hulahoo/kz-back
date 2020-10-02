package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_IMPORT_LEARNING_LOG")
@Entity(name = "tsadv$ImportLearningLog")
public class ImportLearningLog extends StandardEntity {
    private static final long serialVersionUID = 6117366907995011691L;

    @Column(name = "PROCESSED")
    protected Integer processed;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "LOADING_DATE", nullable = false)
    protected Date loadingDate;

    @NotNull
    @Column(name = "SUCCESS", nullable = false)
    protected Boolean success = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Lob
    @Column(name = "ERROR_MESSAGE")
    protected String errorMessage;

    public void setProcessed(Integer processed) {
        this.processed = processed;
    }

    public Integer getProcessed() {
        return processed;
    }

    public void setLoadingDate(Date loadingDate) {
        this.loadingDate = loadingDate;
    }

    public Date getLoadingDate() {
        return loadingDate;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


}