package kz.uco.tsadv.modules.administration.importer;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.administration.importer.ImportLog;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.List;

/**
 * @author veronika.buksha
 */

@NamePattern("%s|name")
@Table(name = "TSADV_IMPORT_SCENARIO")
@Entity(name = "tsadv$ImportScenario")
public class ImportScenario extends AbstractParentEntity {
    private static final long serialVersionUID = 4845051479645263425L;

    @Column(name = "NAME", nullable = false)
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TEMPLATE_ID")
    protected FileDescriptor template;

    @Lob
    @Column(name = "COMMENT_")
    protected String comment;

    @Column(name = "IMPORTER_BEAN_NAME", nullable = false)
    protected String importerBeanName;


    @OneToMany(mappedBy = "importScenario")
    protected List<ImportLog> log;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setImporterBeanName(String importerBeanName) {
        this.importerBeanName = importerBeanName;
    }

    public String getImporterBeanName() {
        return importerBeanName;
    }


    public List<ImportLog> getLog() {
        return log;
    }

    public void setLog(List<ImportLog> log) {
        this.log = log;
    }


    public void setTemplate(FileDescriptor template) {
        this.template = template;
    }

    public FileDescriptor getTemplate() {
        return template;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


}