package kz.uco.tsadv.web.persondata;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.builders.EditorBuilder;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.PersonQualification;
import kz.uco.tsadv.entity.tb.PersonQualificationRequest;
import kz.uco.tsadv.mixins.SelfServiceMixin;
import kz.uco.tsadv.modules.personal.dictionary.DicPrevJobObligation;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.model.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@UiController("tsadv_PersonData")
@UiDescriptor("person-data.xml")
public class PersonData extends Screen implements SelfServiceMixin {

    @Inject
    protected Screens screens;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected InstanceContainer<PersonExt> personExtDc;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected UserSession userSession;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Button personDataEdit;
    @Inject
    protected Metadata metadata;
    protected boolean latinPropertyChanged = false;
    @Inject
    protected InstanceLoader<PersonExt> personExtDl;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Table<PersonDocument> foreignerDocumentsTable;
    protected DicRequestStatus draftRequestStatus;
    @Inject
    protected CollectionLoader<PersonEducation> personEducationsDl;
    @Inject
    protected CollectionLoader<PersonDocument> personDocumentsForeignerDl;
    @Inject
    protected CollectionLoader<PersonDocument> personDocumentsDl;
    @Inject
    protected CollectionLoader<PersonContact> personContactsDl;
    @Inject
    protected CollectionLoader<PersonQualification> personQualificationsDl;
    @Inject
    protected CollectionLoader<PersonExperience> personExperiencesDl;
    @Inject
    protected Table<Beneficiary> beneficiaryTable;
    @Inject
    protected CollectionLoader<ImprovingProfessionalSkills> improvingProfessionalSkillsesDl;
    @Inject
    protected CollectionLoader<Beneficiary> beneficiariesDl;
    @Named("addressTable.create")
    protected CreateActionExt addressTableCreate;
    @Named("personContactTable.create")
    protected CreateActionExt personContactTableCreate;
    @Named("personLanguageTable.create")
    protected CreateActionExt personLanguageTableCreate;
    @Inject
    protected CollectionLoader<PersonLanguage> personLanguagesDl;
    @Inject
    protected CollectionLoader<Address> addressesDl;
    protected Date systemDate;
    protected UUID personGroupId;
    @Inject
    protected TextField<String> scientificWorksIventionsField;
    @Inject
    protected TextField<String> stateAwardsField;
    @Inject
    protected TextField<String> academicDegreeField;
    @Inject
    protected CollectionLoader<MilitaryForm> militaryFormsDl;
    @Inject
    protected Table<MilitaryForm> militaryTable;
    @Inject
    protected Button firstOtherInformationBtn;
    @Inject
    protected PickerField<DicPrevJobObligation> prevJobObligationField;
    @Inject
    protected LookupField<YesNoEnum> prevJobNDAField;
    @Named("personConvictionTable.create")
    protected CreateActionExt personConvictionTableCreate;
    @Inject
    protected CollectionContainer<PersonConviction> personConvictionsDc;
    @Inject
    protected CollectionLoader<PersonConviction> personConvictionsDl;
    @Inject
    protected CollectionLoader<Retirement> retirementsDl;
    @Inject
    protected Table<Retirement> retirementDocumentsTable;
    @Inject
    protected CollectionLoader<PersonBenefit> personBenefitsDl;
    @Inject
    protected Table<PersonBenefit> benefitTable;
    @Inject
    protected CollectionLoader<PersonClinicDispancer> personClinicDispancersDl;
    @Inject
    protected Table<PersonClinicDispancer> clinicDispancerTable;
    @Inject
    protected Table<Disability> disabilityTable;
    @Named("reasonChangeJobTable.create")
    protected CreateActionExt reasonChangeJobTableCreate;
    @Inject
    protected CollectionLoader<PersonReasonChangingJob> personReasonChangingJobsDl;
    @Inject
    protected CollectionContainer<PersonReasonChangingJob> personReasonChangingJobsDc;
    @Inject
    protected Table<PersonCriminalAdministrativeLiability> cALTable;
    @Inject
    protected CollectionLoader<PersonCriminalAdministrativeLiability> personCriminalAdministrativeLiabilitiesDl;
    @Inject
    protected CollectionLoader<PersonHealth> personHealthsDl;
    @Named("personHealthTable.create")
    protected CreateActionExt personHealthTableCreate;
    @Inject
    protected Table<PersonHealth> personHealthTable;
    @Inject
    protected CollectionContainer<PersonHealth> personHealthsDc;
    @Inject
    protected CollectionLoader<PersonRelativesHaveProperty> personRelativesHavePropertiesDl;
    @Named("relativePropertyTable.create")
    protected CreateActionExt relativePropertyTableCreate;
    @Inject
    protected Table<PersonBankDetails> bankDetailsTable;
    @Inject
    protected CollectionLoader<PersonBankDetails> personBankDetailsesDl;

