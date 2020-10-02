package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PersonContact;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.modules.recruitment.model.RequisitionSearchCandidate;
import kz.uco.tsadv.modules.recruitment.model.RequisitionSearchCandidateResult;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.web.modules.personal.person.PersonCandidate;

import javax.inject.Inject;
import java.util.*;

public class RequisitionSearchCandidateBrowse extends AbstractWindow {

    @WindowParam
    protected Requisition requisition;

    @WindowParam
    protected CollectionDatasource<JobRequest, UUID> jobRequestsDs;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private Datasource<RequisitionSearchCandidate> searchCandidateDs;

    @Inject
    private RequisitionSearchCandidateDatasource candidatePercentagesDs;

    @Inject
    private Button addToCandidate;

    @Inject
    private Table<RequisitionSearchCandidateResult> searchResultTable;

    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private Configuration configuration;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initNewModel();

        addToCandidate.setEnabled(searchResultTable.getSelected().size() != 0);

        candidatePercentagesDs.addItemChangeListener(new Datasource.ItemChangeListener<RequisitionSearchCandidateResult>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<RequisitionSearchCandidateResult> e) {
                addToCandidate.setEnabled(searchResultTable.getSelected().size() != 0);
            }
        });
    }

    public void redirectToCard(RequisitionSearchCandidateResult result, String name) {
        UUID personId = result.getPerson().getId();
        if (personId != null) {
            PersonExt person = getPerson(personId);
            if (person != null) {
                Map<String, Object> params = new HashMap<>();
                DicPersonType dicPersonType = person.getType();
                if (dicPersonType != null) {
                    String code = dicPersonType.getCode();
                    if (code != null) {
                        params.put(StaticVariable.PERSON_TYPE_CODE, code);
                    }
                }

                PersonCandidate personCandidate = (PersonCandidate) openEditor("base$Person.candidate", person, WindowManager.OpenType.THIS_TAB, params);
                personCandidate.addCloseListener(actionId -> candidatePercentagesDs.refresh());
            } else {
                showNotification("Person is NULL!");
            }
        }
    }

    public void redirectToResponds(RequisitionSearchCandidateResult result, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put(StaticVariable.PERSON_TYPE_CODE, result.getPerson().getType().getCode());
        params.put(StaticVariable.REDIRECT_TAB, "jobRequests");

        PersonExt person = dataManager.reload(result.getPerson(), "person.candidate");
        PersonCandidate candidateEdit = (PersonCandidate) openEditor("base$Person.candidate", person, WindowManager.OpenType.THIS_TAB, params);
        candidateEdit.addCloseListener(actionId -> candidatePercentagesDs.refresh());
    }

    private PersonExt getPerson(UUID personId) {
        LoadContext<PersonExt> loadContext = LoadContext.create(PersonExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$PersonExt e " +
                        "where e.id = :pId")
                .setParameter("pId", personId))
                .setView("person.candidate");
        return dataManager.load(loadContext);
    }

    public Component generatePersonImage(RequisitionSearchCandidateResult searchCandidateResult) {
        String webAppUrl = AppContext.getProperty("cuba.webAppUrl");
        Label label = componentsFactory.createComponent(Label.class);
        label.setHtmlEnabled(true);
        /*label.setValue(String.format("<img src=\"/tal/image_api?userId=%s\" class=\"search-candidate-photo\"/>", searchCandidateResult.getPerson().getId()));*/
        label.setValue(String.format("<img src=\"" + webAppUrl + "/dispatch/person_image/%s\" class=\"search-candidate-photo\"/>", searchCandidateResult.getPerson().getId()));
        return label;
    }

    public Component generateContacts(RequisitionSearchCandidateResult searchCandidateResult) {
        PersonGroupExt personGroup = searchCandidateResult.getPerson().getGroup();
        VBoxLayout contacts = componentsFactory.createComponent(VBoxLayout.class);

        for (PersonContact personContact : personGroup.getPersonContacts()) {
            HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
            hBoxLayout.setSpacing(true);
            hBoxLayout.add(createLabel(personContact.getType().getLangValue(), "contact-lbl-k"));
            hBoxLayout.add(createLabel(":", "contact-lbl-dot"));
            hBoxLayout.add(createLabel(personContact.getContactValue(), "contact-lbl-v"));
            contacts.add(hBoxLayout);
        }
        return contacts;
    }

    public Component generateFiles(RequisitionSearchCandidateResult searchCandidateResult) {
        CssLayout cssLayout = componentsFactory.createComponent(CssLayout.class);
        Button button = componentsFactory.createComponent(Button.class);

        button.setCaption("");
        button.setAction(new BaseAction("link")
                .withIcon("font-icon:PAPERCLIP")
                .withHandler(actionPerformedEvent -> {
                    Map<String, Object> params = new HashMap<>();
                    params.put("personGroupId", searchCandidateResult.getPerson().getGroup().getId());
                    openWindow("tsadv$PersonAttachment.browse", WindowManager.OpenType.DIALOG, params);
                }));
        cssLayout.add(button);
        return cssLayout;
    }

    private Label createLabel(String value, String cssClass) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        label.setStyleName(cssClass);
        return label;
    }

    public void search() {
        if (validateAll()) {
            candidatePercentagesDs.refresh(ParamsMap.of(
                    StaticVariable.REQUISITION_ID, requisition.getId(),
                    StaticVariable.REQUISITION_SEARCH_CANDIDATE, searchCandidateDs.getItem()));
        }
    }

    public void addToCandidate() {
        RecruitmentConfig recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);
        boolean isDraft = recruitmentConfig.getJobReuestDefaultDraft();
        Set<RequisitionSearchCandidateResult> selected = searchResultTable.getSelected();
        if (!selected.isEmpty()) {
            for (RequisitionSearchCandidateResult result : selected) {
                JobRequest jobRequest = metadata.create(JobRequest.class);
                jobRequest.setCandidatePersonGroup(dataManager.reload(result.getPerson().getGroup(), "personGroup.search.candidate"));
                jobRequest.setRequestStatus(result.getIsReserved() ? JobRequestStatus.FROM_RESERVE :
                        (isDraft ? JobRequestStatus.DRAFT : JobRequestStatus.ON_APPROVAL));
                jobRequest.setRequestDate(new Date());
                jobRequest.setRequisition(requisition);
                jobRequestsDs.addItem(jobRequest);
                dataManager.commit(jobRequest);
                candidatePercentagesDs.excludeItem(result);
            }
            jobRequestsDs.refresh();
            showNotification(
                    getMessage("msg.success.title"),
                    getMessage("requisition.search.candidate.add.msg"),
                    NotificationType.TRAY);
        }
    }

    private void initNewModel() {
        RequisitionSearchCandidate model = metadata.create(RequisitionSearchCandidate.class);
        model.setReserve(Boolean.TRUE);
        model.setEmployee(Boolean.TRUE);
        //model.setExperience(Boolean.TRUE);
        model.setExternalCandidate(Boolean.TRUE);
        //model.setLevelEducation(Boolean.TRUE);
        model.setReadRelocation(Boolean.TRUE);
        model.setStudent(Boolean.TRUE);
        model.setReservedCandidate(Boolean.TRUE);
        searchCandidateDs.setItem(model);
    }
}