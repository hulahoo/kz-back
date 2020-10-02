package kz.uco.tsadv.modules.administration.importer;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import java.util.List;
import javax.persistence.OneToMany;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "TSADV_IMPORT_HISTORY")
@Entity(name = "tsadv$ImportHistory")
public class ImportHistory extends StandardEntity {
    private static final long serialVersionUID = -2262965451152891467L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMPORT_SCENARIO_ID")
    protected ImportScenario importScenario;

    @OneToMany(mappedBy = "importHistory")
    protected List<ImportHistoryLog> records;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "STARTED")
    protected Date started;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FINISHED")
    protected Date finished;

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getStarted() {
        return started;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public Date getFinished() {
        return finished;
    }


    public void setRecords(List<ImportHistoryLog> records) {
        this.records = records;
    }

    public List<ImportHistoryLog> getRecords() {
        return records;
    }


    public void setImportScenario(ImportScenario importScenario) {
        this.importScenario = importScenario;
    }

    public ImportScenario getImportScenario() {
        return importScenario;
    }


    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }


}