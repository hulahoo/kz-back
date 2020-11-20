package kz.uco.tsadv.web.modules.recruitment.jobrequest;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.dictionary.DicInterviewReason;
import kz.uco.tsadv.modules.recruitment.dictionary.DicJobRequestReason;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.modules.recruitment.model.RequisitionHiringStep;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.*;

import static java.lang.Math.toIntExact;

/**
 * @author Adilbekov Yernar
 */
public class JobRequestFastInterviewEdit extends AbstractEditor<JobRequest> {

    @Inject
    private Metadata metadata;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private CommonService commonService;
    @Inject
    private CollectionDatasource<Interview, UUID> interviewDs;
    @Inject
    private VBoxLayout interviewBlocks;

    private Datasource<Requisition> requisitionDs;

    private List<InterviewPojo> interviewPojoList = new ArrayList<>();

    private List<Interview> interviewRemove = new ArrayList<>();

    @Inject
    private RecruitmentConfig recruitmentConfig;

    @Inject
    private NotificationService notificationService;

    @Inject
    private EmployeeService employeeService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        requisitionDs = (Datasource<Requisition>) params.get("requisitionDs");
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            if (showSaveNotification) {
                frame.showNotification(
                        messages.getMessage(messages.getMainMessagePack(), "requisition.fast.create.interview"),
                        NotificationType.TRAY);
            }
        }
        return true;
    }

    @Override
    protected void postInit() {
        super.postInit();

        JobRequest jobRequest = getItem();

        Requisition requisition = requisitionDs.getItem();
        for (RequisitionHiringStep step : requisition.getHiringSteps()) {

            Interview findInterview = null;

            for (Interview interview : jobRequest.getInterviews()) {
                if (interview.getRequisitionHiringStep().equals(step) && interview.getInterviewStatus() != InterviewStatus.CANCELLED) {
                    findInterview = interview;
                    break;
                }
            }

            if (findInterview != null) {
                interviewBlocks.add(createBlock(findInterview, true));
            } else {
                Interview interview = metadata.create(Interview.class);
                interview.setJobRequest(jobRequest);
                interview.setInterviewDate(CommonUtils.getSystemDate());
                interview.setRequisitionHiringStep(step);
                DicLocation defaultPlace = commonService.getEntity(DicLocation.class, recruitmentConfig.getDefaultAddress());
                if (defaultPlace != null) {
                    interview.setPlace(defaultPlace);
                }
                interviewBlocks.add(createBlock(interview, false));
            }
        }
    }

    @Override
    public void commitAndClose() {
        if (validateAll()) {
            for (InterviewPojo interviewPojo : interviewPojoList) {
                if (interviewPojo.getOptionsGroup().getValue() != null && !interviewPojo.isDeleted()) {
                    interviewDs.addItem(interviewPojo.getLatestInterview());

                }
            }

            for (RequisitionHiringStep step : requisitionDs.getItem().getHiringSteps()) {
                if (step.getRequired().equals(false)) {
                    interviewDs.getItems().forEach(interview -> {
                        if (interview.getRequisitionHiringStep().getHiringStep().getId().equals(step.getHiringStep().getId())) {
                            if (interview.getInterviewStatus() == null) {
                                interviewRemove.add(interview);
                            }
                        }
                    });
                    interviewRemove.forEach(interviewRem -> {
                        interviewDs.removeItem(interviewRem);
                    });
                }
            }
            commit();
            updateJobRequestStatus();

            Long order = commonService.getCount(RequisitionHiringStep.class, "select e from tsadv$RequisitionHiringStep e where e.requisition.id = :reqId",
                    Collections.singletonMap("reqId", requisitionDs.getItem().getId()));

            for (InterviewPojo interviewPojo : interviewPojoList) {
                if (interviewPojo.getLatestInterview() != null && interviewPojo.getLatestInterview().getInterviewStatus() != null) {
                    if (!interviewPojo.isDeleted() && interviewPojo.getLatestInterview().getInterviewStatus().equals(InterviewStatus.FAILED)) {
                        if (interviewPojo.getLatestInterview().getRequisitionHiringStep().getOrder() == toIntExact(order)) {
                            if (interviewPojo.getInterview().getJobRequest() != null) {
                                UserExt user = employeeService.getUserExtByPersonGroupId(interviewPojo.getInterview().getJobRequest().getCandidatePersonGroup() != null ?
                                        interviewPojo.getInterview().getJobRequest().getCandidatePersonGroup().getId() : null);
                                Requisition requisition = interviewPojo.getLatestInterview().getJobRequest().getRequisition();
                                if (user != null && requisition != null && requisition.getJobGroup() != null) {
                                    notificationService.sendParametrizedNotification("candidate.selection.process.completed",
                                            user,
                                            getParams(interviewPojo.getLatestInterview().getJobRequest().getCandidatePersonGroup(), requisition));
                                }
                            }
                        }
                    }
                }
            }
            close("close");
        }
    }

    protected Map<String, Object> getParams(PersonGroupExt candidate, Requisition requisition) {
        Map<String, Object> param = new HashMap<>();
        param.put("personFullName", candidate != null ? candidate.getFullName() : "");
        param.put("job", requisition.getJobGroup().getJob());
        return param;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        List<Object> list = new ArrayList<>();
        errors.getAll().forEach(item -> {
            interviewPojoList.forEach(interviewPojo -> {
                if (interviewPojo.getOptionsGroup().getId().equals(item.component.getId())) {
                    if (!interviewPojo.getInterview().getRequisitionHiringStep().getRequired()) {
                        list.add(item);
                    }
                }
            });
        });
        list.forEach(o -> {
            errors.getAll().remove(o);
        });
    }

    private Component createBlock(Interview interview, boolean find) {
        String hiringStepName = interview.getRequisitionHiringStep().getHiringStep().getStepName();
        InterviewPojo interviewPojo = new InterviewPojo();

        VBoxLayout mainWrapper = createComponent(VBoxLayout.class);
        mainWrapper.setSpacing(true);
        mainWrapper.addStyleName("fast-interview-wrapper");

        /**
         * header
         * */
        HBoxLayout header = createComponent(HBoxLayout.class);
        header.setStyleName("fast-interview-header");
        header.setWidthFull();
        LinkButton stepLink = createComponent(LinkButton.class);
        stepLink.addStyleName("fast-interview-sl");
        stepLink.setCaption(hiringStepName);
        header.add(stepLink);

        if (!find) {
            LinkButton deleteLink = componentsFactory.createComponent(LinkButton.class);
            deleteLink.setCaption(getMessage("table.btn.empty"));
            deleteLink.setIcon("icons/item-remove.png");
            deleteLink.setAction(new BaseAction("delete") {
                @Override
                public void actionPerform(Component component) {
                    showOptionDialog(
                            getMessage("interview.step.remove.t"),
                            getMessage("interview.step.remove.b"),
                            MessageType.CONFIRMATION, new Action[]{
                                    new DialogAction(DialogAction.Type.YES) {
                                        @Override
                                        public void actionPerform(Component component) {
                                            interviewPojo.setDeleted(true);
                                            mainWrapper.setVisible(false);
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.CANCEL)
                            });
                }
            });
            deleteLink.setAlignment(Alignment.TOP_RIGHT);
            header.add(deleteLink);
        }

        mainWrapper.add(header);

        GridLayout gridLayout = createComponent(GridLayout.class);
        gridLayout.setMargin(true);
        gridLayout.setWidthAuto();
        gridLayout.setRows(6);
        gridLayout.setColumns(2);
        gridLayout.setSpacing(true);

        /**
         * date row
         * */
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        DateField dateField = createComponent(DateField.class);
        dateField.setDateFormat("dd.MM.yyyy");
        dateField.setValue(interview.getInterviewDate());

        Label label = createComponent(Label.class);
        label.setValue(getMessage("Interview.interviewDate"));

        //Время c
        TimeField<Date> timeFromField = componentsFactory.createComponent(TimeField.class);
        timeFromField.setValue(interview.getTimeFrom());

        Label timeFromLabel = createComponent(Label.class);
        timeFromLabel.setValue(getMessage( "Interview.timeFromShort"));

        //Время по
        TimeField timeToField = componentsFactory.createComponent(TimeField.class);
        timeToField.setValue(interview.getTimeTo());

        timeFromField.addValueChangeListener(e -> {
            if (timeToField.getValue() == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(timeFromField.getValue());
                calendar.add(Calendar.HOUR, 1);
                timeToField.setValue(calendar.getTime());
            }
        });

        Label timeToLabel = createComponent(Label.class);
        timeToLabel.setValue(getMessage( "Interview.timeToShort"));

        hBoxLayout.add(dateField);
        hBoxLayout.add(timeFromLabel);
        hBoxLayout.add(timeFromField);
        hBoxLayout.add(timeToLabel);
        hBoxLayout.add(timeToField);
        hBoxLayout.setSpacing(true);
        gridLayout.add(label, 0, 0);
        gridLayout.add(hBoxLayout, 1, 0);

        //Адрес
        LookupField addressLookupField = componentsFactory.createComponent(LookupField.class);
        addressLookupField.setWidth("450px");
        List<DicInterviewReason> addressList = commonService.getEntities(DicInterviewReason.class,
                "select e from base$DicLocation e", null, "_local");
        addressLookupField.setOptionsList(addressList);
        addressLookupField.setNullOptionVisible(false);
        addressLookupField.setValue(interview.getPlace());

        Label addressLabel = createComponent(Label.class);
        addressLabel.setValue(getMessage( "Interview.place"));
        gridLayout.add(addressLabel, 0, 1);
        gridLayout.add(addressLookupField, 1, 1);


        /**
         * interview status row
         * */
        OptionsGroup<String, InterviewStatus> optionsGroup = createComponent(OptionsGroup.class);
        optionsGroup.setRequired(true);
        optionsGroup.setRequiredMessage(
                String.format(getMessage("JobRequestFastEdit.requiredField"),
                        getMessage("Interview.interviewStatus"),
                        hiringStepName));
        optionsGroup.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        optionsGroup.setWidth("450px");
        optionsGroup.setId("optionGroupID" + interview.getId());

        Map<String, InterviewStatus> map = new LinkedHashMap<>();
        map.put(getMessage("InterviewStatus.COMPLETED"), InterviewStatus.COMPLETED);
        map.put(getMessage("InterviewStatus.FAILED"), InterviewStatus.FAILED);
        map.put(getMessage("InterviewStatus.PLANNED"), InterviewStatus.PLANNED);
        optionsGroup.setOptionsMap(map);

        if (find) {
            optionsGroup.setValue(String.valueOf(interview.getInterviewStatus()));
        }

        Label statusCaption = createComponent(Label.class);
        statusCaption.setValue(getMessage("Interview.interviewStatus"));
        statusCaption.setId("statusId");
        gridLayout.add(statusCaption, 0, 2);
        gridLayout.add(optionsGroup, 1, 2);

        /**
         * reason type lookup row
         * */
        LookupField<Object> reasonLookupField = createComponent(LookupField.class);
        reasonLookupField.setWidth("450px");
        List<DicInterviewReason> list = commonService.getEntities(DicInterviewReason.class,
                "select e from tsadv$DicInterviewReason e", null, "_local");
        list.sort((o1, o2) -> {
            if (o1 != null && o2 != null && "OTHER".equals(o2.getCode())) return -1;
            else return 0;
        });
        reasonLookupField.setOptionsList(Collections.singletonList(list));
        reasonLookupField.setNullOptionVisible(false);
        if (find) {
            reasonLookupField.setValue(interview.getInterviewReason());
        }

        Label reasonTypeCaption = createComponent(Label.class);
        reasonTypeCaption.setValue(getMessage("Interview.interviewReason"));
        gridLayout.add(reasonTypeCaption, 0, 3);
        gridLayout.add(reasonLookupField, 1, 3);

        /**
         * comment row
         * */
        TextArea txtComment = createComponent(TextArea.class);
        txtComment.setWidth("450px");
        if (find) {
            txtComment.setValue(interview.getComment());
        }

        Label commentCaption = createComponent(Label.class);
        commentCaption.setValue(getMessage("FastInterviewComment"));
        gridLayout.add(commentCaption, 0, 4);
        gridLayout.add(txtComment, 1, 4);

        optionsGroup.addValueChangeListener(e -> {
            if (!dateField.isRequired()) {
                dateField.setRequired(true);
                dateField.setRequiredMessage(
                        String.format(getMessage("JobRequestFastEdit.requiredField"),
                                getMessage("Interview.interviewDate"),
                                hiringStepName));
            }
            if (InterviewStatus.FAILED.equals(e.getValue())) {
                reasonLookupField.setEditable(true);
                reasonLookupField.setRequired(true);
                reasonLookupField.setRequiredMessage(
                        String.format(getMessage("JobRequestFastEdit.requiredField"),
                                getMessage("Interview.interviewReason"),
                                hiringStepName));
            } else {
                reasonLookupField.setRequired(false);
                reasonLookupField.setValue(null);
            }
        });

        reasonLookupField.addValueChangeListener(e -> {
            if (commonService.getEntity(DicInterviewReason.class, "OTHER").equals(e.getValue())) {
                txtComment.setRequired(true);
                txtComment.setRequiredMessage(
                        String.format(getMessage("JobRequestFastEdit.requiredField"),
                                getMessage("Interview.reason"),
                                hiringStepName));
            } else {
                txtComment.setRequired(false);
            }
        });

        mainWrapper.add(gridLayout);

        interviewPojo.setInterview(interview);
        interviewPojo.setDateField(dateField);
        interviewPojo.setTimeFromField(timeFromField);
        interviewPojo.setTimeToField(timeToField);
        interviewPojo.setAddressLookupField(addressLookupField);
        interviewPojo.setOptionsGroup(optionsGroup);
        interviewPojo.setLookupField(reasonLookupField);
        interviewPojo.setTextAreaComment(txtComment);
        interviewPojo.setFind(find);

        interviewPojoList.add(interviewPojo);

        return mainWrapper;
    }

    private <T extends Component> T createComponent(Class<T> componentClass) {
        return componentsFactory.createComponent(componentClass);
    }

    private void updateJobRequestStatus() {
        if (getItem().getRequestStatus() != JobRequestStatus.HIRED
                && getItem().getRequestStatus() != JobRequestStatus.MADE_OFFER
                && getItem().getRequestStatus() != JobRequestStatus.SELECTED
                && getItem().getRequestStatus() != JobRequestStatus.REJECTED) {

            Optional<Interview> failedInterviewOpt = interviewDs.getItems().stream().filter(i -> i.getInterviewStatus() == InterviewStatus.FAILED).findFirst();
            if (failedInterviewOpt.isPresent()) {
                getItem().setRequestStatus(JobRequestStatus.REJECTED);
                getItem().setJobRequestReason(commonService.getEntity(DicJobRequestReason.class, "INTERVIEW_FAILED"));
                getItem().setReason(failedInterviewOpt.get().getRequisitionHiringStep().getHiringStep().getStepName());
                commit();
            } else {
                List<Object[]> notPassedInterviewCount = commonService.emNativeQueryResultList("select count(rhs.id) - count(i.id) as cnt, '' as dummy" +
                        "      from tsadv_job_request jr " +
                        " left join tsadv_requisition_hiring_step rhs on (rhs.requisition_id = jr.requisition_id)" +
                        " left join tsadv_interview i on (i.job_request_id = jr.id and i.requisition_hiring_step_id = rhs.id and i.interview_status = 40)" +
                        "     where jr.id = ?1" +
                        "       and jr.delete_ts is null" +
                        "       and i.delete_ts is null" +
                        "       and rhs.delete_ts is null" +
                        "  group by jr.id", Collections.singletonMap(1, getItem().getId()));

                if (notPassedInterviewCount != null && !notPassedInterviewCount.isEmpty()) {
                    Object[] row = notPassedInterviewCount.get(0);
                    if (((Long) row[0]).intValue() == 0) {
                        getItem().setRequestStatus(JobRequestStatus.SELECTED);
                        commit();
                    }
                }
            }
        }
    }

}
