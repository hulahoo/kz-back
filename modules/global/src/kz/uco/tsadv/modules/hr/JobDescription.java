package kz.uco.tsadv.modules.hr;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_JOB_DESCRIPTION")
@Entity(name = "tsadv_JobDescription")
public class JobDescription extends AbstractParentEntity {
    private static final long serialVersionUID = 5327705754572637424L;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    private JobDescriptionRequest request;

    public String getBasicInteractionsAtWork() {
        return basicInteractionsAtWork;
    }

    public void setBasicInteractionsAtWork(String basicInteractionsAtWork) {
        this.basicInteractionsAtWork = basicInteractionsAtWork;
    }

    public JobDescriptionRequest getRequest() {
        return request;
    }

    public void setRequest(JobDescriptionRequest request) {
        this.request = request;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public String getPositionDuties() {
        return positionDuties;
    }

    public void setPositionDuties(String positionDuties) {
        this.positionDuties = positionDuties;
    }

    public String getGeneralAdditionalRequirements() {
        return generalAdditionalRequirements;
    }

    public void setGeneralAdditionalRequirements(String generalAdditionalRequirements) {
        this.generalAdditionalRequirements = generalAdditionalRequirements;
    }

    public String getCompulsoryQualificationRequirements() {
        return compulsoryQualificationRequirements;
    }

    public void setCompulsoryQualificationRequirements(String compulsoryQualificationRequirements) {
        this.compulsoryQualificationRequirements = compulsoryQualificationRequirements;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setPositionGroup(PositionGroupExt postionGroup) {
        this.positionGroup = postionGroup;
    }
}