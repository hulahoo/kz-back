package kz.uco.tsadv.modules.hr;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_JOB_DESCRIPTION_REQUEST")
@Entity(name = "tsadv_JobDescriptionRequest")
@NamePattern("%s|requestNumber")
public class JobDescriptionRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = 7652517074377070054L;

    public static final String PROCESS_DEFINITION_KEY = "jobDescriptionRequest";

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POSITION_GROUP_ID")
    private PositionGroupExt positionGroup;

    @Lob
    @Column(name = "BASIC_INTERACTIONS_AT_WORK")
    protected String basicInteractionsAtWork;

    @NotNull
    @Lob
    @Column(name = "POSITION_DUTIES", nullable = false)
    private String positionDuties;

    @Lob
    @Column(name = "GENERAL_ADDITIONAL_REQUIREMENTS")
    private String generalAdditionalRequirements;

    @Lob
    @Column(name = "COMPULSORY_QUALIFICATION_REQUIREMENTS")
    private String compulsoryQualificationRequirements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    public String getBasicInteractionsAtWork() {
        return basicInteractionsAtWork;
    }

    public void setBasicInteractionsAtWork(String basicInteractionsAtWork) {
        this.basicInteractionsAtWork = basicInteractionsAtWork;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public String getCompulsoryQualificationRequirements() {
        return compulsoryQualificationRequirements;
    }

    public void setCompulsoryQualificationRequirements(String compulsoryQualificationRequirements) {
        this.compulsoryQualificationRequirements = compulsoryQualificationRequirements;
    }

    public String getGeneralAdditionalRequirements() {
        return generalAdditionalRequirements;
    }

    public void setGeneralAdditionalRequirements(String generalAdditionalRequirements) {
        this.generalAdditionalRequirements = generalAdditionalRequirements;
    }

    public String getPositionDuties() {
        return positionDuties;
    }

    public void setPositionDuties(String positionDuties) {
        this.positionDuties = positionDuties;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
}