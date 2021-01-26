package kz.uco.tsadv.web.modules.recruitment.offer;

/*import com.haulmont.bpm.entity.ProcActor;
import com.haulmont.bpm.entity.ProcTask;
import com.haulmont.bpm.gui.procactions.ProcActionsFrame;
import com.haulmont.bpm.service.ProcessRuntimeService;*/
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.reports.gui.ReportGuiManager;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.enums.OfferStatus;
import kz.uco.tsadv.modules.recruitment.model.Offer;
import kz.uco.tsadv.modules.recruitment.model.OfferHistory;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.web.modules.recruitment.offerhistory.OfferHistoryEdit;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.*;

public class OfferEdit extends AbstractEditor<Offer> {
    @Inject
    protected Datasource<Offer> offerDs;

    @Inject
    protected EmailService emailService;
    @Inject
    protected Datasource<FileDescriptor> fileDs;

    @Inject
    protected CollectionDatasource<DicCurrency, UUID> currenciesDs;

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    protected NotificationService notificationService;
    @Inject
    protected FileLoader fileLoader;

    @Inject
    protected Metadata metadata;

    @Inject
    protected HBoxLayout approvedActionsGroupBoxId;

    protected FileDescriptor fileDescriptor;
    @Inject
    protected DataManager dataManager;
    /*@Inject
    protected ProcActionsFrame procActionsFrame;*/
    @Inject
    protected UserSession userSession;
    /*@Inject
    protected ProcessRuntimeService processRuntimeService;*/

    @Named("fieldGroup.proposedStartDateId")
    protected DateField<Date> proposedStartDateField;

    @Named("fieldGroup.expireDateId")
    protected DateField<Date> expireDateField;

    protected OfferHistory offerHistory;
    @Inject
    protected CollectionDatasource<OfferHistory, UUID> historyDs;
    @Inject
    protected CommonService commonService;
    /*@Inject
    protected CollectionDatasource<ProcTask, UUID> procTasksDs;*/
    protected boolean fromMyOffer;
    @Inject
    protected HBoxLayout procActionsBox;
    @Inject
    protected HBoxLayout procActionButtonHBox;
    @Inject
    protected ActivityService activityService;
    @Inject
    private BusinessRuleService businessRuleService;

