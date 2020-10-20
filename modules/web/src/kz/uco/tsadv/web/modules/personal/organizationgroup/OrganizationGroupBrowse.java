package kz.uco.tsadv.web.modules.personal.organizationgroup;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.screen.OpenMode;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.dictionary.DicOrgType;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.web.modules.filterconfig.FilterConfig;
import kz.uco.tsadv.web.modules.personal.organization.OrganizationEdit;

import javax.inject.Inject;
import java.util.*;

public class OrganizationGroupBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;

    @Inject
    protected CollectionDatasource<OrganizationExt, UUID> listDs;

    @Inject
    protected Metadata metadata;

    @Inject
    protected CollectionDatasource<DicCostCenter, UUID> costCentersDs;
    @Inject
    protected CollectionDatasource<DicLocation, UUID> locationsDs;
    @Inject
    protected CollectionDatasource<DicOrgType, UUID> organizationTypesDs;
    @Inject
    protected CollectionDatasource<DicPayroll, UUID> payrollsDs;
    @Inject
    protected FilterConfig filterConfig;
    @Inject
    protected VBoxLayout filterBox;
    protected Map<String, CustomFilter.Element> filterMap;

    protected CustomFilter customFilter;
    @Inject
    protected GroupBoxLayout groupBox;
    @Inject
    protected Filter organizationGroupsFilter;
    @Inject
    private CommonService commonService;
    @Inject
    protected ScreenBuilders screenBuilders;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        setupFilter();
    }

    protected void setupFilter() {
        if (filterConfig.getOrganizationEnableCustomFilter()) {
            initFilterMap();
            customFilter = CustomFilter.init(organizationGroupsDs, organizationGroupsDs.getQuery(), filterMap);
            filterBox.add(customFilter.getFilterComponent());

        } else {
            groupBox.setVisible(false);
        }
        organizationGroupsFilter.setVisible(filterConfig.getOrganizationEnableCubaFilter());
    }

    protected void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("organizationNameRu",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Organization.organizationNameRu"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(o.organizationNameLang1) ?")
        );
        filterMap.put("organizationNameKz",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Organization.organizationNameKz"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(o.organizationNameLang2) ?")
        );
        filterMap.put("organizationNameEn",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Organization.organizationNameEn"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(o.organizationNameLang3) ?")
        );

        filterMap.put("costCenter",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.dictionary", "DicCostCenter"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", costCentersDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("o.costCenter.id ?")
        );

        filterMap.put("payroll",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.dictionary", "DicPayroll"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", payrollsDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("o.payroll.id ?")
        );

        filterMap.put("location",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.dictionary", "DicLocation"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", locationsDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("o.location.id ?")
        );

        filterMap.put("orgType",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.dictionary", "DicOrgType"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", organizationTypesDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("o.type.id ?")
        );
    }

    public void create() {
        OrganizationExt organizationExt = metadata.create(OrganizationExt.class);
        OrganizationGroupExt organizationGroupExt = metadata.create(OrganizationGroupExt.class);
        organizationExt.setGroup(organizationGroupExt);
        openOrganizationEditor(organizationExt, null);
    }

    public void edit() {
        OrganizationGroupExt organizationGroup = organizationGroupsDs.getItem();
        if (organizationGroup != null) {
            OrganizationExt organization = organizationGroup.getOrganization();
            if (organization != null) {
                openOrganizationEditor(organization, null);
            }
        }
    }

    protected void openOrganizationEditor(OrganizationExt organization, Map<String, Object> params) {
        OrganizationEdit organizationEdit = (OrganizationEdit) screenBuilders.editor(OrganizationExt.class, this)
                .editEntity(organization)
                .withLaunchMode(OpenMode.THIS_TAB)
                .build()
                .show();
        organizationEdit.addAfterCloseListener(actionId ->{
            organizationGroupsDs.refresh();
        });
//        OrganizationEdit organizationEdit = (OrganizationEdit) openEditor("base$Organization.edit", organization, WindowManager.OpenType.THIS_TAB, params);
//        organizationEdit.addCloseListener(actionId ->
//                organizationGroupsDs.refresh());
    }

    public void editHistory() {
        OrganizationExt item = listDs.getItem();

        List<OrganizationExt> items = organizationGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        if (item != null) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("editHistory", Boolean.TRUE);
            paramsMap.put("firstRow", items.indexOf(item) == 0);
            paramsMap.put("lastRow", items.indexOf(item) == items.size() - 1);

            openOrganizationEditor(item, paramsMap);
        }
    }

    public void removeHistory() {
        OrganizationExt item = listDs.getItem();
        List<OrganizationExt> items = organizationGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            showNotification(getMessage("removeHistory.deny"));
        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    if (index == items.size() - 1) {
                                        items.get(index - 1).setEndDate(item.getEndDate());
                                        listDs.modifyItem(items.get(index - 1));
                                    } else {
                                        items.get(index + 1).setStartDate(item.getStartDate());
                                        listDs.modifyItem(items.get(index + 1));
                                    }

                                    listDs.removeItem(item);

                                    listDs.getDsContext().commit();
                                    if (listDs.getItems().isEmpty()) {
                                        organizationGroupsDs.removeItem(item.getGroup());
                                        organizationGroupsDs.getDsContext().commit();
                                    }

                                    organizationGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public void removeHistories() {
        OrganizationExt item = listDs.getItem();
        List<OrganizationExt> items = organizationGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            showNotification(getMessage("removeHistory.deny"));
        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    items.get(index - 1).setEndDate(items.get(items.size() - 1).getEndDate());
                                    listDs.modifyItem(items.get(index - 1));

                                    while (index < items.size())
                                        listDs.removeItem(items.get(index));

                                    listDs.getDsContext().commit();
                                    if (listDs.getItems().isEmpty()) {
                                        organizationGroupsDs.removeItem(item.getGroup());
                                        organizationGroupsDs.getDsContext().commit();
                                    }

                                    organizationGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public void orgAnalyticsEdit() {
        OrgAnalytics orgAnalytics = commonService.getEntity(OrgAnalytics.class,
                "select e from tsadv$OrgAnalytics e where e.organizationGroup.id = :orgGroupId",
                ParamsMap.of("orgGroupId", organizationGroupsDs.getItem().getId()),
                "organalytics.edit");
        if (orgAnalytics == null) {
            orgAnalytics = metadata.create(OrgAnalytics.class);
            orgAnalytics.setOrganizationGroup(organizationGroupsDs.getItem());
        }
        AbstractEditor abstractEditor = openEditor(orgAnalytics, WindowManager.OpenType.THIS_TAB, ParamsMap.empty());
        abstractEditor.addCloseWithCommitListener(() -> organizationGroupsDs.refresh());
    }
}