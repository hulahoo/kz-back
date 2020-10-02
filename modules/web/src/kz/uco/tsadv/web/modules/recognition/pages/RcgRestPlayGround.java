package kz.uco.tsadv.web.modules.recognition.pages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.LookupField;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.recognition.Recognition;
import kz.uco.tsadv.modules.recognition.dictionary.DicPersonPreferenceType;
import kz.uco.tsadv.modules.recognition.dictionary.DicQuality;
import kz.uco.tsadv.modules.recognition.dictionary.DicRecognitionType;
import kz.uco.tsadv.modules.recognition.enums.AwardStatus;
import kz.uco.tsadv.modules.recognition.pojo.PersonAwardPojo;
import kz.uco.tsadv.modules.recognition.pojo.QualityPojo;
import kz.uco.tsadv.modules.recognition.pojo.RecognitionCreatePojo;
import kz.uco.tsadv.service.RecognitionRestService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RcgRestPlayGround extends AbstractWindow {

    @Inject
    private FieldGroup recognitionsCountFG;

    @Inject
    private FieldGroup loadRecognitionsFG, recognitionCommentsCountFG, updatePersonPreferenceFg,
            getPersonPreferenceTypesFg, loadPersonPreferencesFg, createRecognitionFG,
            recognitionCommentsFG, loadTeamProfilesFG, loadProfileFG,
            loadTopNomineeFG, allNomineesCountFG, loadAllNomineeFG,
            myNomineesCountFG, loadMyNomineesFG, nominateFG, getDraftPersonAwardFG,
            loadQualitiesFG, loadOrganizationsFG;
    @Inject
    private DataManager dataManager;
    @Inject
    private RecognitionRestService recognitionRestService;
    @Inject
    private Metadata metadata;

    private FieldGroup valueFieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        LookupField wallType = (LookupField) recognitionsCountFG.getFieldNN("wallType").getComponentNN();
        wallType.setOptionsList(Arrays.asList(-1, 0, 1));
        wallType.setValue(-1);

        LookupField wallType1 = (LookupField) loadRecognitionsFG.getFieldNN("wallType").getComponentNN();
        wallType1.setOptionsList(wallType.getOptionsList());
        wallType1.setValue(wallType.getValue());
    }

    public void loadOrganizations() {
        this.valueFieldGroup = loadOrganizationsFG;

        try {
            String json = recognitionRestService.loadOrganizations(getFieldValue("language"));

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void loadQualities() {
        this.valueFieldGroup = loadQualitiesFG;

        try {
            String json = recognitionRestService.loadQualities(getFieldValue("language"));

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void getDraftPersonAward() {
        this.valueFieldGroup = getDraftPersonAwardFG;
        try {
            String json = recognitionRestService.getDraftPersonAward(
                    getFieldValue("authorEmployeeNumber"),
                    getFieldValue("receiverEmployeeNumber"));

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void nominate() {
        this.valueFieldGroup = nominateFG;

        try {
            PersonAwardPojo personAwardPojo = metadata.create(PersonAwardPojo.class);
            personAwardPojo.setAuthorEmployeeNumber(getFieldValue("authorEmployeeNumber"));
            personAwardPojo.setReceiverEmployeeNumber(getFieldValue("receiverEmployeeNumber"));
            personAwardPojo.setHistory(getFieldValue("history"));
            personAwardPojo.setWhy(getFieldValue("why"));

            AwardStatus awardStatus = getFieldValue("status");
            personAwardPojo.setStatus(awardStatus != null ? awardStatus.getId() : null);

            recognitionRestService.createPersonAward(personAwardPojo, getFieldValue("language"));
            showNotification("PersonAward success created!");
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void loadMyNominees() {
        this.valueFieldGroup = loadMyNomineesFG;

        try {
            OrganizationGroupExt organizationGroupExt = getFieldValue("organizationGroupId");

            String json = recognitionRestService.loadMyNominees(
                    getFieldValue("offset"),
                    getFieldValue("maxResults"),
                    getFieldValue("year"),
                    getFieldValue("language"),
                    organizationGroupExt != null ? organizationGroupExt.getId().toString() : null);

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void myNomineesCount() {
        this.valueFieldGroup = myNomineesCountFG;

        try {
            OrganizationGroupExt organizationGroupExt = getFieldValue("organizationGroupId");

            Long count = recognitionRestService.myNomineesCount(
                    getFieldValue("year"),
                    organizationGroupExt != null ? organizationGroupExt.getId().toString() : null);

            showJsonResult(count.toString());
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void loadAllNominee() {
        this.valueFieldGroup = loadAllNomineeFG;

        try {
            OrganizationGroupExt organizationGroupExt = getFieldValue("organizationGroupId");

            String json = recognitionRestService.loadAllNominee(
                    getFieldValue("offset"),
                    getFieldValue("maxResults"),
                    getFieldValue("language"),
                    getFieldValue("year"),
                    organizationGroupExt != null ? organizationGroupExt.getId().toString() : null);

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void allNomineesCount() {
        this.valueFieldGroup = allNomineesCountFG;

        try {
            OrganizationGroupExt organizationGroupExt = getFieldValue("organizationGroupId");

            Long count = recognitionRestService.allNomineesCount(
                    getFieldValue("year"),
                    organizationGroupExt != null ? organizationGroupExt.getId().toString() : null);

            showJsonResult(count.toString());
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void loadTopNominee() {
        this.valueFieldGroup = loadTopNomineeFG;

        try {
            OrganizationGroupExt organizationGroupExt = getFieldValue("organizationGroupId");

            String json = recognitionRestService.loadTopNominee(
                    getFieldValue("year"),
                    organizationGroupExt != null ? organizationGroupExt.getId().toString() : null);

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void loadProfile() {
        this.valueFieldGroup = loadProfileFG;

        try {
            String json = recognitionRestService.loadProfile(
                    getFieldValue("employeeNumber"),
                    getFieldValue("language"));

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void loadTeamProfiles() {
        this.valueFieldGroup = loadTeamProfilesFG;

        try {
            String json = recognitionRestService.loadTeamProfiles(
                    getFieldValue("offset"),
                    getFieldValue("maxResults"),
                    getFieldValue("language"));

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void teamProfilesCount() {
        try {
            Long count = recognitionRestService.teamProfilesCount();
            showJsonResult(count.toString());
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void recognitionComments() {
        this.valueFieldGroup = recognitionCommentsFG;

        try {
            Recognition recognition = getFieldValue("recognitionId");
            if (recognition == null) {
                throw new NullPointerException("Recognition is null or empty!");
            }

            String json = recognitionRestService.recognitionComments(
                    recognition.getId().toString(),
                    getFieldValue("offset"),
                    getFieldValue("maxResults"),
                    getFieldValue("language"),
                    getFieldValue("automaticTranslate"));

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void recognitionCommentsCount() {
        this.valueFieldGroup = recognitionCommentsCountFG;

        try {
            Recognition recognition = getFieldValue("recognitionId");
            if (recognition == null) {
                throw new NullPointerException("Recognition is null or empty!");
            }

            Long json = recognitionRestService.recognitionCommentsCount(recognition.getId().toString());

            showJsonResult(json.toString());
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void recognitionsCount() {
        this.valueFieldGroup = recognitionsCountFG;

        try {
            OrganizationGroupExt organizationGroupExt = getFieldValue("organizationGroupId");

            Long json = recognitionRestService.recognitionsCount(
                    getFieldValue("wallType"),
                    getFieldValue("profileEmployeeNumber"),
                    organizationGroupExt != null ? organizationGroupExt.getId().toString() : null,
                    getFieldValue("filter"),
                    getFieldValue("language")
            );

            showJsonResult(json.toString());
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void loadRecognitions() {
        this.valueFieldGroup = loadRecognitionsFG;

        try {
            OrganizationGroupExt organizationGroupExt = getFieldValue("organizationGroupId");

            String json = recognitionRestService.loadRecognitions(
                    getFieldValue("offset"),
                    getFieldValue("maxResults"),
                    getFieldValue("wallType"),
                    getFieldValue("profileEmployeeNumber"),
                    null,
                    organizationGroupExt != null ? organizationGroupExt.getId().toString() : null,
                    getFieldValue("filter"),
                    getFieldValue("language"),
                    getFieldValue("automaticTranslate")
            );

            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void updatePersonPreference() {
        this.valueFieldGroup = updatePersonPreferenceFg;

        try {
            DicPersonPreferenceType preferenceType = getFieldValue("preferenceTypeId");
            if (preferenceType == null) {
                throw new NullPointerException("PreferenceType is null or empty!");
            }
            recognitionRestService.updatePersonPreference(preferenceType.getId().toString(), getFieldValue("description"));
            showNotification("Person preference success updated!");
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void getPersonPreferenceTypes() {
        this.valueFieldGroup = getPersonPreferenceTypesFg;

        try {
            String language = getFieldValue("language");
            String json = recognitionRestService.getPersonPreferenceTypes(language);
            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void loadPersonPreferences() {
        this.valueFieldGroup = loadPersonPreferencesFg;

        String employeeNumber = getFieldValue("employeeNumber");
        try {
            String json = recognitionRestService.loadPersonPreferences(employeeNumber);
            showJsonResult(json);
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
    }

    public void createRecognition() {
        this.valueFieldGroup = createRecognitionFG;

        RecognitionCreatePojo recognitionCreatePojo = metadata.create(RecognitionCreatePojo.class);
        recognitionCreatePojo.setAuthorEmployeeNumber(getFieldValue("authorEmployeeNumber"));//анара 8517
        recognitionCreatePojo.setReceiverEmployeeNumber(getFieldValue("receiverEmployeeNumber"));//питер 1735

        DicRecognitionType recognitionTypeValue = getFieldValue("recognitionType");
        recognitionCreatePojo.setRecognitionTypeId(recognitionTypeValue != null ? recognitionTypeValue.getId() : null);//779ea2b6-827c-ce25-cc49-17e36e5ba119,b55d2a8a-1529-8d5a-6a1d-f78b51d3e15e
        recognitionCreatePojo.setComment(getFieldValue("comment"));
        recognitionCreatePojo.setNotifyManager(getFieldValue("notifyManager"));

        List<QualityPojo> qualityPojoList = new LinkedList<>();
        recognitionCreatePojo.setQualities(qualityPojoList);

        DicQuality quality1Value = getValue("quality1");
        if (quality1Value != null) {
            QualityPojo qualityPojo1 = metadata.create(QualityPojo.class);
            qualityPojo1.setId(quality1Value.getId());
            qualityPojoList.add(qualityPojo1);
        }

        DicQuality quality2Value = getValue("quality2");
        if (quality2Value != null) {
            QualityPojo qualityPojo2 = metadata.create(QualityPojo.class);
            qualityPojo2.setId(quality2Value.getId());
            qualityPojoList.add(qualityPojo2);
        }

        try {
            recognitionRestService.createRecognition(recognitionCreatePojo, "ru");

            showNotification("Recognition success created! Please remove created recognition");
        } catch (Exception ex) {
            showNotification("Error", ex.getMessage(), NotificationType.TRAY);
        }
    }

    private void showJsonResult(String json) {
        json = prettyJson(json);

        showMessageDialog("Rest result",
                json,
                MessageType.WARNING);
    }

    private <T> T getValue(String id) {
        return (T) ((HasValue) valueFieldGroup.getComponentNN(id)).getValue();
    }

    private <T> T getFieldValue(String id) {
        return (T) ((HasValue) valueFieldGroup.getFieldNN(id).getComponentNN()).getValue();
    }

    private String prettyJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);
        return gson.toJson(je);
    }
}