    @Override
    protected void initNewItem(Offer item) {
        super.initNewItem(item);
        item.setStatus(OfferStatus.DRAFT);
        List<OfferHistory> list = new ArrayList<>();
        offerHistory = metadata.create(OfferHistory.class);
        offerHistory.setChangedBy(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP));
        offerHistory.setOffer(item);
        offerHistory.setStatus(OfferStatus.DRAFT);
        item.setHistory(new ArrayList<>());
        item.setFile(fileDescriptor);
    }

    @Override
    public void ready() {
        super.ready();
        /*procActionsFrame.remove(procActionsFrame.getComponent("noActionsAvailableLbl"));
        procActionsFrame.remove(procActionsFrame.getComponent("taskInfoGrid"));
        Collection<Component> procActionFrameComp = procActionsFrame.getComponents();
        procActionFrameComp.forEach(component -> {
            if (component instanceof VBoxLayout) {
                Collection<Component> vBoxComp = ((VBoxLayout) component).getComponents();
                procActionsFrame.remove(component);
                vBoxComp.forEach(component1 -> {
                    component1.setParent(null);
                    procActionButtonHBox.add(component1);
                });

            }
        });*/
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.get("fromMyOffer") != null) {
            fromMyOffer = true;
        }
        ((PickerField) fieldGroup.getField("jobRequestId").getComponent()).getLookupAction()
                .setLookupScreenParams(Collections.singletonMap("hrManagerPersonGroupId",
                        userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID)));
    }

    @Override
    protected void postInit() {
        approvedActionsGroupBoxId.setVisible(getItem().getStatus().equals(OfferStatus.APPROVED) &&
                (userSession.getAttribute(StaticVariable.USER_PERSON_GROUP)
                        .equals(commonService.getEntity(PersonGroupExt.class,
                                "select e.recruiterPersonGroup " +
                                        "   from tsadv$Requisition e " +
                                        "  where e.id = :reqId",
                                Collections.singletonMap("reqId", getItem().getJobRequest().getRequisition().getId()),
                                null)) ||
                        userSession.getAttribute(StaticVariable.USER_PERSON_GROUP)
                                .equals(commonService.getEntity(PersonGroupExt.class,
                                        "select e.candidatePersonGroup " +
                                                "   from tsadv$JobRequest e " +
                                                "  where e.id = :reqId",
                                        Collections.singletonMap("reqId", getItem().getJobRequest().getId()),
                                        null))));
        fieldGroup.setEditable(getItem().getStatus().equals(OfferStatus.DRAFT));
        initProcActionsFrame();
        /*for (Component component : procActionsFrame.getComponents()) {
            component.setWidth(null);
        }*/
        proposedStartDateField.addValueChangeListener(e -> {

            proposedStartDateFieldListener(e);
        });

        offerDs.addItemPropertyChangeListener(e -> {
            offerDsListener(e);

        });
        fieldGroup.getField("fileId").setEditable(false);
//        ((LookupPickerField) fieldGroup.getField("offerTemplate").getComponent()).addValueChangeListener(e -> {
//            if ((e.getPrevValue() != null && !e.getPrevValue().equals(e.getValue()))
//                    || (e.getValue() != null && !e.getValue().equals(e.getPrevValue()))) {
//                createOfferReport(e);
//            }
//        });
    }

    protected void proposedStartDateFieldListener(Object e) {
        Date date = proposedStartDateField.getValue();
        date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        expireDateField.setRangeStart(date);
    }

    protected void offerDsListener(Datasource.ItemPropertyChangeEvent<Offer> e) {
        if (!"file".equals(e.getProperty())) {
            if ((e.getPrevValue() != null && !e.getPrevValue().equals(e.getValue()))
                    || (e.getValue() != null && !e.getValue().equals(e.getPrevValue()))) {
                createOfferReport(e);
            }
        }
    }


    protected void createOfferReport(Datasource.ItemPropertyChangeEvent e) {
        if (offerDs.getItem().getFile() != null) {
            try {
                fileLoader.removeFile(offerDs.getItem().getFile());
            } catch (FileStorageException e1) {
                throw new RuntimeException(e1);
            }
        }
        if (offerDs.getItem().getOfferTemplate() == null) {
            offerDs.getItem().setFile(null);
        } else {
            getReportFileDescriptor();
            dataManager.commit(fileDescriptor);

            offerDs.getItem().setFile(fileDescriptor);
        }
    }

    private void getReportFileDescriptor() {
        ReportGuiManager reportGuiManager = AppBeans.get(ReportGuiManager.class);
        Map<String, Object> reportParams = new HashMap<>();
        reportParams.put("candidateFullName", offerDs.getItem().getJobRequest() == null ? "" : offerDs.getItem().getJobRequest().getCandidatePersonGroup().getPerson().getFullName());
        reportParams.put("positionName", offerDs.getItem().getJobRequest() == null ? "" : (offerDs.getItem().getJobRequest().getRequisition().getPositionGroup() == null ? "" : offerDs.getItem().getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName()));
        reportParams.put("salary", offerDs.getItem().getProposedSalary() == null ? 0 : offerDs.getItem().getProposedSalary());
        reportParams.put("currency", offerDs.getItem().getCurrency() == null ? "" : offerDs.getItem().getCurrency().getLangValue());
        reportParams.put("expireDate", offerDs.getItem().getExpireDate());
        reportParams.put("startDate", offerDs.getItem().getProposedStartDate());
        reportParams.put("offerDate", new Date());
        reportParams.put("recruiter", offerDs.getItem().getJobRequest().getRequisition().getRecruiterPersonGroup().getFullName());
        ReportOutputDocument rod = reportGuiManager.getReportResult(offerDs.getItem().getOfferTemplate().getReportTemplate(), reportParams, null);
        byte[] bytes = rod.getContent();
        fileDescriptor = metadata.create(FileDescriptor.class);
        fileDescriptor.setName(rod.getDocumentName());
        fileDescriptor.setExtension(rod.getReportOutputType().toString());
        fileDescriptor.setSize((long) bytes.length);
        fileDescriptor.setCreateDate(new Date());

        try {
            fileLoader.saveStream(fileDescriptor, () -> new ByteArrayInputStream(bytes));
        } catch (FileStorageException a) {
            throw new RuntimeException(a);
        }

    }


    protected void initProcActionsFrame() {
//        procActionsFrame.initializer()
//                .setBeforeStartProcessPredicate(this::commit)
//                .setAfterStartProcessListener(() -> {
//                    close(COMMIT_ACTION_ID);
//                    Map<String, Object> queryParams = new HashMap<>();
//                    queryParams.put("hrManagerTask", "hrManagerTask");
//                    queryParams.put("procInstanceId", procActionsFrame.getProcInstance().getId());
//                    ProcTask procTask = commonService.getEntity(ProcTask.class, "select e " +
//                                    " from bpm$ProcTask e " +
//                                    "where e.procInstance.id = :procInstanceId" +
//                                    "  and e.name = :hrManagerTask",
//                            queryParams, null);
//
//
//                    showNotification(businessRuleService.getBusinessRuleMessage("offerSendToApprove"), NotificationType.TRAY);
//                    if (procTask != null) {
//                        processRuntimeService.completeProcTask(procTask, "onAgreement", null, new HashMap<>());
//                    }
//
//                    Offer offer = offerDs.getItem();
//                    openEditor("tsadv$Offer.edit", offer, WindowManager.OpenType.THIS_TAB, getParentDs());
//                })
//                .setBeforeCompleteTaskPredicate(() -> {
//                    ProcTask procTask = commonService.getEntity(ProcTask.class, "select e " +
//                                    " from bpm$ProcTask e " +
//                                    "where e.procInstance.id = :procInstanceId" +
//                                    "  and e.createTs = (select max(t.createTs) " +
//                                    "                      from bpm$ProcTask t " +
//                                    "                     where t.procInstance.id = :procInstanceId" +
//                                    "                       and t.outcome is not null)",
//                            Collections.singletonMap("procInstanceId",
//                                    procActionsFrame.getProcInstance().getId()), null);
//                    if (procTask != null && "approve".equals(procTask.getOutcome())) {
//                        getReportFileDescriptor();
//                        dataManager.commit(fileDescriptor);
//                        getItem().setFile(fileDescriptor);
//                    }
//                    return commit();
//                })
//
//                .setAfterClaimTaskListener(() -> {
//                    initProcActionsFrame();
//                    procTasksDs.refresh();
//                })
//                .setAfterCompleteTaskListener(() -> {
//                    ProcTask procTask = commonService.getEntity(ProcTask.class, "select e " +
//                                    " from bpm$ProcTask e " +
//                                    "where e.procInstance.id = :procInstanceId" +
//                                    "  and e.createTs = (select max(t.createTs) " +
//                                    "                      from bpm$ProcTask t " +
//                                    "                     where t.procInstance.id = :procInstanceId" +
//                                    "                       and t.outcome is not null)",
//                            Collections.singletonMap("procInstanceId",
//                                    procActionsFrame.getProcInstance().getId()), null);
//                    if (procTask != null && "reject".equals(procTask.getOutcome())) {
//                        if (offerHistory == null) {
//                            offerHistory = metadata.create(OfferHistory.class);
//                        }
//                        offerHistory.setChangedBy(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP));
//                        offerHistory.setOffer(offerDs.getItem());
//                        offerHistory.setStatus(OfferStatus.DRAFT);
//                        offerHistory.setDeclineReason(procTask.getComment());
//                        offerHistory.setStatusChangeDate(new Date());
//                        dataManager.commit(offerHistory);
//                        showNotification(getMessage("offer.send.to.rework"), NotificationType.TRAY);
//                    } else if (procTask != null && "approve".equals(procTask.getOutcome())) {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("procInstanceId", procActionsFrame.getProcInstance().getId());
//                        map.put("businessPartner", "businessPartner");
//                        List<ProcActor> procActorList = commonService.getEntities(ProcActor.class,
//                                "select e from bpm$ProcActor e " +
//                                        "where e.procInstance.id = :procInstanceId " +
//                                        "and e.procRole.code = :businessPartner", map, null);
//                        if (procActorList.size() == 0) {
//                            if (offerDs.getItem().getNeedBuisnessPartnerApprove()) {
//                                showNotification(getMessage("bpm.offerAprove.error.noBusinessPartner"), NotificationType.WARNING);
//                            } else {
//                                showNotification(businessRuleService.getBusinessRuleMessage("offerSendCandidate"));
//                            }
//                        } else {
//                            if (procTask.getProcActor() != null) {
//                                if ("approver".equals(procTask.getProcActor().getProcRole())) {
//                                    showNotification(getMessage("offer.save.and.send.to.business.partner"));
//                                } else {
//                                    showNotification(getMessage("offer.save.and.send.to.candidate"));
//                                }
//                            } else {
//                                showNotification(getMessage("bpm.error.task.has.not.proc.actor"), NotificationType.WARNING);
//                            }
//                        }
//                    }
//                    close(COMMIT_ACTION_ID);
//                })
//                .init("offerApproval", getItem());
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        inPostValidate(errors);

//        if (getItem().getStatus().equals(OfferStatus.DRAFT)) {
//            if (getItem().getJobRequest() != null && getItem().getJobRequest().getRequestStatus() != JobRequestStatus.SELECTED) {
//                errors.add(getMessage("jobRequestStatus.notSelected"));
//            }
//        }
    }

    protected void inPostValidate(ValidationErrors errors) {
        if (expireDateField.getValue().before(proposedStartDateField.getValue())) {
            errors.add(getMessage("ProposedExpiredDates.Error"));
        }
    }

    @Override
    protected boolean preCommit() {
        if (offerHistory != null) {
            if (historyDs.getItem(offerHistory.getId()) != null) {
                historyDs.getItem(offerHistory.getId()).setStatusChangeDate(new Date());
            } else {
                offerHistory.setStatusChangeDate(new Date());
                historyDs.addItem(offerHistory);
            }
        }
        if (offerDs.getItem().getStatus().equals(OfferStatus.ACCEPTED) ||
                offerDs.getItem().getStatus().equals(OfferStatus.REJECTED)) {
            Map<String, Object> mapReqId = new HashMap<>();
            Map<String, Object> map = new HashMap<>();
            mapReqId.put("recId", offerDs.getItem().getJobRequest().getRequisition().getId());

            map.put("keyValue", commonService.getEntity(PersonGroupExt.class,
                    "select e.managerPersonGroup from tsadv$Requisition e where e.id = :recId",
                    mapReqId, "personGroupId.browse"));

            UserExt user = commonService.getEntity(UserExt.class,
                    "select e from tsadv$UserExt e " +
                            "where e.personGroup.id = :keyValue",
                    map,
                    "user.edit");
            Map<String, Object> params = new HashMap<>();
            params.put("user", user);
            params.put("offer", offerDs.getItem());
            params.put("fioPerson", offerDs.getItem().getJobRequest().getCandidatePersonGroup().getFullName());
            params.put("positionName",
                    offerDs.getItem().getJobRequest().getRequisition().getPositionGroup() == null
                            ? offerDs.getItem().getJobRequest().getRequisition().getJobGroup().getJob().getJobName()
                            : offerDs.getItem().getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName());
            params.put("requisitionCode", offerDs.getItem().getJobRequest().getRequisition().getCode());
            notificationService.sendParametrizedNotification("offer." +
                            (offerDs.getItem().getStatus().equals(OfferStatus.ACCEPTED) ? "accepted" : "rejected") +
                            ".notification",
                    user, params);
        }
        return super.preCommit();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && !close) {
            postInit();
        }
        return true;
    }

    public void onAcceptButtonIdClick() {
        getItem().setStatus(OfferStatus.ACCEPTED);
        if (offerHistory == null) {
            offerHistory = metadata.create(OfferHistory.class);
        }
        offerHistory.setChangedBy(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP));
        offerHistory.setOffer(getItem());
        offerHistory.setStatus(OfferStatus.ACCEPTED);
        commitAndClose();
        activityService.doneActivity(getItem().getId(), "OFFER_ACCEPT");
    }

    public void onDeclineButtonIdClick() {
        if (offerHistory == null) {
            offerHistory = metadata.create(OfferHistory.class);
        }
        offerHistory.setChangedBy(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP));
        offerHistory.setOffer(getItem());
        OfferHistoryEdit offerHistoryEdit = (OfferHistoryEdit) openEditor("tsadv$OfferHistory.edit", offerHistory, WindowManager.OpenType.DIALOG, historyDs);
        offerHistoryEdit.addCloseWithCommitListener(() -> {
            getItem().setStatus(OfferStatus.REJECTED);
            historyDs.getItem(offerHistory.getId()).setStatus(OfferStatus.REJECTED);
            commitAndClose();
            activityService.doneActivity(getItem().getId(), "OFFER_ACCEPT");
        });

    }

    public void cancleButton() {
        close(CLOSE_ACTION_ID);
    }
}