package kz.uco.tsadv.web.screens.persongroupext;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.theme.ThemeConstantsManager;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.tsadv.entity.dbview.AbsenceBalanceV;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AbsenceBalance;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.AbsenceBalanceService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.modules.personal.person.LinkWrapper;
import kz.uco.tsadv.web.modules.personal.person.frames.EditableFrame;
import org.jsoup.Connection;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@UiController("base$PersonGroupExt.edit")
@UiDescriptor("person-group-ext-edit.xml")
@EditedEntityContainer("personGroupDc")
@LoadDataBeforeShow
public class PersonGroupExtEdit extends StandardEditor<PersonGroupExt> {
//    @Inject
//    protected InstanceContainer<AssignmentExt> assignmentDc;
//    @Inject
//    protected InstanceLoader<AssignmentExt> assignmentDl;
//    @Inject
//    protected CollectionLoader<Absence> absencesDl;
//    @Inject
//    protected CollectionLoader<AbsenceBalance> absenceBalancesDl;
//    @Inject
//    protected InstanceContainer<PersonGroupExt> personGroupDc;
//    @Inject
//    protected DataManager dataManager;
//    @Inject
//    protected EmployeeService employeeService;
//    @Inject
//    protected AbsenceBalanceService absenceBalanceService;
//    @Inject
//    protected VBoxLayout leftLinks;
//    @Inject
//    private TabSheet tabSheet;
//
//    protected Map<String, List<LinkWrapper>> frames = new HashMap<>();
//    protected AbstractFrame currentFragment = null;
//    protected TabSheet.Tab previewTab;
//    protected boolean fromAssessment = false;
//    protected boolean editable;
//    protected String screenName;
//    @Inject
//    private ComponentsFactory componentsFactory;
//    @Inject
//    private MessageBundle messageBundle;
//    @Inject
//    private Messages messages;
//    @Inject
//    private Notifications notifications;
//    @Inject
//    protected VBoxLayout personCardContent;
//    @Inject
//    private DataContext dataContext;
//    @Inject
//    private Fragments fragments;
//
//
//    @Subscribe
//    public void onInit(InitEvent event) {
//        initQueryParams();
//        ScreenOptions options = event.getOptions();
//        if(options instanceof MapScreenOptions) {
//            Map<String, Object> params = ((MapScreenOptions) options).getParams();
//            if (params.containsKey("fromPersonsAssessments")) fromAssessment = true;
//        }
//        setShowSaveNotification(false);
//        editable = true;
//        initFrames();
//        initVisibleComponent();
//        if (fromAssessment) tabSheet.setSelectedTab("profile");
//        previewTab = tabSheet.getSelectedTab();
//
//
//    }
//
//    @Subscribe
//    public void onAfterShow(AfterShowEvent event) {
//
//    }
//
//    @Subscribe
//    public void onAfterClose(AfterCloseEvent event) {
//        resetDsContext();
//
//        editable = true;
//        initVisibleComponent();
//
//    }
//
//    @Subscribe
//    public void onBeforeShow(BeforeShowEvent event) {
//        PersonExt person = personGroupDc.getItem().getPerson();
//        fillLeftLinks(tabSheet.getTab().getName());
////        initPersonLeftMenu(person);
////        absenceBalancesVDs.setPersonGroupId(personGroupDs.getItem().getId());
//
//    }
//
//    protected void initFrames() {
//        frames.put("mainData", new LinkedList<LinkWrapper>() {{
//            add(new LinkWrapper("pcf-main-data", "personalInfo", "font-icon:ALIGN_LEFT", true));
//            add(new LinkWrapper("pcf-contacts", "Contacts", "font-icon:PHONE_SQUARE"));
//            add(new LinkWrapper("pcf-documents", "Documents", "font-icon:NEWSPAPER_O"));
//            add(new LinkWrapper("pcf-addresses", "PersonCard.adressess", "font-icon:LOCATION_ARROW"));
//            add(new LinkWrapper("pcf-beneficiary", "PersonCard.beneficiaries", "font-icon:USERS"));
//            add(new LinkWrapper("caseFrame", "Person.card.casesTab", "font-icon:ALIGN_CENTER"));
//            add(new LinkWrapper("pcf-military-form", "MilitaryRank", "font-icon:STAR"));
//            add(new LinkWrapper("pcf-disability", "Disability", "font-icon:MEDKIT"));
//            add(new LinkWrapper("pcf-retirement", "Retirement", "font-icon:PASTE"));
//            add(new LinkWrapper("pcf-person-qualification", "PersonQualification", "font-icon:LEANPUB"));
//
//        }});
//        frames.put("assignment", new LinkedList<LinkWrapper>() {{
//            add(new LinkWrapper("pcf-assignment", "Assignments", "icons/paste.png", true));
//            add(new LinkWrapper("pcf-agreement", "personCard.agreementTab", "icons/copy.png"));
//            add(new LinkWrapper("pcf-experience", "PersonExperience", "font-icon:BARS"));
//            add(new LinkWrapper("pcf-job-request", "JobRequests", "font-icon:TASKS"));
//            add(new LinkWrapper("pcf-dismissal", "Dismissals", "font-icon:USER_TIMES"));
//            add(new LinkWrapper("pcf-trade-union", "TradeUnion", "font-icon:BUILDING_O"));
//            add(new LinkWrapper("pcf-relocation", "person.card.ReLocation", "font-icon:MAP_SIGNS"));
//            add(new LinkWrapper("pcf-punishment-browse", "Punishment", "font-icon:GAVEL"));
//            add(new LinkWrapper("pcf-awards-browse", "Awards", "font-icon:TROPHY"));
//            add(new LinkWrapper("pcf-calendar-offset", "ModeOfOperation", "font-icon:CLOCK_O"));
//            add(new LinkWrapper("pcf-person-mentor", "personMentor", "images/manager.png"));
//        }});
//
//        frames.put("compensation", new LinkedList<LinkWrapper>() {{
//            add(new LinkWrapper("pcf-salary", "Salaries", "font-icon:ARCHIVE", true));
//            add(new LinkWrapper("pcf-sur-charge", "PersonCard.sure.charges", "font-icon:ARROW_CIRCLE_UP"));
//            add(new LinkWrapper(null, "PersonCard.social.pay", "font-icon:CHAIN"));
//        }});
//
//        frames.put("profile", new LinkedList<LinkWrapper>() {{
//            add(new LinkWrapper("pcf-person-learning-history", "PersonLearningHistory", "font-icon:GRADUATION_CAP"));
//            add(new LinkWrapper("pcf-person-learning-contract", "PersonLearningContract", ""));
//            add(new LinkWrapper("pcf-education", "PersonEducation", "font-icon:BOOK", true));
//            add(new LinkWrapper("pcf-competence", "Competences", "font-icon:ALIGN_LEFT"));
//            add(new LinkWrapper("pcf-assessment", "Assessments", "font-icon:STAR_O"));
//            add(new LinkWrapper("pcf-succession", "SuccessionPlanning", "font-icon:BANK"));
//            add(new LinkWrapper("pcf-course", "coursesTab", "font-icon:GRADUATION_CAP"));
//        }});
//
//        frames.put("timeManage", new LinkedList<LinkWrapper>() {{
//            add(new LinkWrapper("tsadv$Absence.browse", "Absences", "font-icon:USER_SECRET", true));
//            add(new LinkWrapper("tsadv$AbsenceBalance.browse", "PersonCard.absense.balance", "font-icon:BALANCE_SCALE"));
//            add(new LinkWrapper("pcf-trip", "BusinessTrip", "icons/wf-exchange.png"));
//            add(new LinkWrapper("pcf-leave", "PersonCard.request.leave", "font-icon:CLIPBOARD"));
//        }});
//
//        frames.put("siz", new LinkedList<LinkWrapper>() {{
//            add(new LinkWrapper("pcf-personal-protection", "ppe", "", true));
//            add(new LinkWrapper("", "", ""));
//            add(new LinkWrapper("", "", ""));
//            add(new LinkWrapper("", "", ""));
//        }});
//    }
//
//    protected void fillLeftLinks(String tabName) {
//        leftLinks.removeAll();
//        for (LinkWrapper linkWrapper : frames.get(tabName)) {
//            LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
//            linkButton.setId(linkWrapper.getScreen());
//            linkButton.setCaption(messageBundle.getMessage(linkWrapper.getCaption()));
//            linkButton.setIcon(linkWrapper.getIcon());
//
//            linkButton.setAction(new BaseAction(linkWrapper.getCaption()) {
//                @Override
//                public void actionPerform(Component component) {
//                    if (dataContext.hasChanges()) {
////                        showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
//                                messages.getMainMessage("saveUnsaved"),
//                                Frame.MessageType.WARNING,
//                                new Action[]{
//                                        new DialogAction(DialogAction.Type.OK, Status.PRIMARY)
//                                                .withCaption(messages.getMainMessage("closeUnsaved.save"))
//                                                .withHandler(event -> {
//                                            commit();
//                                            openFrame(linkWrapper);
//                                        }),
//                                        new BaseAction("discard")
//                                                .withIcon(AppBeans.get(ThemeConstantsManager.class).getThemeValue("actions.dialog.Cancel.icon"))
//                                                .withCaption(messages.getMainMessage("closeUnsaved.discard"))
//                                                .withHandler(event -> {
//
//                                            resetDsContext();
//
//                                            openFrame(linkWrapper);
//                                        }),
//                                        new DialogAction(DialogAction.Type.CANCEL)
//                                });
//                    } else {
//                        openFrame(linkWrapper);
//                    }
//                }
//            });
//            leftLinks.add(linkButton);
//        }
//
////        initDefaultPage(tabName);
//    }
//
//    protected void initQueryParams() {
//        absencesDl.setParameter("session$systemDate", BaseCommonUtils.getSystemDate());
//        absenceBalancesDl.setParameter("session$systemDate", BaseCommonUtils.getSystemDate());
//        assignmentDl.setParameter("session$systemDate", BaseCommonUtils.getSystemDate());
//
//    }
//    protected void initVisibleComponent() {
//        if (currentFragment != null) {
//            EditableFrame editableFrame = (EditableFrame) currentFragment;
//            editableFrame.editable(editable);
//        }
//
//
//        if (linkWrapper.getScreen() == null) {
//            notifications.create(Notifications.NotificationType.ERROR)
//                    .withCaption("Page not found!")
//                    .show();
//        } else {
//            screenName = linkWrapper.getCaption();
//            Fragment fragment = fragments.create(this, linkWrapper.getScreen())
//                    .getFragment();
//            personCardContent.add(fragment);
//            currentFragment = (AbstractFrame) fragment;
////            setActive(linkWrapper);
//        }
//
//        initVisibleComponent();
//    }
////    protected void setActive(LinkWrapper linkWrapper) {
////        for (Component component : leftLinks.getOwnComponents()) {
////            if (component.getClass().getSimpleName().equalsIgnoreCase("WebLinkButton")) {
////                boolean selectPage = linkWrapper.getScreen() != null && component.getId() != null && component.getId().equalsIgnoreCase(linkWrapper.getScreen());
////
////                if (selectPage) {
////                    previewScreens.put(tabSheet.getTab().getName(), linkWrapper);
////                    component.setStyleName(ACTIVE_LINK);
////                } else {
////                    component.removeStyleName(ACTIVE_LINK);
////                }
////            }
////        }
////    }
//
//    protected void resetDsContext() {
//        dataContext.evictAll();
//        getScreenData().loadAll();
//    }
//}
    protected void openFrame(LinkWrapper linkWrapper) {
    }

}