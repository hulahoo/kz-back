package kz.uco.tsadv.web.modules.recognition.entity.recognition;


import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.vaadin.server.Page;
import com.vaadin.ui.JavaScript;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.GoogleTranslateService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.*;
import kz.uco.tsadv.modules.recognition.dictionary.DicQuality;
import kz.uco.tsadv.modules.recognition.dictionary.DicRecognitionType;
import kz.uco.tsadv.modules.recognition.exceptions.NotEnoughPointsException;
import kz.uco.tsadv.service.RecognitionService;
import kz.uco.tsadv.web.modules.recognition.RecognitionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class RecognitionDialogEdit extends AbstractEditor<Recognition> {

    private static final Logger logger = LoggerFactory.getLogger(RecognitionDialogEdit.class);

    protected RecognitionHelper recognitionHelper = new RecognitionHelper();

    @Inject
    private GlobalConfig globalConfig;
    @Inject
    private GoogleTranslateService googleTranslateService;
    @Inject
    private SuggestionPickerField suggestionPickerField;
    @Inject
    private CollectionDatasource<RecognitionQuality, UUID> recognitionQualitiesDs;
    @Inject
    private UserSession userSession;
    @Inject
    private Label commentMinLength;
    @Inject
    private FlowBoxLayout qualitiesBox;
    @Inject
    private ResizableTextArea comment;
    @Inject
    private Label rcgTypeLabel;
    @Inject
    private HBoxLayout organization;
    @Inject
    private VBoxLayout qualitiesBlock;
    @Inject
    private Label errorLabel;
    @Inject
    private TextField organizationField;
    @Inject
    private TextField positionField;
    private PersonGroupExt currentPersonGroup;

    @Inject
    private BeanValidation beanValidation;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private DataManager dataManager;

    @Inject
    private HBoxLayout rcgTypeWrapper;

    @Inject
    private Label rcgTypeCoin;

    @Inject
    private VBoxLayout receiverBlock;

    @Inject
    private Metadata metadata;
    private Set<DicQuality> selectedQualities = new HashSet<>();
    private Long personPoints = 0L;

    @Inject
    private RecognitionService recognitionService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initDialogResize();

        rcgTypeLabel.setValue("<span class=\"rcg-gt-title\">" + getMessage("rcg.type") + "</span>");

        suggestionPickerField.addClearAction();
        suggestionPickerField.removeAction(PickerField.ActionType.LOOKUP.getId());
        suggestionPickerField.removeAction(PickerField.ActionType.OPEN.getId());

        currentPersonGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);

        initRecognitionTypes();
        initQualities();
    }

    private void initDialogResize() {
        Page.getCurrent().addBrowserWindowResizeListener((Page.BrowserWindowResizeListener)
                event -> resizeWindow(event.getWidth()));

        getDialogOptions().setWidth(calculateDialogWidth() + "%");
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

    @Override
    protected void postInit() {
        super.postInit();

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
    }

    @Override
    public void ready() {
        super.ready();

        suggestionPickerField.addValueChangeListener(valueChangeEvent -> {
            fillPersonData();
        });

        loadPersonPoints();
        checkFillComment();

        JavaScript.eval("initTooltip()");
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
                recognitionHelper.showNotificationWindow(
                        String.format(getMessage("rcg.send.success"),
                                getItem().getReceiver().getFirstLastName()),
                        "images/recognition/gt-success.png");
            }
        } catch (Exception ex) {
            showNotification(ex.getMessage());
        }
        return success;
    }

    private void loadPersonPoints() {
        PersonPoint personPoint = recognitionService.loadPersonPoint(currentPersonGroup.getId());
        if (personPoint == null) {
            showNotification(getMessage("msg.warning.title"),
                    getMessage("wallet.points.not.found"),
                    NotificationType.ERROR);
        } else {
            personPoints = personPoint.getPoints();
        }
    }

    private void checkFillComment() {
        comment.setTextChangeEventMode(TextInputField.TextChangeEventMode.EAGER);
        comment.addTextChangeListener(event -> {
            String text = event.getText();
            boolean valid = false;
            if (text != null) {
                if (text.length() >= 100 && text.replaceAll("([\\s\\S])\\1{3,}", "$1$1$1").length() >= 100) {
                    valid = true;
                }

                commentMinLength.setValue(String.format(getMessage("comment.length"), text.replaceAll("([\\s\\S])\\1{3,}", "$1$1$1").length()));

                if (text.equalsIgnoreCase("")) {
                    comment.setValue(null);
                    commentMinLength.setValue(getMessage("comment.min.length"));
                }
            }
            comment.setStyleName("valid-fill-" + valid);
        });
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

    private void initRecognitionTypes() {
        List<DicRecognitionType> recognitionTypes = loadRecognitionTypes();
        if (!recognitionTypes.isEmpty()) {

            for (DicRecognitionType recognitionType : recognitionTypes) {
                VBoxLayout typeItem = componentsFactory.createComponent(VBoxLayout.class);
                typeItem.setSpacing(true);
                typeItem.setStyleName("rcg-gt-type-wrapper");
                typeItem.setAlignment(Alignment.MIDDLE_CENTER);

                Image image = createImage(recognitionType.getSticker());
                image.setAlignment(Alignment.MIDDLE_CENTER);
                image.addStyleName("rcg-gt-type-image");
                typeItem.add(image);

                Label label = createLabel(recognitionType.getLangValue());
                label.setStyleName("rcg-gt-type-name");
                label.setWidthFull();
                label.setAlignment(Alignment.MIDDLE_CENTER);

                typeItem.addLayoutClickListener(layoutClickEvent -> {
                    Collection<Component> ownComponents = rcgTypeWrapper.getOwnComponents();
                    if (ownComponents != null && !ownComponents.isEmpty()) {
                        ownComponents.forEach(c -> c.removeStyleName("rcg-gt-wrapper-active"));
                    }

                    typeItem.addStyleName("rcg-gt-wrapper-active");

                    Long coins = recognitionType.getCoins();
                    rcgTypeCoin.setValue(coins == null ? 0 : coins);
                    getItem().setRecognitionType(recognitionType);
                    getItem().setCoins(coins);

                    try {
                        recognitionService.validatePoints(getItem(), personPoints);
                        errorLabel.setValue(null);
                    } catch (RuntimeException ex) {
                        if (ex instanceof NotEnoughPointsException) {
                            errorLabel.setValue(ex.getMessage());
                        } else {
                            showNotification(ex.getMessage());
                        }
                    }
                });

                typeItem.add(label);
                rcgTypeWrapper.add(typeItem);
            }
        }
    }

    private void initQualities() {
        List<DicQuality> qualities = loadQualities();
        if (!qualities.isEmpty()) {
            int counter = 1;
            for (DicQuality quality : qualities) {
                String orderClass = "rcg-gt-quality-link-order-" + counter;
                LinkButton qualityLink = componentsFactory.createComponent(LinkButton.class);
                qualityLink.addStyleName("rcg-gt-quality-link rcg-tooltip " + orderClass);
                qualityLink.setCaption(quality.getLangValue());
                qualityLink.setAction(new BaseAction("quality-action") {
                    @Override
                    public void actionPerform(Component component) {
                        boolean contains = selectedQualities.contains(quality);
                        if (contains) {
                            qualityLink.removeStyleName("rcg-gt-quality-link-active");
                            selectedQualities.remove(quality);
                        } else {
                            selectedQualities.add(quality);
                            qualityLink.addStyleName("rcg-gt-quality-link-active");
                        }
                    }
                });

                qualitiesBox.add(qualityLink);
                counter++;

                JavaScript.eval("$('." + orderClass + "').attr({" +
                        "'title':'" + quality.getFullLangValue() + "'," +
                        "'data-tippy-placement':'top'," +
                        "'data-tippy-flip':'false'});");
            }
        }
    }

    @Override
    protected void initNewItem(Recognition item) {
        super.initNewItem(item);

        item.setAuthor(currentPersonGroup);
        item.setRecognitionDate(new Date());
        item.setNotifyManager(false);
        item.setCoins(0L);
    }

    @Override
    protected boolean preCommit() {
        recognitionQualitiesDs.clear();

        if (!selectedQualities.isEmpty()) {
            Recognition recognition = getItem();
            for (DicQuality quality : selectedQualities) {
                RecognitionQuality recognitionQuality = metadata.create(RecognitionQuality.class);
                recognitionQuality.setRecognition(recognition);
                recognitionQuality.setQuality(quality);
                recognitionQualitiesDs.addItem(recognitionQuality);
            }
        }
        return super.preCommit();
    }

    @Override
    public void commitAndClose() {
        try {
            errorLabel.setValue(null);
            super.commitAndClose();
        } catch (NotEnoughPointsException notEnoughPointsException) {
            errorLabel.setValue(getMessage("wallet.points.less"));
        } catch (Exception ex) {
            showNotification(getMessage("msg.error.title"),
                    ex.getMessage(),
                    NotificationType.TRAY);
        }
    }

    private Label createLabel(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }

    public Image createImage(FileDescriptor fileDescriptor) {
        Image image = componentsFactory.createComponent(Image.class);
        image.setScaleMode(Image.ScaleMode.FILL);

        if (fileDescriptor == null) {
            image.setSource(ThemeResource.class).setPath("images/recognition/default-rcg-type.png");
        } else {
            image.setSource(FileDescriptorResource.class).setFileDescriptor(fileDescriptor);
        }
        return image;
    }

    private List<DicRecognitionType> loadRecognitionTypes() {
        LoadContext<DicRecognitionType> loadContext = LoadContext.create(DicRecognitionType.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$DicRecognitionType e order by e.order"));
        loadContext.setView("dicRecognitionType.edit");
        return dataManager.loadList(loadContext);
    }

    private List<DicQuality> loadQualities() {
        LoadContext<DicQuality> loadContext = LoadContext.create(DicQuality.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$DicQuality e order by e.order"));
        loadContext.setView(View.LOCAL);
        return dataManager.loadList(loadContext);
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