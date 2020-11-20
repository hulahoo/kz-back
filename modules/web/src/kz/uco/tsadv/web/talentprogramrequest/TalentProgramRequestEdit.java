package kz.uco.tsadv.web.talentprogramrequest;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.OrganizationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.*;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TalentProgramRequestEdit extends AbstractEditor<TalentProgramRequest> {

    protected TalentProgram talentProgram;

    @Inject
    protected DataManager dataManager;
    @Inject
    protected UserSession userSession;
    @Inject
    protected CheckBox requirementsCheck;
    @Inject
    protected Frame windowActions;
    @Inject
    protected Button rulesButton;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected OrganizationHrUserService organizationHrUserService;
    @Inject
    private Datasource<TalentProgramRequest> talentProgramRequestDs;
    @Inject
    protected OrganizationService organizationService;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected Label charsCount;
    @Inject
    protected ResizableTextArea textArea;
    @Inject
    protected Label lblEssayHat;
    @Inject
    protected Metadata metadata;
    protected Pattern pattern = Pattern.compile("[а-яА-ЯёЁ]");
    protected Button windowCommit;
    protected Button btnSendForApproval;
    @Inject
    protected TextField experienceField;
    @Inject
    protected CollectionDatasource<TalentProgramPersonStep, UUID> talentProgramPersonStepsDs;
    @Inject
    protected Table<TalentProgramStep> tableSteps;
    @Inject
    protected CollectionDatasource<TalentProgramStep, UUID> steps;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        talentProgramRequestDs.setItem((TalentProgramRequest) params.get("talentProgramRequest"));
        requirementsCheck.setValue(Boolean.TRUE);
        talentProgramPersonStepsDs.refresh();
    }

    @Override
    protected void initNewItem(TalentProgramRequest item) {
        PersonGroupExt personGroupExt = dataManager.reload(getSessionPersonGroupExt(), "personGroupExt.for.enrollment.lookup");
        item.setPersonGroup(personGroupExt);
        DicTalentProgramRequestStatus status = commonService.getEntity(DicTalentProgramRequestStatus.class, "DRAFT");
        item.setStatus(status);
        super.initNewItem(item);
    }

    @Nonnull
    protected PersonGroupExt getSessionPersonGroupExt() {
        UUID personGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        if (personGroupId == null) throw new ItemNotFoundException("personNotFound");
        PersonGroupExt personGroupExt = metadata.create(PersonGroupExt.class);
        personGroupExt.setId(personGroupId);
        return personGroupExt;
    }

    @Override
    protected void postInit() {
        super.postInit();
        talentProgram = getItem().getTalentProgram();
        experienceField.setValue(employeeService.getExperienceInCompany(getItem().getPersonGroup().getId(), CommonUtils.getSystemDate()));
        if (getItem().getStatus() == null && !PersistenceHelper.isNew(getItem())) {
            getItem().setStatus(commonService.getEntity(DicTalentProgramRequestStatus.class, "DRAFT"));
            commit();
        }
    }

    @Override
    public void ready() {
        showSaveNotification = false;
        super.ready();

        steps.addItemChangeListener(e -> tableSteps.getActionNN("editPersonStep")
                .setEnabled(getTalentProgramPersonStepByTalentProgramStep(e.getItem()) != null));

        windowCommit = (Button) windowActions.getComponentNN("windowCommit");
        windowCommit.setAction(new BaseAction("commit") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                getItem().setStatus(commonService.getEntity(DicTalentProgramRequestStatus.class, "DRAFT"));
                commitAndClose();
            }
        });
        windowCommit.setCaption(getMessage("save.as.draft"));

        requirementsCheck.addValueChangeListener(e -> btnSendForApproval.setEnabled(Boolean.TRUE.equals(e.getValue())));

        charsCount.setValue(getMessage("existingCounts") + " " + 0 + " " + getMessage("countFinish") + (200));

        textArea.addTextChangeListener(e -> {
            if (e.getText() != null && isHasRussianChars(e.getText())) {
                charsCount.setValue(getMessage("russianWords"));
            } else {
                charsCount.setValue(getWordsMessage(countWords(e.getText())));
            }
        });


        if (getItem().getEssay() != null && isHasRussianChars(getItem().getEssay())) {
            charsCount.setValue(getMessage("russianWords"));
        } else {
            charsCount.setValue(getWordsMessage(countWords(getItem().getEssay())));
        }

        boolean isActiveProgram = getItem().getStatus().getCode().equals("DRAFT")
                && Boolean.TRUE.equals(getItem().getTalentProgram().getIsActive())
                && !getItem().getTalentProgram().getStartDate().after(CommonUtils.getSystemDate())
                && !getItem().getTalentProgram().getEndDate().before(CommonUtils.getSystemDate());

        btnSendForApproval = getBtnSendForApproval();
        ((HBoxLayout) windowActions.getComponentNN(0)).add(btnSendForApproval, 1);

        requirementsCheck.setValue(!isActiveProgram);
        btnSendForApproval.setEnabled(false);
        windowCommit.setEnabled(isActiveProgram);
        textArea.setEditable(isActiveProgram);
        requirementsCheck.setEditable(isActiveProgram);
        lblEssayHat.setValue(talentProgram.getQuestionOfEssay());
        if (isActiveProgram) {
            getItem().setRequestDate(CommonUtils.getSystemDate());
        }

        setBtnStyle();
    }

    protected Button getBtnSendForApproval() {
        Button button = componentsFactory.createComponent(Button.class);
        button.setAction(new BaseAction("sendForApproval") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                onSaveSendForApproval();
            }

            @Override
            public String getCaption() {
                return getMessage("sendForApproval");
            }
        });
        return button;
    }

    protected void onSaveSendForApproval() {
        getItem().setStatus(commonService.getEntity(DicTalentProgramRequestStatus.class, "ON_APPROVAL"));
        commitAndClose();
    }

    protected void setBtnStyle() {
        int index = 1;
        for (Component component : windowActions.getComponents()) {
            if (Button.class.isAssignableFrom(component.getClass())) {
                component.setStyleName("aa-bg-color-" + index++ + " border-none text-color");
                if (index > 6) {
                    index = 1;
                }
            }
        }
    }

    protected String getWordsMessage(int count) {
        if (count > 600) {
            return getMessage("moreThanMaxCount");
        } else if (count <= 200)
            return getMessage("existingCounts") + " " + count + " " + getMessage("countFinish") + (200 - count);
        else
            return getMessage("existingCounts") + " " + count + " " + getMessage("maxCounts") + (600 - count);
    }

    protected int countWords(String text) {
        if (StringUtils.isBlank(text)) return 0;
        int count = 0;

        Pattern cleanSomeSymbolsPattern = Pattern.compile("(['-])");
        Matcher cleanSomeSymbolsMatcher = cleanSomeSymbolsPattern.matcher(text);
        text = cleanSomeSymbolsMatcher.replaceAll("");
        text += " ";

        Pattern notWordPattern = Pattern.compile("([\\s\\!,\\.;\\{\\}\\[\\]\\(\\)\"?])+");
        Matcher notWordMatcher = notWordPattern.matcher(text);
        text = notWordMatcher.replaceAll(" ");
        text += " ";

        Pattern doubleWordPattern = Pattern.compile("\\b(\\w+\\s)\\1{2,}", Pattern.CASE_INSENSITIVE);
        Matcher doubleWordMatcher = doubleWordPattern.matcher(text);
        text = doubleWordMatcher.replaceAll("$1$1");

        count = text.split(" ").length;
        /*
        boolean isSpaceChar = true;
        for (int i = 0; i < text.length(); i++) {
            if (isSpaceChar && !Character.isWhitespace(text.charAt(i))) {
                count++;
            }
            isSpaceChar = Character.isWhitespace(text.charAt(i));
        }*/
        return count;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (!errors.isEmpty() || getItem().getStatus().getCode().equals("DRAFT")) return;
        if (isHasRussianChars(getItem().getEssay())) {
            errors.add(getMessage("russianWords"));
        } else {
            int countWords = countWords(getItem().getEssay());
            if (countWords < 200 || countWords > 600) errors.add(getMessage("range.words"));
        }
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && "ON_APPROVAL".equals(getItem().getStatus().getCode())) {
            showNotification(getMessage("submitted.for.review"));

            Map<String, Object> dictionary = new HashMap<>();
            dictionary.put("adminRoleCode", "TALENT_ADMIN");
            dictionary.put("notificationCode", "talent.request.notification");
            dictionary.put("notificationEmployeeCode", "talent.request.employee.notification");
            dictionary.put("notificationAdminCode", "talent.request.admin.notification");
            notifyManagerAndTalentAdmin(dictionary);
        }
        return super.postCommit(committed, close);
    }

    protected void notifyManagerAndTalentAdmin(Map<String, Object> dictionary) {
        UserExt employee = (UserExt) userSession.getUser();
        List<OrganizationHrUser> hrUser = getHrUsers(dictionary);
        List<UserExt> managerList = employeeService.recursiveFindManagerInActiveOne(userSession.getAttribute(StaticVariable.POSITION_GROUP_ID));

        PersonGroupExt personGroupExt = employeeService.getPersonGroupByUserId(userSession.getUser().getId());
        dictionary.put("fullName", personGroupExt.getFullName());
        dictionary.put("firstNameRu", personGroupExt.getPersonLatinFioWithEmployeeNumber("ru"));
        dictionary.put("firstNameEn", personGroupExt.getPersonLatinFioWithEmployeeNumber("en"));
        dictionary.put("personNameLastNameRu", personGroupExt.getPersonLatinFioWithEmployeeNumber("ru"));
        dictionary.put("personNameLastNameEn", personGroupExt.getPersonLatinFioWithEmployeeNumber("en"));

//        dictionary.put("managerName", personGroupManager.getFirstLastName());
//        dictionary.put("managerNameRu", personGroupManager.getPersonLatinFioWithEmployeeNumber("ru"));
//        dictionary.put("managerNameEn", personGroupManager.getPersonLatinFioWithEmployeeNumber("en"));

        UserExt adminUser = employeeService.getSystemUser();
        ActivityType notification = commonService.getEntity(ActivityType.class, "NOTIFICATION");

        //notificate employee
        notificationService.sendParametrizedNotification(dictionary.get("notificationEmployeeCode").toString(), employee, dictionary);
        activityService.createActivity(
                employee,
                adminUser,
                notification,
                StatusEnum.active,
                "descripiton",
                null,
                new Date(),
                null,
                null,
                null,
                dictionary.get("notificationEmployeeCode").toString(),
                dictionary);

        //notificate manager
        if (managerList != null)
            for (UserExt manager : managerList) {
                PersonGroupExt personManager = employeeService.getPersonGroupByUserId(manager.getId());

                dictionary.put("managerName", Optional.ofNullable(personManager).map(PersonGroupExt::getFirstLastName).orElse(""));
                dictionary.put("managerNameRu", Optional.ofNullable(personManager).map(personGroup -> personGroup.getPersonLatinFioWithEmployeeNumber("ru")).orElse(""));
                dictionary.put("managerNameEn", Optional.ofNullable(personManager).map(personGroup -> personGroup.getPersonLatinFioWithEmployeeNumber("en")).orElse(""));

                notificationService.sendParametrizedNotification(dictionary.get("notificationCode").toString(), manager, dictionary);
                activityService.createActivity(
                        manager,
                        adminUser,
                        notification,
                        StatusEnum.active,
                        "descripiton",
                        null,
                        new Date(),
                        null,
                        null,
                        null,
                        dictionary.get("notificationCode").toString(),
                        dictionary);
            }

        //notificate talent admin
        hrUser.forEach(userToNotify -> {
            UserExt user = userToNotify.getUser();
            notificationService.sendParametrizedNotification(dictionary.get("notificationAdminCode").toString(), user, dictionary);
            activityService.createActivity(
                    user,
                    adminUser,
                    notification,
                    StatusEnum.active,
                    "descripiton",
                    null,
                    new Date(),
                    null,
                    null,
                    null,
                    dictionary.get("notificationAdminCode").toString(),
                    dictionary);
        });
    }

    public void rules() {
        if (talentProgram == null) {
            showNotification("No Talent Programs Available");
            return;
        }
        showMessageDialog(getMessage("essayRules"), talentProgram.getEssayRequirementLang(), MessageType.CONFIRMATION_HTML);
    }

    protected List<OrganizationHrUser> getHrUsers(Map<String, Object> dictionary) {
        UUID positionGroupId = userSession.getAttribute(StaticVariable.POSITION_GROUP_ID);
        if (positionGroupId == null) {
            showNotification("No Talent Admin");
            return new ArrayList<>();
        }
        PositionGroupExt positionGroupExt = metadata.create(PositionGroupExt.class);
        positionGroupExt.setId(positionGroupId);
        OrganizationGroupExt organizationGroupExt = employeeService.getOrganizationGroupExtByPositionGroup(positionGroupExt, View.LOCAL);
        return organizationHrUserService.getHrUsers(organizationGroupExt.getId(), dictionary.get("adminRoleCode").toString(), null);
    }

    protected Boolean isHasRussianChars(String string) {
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    protected Label getLabelWithValue(TalentProgramPersonStep talentProgramPersonStep, String... property) {
        Label label = componentsFactory.createComponent(Label.class);
        Object item = talentProgramPersonStep;
        for (String s : property) {
            item = item instanceof Entity ? ((Entity) item).getValue(s) : null;
        }
        label.setValue(item);
        return label;
    }

    @Nullable
    protected TalentProgramPersonStep getTalentProgramPersonStepByTalentProgramStep(@Nullable TalentProgramStep talentProgramStep) {
        if (talentProgramStep == null) return null;
        return talentProgramPersonStepsDs.getItems().stream()
                .filter(talentProgramPersonStep -> talentProgramPersonStep.getDicTalentProgramStep().equals(talentProgramStep.getStep()))
                .findFirst()
                .orElse(null);
    }

    public void openEditorPersonStep() {
        TalentProgramPersonStep talentProgramPersonStepByTalentProgramStep = getTalentProgramPersonStepByTalentProgramStep(tableSteps.getSingleSelected());
        if (talentProgramPersonStepByTalentProgramStep != null)
            openEditor(talentProgramPersonStepByTalentProgramStep, WindowManager.OpenType.THIS_TAB)
                    .addCloseWithCommitListener(() -> {
                        talentProgramPersonStepsDs.refresh();
                        steps.refresh();
                    });

    }

    public Component statusGenerator(Entity entity) {
        return getLabelWithValue(getTalentProgramPersonStepByTalentProgramStep((TalentProgramStep) entity), "status", "langValue");
    }

    public Component fileGenerator(Entity entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        TalentProgramPersonStep talentProgramPersonStepByTalentProgramStep = getTalentProgramPersonStepByTalentProgramStep((TalentProgramStep) entity);
        FileDescriptor file = talentProgramPersonStepByTalentProgramStep != null ? talentProgramPersonStepByTalentProgramStep.getFile() : null;
        if (file != null) {
            linkButton.setCaption(file.getName());
            linkButton.setAction(new BaseAction("fileAction") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    AppBeans.get(ExportDisplay.class).show(file);
                }
            });
        }
        return linkButton;
    }

}
