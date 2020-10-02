package kz.uco.tsadv.web.modules.recruitment.jobrequest;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.validators.EmailValidator;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.entity.dictionary.DicCountry;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAddressType;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.dictionary.DicPhoneType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Address;
import kz.uco.tsadv.modules.personal.model.PersonContact;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.dictionary.DicInterviewReason;
import kz.uco.tsadv.modules.recruitment.dictionary.DicSource;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.InterviewAnswer;
import kz.uco.tsadv.modules.recruitment.model.InterviewDetail;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.validators.PhoneValidator;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

public class JobRequestFastEdit extends AbstractEditor<JobRequest> {
    @Inject
    protected Datasource<PersonGroupExt> candidatePersonGroupDs;

    @Inject
    protected Datasource<JobRequest> jobRequestDs;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected PickerField<Entity> pickerFieldCandidate;

    @Inject
    protected FieldGroup candidatePersonGroupFieldGroup;

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Datasource<PersonExt> personDs;
    @Inject
    protected CollectionDatasource<DicLocation, UUID> dicLocationsDs;
    @Inject
    protected CollectionDatasource<DicSource, UUID> sourcesDs;
    @Inject
    protected CommonService commonService;
    @Inject
    protected RecruitmentConfig recruitmentConfig;
    @Named("candidatePersonGroupFieldGroup.sourceField")
    protected LookupField sourceField;
    @Inject
    protected VBoxLayout vbox;
    @Inject
    protected CollectionDatasource<Interview, UUID> interviewsDs;

