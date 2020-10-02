package kz.uco.tsadv.modules.administration.importer;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.administration.importer.*;
import kz.uco.tsadv.modules.administration.importer.ImportLogRecord;
import kz.uco.tsadv.modules.administration.importer.ImportScenario;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author veronika.buksha
 */
@NamePattern("%s - %s|started,finished")
@Table(name = "TSADV_IMPORT_LOG")
@Entity(name = "tsadv$ImportLog")
public class ImportLog extends AbstractParentEntity {
    private static final long serialVersionUID = -1784817362652283170L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "STARTED")
    protected Date started;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FINISHED")
    protected Date finished;

    @Column(name = "ENTITIES_PROCESSED")
    protected Integer entitiesProcessed;

    @OneToMany(mappedBy = "importLog")
    protected List<ImportLogRecord> records;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMPORT_SCENARIO_ID")
    protected kz.uco.tsadv.modules.administration.importer.ImportScenario importScenario;

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }


    public void setImportScenario(kz.uco.tsadv.modules.administration.importer.ImportScenario importScenario) {
        this.importScenario = importScenario;
    }

    public ImportScenario getImportScenario() {
        return importScenario;
    }


    public void setEntitiesProcessed(Integer entitiesProcessed) {
        this.entitiesProcessed = entitiesProcessed;
    }

    public Integer getEntitiesProcessed() {
        return entitiesProcessed;
    }


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

    public void setRecords(List<ImportLogRecord> records) {
        this.records = records;
    }

    public List<ImportLogRecord> getRecords() {
        return records;
    }


}