    @Subscribe(id = "personExtDc", target = Target.DATA_CONTAINER)
    protected void onPersonExtDcItemChange(InstanceContainer.ItemChangeEvent<PersonExt> event) {
        personGroupId = event.getItem().getGroup().getId();
        personDocumentsForeignerDl.setParameter("personGroup", personGroupId);
        personDocumentsForeignerDl.setParameter("systemDate", systemDate);
        personDocumentsForeignerDl.load();
        personEducationsDl.setParameter("personGroup", personGroupId);
        personEducationsDl.setParameter("systemDate", systemDate);
        personEducationsDl.load();
        personExperiencesDl.setParameter("personGroup", personGroupId);
        personExperiencesDl.setParameter("systemDate", systemDate);
        personExperiencesDl.load();
        personQualificationsDl.setParameter("personGroup", personGroupId);
        personQualificationsDl.setParameter("systemDate", systemDate);
        personQualificationsDl.load();
        personDocumentsDl.setParameter("personGroup", personGroupId);
        personDocumentsDl.setParameter("systemDate", systemDate);
        personDocumentsDl.load();
        beneficiariesDl.setParameter("personGroup", personGroupId);
        beneficiariesDl.setParameter("systemDate", systemDate);
        beneficiariesDl.load();
        improvingProfessionalSkillsesDl.setParameter("personGroup", personGroupId);
        improvingProfessionalSkillsesDl.setParameter("systemDate", systemDate);
        improvingProfessionalSkillsesDl.load();
        addressesDl.setParameter("personGroup", personGroupId);
        addressesDl.load();
        personContactsDl.setParameter("personGroup", personGroupId);
        personContactsDl.load();
        personLanguagesDl.setParameter("personGroup", personGroupId);
        personLanguagesDl.load();
        personConvictionsDl.setParameter("personGroup", personGroupId);
        personConvictionsDl.load();
        personConvictionTableCreate.setEnabled(personConvictionsDc.getItems().isEmpty());
        militaryFormsDl.setParameter("personGroup", personGroupId);
        militaryFormsDl.setParameter("systemDate", systemDate);
        militaryFormsDl.load();
        retirementsDl.setParameter("personGroup", personGroupId);
        retirementsDl.setParameter("systemDate", systemDate);
        retirementsDl.load();
        personBenefitsDl.setParameter("personGroup", personGroupId);
        personBenefitsDl.setParameter("systemDate", systemDate);
        personBenefitsDl.load();
        personCriminalAdministrativeLiabilitiesDl.setParameter("personGroup", personGroupId);
        personCriminalAdministrativeLiabilitiesDl.setParameter("systemDate", systemDate);
        personCriminalAdministrativeLiabilitiesDl.load();
        personClinicDispancersDl.setParameter("personGroup", personGroupId);
        personClinicDispancersDl.setParameter("systemDate", systemDate);
        personClinicDispancersDl.load();
        personReasonChangingJobsDl.setParameter("personGroup", personGroupId);
        personReasonChangingJobsDl.load();
        reasonChangeJobTableCreate.setEnabled(personReasonChangingJobsDc.getItems().isEmpty());
        personHealthsDl.setParameter("personGroup", personGroupId);
        personHealthsDl.setParameter("systemDate", systemDate);
        personHealthsDl.load();
        personRelativesHavePropertiesDl.setParameter("personGroup", personGroupId);
        personRelativesHavePropertiesDl.load();
        personBankDetailsesDl.setParameter("personGroup", personGroupId);
        personBankDetailsesDl.setParameter("systemDate", systemDate);
        personBankDetailsesDl.load();
    }


