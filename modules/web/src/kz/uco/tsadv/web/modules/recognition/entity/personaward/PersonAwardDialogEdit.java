package kz.uco.tsadv.web.modules.recognition.entity.personaward;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.data.DataGridItems;
import com.haulmont.cuba.security.global.UserSession;
import com.vaadin.server.Page;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.RecognitionConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.recognition.PersonAward;
import kz.uco.tsadv.modules.recognition.dictionary.DicPersonAwardType;
import kz.uco.tsadv.modules.recognition.enums.AwardStatus;
import kz.uco.tsadv.service.RecognitionService;
import kz.uco.tsadv.web.modules.recognition.RecognitionHelper;
import kz.uco.tsadv.web.toolkit.ui.fontratestarscomponent.FontRateStarsComponent;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PersonAwardDialogEdit extends AbstractEditor<PersonAward> {

    protected RecognitionHelper recognitionHelper = new RecognitionHelper();

    @Inject
    protected LinkButton close, save;
    @Inject
    protected VBoxLayout instruction;
    @Inject
    protected TextField nomineeNameLabel;
    @Inject
    protected Label awardRules;
    @Inject
    protected LinkButton awardRulesToggle;
    @Inject
    protected Label historyMinLengthLabel;
    @Inject
    protected Label whyMinLengthLabel;
    @Inject
    protected TextField organizationField;
    @Inject
    protected ResizableTextArea historyTextArea;
    @Inject
    protected ResizableTextArea whyTextArea;
    @Inject
    protected TextField positionField;
    @Inject
    private SuggestionPickerField<PersonGroupExt> suggestionPickerField;
    @Inject
    protected HBoxLayout organizationPositionWrapper;
    @Inject
    protected VBoxLayout receiverBlock;
    @Inject
    protected UserSession userSession;
    @Inject
    protected LinkButton saveDraft;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected MessageTools messageTools;
    @Inject
    protected CommonService commonService;
    protected PersonGroupExt currentPersonGroup;
    @Inject
    protected LinkButton windowCommit;
    @Inject
    protected RecognitionService recognitionService;
    @Inject
    private RecognitionConfig recognitionConfig;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initDialogResize();

        awardRulesToggle.setCaption("");
        awardRulesToggle.setAction(new BaseAction("award-rules-toggle") {
            @Override
            public void actionPerform(Component component) {
                boolean isVisible = !awardRules.isVisible();
                awardRules.setVisible(isVisible);
                awardRulesToggle.setIcon(isVisible ? "icons/up.png" : "icons/down.png");
            }
        });

        currentPersonGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);

        initSuggestionField();

        initButtonsAction();
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

    protected void initSuggestionField() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -recognitionConfig.getCompareDate());

        final Date compareDate = calendar.getTime();
        LoadContext<PersonGroupExt> loadContext = recognitionService.getSuggestionSearchLC();

        suggestionPickerField.setSearchExecutor(new SuggestionField.ParametrizedSearchExecutor<PersonGroupExt>() {
            @Override
            public List<PersonGroupExt> search(String searchString, Map<String, Object> searchParams) {
                LoadContext.Query query = loadContext.getQuery();
                query.setParameter("searchString", "%" + searchString + "%");

                if (searchParams != null && !searchParams.isEmpty()) {
                    query.setParameters(searchParams);
                }
                return dataManager.loadList(loadContext);
            }

            @Override
            public Map<String, Object> getParams() {
                return ParamsMap.of("compareDate", compareDate);
            }
        });

        suggestionPickerField.addClearAction();
        suggestionPickerField.removeAction(PickerField.ActionType.LOOKUP.getId());
        suggestionPickerField.removeAction(PickerField.ActionType.OPEN.getId());
    }

    protected void initButtonsAction() {
        saveDraft.setAction(new BaseAction("draft") {
            @Override
            public void actionPerform(Component component) {
                getItem().setStatus(AwardStatus.DRAFT);
                if (validateAll()) {
                    commitAndClose();
                }
            }
        });

        windowCommit.setAction(new BaseAction("commit") {
            @Override
            public void actionPerform(Component component) {
                getItem().setStatus(AwardStatus.NOMINATED);
                if (validateAll()) {
                    commitAndClose();
                }
            }
        });
    }

    @Override
    protected void initNewItem(PersonAward item) {
        super.initNewItem(item);

        if (item.getAuthor() == null) {
            item.setAuthor(currentPersonGroup);
        }

        item.setStatus(AwardStatus.NOMINATED);
        item.setDate(new Date());

        DicPersonAwardType awardType = commonService.getEntity(DicPersonAwardType.class, "HEART_AWARD");
        if (awardType == null) {
            showNotification(getMessage("msg.warning.title"),
                    getMessage("award.type.ha.null"),
                    NotificationType.ERROR);
        }
        item.setType(awardType);
    }

    @Override
    protected void postInit() {
        super.postInit();

        try {
            getItem().setAwardProgram(recognitionService.getActiveAwardProgram());
        } catch (Exception ex) {
            showNotification(getMessage("msg.warning.title"),
                    ex.getMessage(),
                    NotificationType.TRAY);
        }
    }

    @Override
    public void ready() {
        super.ready();

        initSuggestionPickerListener();

        PersonGroupExt personGroupExt = getItem().getReceiver();
        if (personGroupExt != null) {
            setCaption(String.format(getMessage("editorCaption2"), personGroupExt.getFullName()));
            receiverBlock.setVisible(false);
        }

        fillPersonData();
        checkFillHistoryWhy();
    }

    protected void initSuggestionPickerListener() {
        suggestionPickerField.addValueChangeListener(personGroupExtValueChangeEvent -> {
            if (PersistenceHelper.isNew(getItem())) {
                PersonGroupExt receiverExt = getItem().getReceiver();
                if (receiverExt != null) {
                    PersonAward draftPersonAward = recognitionService.findDraftPersonAward(receiverExt.getId());
                    if (draftPersonAward != null) {
                        setItem(draftPersonAward);
                    }
                }
            }

            fillPersonData();
        });
    }

    private void checkFillHistoryWhy() {
        whyTextArea.setTextChangeEventMode(TextInputField.TextChangeEventMode.EAGER);
        whyTextArea.addTextChangeListener(event -> validateTextArea(
                whyTextArea,
                event.getText(),
                whyMinLengthLabel,
                getMessage("why.min.length"),
                100));

        historyTextArea.setTextChangeEventMode(TextInputField.TextChangeEventMode.EAGER);
        historyTextArea.addTextChangeListener(event -> validateTextArea(
                historyTextArea,
                event.getText(),
                historyMinLengthLabel,
                getMessage("history.min.length"),
                300));

        initValidateTextArea();
    }

    private void initValidateTextArea() {
        validateTextArea(
                whyTextArea,
                getItem().getWhy(),
                whyMinLengthLabel,
                getMessage("why.min.length"),
                100);

        validateTextArea(
                historyTextArea,
                getItem().getHistory(),
                historyMinLengthLabel,
                getMessage("history.min.length"),
                300);
    }

    private void validateTextArea(ResizableTextArea textArea, String text, Label label, String defaultMessage, int minLength) {
        boolean valid = false;
        if (text != null) {
            if (text.length() >= minLength) {
                valid = true;
            }

            label.setValue(String.format(getMessage("comment.length"), text.length()));

            if (text.equalsIgnoreCase("")) {
                textArea.setValue(null);
                label.setValue(defaultMessage);
            }
        } else {
            label.setValue(defaultMessage);
        }
        textArea.setStyleName("valid-fill-" + valid);
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
        if (committed) {
            PersonAward personAward = getItem();
            if (personAward.getStatus().equals(AwardStatus.DRAFT)) {
                recognitionHelper.showNotificationWithTitle(
                        String.format(getMessage("award.draft.save.title"), personAward.getReceiver().getFirstLastName()),
                        getMessage("award.draft.save.body"));
            } else {
                recognitionHelper.showNotificationWindow(
                        String.format(getMessage("award.save.title"), personAward.getReceiver().getFirstLastName()),
                        "images/recognition/n-success.png");
            }
        }
        return super.postCommit(committed, close);
    }

    private void fillPersonData() {
        PersonGroupExt personGroupExt = getItem().getReceiver();
        if (personGroupExt != null) {
            organizationPositionWrapper.setVisible(true);

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
        } else {
            organizationField.setValue(null);
            positionField.setValue(null);

            organizationPositionWrapper.setVisible(false);

            getItem().setWhy(null);
            getItem().setHistory(null);
        }

        initValidateTextArea();
    }

    private AssignmentExt getAssignmentExt(UUID personGroupId) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        LoadContext.Query loadContextQuery = LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where e.personGroup.id = :pgId " +
                        "and :systemDate between e.startDate and e.endDate and e.primaryFlag = True");
        loadContextQuery.setParameter("pgId", personGroupId);
        loadContextQuery.setParameter("systemDate", CommonUtils.getSystemDate());
        loadContext.setQuery(loadContextQuery);
        loadContext.setView("assignment.rcg.org-pos");
        return dataManager.load(loadContext);
    }
}