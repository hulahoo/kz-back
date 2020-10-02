package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.personal.dictionary.*;

import javax.persistence.*;
import java.util.Date;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_MILITARY_FORM")
@Entity(name = "tsadv$MilitaryForm")
public class MilitaryForm extends AbstractParentEntity {
    private static final long serialVersionUID = -5715300573800174300L;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date date_from;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date date_to;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MILITARY_DOCUMENT_TYPE_ID")
    protected DicMilitaryDocumentType military_document_type;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UDO_ID")
    protected DicUdo udo;

    @Column(name = "DOCUMENT_NUMBER", length = 100)
    protected String document_number;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MILITARY_TYPE_ID")
    protected DicMilitaryType military_type;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTITUDE_TO_MILITARY_ID")
    protected DicAttitudeToMilitary attitude_to_military;


    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TROOPS_STRUCTURE_ID")
    protected DicTroopsStructure troops_structure;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MILITARY_RANK_ID")
    protected DicMilitaryRank military_rank;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFICER_TYPE_ID")
    protected DicOfficerType officer_type;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUITABILITY_TO_MILITARY_ID")
    protected DicSuitabilityToMilitary suitability_to_military;

    @Column(name = "SPECIALIZATION")
    protected String specialization;

    @Column(name = "DELY")
    protected Boolean dely;

    @Column(name = "DELY_DESCRIPTION")
    protected String dely_description;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGISTER_GROUP_ID")
    protected DicRegisterGroup register_group;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGISTER_CATEGORY_ID")
    protected DicRegisterCategory register_category;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_POST")
    protected Date date_post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

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