    @Subscribe
    protected void onInit(InitEvent event) {
        systemDate = userSessionSource.getUserSession().getAttribute("systemDate");
        draftRequestStatus = dataManager.loadValue(
                "select e from tsadv$DicRequestStatus e " +
                        " where e.code = 'DRAFT'", DicRequestStatus.class).one();
        PersonGroupExt personGroupExt = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);
        PersonExt person = dataManager.reload(personGroupExt, "personGroupExt-person-data")
                .getPerson();
        personExtDc.setItem(person);

    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        PersonExt personExt = personExtDc.getItem();
        dataManager.load(PersonalDataRequest.class).query(
                "select e from tsadv$PersonalDataRequest e " +
                        " where e.personGroup.id = :personId and e.status.code = 'DRAFT'")
                .parameter("personId", personExt.getGroup().getId()).view("personalDataRequest-edit").optional()
                .ifPresent(personalDataRequest -> personDataEdit.setEnabled(false));
        personExtDc.addItemPropertyChangeListener(personExtItemPropertyChangeEvent -> {
            if ("lastNameLatin".equals(personExtItemPropertyChangeEvent.getProperty()) ||
                    "firstNameLatin".equals(personExtItemPropertyChangeEvent.getProperty())) {
                latinPropertyChanged = true;
            }
        });
        addressTableCreate.setInitializer(o -> {
            Address address = (Address) o;
            address.setPersonGroup(personExtDc.getItem().getGroup());
        });
        personContactTableCreate.setInitializer(o -> {
            PersonContact personContact = (PersonContact) o;
            personContact.setPersonGroup(personExtDc.getItem().getGroup());
        });
        personLanguageTableCreate.setInitializer(o -> {
            PersonLanguage personLanguage = (PersonLanguage) o;
            personLanguage.setPersonGroup(personExtDc.getItem().getGroup());
        });
        firstOtherInformationBtn.setEnabled(personExtDc.getItem().getPrevJobObligation() == null
                || personExtDc.getItem().getPrevJobNDA() == null);
        prevJobNDAField.setEditable(personExtDc.getItem().getPrevJobNDA() == null);
        prevJobObligationField.setEditable(personExtDc.getItem().getPrevJobObligation() == null);
        personConvictionTableCreate.setInitializer(o -> {
            PersonConviction personConviction = (PersonConviction) o;
            personConviction.setPersonGroup(personExtDc.getItem().getGroup());
        });
        reasonChangeJobTableCreate.setInitializer(o -> {
            PersonReasonChangingJob personReasonChangingJob = (PersonReasonChangingJob) o;
            personReasonChangingJob.setPersonGroup(personExtDc.getItem().getGroup());
        });
        personHealthTableCreate.setInitializer(o -> {
            PersonHealth personHealth = (PersonHealth) o;
            personHealth.setPersonGroup(personExtDc.getItem().getGroup());
            personHealth.setStartDateHistory(BaseCommonUtils.getSystemDate());
            personHealth.setEndDateHistory(BaseCommonUtils.getEndOfTime());
        });
        relativePropertyTableCreate.setInitializer(o -> {
            PersonRelativesHaveProperty haveProperty = (PersonRelativesHaveProperty) o;
            haveProperty.setPersonGroup(personExtDc.getItem().getGroup());
        });

    }


    public void savePersonData() {
        if (latinPropertyChanged) {
            personExtDc.getItem().setWriteHistory(false);
            dataManager.commit(personExtDc.getItem());
            personExtDl.load();
        }
    }

    public void saveHaveChildWithoutParent() {
        personExtDc.getItem().setWriteHistory(false);
        dataManager.commit(personExtDc.getItem());
        personExtDl.load();
    }

    public void savePersonDataWithHistory() {
        if (latinPropertyChanged) {
            personExtDc.getItem().setWriteHistory(true);
            dataManager.commit(personExtDc.getItem());
            personExtDl.load();
        }
    }

    public void personDataRequest() {
        PersonExt personExt = personExtDc.getItem();
        PersonalDataRequest personalData = dataManager.load(PersonalDataRequest.class).query(
                "select e from tsadv$PersonalDataRequest e " +
                        " where e.personGroup.id = :personId and e.status.code = 'DRAFT'")
                .parameter("personId", personExt.getGroup().getId()).view("personalDataRequest-edit").optional()
                .orElse(null);
        EditorBuilder<PersonalDataRequest> editorBuilder;
        if (personalData != null) {
            editorBuilder = screenBuilders.editor(PersonalDataRequest.class, this).editEntity(personalData);
        } else {
            editorBuilder = screenBuilders.editor(PersonalDataRequest.class, this).newEntity()
                    .withInitializer(personalDataRequest -> {
                        personalDataRequest.setPersonGroup(personExt.getGroup());
                        personalDataRequest.setLastName(personExt.getLastName());
                        personalDataRequest.setFirstName(personExt.getFirstName());
                        personalDataRequest.setMiddleName(personExt.getMiddleName());
                        personalDataRequest.setDateOfBirth(personExt.getDateOfBirth());
                        personalDataRequest.setNationality(personExt.getNationality());
                        personalDataRequest.setCitizenship(personExt.getCitizenship());
                        personalDataRequest.setNationalIdentifier(personExt.getNationalIdentifier());
                        personalDataRequest.setStatus(draftRequestStatus);
                    });
        }
        editorBuilder.withOpenMode(OpenMode.DIALOG).build().show();
    }


    public void createRequest(Component source) {
        screenBuilders.editor(PersonDocumentRequest.class, this).newEntity()
                .withInitializer(personDocumentRequest -> {
                    personDocumentRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    personDocumentRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true,
                "foreigner", source.getId().equals("createRequest"))))
                .build().show();
    }

    public void editRequest(Component source) {
        PersonDocument personDocument = foreignerDocumentsTable.getSingleSelected();
        screenBuilders.editor(PersonDocumentRequest.class, this).newEntity()
                .withInitializer(personDocumentRequest -> {
                    personDocumentRequest.setDocumentNumber(personDocument.getDocumentNumber());
                    personDocumentRequest.setDocumentType(personDocument.getDocumentType());
                    personDocumentRequest.setIssuingAuthority(personDocument.getIssuingAuthority());
                    personDocumentRequest.setIssueDate(personDocument.getIssueDate());
                    personDocumentRequest.setExpiredDate(personDocument.getExpiredDate());
                    personDocumentRequest.setDescription(personDocument.getDescription());
                    personDocumentRequest.setFile(personDocument.getFile());
                    personDocumentRequest.setSeries(personDocument.getSeries());
                    personDocumentRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    personDocumentRequest.setRequestStatus(draftRequestStatus);
                    personDocumentRequest.setEditedPersonDocument(personDocument);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true,
                "foreigner", source.getId().equals("createRequest"))))
                .build().show();
    }

    public void createEducationRequest(Component source) {
        screenBuilders.editor(PersonEducationRequest.class, this).newEntity()
                .withInitializer(personEducationRequest -> {
                    personEducationRequest.setPersonGroup(dataManager.reload(personExtDc.getItem().getGroup(),
                            "personGroupExt-for-person-data"));
                    personEducationRequest.setStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void createImprovingProfSkillsRequest(Component source) {
        screenBuilders.editor(ImprovingProfessionalSkillsRequest.class, this).newEntity()
                .withInitializer(improvingProfessionalSkillsRequest -> {
                    improvingProfessionalSkillsRequest.setPersonGroup(dataManager.reload(personExtDc.getItem().getGroup(),
                            "personGroupExt-for-person-data"));
                    improvingProfessionalSkillsRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void createQualificationRequest(Component source) {
        screenBuilders.editor(PersonQualificationRequest.class, this).newEntity()
                .withInitializer(personQualificationRequest -> {
                    personQualificationRequest.setPersonGroup(dataManager.reload(personExtDc.getItem().getGroup(),
                            "personGroupExt-for-person-data"));
                    personQualificationRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void createExperiencesRequest(Component source) {
        screenBuilders.editor(PersonExperienceRequest.class, this).newEntity()
                .withInitializer(personExperienceRequest -> {
                    personExperienceRequest.setPersonGroup(dataManager.reload(personExtDc.getItem().getGroup(),
                            "personGroupExt-for-person-data"));
                    personExperienceRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void createBeneficiaryRequest(Component source) {
        screenBuilders.editor(BeneficiaryRequest.class, this).newEntity()
                .withInitializer(beneficiary -> {
                    beneficiary.setPersonGroup(dataManager.reload(personExtDc.getItem().getGroup(),
                            "personGroupExt-for-person-data"));
                    beneficiary.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void editBeneficiaryRequest(Component source) {
        Beneficiary beneficiary = beneficiaryTable.getSingleSelected();
        screenBuilders.editor(BeneficiaryRequest.class, this).newEntity()
                .withInitializer(beneficiaryRequest -> {
                    beneficiaryRequest.setFirstName(beneficiary.getFirstName());
                    beneficiaryRequest.setLastName(beneficiary.getLastName());
                    beneficiaryRequest.setMiddleName(beneficiary.getMiddleName());
                    beneficiaryRequest.setRelationshipType(beneficiary.getRelationshipType());
                    beneficiaryRequest.setBirthDate(beneficiary.getBirthDate());
                    beneficiaryRequest.setRelationshipType(beneficiary.getRelationshipType());
                    beneficiaryRequest.setBeneficiary(beneficiary);
                    beneficiaryRequest.setWorkLocation(beneficiary.getWorkLocation());
                    beneficiaryRequest.setAdditionalContact(beneficiary.getAdditionalContact());
                    beneficiaryRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    beneficiaryRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }


    public void additionalDataRequest() {
        PersonExt personExt = personExtDc.getItem();
        PersonAdwardRequest personAdwardRequest = dataManager.load(PersonAdwardRequest.class).query(
                "select e from tsadv_PersonAdwardRequest e " +
                        " where e.personGroup.id = :personId and e.requestStatus.code = 'DRAFT'")
                .parameter("personId", personExt.getGroup().getId()).view("personAdwardRequest-edit").optional()
                .orElse(null);
        EditorBuilder<PersonAdwardRequest> editorBuilder;
        if (personAdwardRequest != null) {
            editorBuilder = screenBuilders.editor(PersonAdwardRequest.class, this).editEntity(personAdwardRequest);
        } else {
            editorBuilder = screenBuilders.editor(PersonAdwardRequest.class, this).newEntity()
                    .withInitializer(adwardRequest -> {
                        adwardRequest.setPersonGroup(personExt.getGroup());
                        adwardRequest.setAcademicDegree(personExt.getAcademicDegree());
                        adwardRequest.setScientificWorksIventions(personExt.getScientificWorksIventions());
                        adwardRequest.setStateAwards(personExt.getStateAwards());
                        adwardRequest.setRequestStatus(draftRequestStatus);
                    });
        }
        editorBuilder.withOpenMode(OpenMode.DIALOG).build().show();
    }

    public void editMilitaryRequest(Component source) {
        MilitaryForm militaryForm = militaryTable.getSingleSelected();
        screenBuilders.editor(MilitaryFormRequest.class, this).newEntity()
                .withInitializer(militaryFormRequest -> {
                    militaryFormRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    militaryFormRequest.setRequestStatus(draftRequestStatus);
                    militaryFormRequest.setMilitaryForm(militaryForm);
                    militaryFormRequest.setAttitude_to_military(militaryForm.getAttitude_to_military());
                    militaryFormRequest.setCompositionMilitaryRegistration(militaryForm.getCompositionMilitaryRegistration());
                    militaryFormRequest.setDate_from(militaryForm.getDate_from());
                    militaryFormRequest.setDate_to(militaryForm.getDate_to());
                    militaryFormRequest.setDocument_number(militaryForm.getDocument_number());
                    militaryFormRequest.setIssueDocDate(militaryForm.getIssueDocDate());
                    militaryFormRequest.setIssuingAuthority(militaryForm.getIssuingAuthority());
                    militaryFormRequest.setMilitaryDocumentTypeName(militaryForm.getMilitaryDocumentTypeName());
                    militaryFormRequest.setOfficerTypeName(militaryForm.getOfficerTypeName());
                    militaryFormRequest.setMilitaryRankName(militaryForm.getMilitaryRankName());
                    militaryFormRequest.setMilitaryTypeName(militaryForm.getMilitaryTypeName());
                    militaryFormRequest.setSpecialization(militaryForm.getSpecialization());
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void createMilitaryRequest(Component source) {
        screenBuilders.editor(MilitaryFormRequest.class, this).newEntity()
                .withInitializer(militaryFormRequest -> {
                    militaryFormRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    militaryFormRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void saveFirstOtherInformation() {
        personExtDc.getItem().setWriteHistory(false);
        dataManager.commit(personExtDc.getItem());
        personExtDl.load();
        firstOtherInformationBtn.setEnabled(personExtDc.getItem().getPrevJobObligation() == null
                || personExtDc.getItem().getPrevJobNDA() == null);
        prevJobNDAField.setEditable(personExtDc.getItem().getPrevJobNDA() == null);
        prevJobObligationField.setEditable(personExtDc.getItem().getPrevJobObligation() == null);
    }

    public void createRetirementRequest() {
        screenBuilders.editor(RetirementRequest.class, this).newEntity()
                .withInitializer(retirementRequest -> {
                    retirementRequest.setPersonGroupExt(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    retirementRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void editRetirementRequest() {
        Retirement retirement = retirementDocumentsTable.getSingleSelected();
        screenBuilders.editor(RetirementRequest.class, this).newEntity()
                .withInitializer(retirementRequest -> {
                    retirementRequest.setDocumentNumber(retirement.getDocumentNumber());
                    retirementRequest.setRetirementType(retirement.getRetirementType());
                    retirementRequest.setRetirement(retirement);
                    retirementRequest.setIsseuDocDate(retirement.getIsseuDocDate());
                    retirementRequest.setPersonGroupExt(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    retirementRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void createBenefitRequest() {
        screenBuilders.editor(PersonBenefitRequest.class, this).newEntity()
                .withInitializer(benefitRequest -> {
                    benefitRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    benefitRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void editBenefitRequest() {
        PersonBenefit personBenefit = benefitTable.getSingleSelected();
        screenBuilders.editor(PersonBenefitRequest.class, this).newEntity()
                .withInitializer(benefitRequest -> {
                    benefitRequest.setDocumentNumber(personBenefit.getDocumentNumber());
                    benefitRequest.setReason(personBenefit.getReason());
                    benefitRequest.setPersonBenefit(personBenefit);
                    benefitRequest.setCertificateFromDate(personBenefit.getCertificateFromDate());
                    benefitRequest.setCombatant(personBenefit.getCombatant());
                    benefitRequest.setRadiationRiskZone(personBenefit.getRadiationRiskZone());
                    benefitRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    benefitRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void createСlinicDispancerRequest() {
        screenBuilders.editor(PersonClinicDispancerRequest.class, this).newEntity()
                .withInitializer(dispancerRequest -> {
                    dispancerRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    dispancerRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void editСlinicDispancerRequest() {
        PersonClinicDispancer personClinicDispancer = clinicDispancerTable.getSingleSelected();
        screenBuilders.editor(PersonClinicDispancerRequest.class, this).newEntity()
                .withInitializer(dispancerRequest -> {
                    dispancerRequest.setHaveClinicDispancer(personClinicDispancer.getHaveClinicDispancer());
                    dispancerRequest.setPeriodFrom(personClinicDispancer.getPeriodFrom());
                    dispancerRequest.setPersonClinicDispancer(personClinicDispancer);
                    dispancerRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    dispancerRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void createDisabilityRequest() {
        screenBuilders.editor(DisabilityRequest.class, this).newEntity()
                .withInitializer(disabilityRequest -> {
                    disabilityRequest.setPersonGroupExt(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    disabilityRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void editDisabilityRequest() {
        Disability disability = disabilityTable.getSingleSelected();
        screenBuilders.editor(DisabilityRequest.class, this).newEntity()
                .withInitializer(disabilityRequest -> {
                    disabilityRequest.setAttachment(disability.getAttachment());
                    disabilityRequest.setGroup(disability.getGroup());
                    disabilityRequest.setDisability(disability);
                    disabilityRequest.setAttachmentName(disability.getAttachmentName());
                    disabilityRequest.setDateFrom(disability.getDateFrom());
                    disabilityRequest.setDateTo(disability.getDateTo());
                    disabilityRequest.setDisabilityType(disability.getDisabilityType());
                    disabilityRequest.setDuration(disability.getDuration());
                    disabilityRequest.setHaveDisability(disability.getHaveDisability());
                    disabilityRequest.setPersonGroupExt(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    disabilityRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    @Subscribe(id = "personReasonChangingJobsDc", target = Target.DATA_CONTAINER)
    protected void onPersonReasonChangingJobsDcCollectionChange(CollectionContainer.CollectionChangeEvent<PersonReasonChangingJob> event) {
        reasonChangeJobTableCreate.setEnabled(event.getSource().getItems().isEmpty());

    }


    public void createCALRequest() {
        screenBuilders.editor(PersonCriminalAdministrativeLiabilityRequest.class, this).newEntity()
                .withInitializer(liabilityRequest -> {
                    liabilityRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    liabilityRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void editCALRequest() {
        PersonCriminalAdministrativeLiability criminalAdministrativeLiability = cALTable.getSingleSelected();
        screenBuilders.editor(PersonCriminalAdministrativeLiabilityRequest.class, this).newEntity()
                .withInitializer(disabilityRequest -> {
                    disabilityRequest.setHaveLiability(criminalAdministrativeLiability.getHaveLiability());
                    disabilityRequest.setReasonPeriod(criminalAdministrativeLiability.getReasonPeriod());
                    disabilityRequest.setLiability(criminalAdministrativeLiability);
                    disabilityRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    disabilityRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void editHealth(Component source) {
        PersonHealth personHealth = personHealthTable.getSingleSelected();
        PersonHealth personHealthOld = metadata.getTools().copy(personHealth);
        screenBuilders.editor(PersonHealth.class, this).editEntity(personHealth)
                .build().show().addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.getCloseAction() != null
                    && afterCloseEvent.getCloseAction().toString().contains("commit")
                    && !personHealthOld.getStartDateHistory().equals(BaseCommonUtils.getSystemDate())) {
                personHealthOld.setId(UUID.randomUUID());
                personHealthOld.setEndDateHistory(Date.from(LocalDateTime.from(BaseCommonUtils.getSystemDate().toInstant())
                        .plusDays(-1).atZone(ZoneId.systemDefault()).toInstant()));
                dataManager.commit(personHealthOld);
            }
            personHealthsDl.load();
        });
    }

    public void createBankDetailsRequest() {
        screenBuilders.editor(PersonBankdetailsRequest.class, this).newEntity()
                .withInitializer(bankdetailsRequest -> {
                    bankdetailsRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    bankdetailsRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void editBankDetailsRequest() {
        PersonBankDetails bankDetails = bankDetailsTable.getSingleSelected();
        screenBuilders.editor(PersonBankdetailsRequest.class, this).newEntity()
                .withInitializer(bankdetailsRequest -> {
                    bankdetailsRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    bankdetailsRequest.setRequestStatus(draftRequestStatus);
                    bankdetailsRequest.setBank(bankDetails.getBank());
                    bankdetailsRequest.setFullNameBankCard(bankDetails.getFullNameBankCard());
                    bankdetailsRequest.setBicBank(bankDetails.getBicBank());
                    bankdetailsRequest.setIban(bankDetails.getIban());
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }
}