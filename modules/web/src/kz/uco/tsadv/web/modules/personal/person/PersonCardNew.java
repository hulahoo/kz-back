package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.theme.ThemeConstantsManager;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebProgressBar;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.service.AbsenceBalanceService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.modules.personal.person.frames.EditableFrame;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

//import kz.uco.tsadv.datasource.AbsenceBalanceDatasource;

/**
 * @author Adilbekov Yernar
 */

@SuppressWarnings("all")
public class PersonCardNew extends AbstractEditor<PersonGroupExt> {

    protected static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    protected static String ACTIVE_LINK = "pc-active-link";

    @Inject
    protected Metadata metadata;

    @Inject
    protected VBoxLayout personCardContent;

    @Inject
    protected VBoxLayout leftLinks;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Datasource<PersonGroupExt> personGroupDs;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected HtmlBoxLayout personCardLeftMenu;

    @Inject
    protected TabSheet tabSheet;

    protected int filledRowCount;

    protected boolean editable;

    @Inject
    protected Datasource<PersonExt> personDs;

    //    @Inject
//    protected AbsenceBalanceDatasource absenceBalancesVDs;
    @Inject
    protected DataSupplier dataSupplier;

    @Inject
    protected Datasource<AssignmentExt> assignmentDs;
    protected Map<String, List<LinkWrapper>> frames = new HashMap<>();
    protected Map<String, LinkWrapper> previewScreens = new HashMap<>();

    protected AbstractFrame currentFrame = null;
    @Inject
    protected CommonService commonService;
    protected TabSheet.Tab previewTab;
    protected boolean setTabFromCode = false;
    protected boolean fromAssessment = false;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected Datasource<OrgAnalytics> orgAnalyticsDs;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected AbsenceBalanceService absenceBalanceService;

    protected String screenName;
    public boolean existingSalariesAreChanged;

