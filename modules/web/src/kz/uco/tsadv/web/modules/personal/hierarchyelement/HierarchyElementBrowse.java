package kz.uco.tsadv.web.modules.personal.hierarchyelement;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.theme.ThemeConstantsManager;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.gui.components.WebFieldGroup;
import com.haulmont.cuba.web.widgets.CubaHorizontalSplitPanel;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.v7.event.ItemClickEvent;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.OrganizationStructureConfig;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.dictionary.DicWorkingCondition;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.timesheet.model.Calendar;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.modules.timesheet.model.StandardOffset;
import kz.uco.tsadv.service.OrganizationService;
import kz.uco.tsadv.web.modules.personal.frames.OrganizationFrame;
import kz.uco.tsadv.web.modules.personal.frames.PositionFrame;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class HierarchyElementBrowse extends AbstractLookup {

    @Inject
    protected PositionStructureConfig positionStructureConfig;
    @Inject
    protected OrganizationStructureConfig organizationStructureConfig;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected OrganizationService organizationService;
    @Inject
    protected OrganizationFrame organizationFrame;
    @Inject
    protected PositionFrame positionFrame;
    @Inject
    protected Datasource<OrganizationExt> organizationDs;
    @Inject
    protected Datasource<PositionExt> positionDs;
    @Inject
    protected HierarchicalDatasource<HierarchyElementExt, UUID> hierarchyElementsDs;
    @Inject
    protected CollectionDatasource<CompetenceElement, UUID> posCompetenceElementsDs;
    @Inject
    protected CollectionDatasource<SurCharge, UUID> surChargeDs;
    @Inject
    protected CollectionDatasource<CompetenceElement, UUID> orgCompetenceElementsDs;
    @Inject
    protected CollectionDatasource<AssignmentExt, UUID> assignmentsDs;
    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    protected SplitPanel splitter;
    @Inject
    protected Metadata metadata;
    protected ElementType elementType = ElementType.ORGANIZATION;
    @Inject
    protected Tree<HierarchyElementExt> tree;
    @Named("tree.edit")
    protected EditAction treeEdit;
    @Inject
    protected HBoxLayout organizationButtons;
    protected boolean create;
    protected boolean parentIsPosition;
    protected String elType;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected LookupField hierarchyLookup;
    @Inject
    protected Datasource<OrgAnalytics> orgAnalyticsOrganizationDs;
    @Inject
    protected Datasource<OrgAnalytics> orgAnalyticsPositionDs;
    @Inject
    protected CollectionDatasource<Hierarchy, UUID> hierarchiesDs;
    @Inject
    protected CommonService commonService;
    protected List<Entity> commitInstances = new ArrayList<>();
    @Inject
    protected Datasource<OrgAnalytics> orgAnalyticsBetweenDs;
    @Inject
    protected Datasource<PositionGroupExt> positionGroupDs;
    @Inject
    protected TextField<String> searchField;

    @Override
    public void init(Map<String, Object> params) {
        searchField.addEnterPressListener(enterPressEvent -> searchForHierarchyElement());
        organizationButtons.setVisible(false);
        hierarchyLookup.addValueChangeListener(e -> {
            hierarchyElementsDs.refresh();
            organizationButtons.setVisible(false);
            positionFrame.setVisible(false);
            organizationFrame.setVisible(false);
        });

        assignmentsDs.addCollectionChangeListener(e -> {
            switch (e.getOperation()) {
                case ADD:
                    positionFrame.setTab("tab3").editable(true);
                    break;
                case REFRESH:
                    List<AssignmentExt> excludeItems = assignmentsDs.getItems().stream().filter(a -> CommonUtils.getSystemDate().before(a.getStartDate())
                            || CommonUtils.getSystemDate().after(a.getEndDate())).collect(Collectors.toList());
                    for (AssignmentExt excludeItem : excludeItems)
                        assignmentsDs.excludeItem(excludeItem);
            }
        });
//        com.vaadin.v7.ui.Tree vaadinTree = tree.unwrap(com.vaadin.v7.ui.Tree.class);



        hierarchyElementsDs.addItemChangeListener(e -> openFrame(e.getItem()));

        tree.addStyleName("b-tree");
        tree.setIconProvider(new ListComponent.IconProvider<HierarchyElementExt>() {
            @Nullable
            @Override
            public String getItemIcon(HierarchyElementExt entity) {
                switch (entity.getElementType()) {
                    case ORGANIZATION:
                        return "font-icon:BANK";
                    case POSITION:
                        return "font-icon:BRIEFCASE";
                    default:
                        return "font-icon:USER";
                }
            }
        });

        organizationFrame.setHierarchyElementBrowse(this);
        positionFrame.setHierarchyElementBrowse(this);

        CubaHorizontalSplitPanel vSplitPanel = (CubaHorizontalSplitPanel) WebComponentsHelper.unwrap(splitter);

        vSplitPanel.addSplitterClickListener((AbstractSplitPanel.SplitterClickListener) event -> vSplitPanel.setSplitPosition(vSplitPanel.getSplitPosition() > 0 ? 0 : 300));
    }

    @Subscribe("tree")
    protected void onTreeSelection(Tree.SelectionEvent itemClickEvent){
            HierarchyElementExt hierarchyElement = (HierarchyElementExt) itemClickEvent.getSelected().stream().findFirst().orElse(null);// tree.getDatasource().getItem(itemClickEvent.getItemId());
            if (positionDs.getItem() != null) {
                if (positionDs.getItem().getGroup() != null) {
                    if (orgAnalyticsBetweenDs.getItem() != null) {
                        Calendar newCalendar = orgAnalyticsBetweenDs.getItem().getCalendar();
                        StandardOffset newOffset = orgAnalyticsBetweenDs.getItem().getOffset();
                        DicWorkingCondition dicWorkingCondition = orgAnalyticsBetweenDs.getItem().getWorkingCondition();
                        if (newCalendar == null & newOffset == null & dicWorkingCondition == null) {
                            ((AbstractDatasource) orgAnalyticsBetweenDs).setModified(false);
                            ((AbstractDatasource) positionGroupDs).setModified(false);
                            ((AbstractDatasource) positionDs).setModified(false);
                            ((AbstractDatasource) orgAnalyticsPositionDs).setModified(false);
                        } else {
                            OrgAnalytics origAnalytics = positionDs.getItem().getGroup().getAnalytics();
                            if (origAnalytics != null) {/* item came from db*/
                                boolean ec = (newCalendar == null && origAnalytics.getCalendar() == null) ||
                                        (newCalendar != null && origAnalytics.getCalendar() != null && newCalendar.equals(origAnalytics.getCalendar()));


                                boolean eo = (newOffset == null && origAnalytics.getOffset() == null) ||
                                        (newOffset != null && origAnalytics.getOffset() != null && newOffset.equals(origAnalytics.getOffset()));

                                boolean ew = (dicWorkingCondition == null && origAnalytics.getWorkingCondition() == null) ||
                                        (dicWorkingCondition != null && origAnalytics.getWorkingCondition() != null && dicWorkingCondition.equals(origAnalytics.getWorkingCondition()));
                                if (ec && eo && ew) {/* new anal is not changed!*/
                                    ((AbstractDatasource) orgAnalyticsBetweenDs).setModified(false);
                                    ((AbstractDatasource) positionGroupDs).setModified(false);
                                    ((AbstractDatasource) positionDs).setModified(false);
                                    ((AbstractDatasource) orgAnalyticsPositionDs).setModified(false);
                                } else {
                                    if (hierarchyElement.getPositionGroup() != null) {
                                        //   orgAnalyticsPositionDs.setItem(orgAnalyticsBetweenDs.getItem());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (organizationDs.getItem() != null) {
                if (organizationDs.getItem().getGroup() != null) {
                    if (orgAnalyticsBetweenDs.getItem() != null) {
                        Calendar newCalendar = orgAnalyticsBetweenDs.getItem().getCalendar();
                        StandardOffset newOffset = orgAnalyticsBetweenDs.getItem().getOffset();
                        DicWorkingCondition dicWorkingCondition = orgAnalyticsBetweenDs.getItem().getWorkingCondition();
                        if (newCalendar == null & newOffset == null & dicWorkingCondition == null) {
                            ((AbstractDatasource) orgAnalyticsBetweenDs).setModified(false);
                            ((AbstractDatasource) orgGroupDs).setModified(false);
                            ((AbstractDatasource) organizationDs).setModified(false);
                            ((AbstractDatasource) orgAnalyticsOrganizationDs).setModified(false);
                        } else {
                            OrgAnalytics origAnalytics = organizationDs.getItem().getGroup().getAnalytics();
                            if (origAnalytics != null) {/* item came from db*/
                                boolean ec = (newCalendar == null && origAnalytics.getCalendar() == null) ||
                                        (newCalendar != null && origAnalytics.getCalendar() != null && newCalendar.equals(origAnalytics.getCalendar()));

                                boolean eo = (newOffset == null && origAnalytics.getOffset() == null) ||
                                        (newOffset != null && origAnalytics.getOffset() != null && newOffset.equals(origAnalytics.getOffset()));

                                boolean ew = (dicWorkingCondition == null && origAnalytics.getWorkingCondition() == null) ||
                                        (dicWorkingCondition != null && origAnalytics.getWorkingCondition() != null && dicWorkingCondition.equals(origAnalytics.getWorkingCondition()));
                                if (ec && eo && ew) {/* new anal is not changed!*/
                                    ((AbstractDatasource) orgAnalyticsBetweenDs).setModified(false);
                                    ((AbstractDatasource) orgGroupDs).setModified(false);
                                    ((AbstractDatasource) organizationDs).setModified(false);
                                    ((AbstractDatasource) orgAnalyticsOrganizationDs).setModified(false);
                                } else {
                                    if (hierarchyElement.getOrganizationGroup() != null) {
                                        // orgAnalyticsOrganizationDs.setItem(orgAnalyticsBetweenDs.getItem());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (getDsContext().isModified()) {
                tree.setSelectionMode(Tree.SelectionMode.NONE);

                showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
                        messages.getMainMessage("saveUnsaved"),
                        MessageType.WARNING,
                        new Action[]{
                                new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY)
                                        .withCaption(messages.getMainMessage("closeUnsaved.save"))
                                        .withHandler(event -> {

                                    save(null);
                                    tree.setSelectionMode(Tree.SelectionMode.SINGLE);
                                    tree.setSelected(hierarchyElement);
                                    hierarchyElementsDs.setItem(hierarchyElement);
                                }),
                                new BaseAction("discard")
                                        .withIcon(AppBeans.get(ThemeConstantsManager.class).getThemeValue("actions.dialog.Cancel.icon"))
                                        .withCaption(messages.getMainMessage("closeUnsaved.discard"))
                                        .withHandler(event -> {

                                    tree.setSelectionMode(Tree.SelectionMode.SINGLE);
                                    tree.setSelected(hierarchyElement);
                                    hierarchyElementsDs.setItem(hierarchyElement);
                                }),
                                new DialogAction(DialogAction.Type.CANCEL)
                        });
            } else {
                tree.setSelected(hierarchyElement);
                hierarchyElementsDs.setItem(hierarchyElement);
            }

    }

    protected void openFrame(HierarchyElementExt element) {
        if (element != null) {
            switch (element.getElementType()) {
                case ORGANIZATION: {
                    OrganizationExt reloadedItem = dataSupplier.reload(element.getOrganizationGroup().getOrganization(), organizationDs.getView());
                    organizationDs.setItem(reloadedItem);
                    setVisibleFrame(ElementType.ORGANIZATION);
                    //organizationButtons.setVisible(true);
                    //positionFrame.showButtons(false);

                    String organizationNameLang1;
                    String organizationNameLang2;
                    String organizationNameLang3;
                    FieldGroup.FieldConfig organizationNameLang1ReductedField;
                    FieldGroup.FieldConfig organizationNameLang2ReductedField;
                    FieldGroup.FieldConfig organizationNameLang3ReductedField;

                    if ((organizationNameLang1ReductedField = ((WebFieldGroup) (organizationFrame.getComponent("fieldGroup"))).getField("organizationNameLang1Reducted")) != null) {
                        if ((organizationNameLang1 = organizationDs.getItem().getOrganizationNameLang1()) != null) {
                            organizationNameLang1ReductedField.setDescription(organizationNameLang1);
                        } else {
                            organizationNameLang1ReductedField.setDescription("");
                        }
                    }

                    if ((organizationNameLang2ReductedField = ((WebFieldGroup) (organizationFrame.getComponent("fieldGroup"))).getField("organizationNameLang2Reducted")) != null) {
                        if ((organizationNameLang2 = organizationDs.getItem().getOrganizationNameLang2()) != null) {
                            organizationNameLang2ReductedField.setDescription(organizationNameLang2);
                        } else {
                            organizationNameLang2ReductedField.setDescription("");
                        }
                    }

                    if ((organizationNameLang3ReductedField = ((WebFieldGroup) (organizationFrame.getComponent("fieldGroup"))).getField("organizationNameLang3Reducted")) != null) {
                        if ((organizationNameLang3 = organizationDs.getItem().getOrganizationNameLang3()) != null) {
                            organizationNameLang3ReductedField.setDescription(organizationNameLang3);
                        } else {
                            organizationNameLang3ReductedField.setDescription("");
                        }
                    }

                    if (organizationDs.getItem().getGroup().getAnalytics() == null) {
                        OrgAnalytics analytics = metadata.create(OrgAnalytics.class);
                        orgAnalyticsBetweenDs.setItem(analytics);
                    } else {
                        orgAnalyticsBetweenDs.setItem(organizationDs.getItem().getGroup().getAnalytics());
                    }
                    break;
                }
                case POSITION: {
                    PositionExt reloadedItem = dataSupplier.reload(element.getPositionGroup().getPosition(), positionDs.getView());
                    positionDs.setItem(reloadedItem);
                    //organizationButtons.setVisible(false);
                    //positionFrame.showButtons(true);
                    setVisibleFrame(ElementType.POSITION);

                    String positionNameLang1;
                    String positionNameLang2;
                    String positionNameLang3;
                    FieldGroup.FieldConfig positionNameLang1ReductedField;
                    FieldGroup.FieldConfig positionNameLang2ReductedField;
                    FieldGroup.FieldConfig positionNameLang3ReductedField;

                    if ((positionNameLang1ReductedField = ((WebFieldGroup) (positionFrame.getComponent("fieldGroup2"))).getField("positionNameLang1Reducted")) != null) {
                        if ((positionNameLang1 = positionDs.getItem().getPositionFullNameLang1()) != null) {
                            positionNameLang1ReductedField.setDescription(positionNameLang1);
                        } else {
                            positionNameLang1ReductedField.setDescription("");
                        }
                    }

                    if ((positionNameLang2ReductedField = ((WebFieldGroup) (positionFrame.getComponent("fieldGroup2"))).getField("positionNameLang2Reducted")) != null) {
                        if ((positionNameLang2 = positionDs.getItem().getPositionFullNameLang2()) != null) {
                            positionNameLang2ReductedField.setDescription(positionNameLang2);
                        } else {
                            positionNameLang2ReductedField.setDescription("");
                        }
                    }

                    if ((positionNameLang3ReductedField = ((WebFieldGroup) (positionFrame.getComponent("fieldGroup2"))).getField("positionNameLang3Reducted")) != null) {
                        if ((positionNameLang3 = positionDs.getItem().getPositionFullNameLang3()) != null) {
                            positionNameLang3ReductedField.setDescription(positionNameLang3);
                        } else {
                            positionNameLang3ReductedField.setDescription("");
                        }
                    }

                    if (positionDs.getItem().getGroup().getAnalytics() == null) {
                        OrgAnalytics analytics = metadata.create(OrgAnalytics.class);
                        orgAnalyticsBetweenDs.setItem(analytics);
                    } else {
                        orgAnalyticsBetweenDs.setItem(positionDs.getItem().getGroup().getAnalytics());
                    }
                    break;
                }
            }
            elType = element.getElementType().toString();
        }
    }


    @Override
    public void ready() {
        super.ready();
        for (Hierarchy h : hierarchiesDs.getItems()) {
            hierarchyLookup.setValue(h);
            break;
        }
        treeEdit.setWindowParams(ParamsMap.of("openedFromHierarchyElementBrowse", ""));
        treeEdit.setAfterWindowClosedHandler((window, closeActionId) -> {
            hierarchyElementsDs.refresh();
            tree.repaint();
        });

    }


    public void close() {
        AbstractEditor<HierarchyElementExt> hierarchyElementEditor = openEditor("base$HierarchyElement.edit", hierarchyElementsDs.getItem(), WindowManager.OpenType.DIALOG, Collections.singletonMap("close", Boolean.TRUE));
        hierarchyElementEditor.addCloseWithCommitListener(() -> hierarchyElementsDs.refresh());
    }


    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
    }

    public void save(FieldGroup fieldGroup) {
        HierarchyElementExt parent = hierarchyElementsDs.getItem();

        if (validateAll()) {
            getDsContext().commit();
            if (create) {
                HierarchyElementExt model = metadata.create(HierarchyElementExt.class);
                model.setHierarchy(parent.getHierarchy());
                model.setElementType(elementType);
                model.setParent(parent);

                switch (elementType) {
                    case ORGANIZATION: {
                        model.setOrganizationGroup(organizationDs.getItem().getGroup());
                        OrganizationGroupExt organizationGroup = model.getOrganizationGroup();
                        organizationGroup.setList(new ArrayList<OrganizationExt>());
                        organizationGroup.getList().add(organizationDs.getItem());
                        organizationFrame.editable(false);
                        if (orgAnalyticsBetweenDs.getItem().getCalendar() != null || orgAnalyticsBetweenDs.getItem().getOffset() != null) {
                            orgAnalyticsOrganizationDs.setItem(orgAnalyticsBetweenDs.getItem());
                            organizationDs.getItem().getGroup().setAnalytics(orgAnalyticsOrganizationDs.getItem());
                        }
                        orgCompetenceElementsDs.commit();
                        organizationDs.commit();
                        create = false;
                        break;
                    }
                    case POSITION: {
                        model.setPositionGroup(positionDs.getItem().getGroup());
                        PositionGroupExt positionGroup = model.getPositionGroup();
                        positionGroup.setList(new ArrayList<PositionExt>());
                        positionGroup.getList().add(positionDs.getItem());
                        positionFrame.editable(false);
                        if (orgAnalyticsBetweenDs.getItem() != null) {
                            orgAnalyticsPositionDs.setItem(orgAnalyticsBetweenDs.getItem());
                            positionDs.getItem().getGroup().setAnalytics(orgAnalyticsPositionDs.getItem());
                        }
                        posCompetenceElementsDs.commit();
                        surChargeDs.commit();
                        positionDs.commit();
                        create = false;
                        break;
                    }
                }

                hierarchyElementsDs.addItem(model);
                hierarchyElementsDs.setItem(model);
                tree.setSelected(model);
                tree.expand(parent.getId());
            } else {
                switch (elementType) {
                    case ORGANIZATION: {
                        organizationFrame.editable(false);
                        if (orgAnalyticsBetweenDs.getItem().getCalendar() != null || orgAnalyticsBetweenDs.getItem().getOffset() != null) {
                            orgAnalyticsOrganizationDs.setItem(orgAnalyticsBetweenDs.getItem());
                            getDsContext().commit();
                        }
                        orgCompetenceElementsDs.commit();
                        organizationDs.commit();
                        break;
                    }
                    case POSITION: {
                        positionFrame.editable(false);
                        if (orgAnalyticsBetweenDs.getItem() != null) {
                            orgAnalyticsPositionDs.setItem(orgAnalyticsBetweenDs.getItem());
                            getDsContext().commit();
                        }
                        posCompetenceElementsDs.commit();
                        surChargeDs.commit();
                        positionDs.commit();
                        break;
                    }
                }
            }
            hierarchyElementsDs.commit();
            hierarchyElementsDs.refresh();

            if (fieldGroup != null) refreshOptionsForLookupFields(fieldGroup);

            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
            if (hierarchyElementsDs.getItem().getOrganizationGroup() != null) {
                if (hierarchyElementsDs.getItem().getOrganizationGroup().getAnalytics() != null) {
                    commitInstances.clear();
                    setWorkTime(hierarchyElementsDs.getItem(), "hierarchyElement.browse", true);
                }
            }
            if (hierarchyElementsDs.getItem().getPositionGroup() != null) {
                if (hierarchyElementsDs.getItem().getPositionGroup().getAnalytics() != null) {
                    commitInstances.clear();
                    setWorkTime(hierarchyElementsDs.getItem(), "hierarchyElement.browse", true);
                }
            }
            if (!commitInstances.isEmpty()) {
                dataManager.commit(new CommitContext(commitInstances));
            }
            getDsContext().commit();
            close(COMMIT_ACTION_ID);
            openWindow("base$HierarchyElement.browse", WindowManager.OpenType.THIS_TAB);
//            getDsContext().refresh();
        }
    }

    protected void setWorkTime(HierarchyElementExt hierarchyElement, String viewName, boolean isParent) {
        HierarchyElementExt currentHierrarchyElement = hierarchyElement;
        OrgAnalytics originalAnalitics = null;
        if (hierarchyElementsDs.getItem().getOrganizationGroup() != null) {
            originalAnalitics = hierarchyElementsDs.getItem().getOrganizationGroup().getAnalytics();
        } else if (hierarchyElementsDs.getItem().getPositionGroup() != null) {
            originalAnalitics = hierarchyElementsDs.getItem().getPositionGroup().getAnalytics();
        }
        if (currentHierrarchyElement.getOrganizationGroup() != null) {
            OrganizationGroupExt organizationGroupCurrent = currentHierrarchyElement.getOrganizationGroup();
            if (organizationGroupCurrent.getAnalytics() != null) {
                OrgAnalytics analyticsOrgBetween = organizationGroupCurrent.getAnalytics();
                analyticsOrgBetween.setCalendar(originalAnalitics.getCalendar());
                analyticsOrgBetween.setOffset(originalAnalitics.getOffset());
                analyticsOrgBetween.setWorkingCondition(originalAnalitics.getWorkingCondition());
                organizationGroupCurrent.setAnalytics(analyticsOrgBetween);
                commitInstances.add(analyticsOrgBetween);
            } else {
                OrgAnalytics analyticsOrgBetweenNew = metadata.create(OrgAnalytics.class);
                analyticsOrgBetweenNew.setCalendar(originalAnalitics.getCalendar());
                analyticsOrgBetweenNew.setOffset(originalAnalitics.getOffset());
                analyticsOrgBetweenNew.setWorkingCondition(originalAnalitics.getWorkingCondition());
                analyticsOrgBetweenNew.setOrganizationGroup(organizationGroupCurrent);
                organizationGroupCurrent.setAnalytics(analyticsOrgBetweenNew);
                commitInstances.add(analyticsOrgBetweenNew);
            }
            commitInstances.add(organizationGroupCurrent);//changed
        }
        if (currentHierrarchyElement.getPositionGroup() != null) {
            PositionGroupExt positionGroupCurrent = currentHierrarchyElement.getPositionGroup();
            if (positionGroupCurrent.getAnalytics() != null) {
                OrgAnalytics analyticsPosBetween = positionGroupCurrent.getAnalytics();
                analyticsPosBetween.setCalendar(originalAnalitics.getCalendar());
                analyticsPosBetween.setOffset(originalAnalitics.getOffset());
                analyticsPosBetween.setWorkingCondition(originalAnalitics.getWorkingCondition());
                positionGroupCurrent.setAnalytics(analyticsPosBetween);
                commitInstances.add(analyticsPosBetween);

            } else {
                OrgAnalytics analyticsPosBetweenNew = metadata.create(OrgAnalytics.class);
                analyticsPosBetweenNew.setCalendar(originalAnalitics.getCalendar());
                analyticsPosBetweenNew.setOffset(originalAnalitics.getOffset());
                analyticsPosBetweenNew.setWorkingCondition(originalAnalitics.getWorkingCondition());
                analyticsPosBetweenNew.setPositionGroup(positionGroupCurrent);
                positionGroupCurrent.setAnalytics(analyticsPosBetweenNew);
                commitInstances.add(analyticsPosBetweenNew);
            }
            commitInstances.add(positionGroupCurrent);
            LoadContext<AssignmentExt> assignmentExtLoadContext = LoadContext.create(AssignmentExt.class);
            assignmentExtLoadContext.setQuery(
                    LoadContext.createQuery(
                            "select e from base$AssignmentExt e " +
                                    " where e.positionGroup.id = :posGroupId")
                            .setParameter("posGroupId", positionGroupCurrent.getId()))
                    .setView("assignment.card");
            List<AssignmentExt> assignmentExtList = dataManager.loadList(assignmentExtLoadContext);
            OrgAnalytics finalOriginalAnalytics = originalAnalitics;
            assignmentExtList.forEach(assignmentExt -> {
                AssignmentGroupExt assignmentGroupExt = assignmentExt.getGroup();
                if (assignmentGroupExt.getAnalytics() != null) {
                    OrgAnalytics analyticsAssignmentBetween = assignmentGroupExt.getAnalytics();
                    analyticsAssignmentBetween.setOffset(finalOriginalAnalytics.getOffset());
                    analyticsAssignmentBetween.setCalendar(finalOriginalAnalytics.getCalendar());
                    analyticsAssignmentBetween.setWorkingCondition(finalOriginalAnalytics.getWorkingCondition());
                    assignmentGroupExt.setAnalytics(analyticsAssignmentBetween);
                    commitInstances.add(analyticsAssignmentBetween);
                } else {
                    OrgAnalytics analyticsAssignmentBetweenNew = metadata.create(OrgAnalytics.class);
                    analyticsAssignmentBetweenNew.setCalendar(finalOriginalAnalytics.getCalendar());
                    analyticsAssignmentBetweenNew.setOffset(finalOriginalAnalytics.getOffset());
                    analyticsAssignmentBetweenNew.setWorkingCondition(finalOriginalAnalytics.getWorkingCondition());
                    analyticsAssignmentBetweenNew.setAssignmentGroup(assignmentGroupExt);
                    assignmentGroupExt.setAnalytics(analyticsAssignmentBetweenNew);
                    commitInstances.add(analyticsAssignmentBetweenNew);
                }
                commitInstances.add(assignmentGroupExt);
            });

        }
        LoadContext<HierarchyElementExt> hierarchyElementLoadContext = LoadContext.create(HierarchyElementExt.class);
        hierarchyElementLoadContext.setQuery(
                LoadContext.createQuery(
                        "select e from base$HierarchyElementExt e " +
                                " where e.parent.id = :hierarchyElementId")
                        .setParameter("hierarchyElementId", currentHierrarchyElement.getId()))
                .setView(viewName);
        List<HierarchyElementExt> hierarchyElementList1 = dataManager.loadList(hierarchyElementLoadContext);
        hierarchyElementList1.forEach(hierarchyElement1 -> {
            setWorkTime(hierarchyElement1, viewName, false);
        });
    }


    @Inject
    protected Datasource<OrganizationGroupExt> orgGroupDs;

    public void cancel() {
        switch (elementType) {
            case ORGANIZATION: {
                if (create) {
                    OrganizationExt reloadedItem = dataSupplier.reload(hierarchyElementsDs.getItem().getOrganizationGroup().getOrganization(), organizationDs.getView());
                    organizationDs.setItem(reloadedItem);
                } else {
                    OrganizationExt reloadedItem = dataSupplier.reload(organizationDs.getItem(), organizationDs.getView());
                    organizationDs.setItem(reloadedItem);
                    for (Datasource datasource : getDsContext().getAll()) {
                        if (datasource.isModified())
                            ((AbstractDatasource) datasource).getItemsToCreate().clear();
                        ((AbstractDatasource) datasource).getItemsToUpdate().clear();
                        ((AbstractDatasource) datasource).getItemsToDelete().clear();
                    }
                }
                organizationFrame.editable(false);
                break;
            }
            case POSITION: {
                if (create) {
                    if (parentIsPosition) {
                        PositionExt reloadedItem = dataSupplier.reload(hierarchyElementsDs.getItem().getPositionGroup().getPosition(), positionDs.getView());
                        positionDs.setItem(reloadedItem);
                    } else {
                        setVisibleFrame(ElementType.ORGANIZATION);
                        OrganizationExt reloadedItem = dataSupplier.reload(hierarchyElementsDs.getItem().getOrganizationGroup().getOrganization(), organizationDs.getView());
                        organizationDs.setItem(reloadedItem);
                    }
                } else {
                    PositionExt reloadedItem = dataSupplier.reload(positionDs.getItem(), positionDs.getView());
                    positionDs.setItem(reloadedItem);
                }

                positionFrame.editable(false);
                break;
            }
        }

        com.vaadin.v7.ui.Tree vaadinTree = tree.unwrap(com.vaadin.v7.ui.Tree.class);
        vaadinTree.setSelectable(true);

    }

    public void edit(boolean isOrganization) {
        create = false;
        elementType = isOrganization ? ElementType.ORGANIZATION : ElementType.POSITION;
    }

    public void createOrganization() {
        positionDs.setItem(null);
        create = true;
        setVisibleFrame(ElementType.ORGANIZATION);
        organizationFrame.setTab("tab1");
        organizationFrame.editable(true);
        OrganizationExt organization = metadata.create(OrganizationExt.class);
        organization.setStartDate(CommonUtils.getSystemDate());
        organization.setEndDate(CommonUtils.getEndOfTime());
        DicPayroll payroll;
        if ((payroll = organizationService.getPayroll()) != null) {
            organization.setPayroll(payroll);
        }
        organizationDs.setItem(organization);
        if (PersistenceHelper.isNew(organizationDs.getItem())) {
            organizationFrame.setTabVisible("tab2", false);
            organizationFrame.setTabVisible("tab3", false);
            organizationFrame.setTabVisible("modeOfOperation", false);

        }
    }

    public void createPosition() {
        organizationDs.setItem(null);
        parentIsPosition = false;
        create = true;
        setVisibleFrame(ElementType.POSITION);
        /*positionFrame
                .setTab("tab1")
                .editable(true)
                .showButtons(false);*/
        positionDs.setItem(metadata.create(PositionExt.class));
        if (PersistenceHelper.isNew(positionDs.getItem())) {
            positionFrame.setTabVisible("tab2", false);
            positionFrame.setTabVisible("tab3", false);
            positionFrame.setTabVisible("tab4", false);
            positionFrame.setTabVisible("additionalDaysTab", false);
            positionFrame.setTabVisible("surCharges", false);
        }
    }

    public void createReport() {
        create = true;
        positionFrame
                .setTab("tab1")
                .editable(true);
        Report report = commonService.emQuerySingleRelult(Report.class, "select e from report$Report e where e.code = 'STAFF_SCHEDULE'", null);
        ReportGuiManager reportGuiManager = AppBeans.get(ReportGuiManager.class);
        Map<String, Object> map = new HashMap<>();
        map.put("sysDate", CommonUtils.getSystemDate());
        map.put("Staff_Schedule", "Штатное расписание на" + " " + CommonUtils.getSystemDate());
        switch (elType) {
            case "ORGANIZATION": {
                map.put("GroupId", hierarchyElementsDs.getItem().getOrganizationGroup());
                break;
            }
            case "POSITION": {
                map.put("GroupId", hierarchyElementsDs.getItem().getPositionGroup());
                break;
            }
        }
        reportGuiManager.printReport(report, map, null, report.getLocName());
    }


//    public void createChildPosition() {
//        organizationDs.setItem(null);
//        parentIsPosition = true;
//
//        create = true;
//        setVisibleFrame(ElementType.POSITION);
//        positionFrame
//                .setTab("tab1")
//                .editable(true)
//                .showButtons(true);
//        positionDs.setItem(metadata.create(PositionExt.class));
//        if (PersistenceHelper.isNew(positionDs.getItem())) {
//            positionFrame.setTabVisible("tab2", false);
//            positionFrame.setTabVisible("tab3", false);
//            positionFrame.setTabVisible("tab4", false);
//            positionFrame.setTabVisible("additionalDaysTab", false);
//            positionFrame.setTabVisible("surCharges", false);
//
//        }
//    }

    protected void setVisibleFrame(ElementType type) {
        organizationFrame.setVisible(type.equals(ElementType.ORGANIZATION));
        positionFrame.setVisible(type.equals(ElementType.POSITION));
        this.elementType = type;
    }

    protected void refreshOptionsForLookupFields(FieldGroup fieldGroup) {
        for (Component component : fieldGroup.getOwnComponents()) {
            if (component instanceof LookupField) {
                CollectionDatasource optionsDatasource = ((LookupField) component).getOptionsDatasource();
                if (optionsDatasource != null) {
                    optionsDatasource.refresh();
                }
            }
        }
    }

    public void searchForHierarchyElement() {
        String searchStr = searchField.getValue();
        if (searchStr != null && !StringUtils.isBlank(searchStr)) {
            String language = userSessionSource.getLocale().getLanguage();

            String hierarchyElement = "";
            String elementLangValue = "";

            if (((Hierarchy) hierarchyLookup.getValue()).getId().equals(organizationStructureConfig.getOrganizationStructureId())) {
                hierarchyElement = "organizationGroup";
                if (language.equals("ru")) {
                    elementLangValue = "organizationNameLang1";
                }
                if (language.equals("kz")) {
                    elementLangValue = "organizationNameLang2";
                }
                if (language.equals("en")) {
                    elementLangValue = "organizationNameLang3";
                }
            }

            if (((Hierarchy) hierarchyLookup.getValue()).getId().equals(positionStructureConfig.getPositionStructureId())) {
                hierarchyElement = "positionGroup";
                if (language.equals("ru")) {
                    elementLangValue = "positionFullNameLang1";
                }
                if (language.equals("kz")) {
                    elementLangValue = "positionFullNameLang1";
                }
                if (language.equals("en")) {
                    elementLangValue = "positionFullNameLang1";
                }
            }

            if (!StringUtils.isBlank(hierarchyElement) && !StringUtils.isBlank(elementLangValue)) {
                String queryForChildren = "SELECT e\n" +
                        " FROM base$HierarchyElementExt e\n" +
                        " JOIN e." + hierarchyElement + " AS og\n" +
                        " JOIN og.list AS o\n" +
                        " WHERE :systemDate BETWEEN e.startDate AND e.endDate\n" +
                        " AND e.hierarchy.id = :hierarchyLookup\n" +
                        " AND LOWER(o." + elementLangValue + ") LIKE LOWER(CONCAT( '%', :searchStr, '%' ))";
                Map<String, Object> params = new HashMap<>();
                params.put("searchStr", searchStr);
                params.put("systemDate", CommonUtils.getSystemDate());
                params.put("hierarchyLookup", hierarchyLookup.getValue());
                List<HierarchyElementExt> childrenElements = commonService.getEntities(HierarchyElementExt.class, queryForChildren, params, "hierarchyElement.parent");

                if (!childrenElements.isEmpty()) {
                    //создаем копию списка для поиска "внучек"
                    List<HierarchyElementExt> childrenElements1 = new ArrayList<>();
                    List<UUID> elementsIds = new ArrayList<>();
                    //достаем родителей
                    for (HierarchyElementExt element : childrenElements) {
                        childrenElements1.add(element);
                    }
                    for (HierarchyElementExt element : childrenElements) {
                        getParents(element, elementsIds);
                    }
                    //достаем "внучек"
                    for (HierarchyElementExt element : childrenElements1) {
                        getGrandChildren(element, elementsIds);
                    }
                    String query = "SELECT e\n" +
                            " FROM base$HierarchyElementExt e\n" +
                            " WHERE e.id IN :custom$elementsIds\n" +
                            " AND :session$systemDate between e.startDate and e.endDate";
                    Map<String, Object> map = new HashMap<>();
                    map.put("elementsIds", elementsIds);
                    hierarchyElementsDs.setQuery(query);
                    hierarchyElementsDs.refresh(map);
                    tree.expandTree();
                } else {
                    hierarchyElementsDs.setQuery("select e\n" +
                            "                           from base$HierarchyElementExt e\n" +
                            "                          where :session$systemDate between e.startDate and e.endDate\n" +
                            "                            and e.hierarchy.id = :component$hierarchyLookup");
                    hierarchyElementsDs.refresh();
                    tree.collapseTree();
                }
            }
        } else {
            hierarchyElementsDs.setQuery("select e\n" +
                    "                           from base$HierarchyElementExt e\n" +
                    "                          where :session$systemDate between e.startDate and e.endDate\n" +
                    "                            and e.hierarchy.id = :component$hierarchyLookup");
            hierarchyElementsDs.refresh();
            tree.collapseTree();
        }

        List<HierarchyElementExt> items = new ArrayList<>();

        if (((Hierarchy) hierarchyLookup.getValue()).getId().equals(organizationStructureConfig.getOrganizationStructureId())) {
            hierarchyElementsDs.getItems().forEach(element -> {
                if (element.getOrganizationGroup().getOrganization() == null) {
                    items.add(element);
                }
            });
        }

        if (((Hierarchy) hierarchyLookup.getValue()).getId().equals(positionStructureConfig.getPositionStructureId())) {
            hierarchyElementsDs.getItems().forEach(element -> {
                if (element.getPositionGroup().getPosition() == null) {
                    items.add(element);
                }
            });
        }

        for (HierarchyElementExt element : items) {
            hierarchyElementsDs.removeItem(hierarchyElementsDs.getItem(element.getId()));
        }
    }

    protected void getParents(HierarchyElementExt element, List<UUID> list) {
        list.add(element.getId());
        if (element.getParent() != null) {
            String queryString = "SELECT e FROM base$HierarchyElementExt e\n" +
                    "WHERE e.id = :id";
            Map<String, Object> map = new HashMap<>();
            map.put("id", element.getParent().getId());
            HierarchyElementExt elementExt = commonService.getEntity(HierarchyElementExt.class, queryString, map, "hierarchyElement.parent");
            getParents(elementExt, list);
        }
    }

    protected void getGrandChildren(HierarchyElementExt element, List<UUID> list) {
        if (element.getParent() != null) {
            String queryString = "SELECT e FROM base$HierarchyElementExt e\n" +
                    "WHERE e.parent.id = :id";
            Map<String, Object> map = new HashMap<>();
            map.put("id", element.getId());
            //map.put("systemDate", CommonUtils.getSystemDate());
            List<HierarchyElementExt> elementExt = commonService.getEntities(HierarchyElementExt.class, queryString, map, "hierarchyElement.parent");
            if (!elementExt.isEmpty()) {
                elementExt.forEach(e -> list.add(e.getId()));
            }
        }
    }

}