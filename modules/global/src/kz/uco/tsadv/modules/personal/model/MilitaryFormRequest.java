package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_MILITARY_FORM_REQUEST")
@Entity(name = "tsadv_MilitaryFormRequest")
public class MilitaryFormRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 5958264584613260438L;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date date_from;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date date_to;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MILITARY_DOCUMENT_TYPE_ID")
    protected DicMilitaryDocumentType military_document_type;

    @Column(name = "MILITARY_DOCUMENT_TYPE_NAME", length = 2000)
    private String militaryDocumentTypeName;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UDO_ID")
    protected DicUdo udo;

    @Column(name = "DOCUMENT_NUMBER", length = 100)
    protected String document_number;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MILITARY_TYPE_ID")
    protected DicMilitaryType military_type;

    @Column(name = "MILITARY_TYPE_NAME", length = 2000)
    private String militaryTypeName;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTITUDE_TO_MILITARY_ID")
    protected DicAttitudeToMilitary attitude_to_military;


    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TROOPS_STRUCTURE_ID")
    protected DicTroopsStructure troops_structure;

    @Column(name = "COMPOSITION_MILITARY_REGISTRATION", length = 2000)
    private String compositionMilitaryRegistration;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MILITARY_RANK_ID")
    protected DicMilitaryRank military_rank;

    @Column(name = "MILITARY_RANK_NAME", length = 2000)
    private String militaryRankName;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFICER_TYPE_ID")
    protected DicOfficerType officer_type;

    @Column(name = "OFFICER_TYPE_NAME", length = 2000)
    private String officerTypeName;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUITABILITY_TO_MILITARY_ID")
    protected DicSuitabilityToMilitary suitability_to_military;

    @Column(name = "SPECIALIZATION")
    protected String specialization;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSUE_DOC_DATE")
    private Date issueDocDate;

    @Column(name = "ISSUING_AUTHORITY", length = 2000)
    private String issuingAuthority;

    @Column(name = "DELY")
    protected Boolean dely;

    @Column(name = "DELY_DESCRIPTION")
    protected String dely_description;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGISTER_GROUP_ID")
    protected DicRegisterGroup register_group;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGISTER_CATEGORY_ID")
    protected DicRegisterCategory register_category;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_POST")
    protected Date date_post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    private DicRequestStatus requestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MILITARY_FORM_ID")
    private MilitaryForm militaryForm;

    public MilitaryForm getMilitaryForm() {
        return militaryForm;
    }

    public void setMilitaryForm(MilitaryForm militaryForm) {
        this.militaryForm = militaryForm;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public Date getIssueDocDate() {
        return issueDocDate;
    }

    public void setIssueDocDate(Date issueDocDate) {
        this.issueDocDate = issueDocDate;
    }

    public String getOfficerTypeName() {
        return officerTypeName;
    }

    public void setOfficerTypeName(String officerTypeName) {
        this.officerTypeName = officerTypeName;
    }

    public String getMilitaryRankName() {
        return militaryRankName;
    }

    public void setMilitaryRankName(String militaryRankName) {
        this.militaryRankName = militaryRankName;
    }

    public String getCompositionMilitaryRegistration() {
        return compositionMilitaryRegistration;
    }

    public void setCompositionMilitaryRegistration(String compositionMilitaryRegistration) {
        this.compositionMilitaryRegistration = compositionMilitaryRegistration;
    }

    public String getMilitaryTypeName() {
        return militaryTypeName;
    }

    public void setMilitaryTypeName(String militaryTypeName) {
        this.militaryTypeName = militaryTypeName;
    }

    public String getMilitaryDocumentTypeName() {
        return militaryDocumentTypeName;
    }

    public void setMilitaryDocumentTypeName(String militaryDocumentTypeName) {
        this.militaryDocumentTypeName = militaryDocumentTypeName;
    }

    public void setUdo(DicUdo udo) {
        this.udo = udo;
    }

    public DicUdo getUdo() {
        return udo;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


    public DicMilitaryDocumentType getMilitary_document_type() {
        return military_document_type;
    }

    public void setMilitary_document_type(DicMilitaryDocumentType military_document_type) {
        this.military_document_type = military_document_type;
    }


    public void setDate_from(Date date_from) {
        this.date_from = date_from;
    }

    public Date getDate_from() {
        return date_from;
    }

    public void setDate_to(Date date_to) {
        this.date_to = date_to;
    }

    public Date getDate_to() {
        return date_to;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }

    public String getDocument_number() {
        return document_number;
    }

    public void setMilitary_type(DicMilitaryType military_type) {
        this.military_type = military_type;
    }

    public DicMilitaryType getMilitary_type() {
        return military_type;
    }

    public void setAttitude_to_military(DicAttitudeToMilitary attitude_to_military) {
        this.attitude_to_military = attitude_to_military;
    }

    public DicAttitudeToMilitary getAttitude_to_military() {
        return attitude_to_military;
    }


    public void setTroops_structure(DicTroopsStructure troops_structure) {
        this.troops_structure = troops_structure;
    }

    public DicTroopsStructure getTroops_structure() {
        return troops_structure;
    }

    public void setMilitary_rank(DicMilitaryRank military_rank) {
        this.military_rank = military_rank;
    }

    public DicMilitaryRank getMilitary_rank() {
        return military_rank;
    }

    public void setOfficer_type(DicOfficerType officer_type) {
        this.officer_type = officer_type;
    }

    public DicOfficerType getOfficer_type() {
        return officer_type;
    }

    public void setSuitability_to_military(DicSuitabilityToMilitary suitability_to_military) {
        this.suitability_to_military = suitability_to_military;
    }

    public DicSuitabilityToMilitary getSuitability_to_military() {
        return suitability_to_military;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setDely(Boolean dely) {
        this.dely = dely;
    }

    public Boolean getDely() {
        return dely;
    }

    public void setDely_description(String dely_description) {
        this.dely_description = dely_description;
    }

    public String getDely_description() {
        return dely_description;
    }

    public void setRegister_group(DicRegisterGroup register_group) {
        this.register_group = register_group;
    }

    public DicRegisterGroup getRegister_group() {
        return register_group;
    }

    public void setRegister_category(DicRegisterCategory register_category) {
        this.register_category = register_category;
    }

    public DicRegisterCategory getRegister_category() {
        return register_category;
    }

    public void setDate_post(Date date_post) {
        this.date_post = date_post;
    }

    public Date getDate_post() {
        return date_post;
    }


}