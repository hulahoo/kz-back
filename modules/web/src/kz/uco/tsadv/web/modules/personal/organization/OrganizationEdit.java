package kz.uco.tsadv.web.modules.personal.organization;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.OrganizationStructureConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.service.HierarchyService;
import kz.uco.tsadv.service.OrganizationService;
import kz.uco.tsadv.web.modules.personal.case_.Caseframe;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class OrganizationEdit extends AbstractHrEditor<OrganizationExt> {
    protected int languageIndex = languageIndex();

    @Inject
    protected OrganizationService organizationService;
    @Inject
    protected OrganizationStructureConfig organizationStructureConfig;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionDatasource<OrganizationGroupGoalLink, UUID> goalsDs;
    @Inject
    protected CollectionDatasource<ParentElementsGoal, UUID> parentElementsGoalsDs;
    @Inject
    protected Datasource<OrganizationExt> organizationDs;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected HierarchyService hierarchyService;
    @Named("goalsTable.create")
    protected CreateAction goalsTableCreate;
    @Named("orgHrUsersTable.create")
    protected CreateAction orgHrUsersTableCreate;
    @Inject
    protected Caseframe caseFrame;
    @Named("competenceOrgTable.create")
    protected CreateAction competenceOrgTableCreate;
    @Inject
    protected Datasource<OrgAnalytics> analyticsDs;
    @Inject
    protected CollectionDatasource<HierarchyElementExt, UUID> hierarchyElementsDs;
    @Named("hierarchyElementsTable.create")
    protected CreateAction hierarchyElementsTableCreate;
    @Named("hierarchyElementsTable.edit")
    protected EditAction hierarchyElementsTableEdit;
    @Named("hierarchyElementsTable.close")
    protected Action hierarchyElementsTableClose;
    @Named("hierarchyElementsTable.reassignElement")
    protected Action hierarchyElementsTableReassignElement;


    @Override
    protected FieldGroup getStartEndDateFieldGroup() {
        return fieldGroup;
    }

    public Component generateParentName(HierarchyElementExt element) {
        Label label = (Label) componentsFactory.createComponent(Label.NAME);
        if (element.getParent() != null) {
            HierarchyElementExt parentElement = dataManager.reload(element.getParent(), "hierarchyElement.parent");
//                    commonService.getEntity(HierarchyElementExt.class, "select e from base$HierarchyElementExt e where e.id = :parentId", Collections.singletonMap("parentId", element.getParent().getId()), "hierarchyElement.parent");
            label.setValue(parentElement.getName());
        }
        return label;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        tabSheet.addSelectedTabChangeListener(event -> {
            if (PersistenceHelper.isNew(getItem())) {
                if (!"tab1".equals(event.getSelectedTab().getName())) {
                    if (getDsContext().isModified()) {
                        showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
                                messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "closeText"),
                                MessageType.WARNING,
                                new Action[]{
                                        new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY)
                                                .withCaption(messages.getMainMessage("closeUnsaved.save"))
                                                .withHandler(event1 -> {
                                            commit();
                                        }),
                                        new DialogAction(DialogAction.Type.CANCEL)
                                                .withCaption(messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "closeFormButtonText"))
                                                .withHandler(event1 -> {
                                            Utils.resetDsContext(getDsContext());
                                            close(CLOSE_ACTION_ID);
                                        })
                                });
                    } else {
                        showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
                                messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "questionIsNotFillForm"),
                                MessageType.WARNING,
                                new Action[]{
                                        new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY)
                                                .withCaption(messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "fillForm"))
                                                .withHandler(event1 -> {
                                            tabSheet.setSelectedTab("tab1");
                                        }),
                                        new DialogAction(DialogAction.Type.CANCEL)
                                                .withCaption(messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "closeFormButtonText"))
                                                .withHandler(event1 -> {
                                            close(CLOSE_ACTION_ID);
                                        })
                                });
                    }
                }
            }
        });
        if (analyticsDs.getItem() != null) {
            getDsContext().addBeforeCommitListener(context -> context.addInstanceToCommit(analyticsDs.getItem()));
        }
        goalsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("organizationGroup", getItem()));
        orgHrUsersTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("organizationGroup", getItem().getGroup()));
        competenceOrgTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("organizationGroup", getItem().getGroup()));
    }

    protected void setTabsVisibility(boolean visible) {
        for (TabSheet.Tab tab : tabSheet.getTabs()) {
            if (!"tab1".equals(tab.getName())) {
                tab.setVisible(visible);
            }
        }
    }

    @Override
    protected void initNewItem(OrganizationExt item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());

        DicPayroll payroll;
        if ((payroll = organizationService.getPayroll()) != null) {
            item.setPayroll(payroll);
        }

        setDatesEditable(true);

        item.setWriteHistory(false);
        item.setInternal(true);
    }

    @Override
    protected void postInit() {
        super.postInit();
        if (PersistenceHelper.isNew(getItem())) {
            setTabsVisibility(false);
        } else {
            setTabsVisibility(true);
            if (isFirst()) {
                fieldGroup.getFieldNN("startDate").setEditable(true);
            }
        }
    }

    @Override
    public void ready() {
        ((CreateAction) ((Table) caseFrame.getComponentNN("casesTable")).getActionNN("create")).setInitialValuesSupplier(() -> ParamsMap.of("organizationGroup", getItem().getGroup()));
        super.ready();
        setEnabledHierarchyElementCloseReassign(Objects.nonNull(hierarchyElementsDs.getItem()));
        hierarchyElementsDs.addItemChangeListener(e -> setEnabledHierarchyElementCloseReassign(Objects.nonNull(hierarchyElementsDs.getItem())));
        hierarchyElementsTableCreate.setWindowParamsSupplier(() -> ParamsMap.of("openedForCreateFromOrganizationEdit", true));
        hierarchyElementsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("organizationGroup", getItem().getGroup(),
                "elementType", ElementType.ORGANIZATION,
                "hierarchy", commonService.getEntity(Hierarchy.class, organizationStructureConfig.getOrganizationStructureId())));
        hierarchyElementsTableEdit.setWindowParams(ParamsMap.of("openedForEdit", ""));
        hierarchyElementsTableEdit.setAfterWindowClosedHandler((window, closeActionId) ->
                hierarchyElementsDs.refresh());

        hierarchyElementsDs.addCollectionChangeListener(e -> hierarchyElementsTableCreate.setEnabled(hierarchyElementsDs.getItems().isEmpty()));
        hierarchyElementsTableCreate.setEnabled(hierarchyElementsDs.getItems().isEmpty());
    }

    protected void setEnabledHierarchyElementCloseReassign(boolean enable) {
        hierarchyElementsTableClose.setEnabled(enable);
        hierarchyElementsTableReassignElement.setEnabled(enable);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (getItem().getGroup()!= null && getItem().getGroup().getAnalytics() == null) {
            OrgAnalytics orgAnalytics = metadata.create(OrgAnalytics.class);
            orgAnalytics.setOrganizationGroup(getItem().getGroup());
            getItem().getGroup().setAnalytics(dataManager.commit(orgAnalytics));
            getDsContext().commit();
        }
        return super.postCommit(committed, close);
    }

    protected int languageIndex() {
        String language = ((UserSessionSource) AppBeans.get(UserSessionSource.NAME)).getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            for (int i = 0; i < langs.size(); i++) {
                if (language.equals(langs.get(i))) {
                    return i + 1;
                }
            }
        }
        return 1;
    }

    public Component getPersonFio(Entity entity) {
        OrganizationHrUser hrUser = (OrganizationHrUser) entity;
        Label label = componentsFactory.createComponent(Label.class);
        StringBuilder builder = new StringBuilder();
        if (hrUser != null && hrUser.getUser() != null) {
            if (hrUser.getUser().getFirstName() != null) {
                builder.append(hrUser.getUser().getFirstName());
                if (hrUser.getUser().getLastName() != null) {
                    builder.append(" ");
                }
            }
            if (hrUser.getUser().getLastName() != null) {
                builder.append(hrUser.getUser().getLastName());
                if (hrUser.getUser().getMiddleName() != null) {
                    builder.append(" ");
                }
            }
            if (hrUser.getUser().getMiddleName() != null) {
                builder.append(hrUser.getUser().getMiddleName());
            }
        }
        label.setValue(builder.toString());
        return label;
    }

    protected List<String> getUserRoles(OrganizationHrUser user) {
        List<String> result = new ArrayList<>();
        if (user != null && user.getUser() != null) {
            String queryString = "SELECT e FROM tsadv$HrUserRole e " +
                    "WHERE e.user.id = :userId" +
                    " and :sysDate between e.dateFrom and e.dateTo";
            Map<String, Object> params = new HashMap<>();
            params.put("userId", user.getUser().getId());
            params.put("sysDate", CommonUtils.getSystemDate());
            List<HrUserRole> roles = commonService.getEntities(HrUserRole.class, queryString, params, "hrUserRole.view");
            if (!roles.isEmpty()) {
                for (HrUserRole role : roles) {
                    result.add(role.getRole().getLangValue());
                }
            }
        }
        return result;
    }

    protected boolean isFirst() {
        OrganizationExt item = getItem();
        return item == item
                .getGroup()
                .getList()
                .stream()
                .min((firstOrganization, secondOrganization) ->
                        firstOrganization.getStartDate().before(secondOrganization.getStartDate()) ? -1 : 1)
                .get();
    }

    private void setDatesEditable(boolean editable) {
        fieldGroup.getField("startDate").setEditable(editable);
        fieldGroup.getField("endDate").setEditable(editable);
    }

    public void close() {
        openEditorHierarchyElement(hierarchyElementsDs.getItem(), WindowManager.OpenType.DIALOG, ParamsMap.of("close", Boolean.TRUE));
    }

    public void reassignElement() {
        openEditorHierarchyElement(hierarchyElementsDs.getItem(), WindowManager.OpenType.DIALOG, ParamsMap.of("reassignElement", ""));
    }

    protected AbstractEditor<HierarchyElementExt> openEditorHierarchyElement(HierarchyElementExt item, WindowManager.OpenType openType, Map<String, Object> params) {
        AbstractEditor<HierarchyElementExt> hierarchyElementAbstractEditor = openEditor(item, openType, params);
        hierarchyElementAbstractEditor.addCloseListener(actionId -> hierarchyElementsDs.refresh());
        return hierarchyElementAbstractEditor;
    }
}