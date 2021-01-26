package kz.uco.tsadv.web.modules.recognition.feedback.rcgfeedback;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.vaadin.server.Page;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.feedback.DicRcgFeedbackType;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedback;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackAttachment;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackDirection;
import kz.uco.tsadv.web.modules.recognition.RecognitionHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class RcgFeedbackDialogEdit extends AbstractEditor<RcgFeedback> {

    protected RecognitionHelper recognitionHelper = new RecognitionHelper();

    @Inject
    private Metadata metadata;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private DataManager dataManager;

    @Inject
    private UserSession userSession;

    @Inject
    private LinkButton windowCommit;

    @Inject
    private CommonService commonService;

    @Inject
    private CollectionDatasource<RcgFeedbackAttachment, UUID> attachmentsDs;

    @Inject
    private VBoxLayout attachmentsBox, rfTypeBlock, typeBlock;

    @Inject
    private FlowBoxLayout attachmentsFlowBox;

    @Inject
    private SuggestionPickerField suggestionPickerField;

    @Inject
    private HBoxLayout organization;

    @Inject
    private TextField organizationField, positionField;

    @Inject
    private VBoxLayout receiverBlock;

    @Inject
    private ResizableTextArea comment;

    @Inject
    private Label commentMinLengthLabel;

    @WindowParam
    protected String type;
    @WindowParam
    protected RcgFeedback requiredFeedBack;

    private boolean attachmentChanged;
    private String defaultCommentMinLengthMessage;
    private boolean isNew = false;

    @Override
    protected void initNewItem(RcgFeedback item) {
        super.initNewItem(item);

        item.setAuthor(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP));
        if (item.getFeedbackDate() == null) {
            item.setFeedbackDate(CommonUtils.getSystemDate());
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        try {
            initFeedbackTypes();

            initDialogResize();

            suggestionPickerField.addClearAction();
            suggestionPickerField.removeAction(PickerField.ActionType.LOOKUP.getId());
            suggestionPickerField.removeAction(PickerField.ActionType.OPEN.getId());

            PersonGroupExt personGroupExt = getItem().getReceiver();
            if (personGroupExt != null) {
                PersonExt personExt = personGroupExt.getPerson();

                String fullName = personExt.getFirstNameLatin() + " " + personExt.getLastNameLatin();
                if (userSession.getLocale().getLanguage().equalsIgnoreCase("ru")) {
                    fullName = personExt.getFirstName() + " " + personExt.getLastName();
                }

                setCaption(getCaption() + " " + fullName);
                receiverBlock.setVisible(false);

                fillPersonData();
            } else {
                organization.setVisible(false);
            }

            if (!PersistenceHelper.isNew(getItem())) {
                if (getItem().getDirection().equals(RcgFeedbackDirection.SEND)) {
                    initAttachments();
                }
            }

//            rcgFeedbackDs.setAllowCommit(getItem().getDirection().equals(RcgFeedbackDirection.SEND));

            defaultCommentMinLengthMessage = getMessage("rf.comment.min.length");
            commentMinLengthLabel.setValue(defaultCommentMinLengthMessage);

            comment.addValidator(value -> {
                String rawValue = comment.getRawValue();
                if (StringUtils.isNotBlank(rawValue) && rawValue.length() < 50) {
                    throw new ValidationException(defaultCommentMinLengthMessage);
                }
            });
        } catch (Exception ex) {
            setEnabled(false);

            showNotification(getMessage("msg.error.title"),
                    ex.getMessage(),
                    NotificationType.ERROR);
        }
    }

    @Override
    public void ready() {
        super.ready();

        if ("answer".equals(type)) {

            suggestionPickerField.setEditable(false);

            if (requiredFeedBack != null) {
                requiredFeedBack.setDirection(RcgFeedbackDirection.ANSWERED);
                getDsContext().addBeforeCommitListener(context -> context.addInstanceToCommit(requiredFeedBack));
            }
        }

        suggestionPickerField.addValueChangeListener(e -> fillPersonData());

        initListeners();

        initPageCaption();

        isNew = PersistenceHelper.isNew(getItem());
        if (isNew) {
            windowCommit.setCaption(getMessage(getItem().getDirection().equals(RcgFeedbackDirection.SEND) ?
                    "rf.send.btn" : "rf.request.btn"));
        } else {
            windowCommit.setCaption(getMessage("rf.save.btn"));

            attachmentsDs.addCollectionChangeListener(e -> attachmentChanged = true);
        }
    }

    @Override
    public void commitAndClose() {
        try {
            super.commitAndClose();
        } catch (Exception ex) {
            showNotification(getMessage("msg.error.title"),
                    ex.getMessage(),
                    NotificationType.TRAY);
        }
    }

    @Override
    public boolean close(String actionId) {
        return super.close(actionId, true);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        boolean success = false;
        try {
            success = super.postCommit(committed, close);
            if (success) {
                String messageCode = isNew ? (getItem().getDirection().equals(RcgFeedbackDirection.SEND)
                        ? "rf.send.success" : "rf.request.success") : "rf.save.success";

                recognitionHelper.showNotificationWindow(
                        String.format(getMessage(messageCode),
                                getItem().getReceiver().getFirstLastName()),
                        "images/recognition/gt-success.png");
            }
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
        return success;
    }

    public boolean isAttachmentChanged() {
        return attachmentChanged;
    }

    public void addFile() {
        RcgFeedbackAttachment rcgFeedbackAttachment = metadata.create(RcgFeedbackAttachment.class);
        rcgFeedbackAttachment.setRcgFeedback(getItem());
        AbstractEditor editor = openEditor(rcgFeedbackAttachment, WindowManager.OpenType.DIALOG, null, attachmentsDs);
        editor.addCloseWithCommitListener(() -> {
            RcgFeedbackAttachment editedItem = (RcgFeedbackAttachment) editor.getItem();

            attachmentsDs.addItem(editedItem);
            createThumbnail(editedItem);
        });
    }

    private void initAttachments() {
        Collection<RcgFeedbackAttachment> attachments = attachmentsDs.getItems();
        if (CollectionUtils.isNotEmpty(attachments)) {
            for (RcgFeedbackAttachment attachment : attachments) {
                createThumbnail(attachment);
            }
        }
    }

    private void createThumbnail(RcgFeedbackAttachment rcgFeedbackAttachment) {
        if (rcgFeedbackAttachment != null) {
            FileDescriptor fd = rcgFeedbackAttachment.getFile();
            if (fd != null) {
                String extension = fd.getExtension();
                if (StringUtils.isNotBlank(extension)) {
                    extension = extension.toLowerCase();

                    CssLayout imageWrapper = componentsFactory.createComponent(CssLayout.class);
                    imageWrapper.setId(rcgFeedbackAttachment.getId().toString());
                    imageWrapper.setStyleName("rf-attachment-w");

                    Image image = componentsFactory.createComponent(Image.class);
                    image.setStyleName("rf-attachment-f");
                    image.setWidth("50px");
                    image.setHeight("50px");
                    image.setScaleMode(Image.ScaleMode.SCALE_DOWN);

                    if (extension.matches("png|jpg|jpeg")) {
                        image.setSource(FileDescriptorResource.class).setFileDescriptor(fd);
                    } else {
                        image.setSource(ThemeResource.class).setPath("images/video-icon.png");
                    }
                    imageWrapper.add(image);

                    LinkButton removeButton = componentsFactory.createComponent(LinkButton.class);
                    removeButton.setStyleName("rf-attachment-r");
                    removeButton.setCaption("");
                    removeButton.setIcon("font-icon:CLOSE");
                    removeButton.setAction(new BaseAction("remove-action") {
                        @Override
                        public void actionPerform(Component component) {
                            showOptionDialog("Подтверждение",
                                    "Вы действительно хотите удалить вложение?",
                                    MessageType.CONFIRMATION,
                                    new Action[]{
                                            new DialogAction(DialogAction.Type.YES) {
                                                @Override
                                                public void actionPerform(Component component) {
                                                    attachmentsDs.removeItem(rcgFeedbackAttachment);

                                                    Component findImageWrapper = attachmentsFlowBox.getComponent(rcgFeedbackAttachment.getId().toString());
                                                    if (findImageWrapper != null) {
                                                        attachmentsFlowBox.remove(findImageWrapper);
                                                    }
                                                }
                                            },
                                            new DialogAction(DialogAction.Type.NO)
                                    });
                        }
                    });
                    imageWrapper.add(removeButton);
                    attachmentsFlowBox.add(imageWrapper);
                }
            }
        }
    }

    private void initPageCaption() {
        setCaption(getMessage(getItem().getDirection().equals(RcgFeedbackDirection.SEND) ? "rf.send.caption" : "rf.request.caption"));

        PersonGroupExt personGroupExt = getItem().getReceiver();
        if (personGroupExt != null) {
            PersonExt personExt = personGroupExt.getPerson();

            String fullName = personExt.getFirstNameLatin() + " " + personExt.getLastNameLatin();
            if (userSession.getLocale().getLanguage().equalsIgnoreCase("ru")) {
                fullName = personExt.getFirstName() + " " + personExt.getLastName();
            }

            setCaption(getCaption() + " " + fullName);
            receiverBlock.setVisible(false);

            fillPersonData();
        }
    }

    private void fillPersonData() {
        PersonGroupExt personGroupExt = getItem().getReceiver();
        if (personGroupExt != null) {
            AssignmentExt assignmentExt = getAssignmentExt(personGroupExt.getId());
            if (assignmentExt == null) {
                throw new NullPointerException(getMessage("user.person.assignment.null"));
            }
            OrganizationGroupExt organizationGroup = assignmentExt.getOrganizationGroup();
            if (organizationGroup != null) {
                organizationField.setValue(organizationGroup.getOrganizationName());
            }

            PositionGroupExt positionGroupExt = assignmentExt.getPositionGroup();
            if (positionGroupExt != null) {
                positionField.setValue(positionGroupExt.getPositionName());
            }

            organization.setVisible(true);
        } else {
            organization.setVisible(false);
            organizationField.setValue(null);
            positionField.setValue(null);
        }
    }

    private void initFeedbackTypes() {
        if (getItem().getDirection().equals(RcgFeedbackDirection.SEND)) {
            rfTypeBlock.setVisible(true);
            attachmentsBox.setVisible(true);

            List<DicRcgFeedbackType> feedbackTypes = commonService.getEntities(DicRcgFeedbackType.class,
                    "select e from tsadv$DicRcgFeedbackType e order by e.order",
                    null,
                    "dicRcgFeedbackType.edit");

            if (CollectionUtils.isEmpty(feedbackTypes)) {
                throw new RuntimeException(getMessage("rf.types.null"));
            }

            boolean isEdit = !PersistenceHelper.isNew(getItem());

            for (DicRcgFeedbackType feedbackType : feedbackTypes) {
                HBoxLayout wrapper = componentsFactory.createComponent(HBoxLayout.class);
                wrapper.setId(feedbackType.getId().toString());
                wrapper.setStyleName("rf-type-w");
                wrapper.setSpacing(true);
                wrapper.setWidthFull();

                if (isEdit) {
                    if (feedbackType.equals(getItem().getType())) {
                        wrapper.addStyleName("rf-type-active");
                    }
                }

                wrapper.addLayoutClickListener(event -> {
                    DicRcgFeedbackType selectedFeedbackType = getItem().getType();
                    if (selectedFeedbackType != null) {
                        typeBlock.getComponentNN(selectedFeedbackType.getId().toString()).removeStyleName("rf-type-active");
                    }

                    wrapper.addStyleName("rf-type-active");
                    getItem().setType(feedbackType);
                });

                Image image = componentsFactory.createComponent(Image.class);
                image.setWidth("24px");
                image.setHeight("24px");
                image.setAlignment(Alignment.MIDDLE_CENTER);
                image.setSource(FileDescriptorResource.class).setFileDescriptor(feedbackType.getImage());
                image.setScaleMode(Image.ScaleMode.SCALE_DOWN);
                wrapper.add(image);

                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(feedbackType.getLangValue());
                label.setAlignment(Alignment.MIDDLE_LEFT);
                wrapper.add(label);
                wrapper.expand(label);
                typeBlock.add(wrapper);
            }
        }
    }

    private void initListeners() {
        comment.setTextChangeEventMode(TextInputField.TextChangeEventMode.EAGER);
        comment.addTextChangeListener(event -> {
            String text = event.getText();
            boolean valid = false;
            if (text != null) {
                int textLength = text.replaceAll("([\\s\\S])\\1{3,}", "$1$1$1").length();

                valid = textLength >= 50;

                commentMinLengthLabel.setValue(String.format(getMessage("rf.comment.length"), textLength));

                if (text.equalsIgnoreCase("")) {
                    comment.setValue(null);
                    commentMinLengthLabel.setValue(defaultCommentMinLengthMessage);
                }
            }
            comment.setStyleName("valid-fill-" + valid);
        });
    }

    private void initDialogResize() {
        Page.getCurrent().addBrowserWindowResizeListener((Page.BrowserWindowResizeListener)
                event -> resizeWindow(event.getWidth()));

        getDialogOptions().setWidth(calculateDialogWidth() + "%");

        if (getItem().getDirection().equals(RcgFeedbackDirection.REQUEST)) {
            getDialogOptions().setHeight("450px");
        }
    }

    private int calculateDialogWidth() {
        int browseWidth = Page.getCurrent().getBrowserWindowWidth();
        int width;
        if (browseWidth < 768) {
            width = 95;
        } else if (browseWidth > 768 && browseWidth < 962) {
            width = 70;
        } else {
            width = 40;
        }
        return width;
    }

    private void resizeWindow(int browseWidth) {
        int width = calculateDialogWidth();

        getDialogOptions().setWidth(width + "%");
        int x = (browseWidth - browseWidth * width / 100) / 2;

        getDialogOptions().setPositionX(x);
    }

    private AssignmentExt getAssignmentExt(UUID personGroupId) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        LoadContext.Query loadContextQuery = LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where e.personGroup.id = :pgId " +
                        "  and e.primaryFlag = true " +
                        "and :systemDate between e.startDate and e.endDate");
        loadContextQuery.setParameter("pgId", personGroupId);
        loadContextQuery.setParameter("systemDate", CommonUtils.getSystemDate());
        loadContext.setQuery(loadContextQuery);
        loadContext.setView("assignment.rcg.org-pos");
        return dataManager.load(loadContext);
    }
}