    @Inject
    protected CollectionDatasource<InterviewDetail, UUID> interviewDetailsDs;
    @Named("candidatePersonGroupFieldGroup.sex")
    protected LookupPickerField sexField;
    @Inject
    protected CollectionDatasource<DicSex, UUID> sexesDs;
    @Inject
    private Datasource<PersonContact> personContactEmailDs;
    @Inject
    private Datasource<PersonContact> personContactPhoneDs;
    @Inject
    private Datasource<Address> addressDs;
    @Named("candidatePersonGroupFieldGroup.email")
    protected TextField emailField;
    protected FieldGroup.FieldConfig fieldConfig;
    protected EmailValidator emailValidator = new EmailValidator();
    protected PhoneValidator phoneValidator = new PhoneValidator();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("excludedPersons")) {
            pickerFieldCandidate.getLookupAction().setLookupScreen("base$PersonGroup.candidate");
            Map<String, Object> map = new HashMap<>();
            map.put("excludedPersons", params.get("excludedPersons"));
            pickerFieldCandidate.getLookupAction().setLookupScreenParams(map);
        }
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && !close) {
            if (showSaveNotification) {
                frame.showNotification(
                        messages.getMessage(messages.getMainMessagePack(), "jobRequestSelf.fast.create.candidate"),
                        NotificationType.TRAY);
            }
        }
        return true;
    }

    @Override
    protected void postInit() {
        super.postInit();

        pickerFieldCandidate.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                candidatePersonGroupFieldGroup.setEditable(false);
                for (FieldGroup.FieldConfig fc : candidatePersonGroupFieldGroup.getFields()) {
                    fc.setEditable(false);
                }
            } else {
                PersonGroupExt personGroup = metadata.create(PersonGroupExt.class);
                PersonExt person = metadata.create(PersonExt.class);
                person.setGroup(personGroup);
                List<PersonExt> list = new ArrayList<>();
                list.add(person);
                personGroup.setList(list);
                person.setType(commonService.getEntity(DicPersonType.class, "CANDIDATE"));
                person.setStartDate(new Date());
                try {
                    person.setEndDate(new SimpleDateFormat("dd.MM.yyyy").parse("31.12.9999"));
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                getItem().setCandidatePersonGroup(personGroup);
                candidatePersonGroupDs.setItem(personGroup);
                personDs.setItem(person);

                candidatePersonGroupFieldGroup.setEditable(true);
                for (FieldGroup.FieldConfig fc : candidatePersonGroupFieldGroup.getFields()) {
                    fc.setEditable(true);
                }
            }
        });
        DicSource source = getSource(recruitmentConfig.getDefaultSourceForFastCreateCode());
        if (source != null) {
            sourceField.setValue(source);
        }

        Address addressNew = metadata.create(Address.class);
        addressNew.setPersonGroup(candidatePersonGroupDs.getItem());
        addressNew.setAddressType(commonService.getEntity(DicAddressType.class, "RESIDENCE"));
        addressNew.setCountry(commonService.getEntity(DicCountry.class, "398"));
        addressNew.setStartDate(CommonUtils.getSystemDate());
        addressNew.setEndDate(CommonUtils.getEndOfTime());
        addressDs.setItem(addressNew);

        PersonContact personContactPhone = metadata.create(PersonContact.class);
        personContactPhone.setStartDate(CommonUtils.getSystemDate());
        personContactPhone.setEndDate(CommonUtils.getEndOfTime());
        personContactPhone.setPersonGroup(candidatePersonGroupDs.getItem());
        personContactPhone.setType(commonService.getEntity(DicPhoneType.class, "mobile"));
        personContactPhoneDs.setItem(personContactPhone);

        PersonContact personContactEmail = metadata.create(PersonContact.class);
        personContactEmail.setStartDate(CommonUtils.getSystemDate());
        personContactEmail.setEndDate(CommonUtils.getEndOfTime());
        personContactEmail.setPersonGroup(candidatePersonGroupDs.getItem());
        personContactEmail.setType(commonService.getEntity(DicPhoneType.class, "email"));
        personContactEmailDs.setItem(personContactEmail);
    }

    protected DicSource getSource(String defaultSourceForFastCreateCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", defaultSourceForFastCreateCode);

        return commonService.getEntity(DicSource.class, "select e from tsadv$DicSource e\n" +
                " where e.code = :code", map, null);
    }

    protected DicLocation getLocation(String defaultAddressCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", defaultAddressCode);
        return commonService.getEntity(DicLocation.class, "select e from base$DicLocation e\n" +
                " where e.code = :code", map, null);
    }

    protected class InterviewModel {
        public InterviewModel() {
        }

        InterviewModel(Interview interview,
                       DateField dateField,
                       TimeField timeFromField,
                       TimeField timeToField,
                       LookupField addressField,
                       OptionsGroup optionsGroup,
                       LookupField lookupField,
                       TextArea textArea) {
            this.interview = interview;
            this.dateField = dateField;
            this.timeFromField = timeFromField;
            this.timeToField = timeToField;
            this.addressField = addressField;
            this.optionsGroup = optionsGroup;
            this.lookupField = lookupField;
            this.textArea = textArea;
        }

        Interview interview;
        DateField<Date> dateField;
        TimeField<Time> timeFromField;
        TimeField<Time> timeToField;
        LookupField<Entity> addressField;
        OptionsGroup optionsGroup;
        LookupField lookupField;
        TextArea textArea;

        public Interview getLatestInterview() {
            interview.setInterviewDate(dateField.getValue());
            interview.setTimeFrom(timeFromField.getValue());
            interview.setTimeTo(timeToField.getValue());
            interview.setPlace((DicLocation) addressField.getValue());
            interview.setInterviewStatus((InterviewStatus) optionsGroup.getValue());
            interview.setInterviewReason((DicInterviewReason) lookupField.getValue());
            interview.setComment((String) textArea.getValue());
            return interview;
        }
    }

    List<InterviewModel> interviewModelList = new ArrayList<>();

    public void ready() {
        super.ready();
        DicSex dicSex=sexesDs.getItems().stream().filter(tempDicSex ->
                "NONE".equals(tempDicSex.getCode())).findFirst().orElse(null);
        if (dicSex!=null){
            sexField.setValue(dicSex);
        }else {
            sexField.setValue(null);
        }

//        jobRequestDs.getItem().setRequestStatus(JobRequestStatus.HIRED);
//test96
//      if (!interview.getId().equals(jobRequestDs.getItem().getInterview().getId())) {
        jobRequestDs.getItem().getInterviews().forEach(interview -> {

            GroupBoxLayout groupBoxLayout = createComponent(GroupBoxLayout.class);
            groupBoxLayout.setCaption(interview.getRequisitionHiringStep().getHiringStep().getStepName());
            groupBoxLayout.addStyleName("without-border");
            groupBoxLayout.setSpacing(true);

            GridLayout firstGridLayout = createComponent(GridLayout.class);
//            firstGridLayout.setWidth("500");
            firstGridLayout.setRows(1);
            firstGridLayout.setColumns(6);
            firstGridLayout.setSpacing(true);

            GridLayout gridLayout = createComponent(GridLayout.class);
            gridLayout.setRows(5);
            gridLayout.setColumns(6);
            gridLayout.setSpacing(true);

            DateField dateField = createComponent(DateField.class);
            dateField.setDateFormat("dd.MM.yyyy");
            dateField.setWidth("210px");
            dateField.setRequired(true);
            dateField.setRequiredMessage(
                    String.format(messages.getMessage("kz.uco.tsadv", "JobRequestFastEdit.requiredField"),
                            messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.interviewDate"),
                            interview.getRequisitionHiringStep().getHiringStep().getStepName()));
            dateField.setValue(interview.getInterviewDate());
            Label label = createComponent(Label.class);
            label.setValue(messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.interviewDate"));
//            label.setWidth("156");
            firstGridLayout.add(wrapLabel(label), 0, 0);
            firstGridLayout.add(dateField, 1, 0);

            TimeField<Date> timeFromField = createComponent(TimeField.class);
            timeFromField.setRequiredMessage(
                    String.format(messages.getMessage("kz.uco.tsadv", "JobRequestFastEdit.requiredField"),
                            messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.timeFrom"),
                            interview.getRequisitionHiringStep().getHiringStep().getStepName()));
            timeFromField.setValue(interview.getTimeFrom());
            Label labelForDateFrom = createComponent(Label.class);
            labelForDateFrom.setValue(messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.timeFrom"));
            firstGridLayout.add(labelForDateFrom, 2, 0);
            firstGridLayout.add(timeFromField, 3, 0);


            TimeField timeToField = createComponent(TimeField.class);
            timeToField.setRequiredMessage(
                    String.format(messages.getMessage("kz.uco.tsadv", "JobRequestFastEdit.requiredField"),
                            messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.timeTo"),
                            interview.getRequisitionHiringStep().getHiringStep().getStepName()));
            timeToField.setValue(interview.getTimeFrom());
            Label labelForDateTo = createComponent(Label.class);
            labelForDateTo.setValue(getMessage("timeTo"));
            firstGridLayout.add(labelForDateTo, 4, 0);
            firstGridLayout.add(timeToField, 5, 0);

            timeFromField.addValueChangeListener(e -> {
                if (timeToField.getValue() == null) {
                    timeToField.setValue(DateUtils.addHours((Date) e.getValue(), 1));
                }
            });


            LookupField addressField = createComponent(LookupField.class);
            addressField.setRequired(true);
            addressField.setWidth("440px");
            addressField.setOptionsDatasource(dicLocationsDs);
            DicLocation dicLocation = getLocation(recruitmentConfig.getDefaultAddress());
            if (dicLocation != null) {
                interview.setPlace(dicLocation);
            }
            addressField.setRequiredMessage(
                    String.format(messages.getMessage("kz.uco.tsadv", "JobRequestFastEdit.requiredField"),
                            messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "start.course.address"),
                            interview.getRequisitionHiringStep().getHiringStep().getStepName()));
            addressField.setValue(interview.getPlace());
            Label labelForAddress = createComponent(Label.class);
            labelForAddress.setValue(messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "start.course.address"));
            gridLayout.add(wrapLabel(labelForAddress), 0, 1);
            gridLayout.add(addressField, 1, 1);

            OptionsGroup<String, InterviewStatus> optionsGroup = createComponent(OptionsGroup.class);
            Map<String, InterviewStatus> map = new LinkedHashMap<>();
            map.put(getMessage("InterviewStatus.COMPLETED"), InterviewStatus.COMPLETED);
            map.put(getMessage("InterviewStatus.FAILED"), InterviewStatus.FAILED);
            map.put(getMessage("InterviewStatus.PLANNED"), InterviewStatus.PLANNED);
            optionsGroup.setOptionsMap(map);
            optionsGroup.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
            optionsGroup.setWidth("440px");
            optionsGroup.setRequired(true);
            optionsGroup.setRequiredMessage(
                    String.format(messages.getMessage("kz.uco.tsadv", "JobRequestFastEdit.requiredField"),
                            messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.interviewStatus"),
                            interview.getRequisitionHiringStep().getHiringStep().getStepName()));
            optionsGroup.setValue(InterviewStatus.CANCELLED.toString());
            Label statusCaption = createComponent(Label.class);
            statusCaption.setValue(messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.interviewStatus"));
            gridLayout.add(wrapLabel(statusCaption), 0, 2);
            gridLayout.add(optionsGroup, 1, 2);

            LookupField<Object> reasonLookupField = createComponent(LookupField.class);
            reasonLookupField.setWidth("440px");
            List<DicInterviewReason> list = commonService.getEntities(DicInterviewReason.class,
                    "select e from tsadv$DicInterviewReason e", null, "_local");
            list.sort((o1, o2) -> {
                if (o1 != null && o2 != null && "OTHER".equals(o2.getCode())) return -1;
                else return 0;
            });
            reasonLookupField.setOptionsList(Collections.singletonList(list));
            reasonLookupField.setNullOptionVisible(false);
            reasonLookupField.setEditable(false);
            Label reasonTypeCaption = createComponent(Label.class);
            reasonTypeCaption.setValue(getMessage("Interview.interviewReason4"));
            gridLayout.add(wrapLabel(reasonTypeCaption), 0, 3);
            gridLayout.add(reasonLookupField, 1, 3);

            TextArea textArea = createComponent(TextArea.class);
            textArea.setWidth("440px");
//              textArea.setEditable(false);
            Label reasonCaption = createComponent(Label.class);
            reasonCaption.setValue(messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.comment"));
            gridLayout.add(wrapLabel(reasonCaption), 0, 4);
            gridLayout.add(textArea, 1, 4);

            optionsGroup.addValueChangeListener(e -> {
                if (InterviewStatus.PLANNED.equals(e.getValue())) {
                    timeFromField.setRequired(true);
                    timeToField.setRequired(true);
                } else {
                    timeFromField.setRequired(false);
                    timeToField.setRequired(false);
                }
                if (InterviewStatus.FAILED.equals(e.getValue())) {
                    reasonLookupField.setEditable(true);
                    reasonLookupField.setRequired(true);
                    reasonLookupField.setRequiredMessage(
                            String.format(getMessage("JobRequestFastEdit.requiredField"),
                                    messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.interviewReason5"),
                                    interview.getRequisitionHiringStep().getHiringStep().getStepName()));
//                      textArea.setEditable(true);
                } else {
                    reasonLookupField.setValue(null);
                    reasonLookupField.setEditable(false);
                    textArea.setValue(null);
//                      textArea.setEditable(false);
                }
            });

            reasonLookupField.addValueChangeListener(e -> {
                if (commonService.getEntity(DicInterviewReason.class, "OTHER").equals(e.getValue())) {
                    textArea.setRequired(true);
                    textArea.setRequiredMessage(
                            String.format(messages.getMessage("kz.uco.tsadv", "JobRequestFastEdit.requiredField"),
                                    messages.getMessage("kz.uco.tsadv.modules.recruitment.model", "Interview.comment"),
                                    interview.getRequisitionHiringStep().getHiringStep().getStepName()));
                } else {
                    textArea.setRequired(false);
                }
            });

            groupBoxLayout.add(firstGridLayout);
            groupBoxLayout.add(gridLayout);
            vbox.add(groupBoxLayout);
            interviewModelList.add(new InterviewModel(interview,
                    dateField,
                    timeFromField,
                    timeToField,
                    addressField,
                    optionsGroup,
                    reasonLookupField,
                    textArea));
        });

        initMaskedField();
        emailField.addValidator(emailValidator);
    }


    protected void initMaskedField() {
        MaskedField maskedField = componentsFactory.createComponent(MaskedField.class);
        maskedField.setRequired(true);
        maskedField.setValueMode(MaskedField.ValueMode.CLEAR);
        maskedField.setMask("#(###)###-##-##");
        maskedField.setDatasource(personContactPhoneDs, "contactValue");
        maskedField.addValidator(phoneValidator);
        fieldConfig = candidatePersonGroupFieldGroup.getFieldNN("phone");
        fieldConfig.setComponent(maskedField);
    }


    protected Label wrapLabel(Label label) {
        label.setWidth("150px");
        return label;
    }

    protected <T extends Component> T createComponent(Class<T> componentClass) {
        return componentsFactory.createComponent(componentClass);
    }

    @Override
    public void commitAndClose() {
        if (!validateAll()) {
            return;
        }
        if (personContactEmailDs.getItem().getContactValue() == null || personContactEmailDs.getItem().getContactValue().equals("")) {
            ((AbstractDatasource) personContactEmailDs).setModified(false);
        }
        if (candidatePersonGroupFieldGroup.isEditable()) {
            PersonExt person = jobRequestDs.getItem().getCandidatePersonGroup().getPerson();
            try {
                hasCandidate(person);

                commitChanges();
            } catch (UniqueFioDateException ex) {
                showOptionDialog(getMessage("msg.warning.title"),
                        getMessage(ex.getMessage()),
                        MessageType.CONFIRMATION,
                        new DialogAction[]{
                                new DialogAction(DialogAction.Type.YES) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        commitChanges();
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO)
                        });
            } catch (Exception ex) {
                showNotification(getMessage("msg.warning.title"), ex.getMessage(), NotificationType.TRAY);
            }
        } else {
            commitChanges();
        }
    }

    protected void commitChanges() {
        List<Interview> list = new ArrayList<>();
        for (InterviewModel interviewModel : interviewModelList) {
            list.add(interviewModel.getLatestInterview());
        }
        if (list.isEmpty()) {
            showNotification(getMessage("ThereAreNoSelectionSteps"), NotificationType.TRAY);
            return;
        }
        jobRequestDs.getItem().setInterviews(list);
        showOptionDialog(getMessage("JobRequestFastEdit.confirm.title"),
                String.format(getMessage("JobRequestFastEdit.confirm.text"), messages.getMessage(getImportantStatus())),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                if (checkInterviewsStatus()) {
                                    jobRequestDs.getItem().setRequestStatus(JobRequestStatus.SELECTED);
                                } else {
                                    InterviewStatus interviewStatus = getImportantStatus();
                                    switch (interviewStatus) {
                                        case COMPLETED:
                                            jobRequestDs.getItem().setRequestStatus(JobRequestStatus.HIRED);
                                            break;
                                        case FAILED:
                                            jobRequestDs.getItem().setRequestStatus(JobRequestStatus.REJECTED);
                                            break;
                                        case PLANNED:
                                            jobRequestDs.getItem().setRequestStatus(JobRequestStatus.ON_APPROVAL);
                                            break;
                                    }
                                }
                                try {
                                    commit();
                                    close("close");
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    if (ex.getMessage().contains("idx_tsadv_job_request_unq")) {
                                        showNotification(getMessage("JobRequestCandidateExistError"), NotificationType.TRAY);
                                    }
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });
    }


    protected void hasCandidate(PersonExt person) throws Exception {
        LoadContext<PersonExt> loadContext = LoadContext.create(PersonExt.class);

        StringBuilder query = new StringBuilder("select e from base$PersonExt e where (:sysDate between e.startDate and e.endDate)");

        LoadContext.Query loadContextQuery = LoadContext.createQuery("");

        String iin = person.getNationalIdentifier();

        boolean checkIin = false;
        if (iin != null && iin.trim().length() > 0 && !iin.equals("000000000000")) {
            checkIin = true;
            query.append(" and e.nationalIdentifier = :iin");
            loadContextQuery.setParameter("iin", iin);
        } else {
            query.append(" and e.firstName = :p1 and e.lastName = :p2 and e.dateOfBirth = :p3");
            loadContextQuery.setParameter("p1", person.getFirstName());
            loadContextQuery.setParameter("p2", person.getLastName());
            loadContextQuery.setParameter("p3", person.getDateOfBirth());
        }

        loadContextQuery.setParameter("sysDate", new Date());
        loadContextQuery.setQueryString(query.toString());
        loadContext.setQuery(loadContextQuery);

        if (dataManager.getCount(loadContext) > 0) {
            //if (checkIin) throw new UniqueIinException(getMessage("candidate.exist.iin"));

            throw new UniqueFioDateException(getMessage("candidate.exist.fid"));
        }
    }

    protected InterviewStatus getImportantStatus() {
        InterviewStatus interviewStatus = InterviewStatus.COMPLETED;
        for (InterviewModel interviewModel : interviewModelList) {
            if (interviewModel.getLatestInterview().getInterviewStatus().equals(InterviewStatus.PLANNED)) {
                interviewStatus = InterviewStatus.PLANNED;
            }
        }

        for (InterviewModel interviewModel : interviewModelList) {
            if (interviewModel.getLatestInterview().getInterviewStatus().equals(InterviewStatus.FAILED)) {
                interviewStatus = InterviewStatus.FAILED;
            }
        }
        return interviewStatus;
    }

    protected boolean checkInterviewsStatus() {
        int i = 0;
        for (InterviewModel model : interviewModelList) {
            if (model.getLatestInterview().getInterviewStatus().equals(InterviewStatus.COMPLETED)) {
                i++;
            }
        }
        if (interviewModelList.size() == i) {
            return true;
        }
        return false;
    }
}