    @SuppressWarnings("all")
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("fromPersonsAssessments")) fromAssessment = true;
        setShowSaveNotification(false);
        editable = true;
        initFrames();
        initVisibleComponent();
        if (fromAssessment) tabSheet.setTab("profile");
        previewTab = tabSheet.getTab();
        tabSheet.addSelectedTabChangeListener(event -> {
            if (!setTabFromCode && getDsContext().isModified()) {

                showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
                        messages.getMainMessage("saveUnsaved"),
                        MessageType.WARNING,
                        new Action[]{
                                new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY)
                                        .withCaption(messages.getMainMessage("closeUnsaved.save"))
                                        .withHandler(e -> {
                                    commit();
                                    getDsContext().refresh();
                                    fillLeftLinks(event.getSelectedTab().getName());
                                    previewTab = tabSheet.getTab();
                                }),
                                new BaseAction("discard")
                                        .withIcon(AppBeans.get(ThemeConstantsManager.class).getThemeValue("actions.dialog.Cancel.icon"))
                                        .withCaption(messages.getMainMessage("closeUnsaved.discard"))
                                        .withHandler(e -> {

                                    resetDsContext();

                                    fillLeftLinks(event.getSelectedTab().getName());
                                    previewTab = tabSheet.getTab();
                                }),
                                new DialogAction(DialogAction.Type.CANCEL) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        setTabFromCode = true;
                                        tabSheet.setTab(previewTab);
                                    }
                                }
                        });
            } else {
                fillLeftLinks(event.getSelectedTab().getName());
                setTabFromCode = false;
            }
        });
        removeAction("windowClose");
        addAction(new BaseAction("windowClose") {
            @Override
            public void actionPerform(Component component) {
                resetDsContext();

                editable = true;
                initVisibleComponent();
            }

            @Override
            public String getCaption() {
                return getMessage("table.btn.cancel");
            }
        });
    }

    protected void resetDsContext() {
        for (Datasource datasource : getDsContext().getAll()) {
            ((AbstractDatasource) datasource).setModified(false);
        }

        getDsContext().refresh();
        close(CLOSE_ACTION_ID);
    }

    @Override
    public void commitAndClose() {
        super.commit();
        editable = true;
        initVisibleComponent();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        }

        return super.postCommit(committed, close);
    }

    @Override
    protected void postInit() {
        PersonExt person = personDs.getItem();
        assignmentDs.setItem(employeeService.getAssignment(person.getGroup().getId(), "assignment.card"));
        fillLeftLinks(tabSheet.getTab().getName());
        initPersonLeftMenu(person);
//        absenceBalancesVDs.setPersonGroupId(personGroupDs.getItem().getId());
    }

    @Override
    public void ready() {
        super.ready();
        if (assignmentDs.getItem().getSubstituteEmployee() != null) {
            if (employeeService.employeeIsExEmployee(assignmentDs.getItem().getSubstituteEmployee())) {
                String substituteEmployeeMessage;
                if ((substituteEmployeeMessage = generateSubstituteEmployeeMessage()) != null) {
                    showNotification(substituteEmployeeMessage);
                }
            }
        }
    }

    protected void initDefaultPage(String tabName) {
        if (fromAssessment) {
            List<LinkWrapper> linkWrappers = frames.get(tabName);
            openFrame(linkWrappers.stream().filter(linkWrapper -> {
                return linkWrapper.getCaption().equals("Assessments");
            }).findFirst().orElse(null));
            fromAssessment = false;
            return;
        }
        LinkWrapper defaultPage = previewScreens.get(tabName);
        if (defaultPage == null) {
            defaultPage = frames.get(tabName).stream().filter(new Predicate<LinkWrapper>() {
                @Override
                public boolean test(LinkWrapper linkWrapper) {
                    return linkWrapper.isDefaultPage();
                }
            }).findFirst().get();
        }
        openFrame(defaultPage);
    }

    protected void initFrames() {
        frames.put("mainData", new LinkedList<LinkWrapper>() {{
            add(new LinkWrapper("pcf-main-data", "personalInfo", "font-icon:ALIGN_LEFT", true));
            add(new LinkWrapper("pcf-contacts", "Contacts", "font-icon:PHONE_SQUARE"));
            add(new LinkWrapper("pcf-documents", "Documents", "font-icon:NEWSPAPER_O"));
            add(new LinkWrapper("pcf-addresses", "PersonCard.adressess", "font-icon:LOCATION_ARROW"));
            add(new LinkWrapper("pcf-beneficiary", "PersonCard.beneficiaries", "font-icon:USERS"));
            add(new LinkWrapper("caseFrame", "Person.card.casesTab", "font-icon:ALIGN_CENTER"));
            add(new LinkWrapper("pcf-military-form", "MilitaryRank", "font-icon:STAR"));
            add(new LinkWrapper("pcf-disability", "Disability", "font-icon:MEDKIT"));
            add(new LinkWrapper("pcf-retirement", "Retirement", "font-icon:PASTE"));
            add(new LinkWrapper("pcf-person-qualification", "PersonQualification", "font-icon:LEANPUB"));

        }});
        frames.put("assignment", new LinkedList<LinkWrapper>() {{
            add(new LinkWrapper("pcf-assignment", "Assignments", "icons/paste.png", true));
            add(new LinkWrapper("pcf-agreement", "personCard.agreementTab", "icons/copy.png"));
            add(new LinkWrapper("pcf-experience", "PersonExperience", "font-icon:BARS"));
            add(new LinkWrapper("pcf-job-request", "JobRequests", "font-icon:TASKS"));
            add(new LinkWrapper("pcf-dismissal", "Dismissals", "font-icon:USER_TIMES"));
            add(new LinkWrapper("pcf-trade-union", "TradeUnion", "font-icon:BUILDING_O"));
            add(new LinkWrapper("pcf-relocation", "person.card.ReLocation", "font-icon:MAP_SIGNS"));
            add(new LinkWrapper("pcf-punishment-browse", "Punishment", "font-icon:GAVEL"));
            add(new LinkWrapper("pcf-awards-browse", "Awards", "font-icon:TROPHY"));
            add(new LinkWrapper("pcf-calendar-offset", "ModeOfOperation", "font-icon:CLOCK_O"));
            add(new LinkWrapper("pcf-person-mentor", "personMentor", "images/manager.png"));
        }});

        frames.put("compensation", new LinkedList<LinkWrapper>() {{
            add(new LinkWrapper("pcf-salary", "Salaries", "font-icon:ARCHIVE", true));
            add(new LinkWrapper("pcf-sur-charge", "PersonCard.sure.charges", "font-icon:ARROW_CIRCLE_UP"));
//            add(new LinkWrapper(null, "PersonCard.social.pay", "font-icon:CHAIN"));
        }});

        frames.put("profile", new LinkedList<LinkWrapper>() {{
            add(new LinkWrapper("pcf-person-learning-history", "PersonLearningHistory", "font-icon:GRADUATION_CAP"));
            add(new LinkWrapper("pcf-person-learning-contract", "PersonLearningContract", ""));
            add(new LinkWrapper("pcf-education", "PersonEducation", "font-icon:BOOK", true));
            add(new LinkWrapper("pcf-competence", "Competences", "font-icon:ALIGN_LEFT"));
            add(new LinkWrapper("pcf-assessment", "Assessments", "font-icon:STAR_O"));
            add(new LinkWrapper("pcf-succession", "SuccessionPlanning", "font-icon:BANK"));
            add(new LinkWrapper("pcf-course", "coursesTab", "font-icon:GRADUATION_CAP"));
        }});

        frames.put("timeManage", new LinkedList<LinkWrapper>() {{
            add(new LinkWrapper("tsadv$Absence.browse", "Absences", "font-icon:USER_SECRET", true));
            add(new LinkWrapper("tsadv$AbsenceBalance.browse", "PersonCard.absense.balance", "font-icon:BALANCE_SCALE"));
            add(new LinkWrapper("pcf-trip", "BusinessTrip", "icons/wf-exchange.png"));
            add(new LinkWrapper("pcf-leave", "PersonCard.request.leave", "font-icon:CLIPBOARD"));
        }});

        frames.put("siz", new LinkedList<LinkWrapper>() {{
            add(new LinkWrapper("pcf-personal-protection", "ppe", "", true));
//            add(new LinkWrapper("", "", ""));
//            add(new LinkWrapper("", "", ""));
//            add(new LinkWrapper("", "", ""));
        }});
    }

    protected void openFrame(LinkWrapper linkWrapper) {

        if (linkWrapper.getScreen() == null) {
            showNotification("Page not found!");
        } else {
            screenName = linkWrapper.getCaption();
            currentFrame = (AbstractFrame) openFrame(personCardContent, linkWrapper.getScreen());
            setActive(linkWrapper);
        }

        initVisibleComponent();
    }

    protected void setActive(LinkWrapper linkWrapper) {
        for (Component component : leftLinks.getOwnComponents()) {
            if (component.getClass().getSimpleName().equalsIgnoreCase("WebLinkButton")) {
                boolean selectPage = linkWrapper.getScreen() != null && component.getId() != null && component.getId().equalsIgnoreCase(linkWrapper.getScreen());

                if (selectPage) {
                    previewScreens.put(tabSheet.getTab().getName(), linkWrapper);
                    component.setStyleName(ACTIVE_LINK);
                } else {
                    component.removeStyleName(ACTIVE_LINK);
                }
            }
        }
    }

    protected void fillLeftLinks(String tabName) {
        leftLinks.removeAll();
        for (LinkWrapper linkWrapper : frames.get(tabName)) {
            LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
            linkButton.setId(linkWrapper.getScreen());
            linkButton.setCaption(getMessage(linkWrapper.getCaption()));
            linkButton.setIcon(linkWrapper.getIcon());
            linkButton.setAction(new BaseAction(linkWrapper.getCaption()) {
                @Override
                public void actionPerform(Component component) {
                    if (getDsContext().isModified()) {
                        showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
                                messages.getMainMessage("saveUnsaved"),
                                MessageType.WARNING,
                                new Action[]{
                                        new DialogAction(DialogAction.Type.OK, Status.PRIMARY)
                                                .withCaption(messages.getMainMessage("closeUnsaved.save"))
                                                .withHandler(event -> {
                                            commit();
                                            openFrame(linkWrapper);
                                        }),
                                        new BaseAction("discard")
                                                .withIcon(AppBeans.get(ThemeConstantsManager.class).getThemeValue("actions.dialog.Cancel.icon"))
                                                .withCaption(messages.getMainMessage("closeUnsaved.discard"))
                                                .withHandler(event -> {

                                            resetDsContext();

                                            openFrame(linkWrapper);
                                        }),
                                        new DialogAction(DialogAction.Type.CANCEL)
                                });
                    } else {
                        openFrame(linkWrapper);
                    }
                }
            });
            leftLinks.add(linkButton);
        }

        initDefaultPage(tabName);
    }

    protected void initPersonLeftMenu(PersonExt person) {
        personCardLeftMenu.add(createLabelWithId("fullName", person.getFullName()));
        personCardLeftMenu.add(createLabelWithId("employeeNumber", person.getEmployeeNumber()));

        filledRowCount = 0;
        editable = true;

        StringBuilder userInfoSb = new StringBuilder("<table>");
        userInfoSb.append(addUserInfoTr(getMessage("Person.hireDate"), person.getHireDate() == null ? "" : dateFormat.format(person.getHireDate())));
        userInfoSb.append(addUserInfoTr(getMessage("Person.nationalIdentifier"), String.valueOf(person.getNationalIdentifier())));
        userInfoSb.append(addUserInfoTr(getMessage("Person.dateOfBirth"), dateFormat.format(person.getDateOfBirth())));

        if (person.getSex() != null)
            userInfoSb.append(addUserInfoTr(getMessage("Person.sex"), person.getSex().getLangValue()));
        if (person.getMaritalStatus() != null)
            userInfoSb.append(addUserInfoTr(getMessage("Person.maritalStatus"), person.getMaritalStatus().getLangValue()));
        if (person.getType() != null)
            userInfoSb.append(addUserInfoTr(getMessage("Person.type"), person.getType().getLangValue()));
        if (person.getNationality() != null)
            userInfoSb.append(addUserInfoTr(getMessage("Person.nationality"), person.getNationality().getLangValue()));
        if (person.getCitizenship() != null)
            userInfoSb.append(addUserInfoTr(getMessage("Person.citizenship"), person.getCitizenship().getLangValue()));

//        userInfoSb.append(addUserInfoTr(String.format(getMessage("Person.absenceDays"), new SimpleDateFormat("dd.MM.yyyy").format(CommonUtils.getSystemDate())), String.valueOf(absenceBalanceService.getCurrentAbsenceDays(personGroupDs.getItem()))));
        userInfoSb.append(addUserInfoTr(getMessage("totalExperience"), String.valueOf(employeeService.getTotalExperience(person.getGroup().getId(), CommonUtils.getSystemDate()))));
        userInfoSb.append(addUserInfoTr(getMessage("experienceInCompany"), String.valueOf(employeeService.getExperienceInCompany(person.getGroup().getId(), CommonUtils.getSystemDate()))));

        userInfoSb.append("</table>");

        Label userInfo = createLabelWithId("userInfo", userInfoSb.toString());
        userInfo.setHtmlEnabled(true);
        personCardLeftMenu.add(userInfo);

        Image personImage = WebCommonUtils.setImage(person.getImage(), null, "70px");
        personImage.addStyleName("b-user-image circle-image");
        personImage.setId("personImage");
        personCardLeftMenu.add(personImage);

        VBoxLayout actions = componentsFactory.createComponent(VBoxLayout.class);
        actions.setId("actions");
        actions.addStyleName("pc-actions-wrapper");
        actions.add(createLinkButton("Добавить примечание", null));
        actions.add(createLinkButton("Печать/ PDF", null));
        actions.add(createLinkButton("Отчет т2 форма", new BaseAction("reportT2") {
            @Override
            public void actionPerform(Component component) {
                Report report = commonService.emQuerySingleRelult(Report.class, "select e from report$Report e where e.code = 'PERSON_CARD'", null);
                ReportGuiManager reportGuiManager = AppBeans.get(ReportGuiManager.class);
                Map<String, Object> map = new HashMap<>();
                map.put("sysDate", CommonUtils.getSystemDate());
                map.put("Person_Card", "Карточка сотрудника на" + " " + CommonUtils.getSystemDate());
                map.put("personId", personGroupDs.getItem().getId());
                if (report != null && report.getLocName() != null) {
                    reportGuiManager.printReport(report, map, null, report.getLocName());
                }
            }
        }));
        actions.add(createLinkButton("Редактировать", new BaseAction("edit-person") {
            @Override
            public void actionPerform(Component component) {
                AbstractEditor abstractEditor = openEditor("tsadv$PersonMainData.edit", person, WindowManager.OpenType.THIS_TAB, ParamsMap.of("assignmentDs", assignmentDs));
                abstractEditor.addCloseListener(e -> {
                    assignmentDs.refresh();
                    commitAndClose();
                });
                editable = true;
                initVisibleComponent();
            }

        }));
        personCardLeftMenu.add(actions);

        VBoxLayout pages = componentsFactory.createComponent(VBoxLayout.class);
        pages.addStyleName("pc-actions-wrapper");
        pages.setId("pages");
        pages.add(createLinkButton("Организационная структура", new BaseAction("beauty-tree") {
            @Override
            public void actionPerform(Component component) {
                openWindow("beautyTree", WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
                    put("personGroupId", person.getGroup().getId());
                }});
            }
        }));
        pages.add(createLinkButton("План целей", null));
        pages.add(createLinkButton("Обзор результативности", null));
        pages.add(createLinkButton("Карьерная ведомость", null));
        pages.add(createLinkButton("План развития", null));
        pages.add(createLinkButton("Профили должностей", null));
        personCardLeftMenu.add(pages);

        LinkButton showAction = componentsFactory.createComponent(LinkButton.class);
        showAction.setCaption("");
        showAction.setIcon("font-icon:MAIL_FORWARD");
        showAction.setId("showAction");
        showAction.addStyleName("revert-person-card");
        personCardLeftMenu.add(showAction);

        LinkButton showCard = componentsFactory.createComponent(LinkButton.class);
        showCard.setCaption("");
        showCard.setIcon("font-icon:MAIL_REPLY");
        showCard.setId("showCard");
        showCard.addStyleName("revert-person-card");
        personCardLeftMenu.add(showCard);

        double fieldPercent = (float) 100 / (float) 11 * (float) filledRowCount;

        WebProgressBar progressBar = componentsFactory.createComponent(WebProgressBar.class);
        progressBar.setId("progressBar");
        progressBar.setWidth("100%");
        progressBar.setValue(fieldPercent / 100);
        personCardLeftMenu.add(progressBar);

        personCardLeftMenu.add(createH4("progressBarLabel", String.format("%s %.1f%s", getMessage("person.card.progress.label"), (float) fieldPercent, "%"), true));
        personCardLeftMenu.add(createH4("userInfoLabel", getMessage("person.card.main.info"), false));
        personCardLeftMenu.add(createH4("doActionLabel", getMessage("person.card.do.action"), false));
        personCardLeftMenu.add(createH4("redirectLabel", getMessage("person.card.redirect"), true));

        personCardLeftMenu.setStyleName("pc-left-links-top-" + filledRowCount);
    }

    protected Label createH4(String id, String message, boolean isBordered) {
        Label redirectLabel = createLabelWithId(id,
                String.format("<h4 class=\"%s\">%s</h4>", isBordered ? " border-top" : "", message));
        redirectLabel.setHtmlEnabled(true);
        redirectLabel.setWidth("100%");
        return redirectLabel;
    }

    protected void initVisibleComponent() {
        if (currentFrame != null) {
            EditableFrame editableFrame = (EditableFrame) currentFrame;
            editableFrame.editable(editable);
        }

    }

    protected LinkButton createLinkButton(String caption, BaseAction baseAction) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(caption);
        linkButton.setAction(baseAction);
        return linkButton;
    }

    protected String addUserInfoTr(String columnName, String columnValue) {
        filledRowCount++;
        return String.format("<tr><td>%s</td><td>:</td><td>%s</td></tr>", columnName, columnValue);
    }

    protected Label createLabelWithId(String id, String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setId(id);
        label.setValue(value);
        return label;
    }

    /**
     * Формирует сообщение, появляющееся в случае если замещаемый сотрудник данного сотрудника уволен.
     *
     * @return объект String - строка сгенерированная из полного имени и двух частей сообщения
     * с ключами substituteEmployeeIsNotActive1 и substituteEmployeeIsNotActive2.
     */
    private String generateSubstituteEmployeeMessage() {
        PersonGroupExt substituteEmployee;
        if ((substituteEmployee = assignmentDs.getItem().getSubstituteEmployee()) == null) {
            return "";
        }
        String substituteEmployeeFullName = substituteEmployee.getFullName();
        return (getMessage("substituteEmployeeIsNotActive1") + " "
                + substituteEmployeeFullName + " "
                + getMessage("substituteEmployeeIsNotActive2")
        );
    }

}