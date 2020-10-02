package kz.uco.tsadv.web.bpm.editor;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bpm.entity.ProcActor;
import com.haulmont.bpm.entity.ProcDefinition;
import com.haulmont.bpm.entity.ProcTask;
import com.haulmont.bpm.gui.procactions.ProcActionsFrame;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebButton;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.beans.BpmUtils;
import kz.uco.tsadv.components.GroupsComponent;
import kz.uco.tsadv.entity.AssignmentRequest;
import kz.uco.tsadv.entity.AssignmentSalaryRequest;
import kz.uco.tsadv.entity.dbview.ProcInstanceV;
import kz.uco.tsadv.entity.tb.TemporaryTranslationRequest;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.entity.UserExtPersonGroup;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.BpmRequestMessage;
import kz.uco.tsadv.modules.personal.model.PositionChangeRequest;
import kz.uco.tsadv.modules.personal.model.SalaryRequest;
import kz.uco.tsadv.service.EmployeeNumberService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class AbstractBpmEditor<T extends StandardEntity> extends AbstractEditor<T> {

    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected GroupsComponent groupsComponent;
    @Inject
    protected BpmUtils bpmUtils;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected UserSession userSession;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CollectionDatasource<BpmRequestMessage, UUID> bpmRequestMessagesDs;
    @Inject
    protected CollectionDatasource<ProcTask, UUID> procTasksDs;
    @Inject
    protected ProcActionsFrame procActionsFrame;
    @Inject
    protected HBoxLayout procActionButtonHBox;
    @Inject
    protected GroupBoxLayout askQuestion;
    @Inject
    protected Button askQuestionBtn;
    @Inject
    protected VBoxLayout bpmActorsVBox;
    @Inject
    protected Table<ProcTask> procTasksTable;
    @Inject
    protected Button reportButton;
    /**
     * dd.MM.yyyy HH:mm:ss
     */
    protected DateFormat format;
    protected List<BpmRequestMessage> bpmRequestMessageList;
    protected UUID selectedMessageId;
    protected ProcTask activeProcTask;
    protected ProcTask lastCurrentUserProcTask;
    protected boolean fromProcInstance = false;
    protected boolean isSessionUserApprover;
    protected int count;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        this.setShowSaveNotification(false);
        format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        if (params.containsKey("bpmRequestMessage")) {
            selectedMessageId = UUID.fromString((String) params.get("bpmRequestMessage"));
        }

        if (Boolean.TRUE.equals(params.get("fromProcInstance"))) {
            fromProcInstance = true;
            String query = bpmRequestMessagesDs.getQuery();
            bpmRequestMessagesDs.setQuery(query.replaceFirst("(\\()(.*)(:session\\$user)(.*)(\\))", " 1=1 "));
        }

        initActivityBody((String) params.get("activity"));
        initLookupActions();
        initIconListeners();
    }

    protected void initActivityBody(String activityId) {
        if (activityId == null) return;
        groupsComponent.addActivityBodyToWindow(this, UUID.fromString(activityId));
        if (selectedMessageId == null) {
            selectedMessageId = getMessageId(activityId);
        }
    }

    protected UUID getMessageId(String activityId) {
        List<BpmRequestMessage> bpmRequestMessageList = commonService.getEntities(BpmRequestMessage.class,
                "select e from tsadv$BpmRequestMessage e where e.activity.id = :activityId",
                ParamsMap.of("activityId", UUID.fromString(activityId)),
                View.MINIMAL);
        return bpmRequestMessageList.size() == 1 ? bpmRequestMessageList.get(0).getId() : null;
    }

    protected void initLookupActions() {
    }

    protected void initIconListeners() {
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        DicRequestStatus status = commonService.getEntity(DicRequestStatus.class, "DRAFT");
        if (status == null) {
            throw new ItemNotFoundException("Status with code 'DRAFT' is not found");
        }
        item.setValue("status", status);
        if (Objects.isNull(item.getValue("requestNumber")))
            item.setValue("requestNumber", employeeNumberService.generateNextRequestNumber());
    }

    @Override
    protected void postInit() {
        super.postInit();
        procTasksDs.refresh();
        procTasksDs.setAllowCommit(false);
        initProcActionsFrame();
        initVariables();
        initVisibleFields();
        initEditableFields();
        initMessages();
        initGenerateColumns();
    }

    protected void initGenerateColumns() {
        if (procTasksTable != null)
            procTasksTable.addGeneratedColumn("comment", this::commentGenerateColumn);
    }

    protected Component commentGenerateColumn(ProcTask procTask) {
        String value = isProcTaskCommentVisible(procTask) ? procTask.getComment() : null;
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }

    protected boolean isProcTaskCommentVisible(ProcTask procTask) {
        return isDefaultCommentVisible()
                || lastCurrentUserProcTask == null && !isCurrentUserInitiator()
                || lastCurrentUserProcTask != null && !procTask.getStartDate().after(lastCurrentUserProcTask.getStartDate());
    }

    protected boolean isDefaultCommentVisible() {
        Class<? extends StandardEntity> entityClass = getItem().getClass();
        return !PositionChangeRequest.class.isAssignableFrom(entityClass)
                && !TemporaryTranslationRequest.class.isAssignableFrom(entityClass)
                && !SalaryRequest.class.isAssignableFrom(entityClass)
                && !AssignmentRequest.class.isAssignableFrom(entityClass)
                && !AssignmentSalaryRequest.class.isAssignableFrom(entityClass);
    }

    protected void initVariables() {
        activeProcTask = getActiveProcTask();
        isSessionUserApprover = isSessionUserApprover();
        lastCurrentUserProcTask = getLastCurrentUserProcTask();
        initInitiatorTask();
    }

    protected void initInitiatorTask() {
        if (!isDraft()) {
            List<ProcTask> sortedCollect = new LinkedList<>(procTasksDs.getItems());
            procTasksDs.clear();
            ProcTask initiatorTask = bpmUtils.createInitiatorTask(procActionsFrame.getProcInstance());
            initiatorTask.setOutcome(messages.getMessage(ProcInstanceV.class, initiatorTask.getOutcome()));
            initiatorTask.setName(messages.getMessage(ProcInstanceV.class, initiatorTask.getName()));
            sortedCollect.add(0, initiatorTask);
            sortedCollect.forEach(procTasksDs::addItem);
        }
    }

    protected ProcTask getLastCurrentUserProcTask() {
        return procTasksDs.getItems().stream()
                .filter(p -> p.getProcActor().getUser().getId().equals(userSession.getUser().getId()))
                .max(Comparator.comparing(ProcTask::getStartDate)).orElse(null);
    }

    protected void initMessages() {
        if (bpmRequestMessagesDs == null || askQuestion == null || !askQuestion.isVisible()) return;
        bpmRequestMessagesDs.refresh();
        if (bpmRequestMessagesDs.size() == 0) {
            askQuestion.setVisible(false);
            return;
        }
        count = 0;
        bpmRequestMessageList = new ArrayList<>(bpmRequestMessagesDs.getItems());

        bpmRequestMessagesDs.getItems().stream()
                .filter(this::isNoParent)
                .forEach(this::addMessages);
    }

    protected boolean isNoParent(BpmRequestMessage bpmRequestMessage) {
        return bpmRequestMessage.getParent() == null
                && Optional.ofNullable(bpmRequestMessage.getLvl()).orElse(0) == 0;
    }

    protected void addMessages(BpmRequestMessage bpmRequestMessage) {
        askQuestion.add(addMessage(bpmRequestMessage), count++);
        bpmRequestMessageList.stream()
                .filter(item -> bpmRequestMessage.equals(item.getParent()))
                .forEach(this::addMessages);
    }

    protected HBoxLayout addMessage(BpmRequestMessage bpmRequestMessage) {
        HBoxLayout hBox = createHBoxLayout(bpmRequestMessage);

        HtmlBoxLayout htmlBoxLayout = createHtmlBoxLayout(bpmRequestMessage);

        hBox.add(createSpaceLabel(bpmRequestMessage), 0);
        hBox.add(htmlBoxLayout, 1);
        hBox.expand(htmlBoxLayout);
        return hBox;
    }

    protected HBoxLayout createHBoxLayout(BpmRequestMessage bpmRequestMessage) {
        HBoxLayout hBox = componentsFactory.createComponent(HBoxLayout.class);
        hBox.setId(bpmRequestMessage.getId().toString());
        hBox.setStyleName("bpm-message-block " + (isSelectedMessage(bpmRequestMessage) ? "selected" : "unselected"));
        hBox.setSpacing(true);
        hBox.setWidth("100%");
        return hBox;
    }

    protected HtmlBoxLayout createHtmlBoxLayout(BpmRequestMessage bpmRequestMessage) {

        HtmlBoxLayout htmlBoxLayout = componentsFactory.createComponent(HtmlBoxLayout.class);
        htmlBoxLayout.setTemplateName("bpm-request-message");

        htmlBoxLayout.add(createImage(bpmRequestMessage));
        htmlBoxLayout.add(createPersonNameLabel(bpmRequestMessage));
        htmlBoxLayout.add(createDateLabel(bpmRequestMessage));
//        htmlBoxLayout.add(createDeleteLinkButton(bpmRequestMessage));
        htmlBoxLayout.add(createMessageLabel(bpmRequestMessage));
        htmlBoxLayout.add(createAnswerLinkButton(bpmRequestMessage));

        return htmlBoxLayout;
    }

    protected LinkButton createAnswerLinkButton(BpmRequestMessage bpmRequestMessage) {
        LinkButton answerBtn = componentsFactory.createComponent(LinkButton.class);
        answerBtn.setId("answerBtn");
        answerBtn.setStyleName("bpm-message-answer-btn");
        answerBtn.setAction(getAnswerAction(bpmRequestMessage));
        answerBtn.setVisible(isVisibleAnswerBtn(bpmRequestMessage));
        return answerBtn;
    }

    protected boolean isVisibleAnswerBtn(BpmRequestMessage bpmRequestMessage) {
        return userSession.getUser().equals(bpmRequestMessage.getAssignedBy().getUserExt())
                || userSession.getUser().equals(bpmRequestMessage.getAssignedUser().getUserExt());
    }

    protected Label createMessageLabel(BpmRequestMessage bpmRequestMessage) {
        Label messageLbl = componentsFactory.createComponent(Label.class);
        messageLbl.setId("message");
        messageLbl.setStyleName("white-space-normal");
        messageLbl.setValue(bpmRequestMessage.getMessage());
        return messageLbl;
    }

    protected LinkButton createDeleteLinkButton(BpmRequestMessage bpmRequestMessage) {
        LinkButton deleteBtn = componentsFactory.createComponent(LinkButton.class);
        deleteBtn.setId("deleteBtn");
        deleteBtn.setStyleName("bpm-message-delete-btn");
        deleteBtn.setAction(new BaseAction("") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                showOptionDialog(getMessage("Attention"),
                        messages.getMainMessage("attention.delete.message"),
                        MessageType.WARNING,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES, Status.PRIMARY) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        onDeleteMessage(bpmRequestMessage);
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO, Status.NORMAL) {
                                    public void actionPerform(Component component) {
                                    }
                                }
                        });
            }
        });
        deleteBtn.setIcon("font-icon:REMOVE");
        deleteBtn.setVisible(isDeletableMessage(bpmRequestMessage));
        return deleteBtn;
    }

    protected Label createDateLabel(BpmRequestMessage bpmRequestMessage) {
        Label dateLbl = componentsFactory.createComponent(Label.class);
        dateLbl.setId("date");
        dateLbl.setValue(format.format(bpmRequestMessage.getSendDate()));
        return dateLbl;
    }

    protected Image createImage(BpmRequestMessage bpmRequestMessage) {
        Image image = componentsFactory.createComponent(Image.class);
        image.setStyleName("bpm-message-img");
        image.setId("personImage");
        if (bpmRequestMessage.getAssignedBy().getUserExt().getImage() != null)
            image.setSource(FileDescriptorResource.class).setFileDescriptor(bpmRequestMessage.getAssignedBy().getUserExt().getImage());
        else
            image.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
        return image;
    }

    protected Label createPersonNameLabel(BpmRequestMessage bpmRequestMessage) {
        Label personNameLbl = componentsFactory.createComponent(Label.class);
        String mess = messages.getMainMessage(Optional.ofNullable(bpmRequestMessage.getLvl()).orElse(0) == 0 ? "ask.question" : "answer.question");
        personNameLbl.setValue(bpmRequestMessage.getAssignedBy().getFullName() + " " + mess + " " + bpmRequestMessage.getAssignedUser().getFullName());
        personNameLbl.setId("personName");
        return personNameLbl;
    }

    protected Label createSpaceLabel(BpmRequestMessage bpmRequestMessage) {
        Label spaceLbl = componentsFactory.createComponent(Label.class);
        spaceLbl.setWidth(Optional.ofNullable(bpmRequestMessage.getLvl()).orElse(0) * 20 + "px");
        return spaceLbl;
    }

    protected boolean isDeletableMessage(BpmRequestMessage bpmRequestMessage) {
        return userSession.getUser().equals(bpmRequestMessage.getAssignedBy().getUserExt())
                && bpmRequestMessageList.stream().noneMatch(item -> bpmRequestMessage.equals(item.getParent()));
    }

    protected void onDeleteMessage(BpmRequestMessage bpmRequestMessage) {
        dataManager.remove(bpmRequestMessage);
        refreshMessage();
    }

    protected boolean isSelectedMessage(BpmRequestMessage bpmRequestMessage) {
        return bpmRequestMessage.getId().equals(selectedMessageId);
    }

    protected Action getAnswerAction(BpmRequestMessage bpmRequestMessage) {
        return new BaseAction("answerAction") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                onAnswerQuestion(bpmRequestMessage);
            }

            @Override
            public String getCaption() {
                return messages.getMainMessage("toAnswer");
            }
        };
    }

    protected void onAnswerQuestion(BpmRequestMessage bpmRequestMessage) {
        openMessageEditor(bpmRequestMessage);
    }

    protected void initEditableFields() {

    }

    protected void initVisibleFields() {
        boolean isActorsUnVisible = isDraft()
                || !isSessionUserApprover && !fromProcInstance
                && !Objects.equals(procActionsFrame.getProcInstance().getCreatedBy(), userSession.getUser().getLogin());

        if (askQuestion != null) askQuestion.setVisible(!isDraft());
        if (bpmActorsVBox != null && isActorsUnVisible) {
            bpmActorsVBox.setVisible(false);
        }
        if (reportButton != null && isActorsUnVisible) {
            reportButton.setVisible(false);
        }
        if (askQuestionBtn != null && !isUserCurrentApprover()) {
            askQuestionBtn.setVisible(false);
        }
    }

    protected boolean isUserCurrentApprover() {
        return userSession.getUser().equals(activeProcTask == null ? null : activeProcTask.getProcActor().getUser());
    }

    protected void initProcActionsFrame() {
        ProcDefinition procDefinition = getProcDefinition();

        procActionsFrame.initializer()
                .setBeforeStartProcessPredicate(this::beforeStartProcessPredicate)
                .setAfterStartProcessListener(this::afterStartProcessListener)
                .setBeforeCompleteTaskPredicate(this::beforeCompleteTaskPredicate)
                .setAfterCompleteTaskListener(this::afterCompleteTaskListener)
                .init(procDefinition.getCode(), getItem());
    }

    protected boolean beforeCompleteTaskPredicate() {
        return true;
    }

    protected void afterCompleteTaskListener() {
        closeWithMessage(messages.getMainMessage("taskCompleted"));
    }

    protected void afterStartProcessListener() {
        UserExtPersonGroup sendTo = bpmUtils.getActiveTaskUser(procActionsFrame.getProcInstance().getId(), "userExtPersonGroup.edit");
        closeWithMessage(String.format(messages.getMainMessage("startProcessNotification"), (sendTo != null ? sendTo.getFullName() : "")));
    }

    protected boolean beforeStartProcessPredicate() {
        if (validateAll()) {
            DicRequestStatus status = commonService.getEntity(DicRequestStatus.class, "APPROVING");
            if (status == null) {
                throw new ItemNotFoundException("Request status with code 'APPROVING' not found!");
            }
            return !alreadyHasRequest(status.getId()) && commit();
        }
        return false;
    }

    protected boolean alreadyHasRequest(UUID statusId) {
        return false;
    }

    @Override
    public void ready() {
        super.ready();

        procActionsFrame.remove(procActionsFrame.getComponent("noActionsAvailableLbl"));
        procActionsFrame.remove(procActionsFrame.getComponent("taskInfoGrid"));
        Collection<Component> procActionFrameComp = procActionsFrame.getComponents();
        procActionFrameComp.forEach(this::overrideProcActionComponents);

        if (selectedMessageId != null && bpmRequestMessagesDs != null && !isContainMessage(selectedMessageId)) {
            showNotification(messages.getMainMessage("message.deleted"));
        }

    }

    protected boolean isSessionUserApprover() {
        return commonService.getCount(ProcActor.class,
                " select e from bpm$ProcActor e where e.user.id = :userId and e.procInstance.id = :procInstanceId",
                ParamsMap.of("userId", userSession.getUser().getId(),
                        "procInstanceId", procActionsFrame.getProcInstance().getId())) > 0;
    }

    protected boolean isContainMessage(UUID messageId) {
        return bpmRequestMessagesDs.getItems().stream().anyMatch(bpmRequestMessage -> bpmRequestMessage.getId().equals(messageId));
    }

    protected void overrideProcActionComponents(Component component) {
        if (component instanceof VBoxLayout) {
            Collection<Component> vBoxComp = ((VBoxLayout) component).getComponents();
            changeSettingsProcActionBtn(vBoxComp);
            procActionsFrame.remove(component);
            vBoxComp.forEach(component1 -> {
                component1.setParent(null);
                component1.setWidthAuto();
                procActionButtonHBox.add(component1);
            });

        }
    }

    protected void changeSettingsProcActionBtn(Collection<Component> vBoxComp) {
        if (vBoxComp.size() == 1) {
            Component component = vBoxComp.iterator().next();
            if (component instanceof WebButton
                    && "completeTask_reassign".equals(((WebButton) component).getAction().getId()))
                component.setVisible(fromProcInstance);
        }
    }

    protected boolean isDraft() {
        return getItem().getValue("status") != null
                && "DRAFT".equalsIgnoreCase(((DicRequestStatus) getItem().getValue("status")).getCode());
    }

    protected boolean isOnApproval() {
        return getItem().getValue("status") != null
                && "APPROVING".equalsIgnoreCase(((DicRequestStatus) getItem().getValue("status")).getCode());
    }

    protected void closeWithMessage(String message) {
        showNotification(message, NotificationType.HUMANIZED);
        close(CLOSE_ACTION_ID, true);
    }

    @Nonnull
    protected ProcDefinition getProcDefinition() {
        ProcDefinition procDefinition = bpmUtils.getProcDefinition(getItem(), getProcessName());
        if (procDefinition == null) {
            throw new ItemNotFoundException(messages.getMainMessage("noProcDefinition"));
        }
        return procDefinition;
    }

    public void closeButton() {
        close(CLOSE_ACTION_ID);
    }

    public void save() {
        commitAndClose();
    }

    @Override
    public boolean close(String actionId) {
        return close(actionId, true);
    }

    public void onAskQuestion() {
        openMessageEditor(null);
    }

    protected AbstractEditor openMessageEditor(BpmRequestMessage parent) {
        BpmRequestMessage message = metadata.create(BpmRequestMessage.class);
        message.setEntityId(getItem().getId());
        message.setEntityName(getItem().getMetaClass().getName());
        message.setEntityRequestNumber(getItem().getValue("requestNumber"));
        message.setLvl(parent != null && parent.getLvl() != null ? parent.getLvl() + 1 : 0);
        message.setParent(parent);
        message.setAssignedUser(parent != null ? parent.getAssignedBy() : null);
        message.setScreenName(getId());
        message.setProcInstance(procActionsFrame.getProcInstance());

        AbstractEditor editor = openEditor(message,
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("isAssignedUserEditable", message.getAssignedUser() == null,
                        "procInstanceId", procActionsFrame.getProcInstance().getId()));
        editor.addCloseListener(this::messageEditorCloseListener);
        return editor;
    }

    protected void messageEditorCloseListener(String actionId) {
        if ("commit".equals(actionId)) {
            showNotification(messages.getMainMessage("message.sent"));
            if (isUserCurrentApprover()) refreshMessage();
            else close(CLOSE_ACTION_ID, true);
        }
    }

    protected void refreshMessage() {
        askQuestion.removeAll();
        askQuestion.setVisible(true);
        initMessages();
    }

    @Nullable
    protected ProcTask getActiveProcTask() {
        if (procTasksDs == null) return null;
        return procTasksDs.getItems().stream().filter(procTask -> procTask.getEndDate() == null)
                .findFirst().orElse(null);
    }

    protected boolean isCurrentUserInitiator() {
        return Objects.equals(procActionsFrame.getProcInstance().getCreatedBy(), userSession.getUser().getLogin());
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (employeeNumberService.hasRequestNumber(getItem().getValue("requestNumber"), getItem().getId())) {
            errors.add(messages.getMainMessage("errorRequestNum"));
        }
    }

    protected abstract String getProcessName();

}