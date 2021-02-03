package kz.uco.tsadv.web.persondata;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.builders.EditorBuilder;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.base.cuba.actions.EditActionExt;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.PersonQualification;
import kz.uco.tsadv.entity.tb.PersonQualificationRequest;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.mixins.SelfServiceMixin;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.web.screens.address.AddressPersonDataEdit;
import kz.uco.tsadv.web.screens.personcontact.PersonContactPersonDataEdit;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@UiController("tsadv_PersonData")
@UiDescriptor("person-data.xml")
public class PersonData extends Screen implements SelfServiceMixin {

    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected Notifications notifications;
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
    protected DicRequestStatus draftRequestStatus;
    @Inject
    protected CollectionLoader<PersonEducation> personEducationsDl;
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
    protected CollectionLoader<MilitaryForm> militaryFormsDl;
    @Inject
    protected Table<MilitaryForm> militaryTable;
    @Inject
    protected Button firstOtherInformationBtn;
    @Inject
    protected CollectionLoader<Retirement> retirementsDl;
    @Inject
    protected Table<Retirement> retirementDocumentsTable;
    @Inject
    protected CollectionLoader<PersonBenefit> personBenefitsDl;
    @Inject
    protected Table<PersonBenefit> benefitTable;
    @Inject
    protected CollectionLoader<PersonRelativesHaveProperty> personRelativesHavePropertiesDl;
    @Named("relativePropertyTable.create")
    protected CreateActionExt relativePropertyTableCreate;
    @Inject
    protected Table<PersonBankDetails> bankDetailsTable;
    @Inject
    protected CollectionLoader<PersonBankDetails> personBankDetailsesDl;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected Table<PersonDocument> personDocumentsTable;
    @Named("addressTable.edit")
    protected EditActionExt addressTableEdit;
    @Named("personContactTable.edit")
    protected EditActionExt personContactTableEdit;
    @Inject
    protected CollectionLoader<Awards> awardsesDl;
    @Inject
    protected CollectionContainer<Awards> awardsesDc;

    @Subscribe(id = "personExtDc", target = Target.DATA_CONTAINER)
    protected void onPersonExtDcItemChange(InstanceContainer.ItemChangeEvent<PersonExt> event) {
        personGroupId = event.getItem().getGroup().getId();
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
//        personContactsDl.setParameter("personGroup", personGroupId);
//        personContactsDl.load();
        personLanguagesDl.setParameter("personGroup", personGroupId);
        personLanguagesDl.load();
        awardsesDl.setParameter("personGroup", personGroupId);
        awardsesDl.setParameter("systemDate", systemDate);
        awardsesDl.load();
        militaryFormsDl.setParameter("personGroup", personGroupId);
        militaryFormsDl.setParameter("systemDate", systemDate);
        militaryFormsDl.load();
        retirementsDl.setParameter("personGroup", personGroupId);
        retirementsDl.setParameter("systemDate", systemDate);
        retirementsDl.load();
        personBenefitsDl.setParameter("personGroup", personGroupId);
        personBenefitsDl.setParameter("systemDate", systemDate);
        personBenefitsDl.load();
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
        addressTableEdit.setScreenClass(AddressPersonDataEdit.class);
        addressTableCreate.setScreenClass(AddressPersonDataEdit.class);
        addressTableCreate.setInitializer(o -> {
            Address address = (Address) o;
            address.setPersonGroup(personExtDc.getItem().getGroup());
            address.setStartDate(CommonUtils.getSystemDate());
            address.setEndDate(CommonUtils.getEndOfTime());
        });
        personContactTableCreate.setScreenClass(PersonContactPersonDataEdit.class);
        personContactTableEdit.setScreenClass(PersonContactPersonDataEdit.class);
        personContactTableEdit.setOpenMode(OpenMode.THIS_TAB);
        personContactTableCreate.setOpenMode(OpenMode.THIS_TAB);
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
        editorBuilder.withOpenMode(OpenMode.THIS_TAB).build().show();
    }


