package kz.uco.tsadv.web.persondata;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.builders.EditorBuilder;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.PersonQualificationRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.model.ImprovingProfessionalSkillsRequest;
import kz.uco.tsadv.modules.recruitment.model.PersonEducationRequest;
import kz.uco.tsadv.modules.recruitment.model.PersonExperienceRequest;

import javax.inject.Inject;
import javax.inject.Named;

@UiController("tsadv_PersonData")
@UiDescriptor("person-data.xml")
public class PersonData extends Screen {

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
    protected Table<Beneficiary> beneficiaryTable;
    @Named("addressTable.create")
    protected CreateActionExt addressTableCreate;

    @Subscribe
    protected void onInit(InitEvent event) {
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

    }


    public void savePersonData() {
        if (latinPropertyChanged) {
            personExtDc.getItem().setWriteHistory(false);
            dataManager.commit(personExtDc.getItem());
            personExtDl.load();
        }
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
}