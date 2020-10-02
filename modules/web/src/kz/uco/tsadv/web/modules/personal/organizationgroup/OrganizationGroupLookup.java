package kz.uco.tsadv.web.modules.personal.organizationgroup;

import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.dictionary.DicOrgType;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.web.modules.filterconfig.FilterConfig;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class OrganizationGroupLookup extends AbstractLookup {
    @Inject
    protected GroupDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;

    @Inject
    protected CollectionDatasource<DicCostCenter, UUID> costCentersDs;
    @Inject
    protected CollectionDatasource<DicLocation, UUID> locationsDs;
    @Inject
    protected CollectionDatasource<DicOrgType, UUID> organizationTypesDs;
    @Inject
    protected CollectionDatasource<DicPayroll, UUID> payrollsDs;

    @Inject
    protected VBoxLayout filterBox;
    protected Map<String, CustomFilter.Element> filterMap;

    protected CustomFilter customFilter;
    @Inject
    protected FilterConfig filterConfig;
    @Inject
    protected GroupBoxLayout groupBox;
    @Inject
    protected Filter organizationGroupsFilter;
    @WindowParam
    protected Object openedFromHierarchyElementEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.get("posOrgJob") != null && (boolean) params.get("posOrgJob")) {
            organizationGroupsDs.setQuery("select e " +
                    "                           from base$OrganizationGroupExt e " +
                    "                           join e.list o " +
                    "                          where :session$systemDate between o.startDate and o.endDate " +
                    "                                and e.id = COALESCE(:param$orgGroupId, e.id) " +
                    "                                and ( e.id = COALESCE(:param$jobGroupId, e.id) or " +
                    "                                       e.id in ( select p.organizationGroupExt.id from base$PositionExt p " +
                    "                                                    where p.jobGroup.id = COALESCE(:param$jobGroupId, p.jobGroup.id) " +
                    "                                                        and :session$systemDate between p.startDate and p.endDate ) )");
        }
        if (openedFromHierarchyElementEdit!=null) {
            organizationGroupsDs.setQuery("SELECT e FROM base$OrganizationGroupExt e            " +
                    "                      JOIN e.list o                                        " +
                    "                      WHERE :session$systemDate BETWEEN o.startDate AND o.endDate " +
                    "                      AND e.id NOT IN (SELECT he.organizationGroup.id    " +
                    "                      FROM base$HierarchyElementExt he)                    ");
            organizationGroupsDs.refresh();
        }
        if (filterConfig.getOrganizationEnableCustomFilter()) {
            initFilterMap();
            customFilter = CustomFilter.init(organizationGroupsDs, organizationGroupsDs.getQuery(), filterMap);
            filterBox.add(customFilter.getFilterComponent());

        } else {
            groupBox.setVisible(false);
        }
        organizationGroupsFilter.setVisible(filterConfig.getOrganizationEnableCubaFilter());

    }

    private void initFilterMap() {
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

}