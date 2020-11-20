package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.mainwindow.AppWorkArea;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.theme.ThemeConstantsRepository;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.security.app.UserManagementService;
import com.haulmont.cuba.security.app.UserTimeZone;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.app.UserSettingsTools;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.ComboBox;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dto.LinkedinProfileDTO;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.LinkedinService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URISyntaxException;
import java.util.*;

import static com.haulmont.cuba.web.security.ExternalUserCredentials.EXTERNAL_AUTH_USER_SESSION_ATTRIBUTE;


public class StandartSettings extends AbstractFrame {

    protected boolean changeThemeEnabled = true;
    protected String msgTabbed;
    protected String msgSingle;
    protected CommonService commonService = AppBeans.get(CommonService.class);

    protected boolean isLinkedinAccessTokenValidAndNotExpired;
    protected PersonGroupExt personGroup;

    protected UserExt userExt;

    private final Logger log = LoggerFactory.getLogger(StandartSettings.class);

    @Inject
    protected FileUploadingAPI fileUploadingAPI;

    @Inject
    protected DataSupplier dataSupplier;

    @Inject
    protected UserSettingsTools userSettingsTools;

    @Inject
    protected UserSession userSession;

    @Inject
    protected UserManagementService userManagementService;

    @Inject
    protected ClientConfig clientConfig;

    @Inject
    protected GlobalConfig globalConfig;

    @Inject
    protected TimeZones timeZones;

    @Inject
    protected Button okBtn;

    @Inject
    protected TextField telegramCodeField;

    @Inject
    protected Button cancelBtn;

    @Inject
    protected Button changePasswordBtn;

    @Inject
    protected OptionsGroup modeOptions;

    @Inject
    protected LookupField<Object> appThemeField;

    @Inject
    protected LookupField<Object> timeZoneLookup;

    @Inject
    protected LookupField<Object> appLangField;

