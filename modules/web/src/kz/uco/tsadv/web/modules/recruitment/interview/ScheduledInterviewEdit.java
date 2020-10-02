package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduledInterviewEdit extends AbstractEditor<Interview> {
    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private CommonService commonService;

    @Inject
    private CollectionDatasource<InterviewQuestionnaire, UUID> questionnairesDs;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Named("fieldGroup.requisition")
    private PickerField<Entity> requisitionField;

    @Named("fieldGroup.requisitionHiringStep")
    private PickerField<Entity> requisitionHiringStepField;
    @Inject
    private EmployeeService employeeService;

    @Override
    protected void initNewItem(Interview item) {
        super.initNewItem(item);
        item.setIsScheduled(true);
        item.setInterviewStatus(InterviewStatus.DRAFT);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Utils.customizeLookup(fieldGroup.getField("requisition").getComponent(),
                "tsadv$Requisition.lookup",
                WindowManager.OpenType.DIALOG,
                Collections.singletonMap("requisitionType", 1));

        Utils.customizeLookup(fieldGroup.getField("requisitionHiringStep").getComponent(), null, WindowManager.OpenType.DIALOG, null);
        Utils.customizeLookup(fieldGroup.getField("place").getComponent(), null, WindowManager.OpenType.DIALOG, null);


        requisitionField.addValueChangeListener((e) -> {
            Map<String, Object> lookupParams = new HashMap<>();

            if (e.getValue() != null) {
                Requisition requisition = (Requisition) e.getValue();

                if (e.getPrevValue() != null) {
                    Requisition prevRequisition = (Requisition) e.getPrevValue();
                    if (!prevRequisition.getId().equals(requisition.getId())) {
                        getItem().setRequisitionHiringStep(null);
                        getItem().setMainInterviewerPersonGroup(null);
                    }
                }

                lookupParams.put("requisitionId", requisition.getId());
            } else {
                getItem().setRequisitionHiringStep(null);
                getItem().setMainInterviewerPersonGroup(null);
            }
            Utils.customizeLookup(fieldGroup.getField("requisitionHiringStep").getComponent(), null, WindowManager.OpenType.DIALOG, lookupParams);

            for (InterviewQuestionnaire item : new ArrayList<>(questionnairesDs.getItems()))
                questionnairesDs.removeItem(item);
        });

    }

    @Override
    protected void postInit() {
        super.postInit();
        calcMainInterviewer(false);
        if (getItem().getMainInterviewerPersonGroup() == null)
            calcMainInterviewer(true);
        requisitionHiringStepField.addValueChangeListener(e -> {
            calcMainInterviewer(false);

            if (e.getValue() != null) {
                RequisitionHiringStep requisitionHiringStep = (RequisitionHiringStep) e.getValue();

                if (e.getPrevValue() != null) {
                    RequisitionHiringStep prevRequisitionHiringStep = (RequisitionHiringStep) e.getPrevValue();
                    if (!prevRequisitionHiringStep.getId().equals(requisitionHiringStep.getId())) {
                        getItem().setMainInterviewerPersonGroup(null);
                    }
                }
            } else {
                getItem().setMainInterviewerPersonGroup(null);
            }

            calcMainInterviewer(true);

            fillDefaultQuestionnaires();

        });
    }


    private void fillDefaultQuestionnaires() {
        Map<String, Object> qParams = new HashMap<>();
        qParams.put("hiringStepId", getItem().getRequisitionHiringStep().getHiringStep().getId());
        qParams.put("requisitionId", getItem().getRequisition().getId());

        List<RcQuestionnaire> rcQuestionnaires = commonService.getEntities(
                RcQuestionnaire.class,
                "select e " +
                        "    from tsadv$RcQuestionnaire e " +
                        "   where e.id in (select hsq.questionnaire.id " +
                        "                    from tsadv$HiringStepQuestionnaire hsq, tsadv$RequisitionQuestionnaire rq " +
                        "                   where hsq.hiringStep.id = :hiringStepId " +
                        "                     and rq.requisition.id = :requisitionId " +
                        "                     and rq.questionnaire.id = hsq.questionnaire.id)",
                qParams,
                "rcQuestionnaire.view");

        for (RcQuestionnaire rcQuestionnaire : rcQuestionnaires) {
            addQuestionnaire(rcQuestionnaire);
        }
    }

    private void addQuestionnaire(RcQuestionnaire rcQuestionnaire) {
        RcQuestionnaire questionnaire = dataManager.reload(rcQuestionnaire, "rcQuestionnaire.view");
        InterviewQuestionnaire interviewQuestionnaire = metadata.create(InterviewQuestionnaire.class);
        interviewQuestionnaire.setInterview(getItem());
        interviewQuestionnaire.setQuestionnaire(questionnaire);
        questionnairesDs.addItem(interviewQuestionnaire);
    }

    private void calcMainInterviewer(Boolean calc) {
        Map<String, Object> params = new HashMap<>();
        params.put("hiringStepId", getItem() != null && getItem().getRequisitionHiringStep() != null && getItem().getRequisitionHiringStep().getHiringStep() != null ? getItem().getRequisitionHiringStep().getHiringStep().getId() : null);
        params.put("systemDate", CommonUtils.getSystemDate());
        HiringStepMember hiringStepMember = commonService.getEntity(HiringStepMember.class, "select hsm" +
                        "                              from tsadv$HiringStepMember hsm" +
                        "                             where hsm.hiringStep.id = :hiringStepId" +
                        "                               and hsm.mainInterviewer = TRUE" +
                        "                               and (hsm.startDate IS NULL or hsm.startDate <= :systemDate) " +
                        "                               and (hsm.endDate IS NULL or hsm.endDate >= :systemDate) ",
                params, "hiringStepMember.view");
        List<OrganizationHrUser> organizationHrUsers = new ArrayList<>();
        if (hiringStepMember != null && "ROLE".equals(hiringStepMember.getHiringMemberType().getCode())) {
            organizationHrUsers = employeeService.getHrUsers(
                    getItem().getRequisition().getOrganizationGroup().getId(),
                    hiringStepMember.getRole().getCode());
            if ("MANAGER".equals(hiringStepMember.getRole().getCode())) {
                params.put("managerPersonGroupId", getItem().getRequisition().getManagerPersonGroup().getId());
            } else if ("RECRUITING_SPECIALIST".equals(hiringStepMember.getRole().getCode())) {
                params.put("recruiterPersonGroupId",
                        getItem().getRequisition().getRecruiterPersonGroup() != null ?
                                getItem().getRequisition().getRecruiterPersonGroup().getId() : null);
            }
        }
        if (!calc) {
            params.put("hiringStepMember", hiringStepMember);
            params.put("organizationGroupId", organizationHrUsers.size() > 0 ?
                    organizationHrUsers.get(0).getOrganizationGroup().getId() : null);
            Utils.customizeLookup(fieldGroup.getField("mainInterviewerPersonGroup").getComponent(),
                    "interviewer.lookup",
                    WindowManager.OpenType.DIALOG,
                    params);
        } else {
            if (hiringStepMember != null && "USER".equals(hiringStepMember.getHiringMemberType().getCode())) {
                params.remove("hiringStepId");
                params.put("hsmPersonGroupId", hiringStepMember.getUserPersonGroup() != null ? hiringStepMember.getUserPersonGroup().getId() : null);
                PersonGroupExt personGroup = commonService.getEntity(PersonGroupExt.class, "select e" +
                                "                           from base$PersonGroupExt e" +
                                "                           join e.list p" +
                                "                           left join e.assignments a" +
                                "                          where :systemDate between p.startDate and p.endDate" +
                                "                            and (p.type.code <> 'EMPLOYEE' OR :systemDate between a.startDate and a.endDate)" +
                                "                            and e.id = :hsmPersonGroupId",
                        params, "personGroup.browse");
                if (personGroup != null)
                    getItem().setMainInterviewerPersonGroup(personGroup);
            } else if (hiringStepMember != null && "ROLE".equals(hiringStepMember.getHiringMemberType().getCode())) {
                if ("MANAGER".equals(hiringStepMember.getRole().getCode())) {
                    getItem().setMainInterviewerPersonGroup(getItem().getRequisition().getManagerPersonGroup());
                } else if ("RECRUITING_SPECIALIST".equals(hiringStepMember.getRole().getCode())) {
                    getItem().setMainInterviewerPersonGroup(getItem().getRequisition().getRecruiterPersonGroup());
                } else if (organizationHrUsers != null && organizationHrUsers.size() == 1) {
                    getItem().setMainInterviewerPersonGroup(employeeService.getPersonGroupByUserId(organizationHrUsers.get(0).getUser().getId())); //TODO:personGroup need to test
                }
            }
        }
    }

    public void addQuestionnaires() {
        Map<String, Object> params = new HashMap<>();
        params.put(WindowParams.MULTI_SELECT.toString(), Boolean.TRUE);
        params.put("requisitionId", getItem().getRequisition());
        params.put("filterByRequisition", Boolean.TRUE);
        params.put("excludeRcQuestionnaireIds", questionnairesDs.getItems() != null ? questionnairesDs.getItems().stream().map(qq -> qq.getQuestionnaire().getId()).collect(Collectors.toList()) : null);

        openLookup("tsadv$RcQuestionnaire.lookup", items -> {
            for (RcQuestionnaire item : (Collection<RcQuestionnaire>) items) {
                addQuestionnaire(item);
            }
            questionnairesDs.setItem(null);

        }, WindowManager.OpenType.DIALOG, params);
    }

    public void removeQuestionnaires() {
        Table<InterviewQuestionnaire> questionnairesTable = (Table<InterviewQuestionnaire>) getComponent("questionnairesTable");
        showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                Set<InterviewQuestionnaire> selected = questionnairesTable.getSelected();
                                for (InterviewQuestionnaire item : selected) {
                                    questionnairesDs.removeItem(item);
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });

    }
}