package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;

@Table(name = "TSADV_AGREEMENT_DOCUMENT")
@Entity(name = "tsadv$AgreementDocument")
public class AgreementDocument extends StandardEntity {
    private static final long serialVersionUID = -1667921818482102949L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Column(name = "DESCRIPTION")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGREEMENT_ID")
    protected Agreement agreement;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }

    public Agreement getAgreement() {
        return agreement;
    }


    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }


}