    @Inject
    protected CheckBox timeZoneAutoField;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected LinkButton linkedinButton;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Image userImage;
    @Inject
    protected FileUploadField imageUpload;
    @Inject
    protected LinkedinService linkedinService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    private VBoxLayout buttons;
    @Inject
    private TabSheet tabSheet;
    @Named("bpmUserSubstitutionsTable.create")
    private CreateAction bpmUserSubstitutionsTableCreate;
    @Named("bpmUserSubstitutionsTable.edit")
    private EditAction bpmUserSubstitutionsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        tabSheet.addSelectedTabChangeListener(event -> buttons.setVisible(!event.getSelectedTab().getName().equals("bpmUserSubstitution")));
        bpmUserSubstitutionsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("user", userSession.getUser()));

        bpmUserSubstitutionsTableCreate.setWindowParamsSupplier(() -> ParamsMap.of("isUserEditable", false));
        bpmUserSubstitutionsTableEdit.setWindowParamsSupplier(() -> ParamsMap.of("isUserEditable", false));

        initSettings(params);

        UUID userExtId = userSession.getAttribute(StaticVariable.USER_EXT_ID);

        userExt = dataManager.load(LoadContext.create(UserExt.class).setId(userExtId).setView("user.edit"));

        getFreshPersonGroup();
        telegramCodeField.setValue(userExt.getTelegramCode());

        FileDescriptor userImageFile = userExt.getImage();
        if (userImageFile == null) {
            userImage.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
        } else {
            userImage.setSource(FileDescriptorResource.class).setFileDescriptor(userImageFile);
        }

        initImageUpload();
        initLinkedinButton();
    }

    protected PersonGroupExt getFreshPersonGroup() {
        UUID personGroupId = ((PersonGroupExt) userSession.getAttribute(StaticVariable.USER_PERSON_GROUP)).getId();
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroupId);
        this.personGroup = commonService.getEntity(PersonGroupExt.class, "select e" +
                        "                          from base$PersonGroupExt e" +
                        "                          where e.id = :personGroupId",
                params, "personGroup.linkedin");
        return personGroup;
    }

    protected void initLinkedinButton() {
        if (checkAccessTokenValidity()) {
            linkedinButton.setIcon("images/linkedin/color-no-border-30px.png");
        }
    }

    protected boolean checkAccessTokenValidity() {
        isLinkedinAccessTokenValidAndNotExpired = StringUtils.isNotEmpty(personGroup.getLinkedinAccessToken()) && personGroup.getLinkedinTokenExpiresInDate().after(new Date());
        return isLinkedinAccessTokenValidAndNotExpired;
    }

    public void linkedinButtonAction() {
        if (!checkAccessTokenValidity()) {
            linkLinkedIn();
        }

        showOptionDialog(
                "",
                getMessage("fillProfileFromLinkedin.confirmation"),
                MessageType.CONFIRMATION.modal(true).closeOnClickOutside(true),
                new Action[]{
                        new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> fillProfileFromLinkedin()),
                        new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
                }
        );
    }

    protected void fillProfileFromLinkedin() {
        try {
            LinkedinProfileDTO linkedinProfileDTO = linkedinService.getFilledProfileDTO(getFreshPersonGroup());
            byte[] userPhoto = linkedinProfileDTO.getPhoto();
//            employeeService.getPersonGroupByUserId(userExt.getId()).getPerson().setPhoto(userPhoto); //TODO:personGroup need to test
            //Utils.getPersonImageEmbedded(employeeService.getPersonGroupByUserId(userExt.getId()).getPerson(), null, userImage); //TODO:personGroup need to test
            showNotification(getMessage("profileFromLinkedinIsFilled"), NotificationType.HUMANIZED);
        } catch (Exception e) {
            showNotification(e.getMessage(), NotificationType.ERROR);
            e.printStackTrace();
        }
    }

    protected void linkLinkedIn() {
        try {
            String url = linkedinService.buildOauthLink(personGroup.getId());
            showWebPage(url, ParamsMap.of("target", "_blank"));
        } catch (URISyntaxException e) {
            showNotification(getMessage(e.getMessage()), NotificationType.ERROR);
            e.printStackTrace();
        }
        initLinkedinButton();
        if (isLinkedinAccessTokenValidAndNotExpired) {
            showNotification(getMessage("linkedinLinked"), NotificationType.HUMANIZED_HTML);
        }
    }

    protected void initImageUpload() {
        imageUpload.setUploadButtonCaption("");
        imageUpload.setClearButtonCaption("");
        imageUpload.addFileUploadSucceedListener(event -> {
            if (userSession != null) {
                FileDescriptor fd = imageUpload.getFileDescriptor();
                try {
                    fileUploadingAPI.putFileIntoStorage(imageUpload.getFileId(), fd);
                } catch (FileStorageException e) {
                    throw new RuntimeException("Error saving file to FileStorage", e);
                }

                FileDescriptor committedImage = dataSupplier.commit(fd);
                userImage.setSource(FileDescriptorResource.class).setFileDescriptor(committedImage);

                userExt.setImage(committedImage);
            }
            imageUpload.setValue(null);
        });

        imageUpload.addBeforeValueClearListener(beforeEvent -> {
            beforeEvent.preventClearAction();
            showOptionDialog("", "Are you sure you want to delete this photo?", MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY) {
                                public void actionPerform(Component component) {
                                    FileDescriptor currentImage = userExt.getImage();
                                    if (currentImage != null) {
                                        try {
                                            fileUploadingAPI.deleteFile(currentImage.getId());
                                        } catch (FileStorageException e) {
                                            e.printStackTrace();
                                        }

                                        userExt.setImage(null);
                                    }

                                    userImage.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
                                }
                            },
                            new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
                    });
        });
    }


    protected void initSettings(Map<String, Object> params) {
        Boolean changeThemeEnabledParam = (Boolean) params.get("changeThemeEnabled");
        if (changeThemeEnabledParam != null) {
            changeThemeEnabled = changeThemeEnabledParam;
        }

        AppWorkArea.Mode mode = userSettingsTools.loadAppWindowMode();
        msgTabbed = getMessage("modeTabbed");
        msgSingle = getMessage("modeSingle");

        modeOptions.setOptionsList(Arrays.asList(msgTabbed, msgSingle));
        if (mode == AppWorkArea.Mode.TABBED)
            modeOptions.setValue(msgTabbed);
        else
            modeOptions.setValue(msgSingle);

        ThemeConstantsRepository themeRepository = AppBeans.get(ThemeConstantsRepository.NAME);
        Set<String> supportedThemes = themeRepository.getAvailableThemes();
        appThemeField.setOptionsList(new ArrayList<>(supportedThemes));

        String userAppTheme = userSettingsTools.loadAppWindowTheme();
        appThemeField.setValue(userAppTheme);

        ComboBox vAppThemeField = (ComboBox) WebComponentsHelper.unwrap(appThemeField);
        vAppThemeField.setTextInputAllowed(false);
        appThemeField.setEditable(changeThemeEnabled);

        initTimeZoneFields();

        User user = userSession.getUser();
        changePasswordBtn.setAction(new BaseAction("changePassw")
                .withCaption(getMessage("changePassw"))
                .withHandler(event -> {
                    Window passwordDialog = openWindow("sec$User.changePassword", WindowManager.OpenType.DIALOG,
                            ParamsMap.of("currentPasswordRequired", true));
                    passwordDialog.addCloseListener(actionId -> {
                        // move focus back to window
                        changePasswordBtn.requestFocus();
                    });
                }));

        if (!user.equals(userSession.getCurrentOrSubstitutedUser())
                || Boolean.TRUE.equals(userSession.getAttribute(EXTERNAL_AUTH_USER_SESSION_ATTRIBUTE))) {
            changePasswordBtn.setEnabled(false);
        }

        Map<String, Locale> locales = globalConfig.getAvailableLocales();
        TreeMap<String, Object> options = new TreeMap<>();
        for (Map.Entry<String, Locale> entry : locales.entrySet()) {
            options.put(entry.getKey(), messages.getTools().localeToString(entry.getValue()));
        }
        appLangField.setOptionsMap(options);
        appLangField.setValue(userManagementService.loadOwnLocale());

        Action commitAction = new BaseAction("commit")
                .withCaption(messages.getMainMessage("actions.Ok"))
                .withShortcut(clientConfig.getCommitShortcut())
                .withHandler(event ->
                        commit()
                );
        addAction(commitAction);
        okBtn.setAction(commitAction);

        cancelBtn.setAction(new BaseAction("cancel")
                .withCaption(messages.getMainMessage("actions.Cancel"))
                .withHandler(event ->
                        cancel()
                ));
    }

    protected void commit() {
        if (changeThemeEnabled) {
            String selectedTheme = (String) appThemeField.getValue();
            userSettingsTools.saveAppWindowTheme(selectedTheme);
            App.getInstance().setUserAppTheme(selectedTheme);
        }
        AppWorkArea.Mode m = modeOptions.getValue() == msgTabbed ? AppWorkArea.Mode.TABBED : AppWorkArea.Mode.SINGLE;
        userSettingsTools.saveAppWindowMode(m);

        setTimeZoneSettings();
        setLocaleSettings();
        userExt = dataManager.commit(userExt);
        showNotification(getMessage("modeChangeNotification"), NotificationType.HUMANIZED);

//        CommitContext commitContext = new CommitContext();
//        commitContext.addInstanceToCommit(userExt);
        //TODO: LinkedIn commitContext.addInstanceToCommit(employeeService.getPersonGroupByUserId(userExt.getId()).getPerson());
    }

    protected void cancel() {
        initSettings(Collections.emptyMap());
    }

    protected void initTimeZoneFields() {
        Map<String, Object> options = new TreeMap<>();
        for (String id : TimeZone.getAvailableIDs()) {
            TimeZone timeZone = TimeZone.getTimeZone(id);
            options.put(timeZones.getDisplayNameLong(timeZone), id);
        }
        timeZoneLookup.setOptionsMap(options);

        timeZoneAutoField.setCaption(messages.getMainMessage("timeZone.auto"));
        timeZoneAutoField.setDescription(messages.getMainMessage("timeZone.auto.descr"));
        timeZoneAutoField.addValueChangeListener(e -> timeZoneLookup.setEnabled(!Boolean.TRUE.equals(e.getValue())));

        UserTimeZone userTimeZone = userManagementService.loadOwnTimeZone();
        timeZoneLookup.setValue(userTimeZone.name);
        timeZoneAutoField.setValue(userTimeZone.auto);
    }

    protected void setTimeZoneSettings() {
        UserTimeZone userTimeZone = new UserTimeZone((String) timeZoneLookup.getValue(), timeZoneAutoField.getValue());
        userExt.setTimeZone(userTimeZone.name);
        userExt.setTimeZoneAuto(userTimeZone.auto);
        log.debug("Saving user's time zone settings: " + userTimeZone);
//        userManagementService.saveOwnTimeZone(userTimeZone);
    }

    protected void setLocaleSettings() {
        String userLocale = (String) appLangField.getValue();
        userExt.setLanguage(userLocale);
        log.debug("Saving user's {} language settings: {}", userExt.getId(), userLocale);
//        String userLocale = appLangField.getValue();
//        userManagementService.saveOwnLocale(userLocale);
    }

    public void generateCode() {
        String telegramCode;
        Random random = new Random();
        int num = 100000 + random.nextInt(999999 - 100000);
        telegramCode = String.valueOf(num);
        userExt.setTelegramCode(telegramCode);
        telegramCodeField.setValue(userExt.getTelegramCode());
    }


}