    public void createRequest(Component source) {
        screenBuilders.editor(PersonDocumentRequest.class, this).newEntity()
                .withInitializer(personDocumentRequest -> {
                    personDocumentRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    personDocumentRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true,
                "isForeigner", isForeigner())))
                .build().show();
    }

    public void editRequest(Component source) {
        PersonDocument personDocument = personDocumentsTable.getSingleSelected();
        screenBuilders.editor(PersonDocumentRequest.class, this).newEntity()
                .withInitializer(personDocumentRequest -> {
                    personDocumentRequest.setDocumentNumber(personDocument.getDocumentNumber());
                    personDocumentRequest.setDocumentType(personDocument.getDocumentType());
                    personDocumentRequest.setIssuingAuthority(personDocument.getIssuingAuthority());
                    personDocumentRequest.setIssueDate(personDocument.getIssueDate());
                    personDocumentRequest.setExpiredDate(personDocument.getExpiredDate());
                    personDocumentRequest.setSeries(personDocument.getSeries());
                    personDocumentRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    personDocumentRequest.setAttachments(personDocument.getAttachments());
                    personDocumentRequest.setRequestStatus(draftRequestStatus);
                    personDocumentRequest.setEditedPersonDocument(personDocument);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true,
                "isForeigner", isForeigner())))
                .build().show();
    }

    protected boolean isForeigner() {
        return !(personExtDc.getItem().getCitizenship() == null
                || personExtDc.getItem().getCitizenship().getCode() == null
                || personExtDc.getItem().getCitizenship().getCode().equals("PQH_KZ"));
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
                    beneficiaryRequest.setRelationDegree(beneficiary.getRelationDegree());
                    beneficiaryRequest.setBeneficiary(beneficiary);
                    beneficiaryRequest.setWorkLocation(beneficiary.getWorkLocation());
                    beneficiaryRequest.setAdditionalContact(beneficiary.getAdditionalContact());
                    beneficiaryRequest.setRelatedPersonGroup(beneficiary.getRelatedPersonGroup());
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


    public void createDisabilityRequest() {
        screenBuilders.editor(DisabilityRequest.class, this).newEntity()
                .withInitializer(disabilityRequest -> {
                    disabilityRequest.setPersonGroupExt(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    disabilityRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
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


    public void createBankDetailsRequest() {
        if (CommonUtils.getSystemDate().getDate() < 11
                && CommonUtils.getSystemDate().getDate() > 20) {
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption(messageBundle.getMessage("bankRangeOut")).show();
            return;
        }

        screenBuilders.editor(PersonBankdetailsRequest.class, this).newEntity()
                .withInitializer(bankdetailsRequest -> {
                    bankdetailsRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    bankdetailsRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void editBankDetailsRequest() {
        if (CommonUtils.getSystemDate().getDate() < 11
                && CommonUtils.getSystemDate().getDate() > 20) {
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption(messageBundle.getMessage("bankRangeOut")).show();
            return;
        }
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


    public Component filesLinks(Entity entity) {
        List<FileDescriptor> attachments = null;
        Method getAttachments = Arrays.stream(entity.getClass().getMethods()).filter(method ->
                method.getName().equals("getAttachments")).findFirst().orElse(null);
        if (getAttachments != null) {
            try {
                attachments = (List<FileDescriptor>) getAttachments.invoke(entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        return getFileLinks(attachments);
    }

    private HBoxLayout getFileLinks(List<FileDescriptor> attachments) {
        HBoxLayout hBoxLayout = uiComponents.create(HBoxLayout.class);
        hBoxLayout.setSpacing(true);
        for (FileDescriptor attachment : attachments) {
            LinkButton linkButton = uiComponents.create(LinkButton.class);
            linkButton.setCaption(attachment.getName());
            linkButton.setAction(new BaseAction(attachment.getId().toString()) {
                @Override
                public void actionPerform(Component component) {
                    exportDisplay.show(attachment);
                }
            });
            hBoxLayout.add(linkButton);
        }
        return hBoxLayout;
    }

    public void createAwardsRequest() {
        screenBuilders.editor(AwardsRequest.class, this).newEntity()
                .withInitializer(awardsRequest -> {
                    awardsRequest.setPersonGroup(dataManager
                            .reload(personExtDc.getItem().getGroup(), "personGroupExt-for-person-data"));
                    awardsRequest.setRequestStatus(draftRequestStatus);
                }).withOptions(new MapScreenOptions(ParamsMap.of("fromPersonData", true)))
                .build().show();
    }

    public void onHaveConvinctionsAttachments() {
    }

    public void onHaveNDAAttachments() {
    }

    public void onСommitmentsFromPrevJobAttachments() {
    }

    public void onRegisteredDispensaryAttachments() {
    }

    public void onDisabilityAttachments() {
    }

    public void onContraindicationsHealthAttachments() {
    }

    public void onChildUnder18WithoutFatherOrMotherAttachments() {
    }

    public void onChildUnder14WithoutFatherOrMotherAttachments() {
    }

    public void onCriminalAdministrativeLiabilityAttachments() {
    }
}