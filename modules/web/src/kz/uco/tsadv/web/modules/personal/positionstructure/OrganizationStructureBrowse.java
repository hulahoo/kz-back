package kz.uco.tsadv.web.modules.personal.positionstructure;

import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.CaptionMode;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationStructure;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class OrganizationStructureBrowse extends AbstractLookup {
    @Inject
    private CollectionDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;
    @Inject
    private GroupDatasource<OrganizationStructure, UUID> organizationStructuresDs;
    @Inject
    private VBoxLayout filterBox;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    protected CommonService commonService;
    private Map<String, CustomFilter.Element> filterMap;
    private CustomFilter customFilter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initFilterMap();

        customFilter = CustomFilter.init(organizationStructuresDs, organizationStructuresDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());

        if (params.containsKey("isMANAGER") && params.containsKey("page")) {
            if (params.get("isMANAGER").equals("MANAGER") && params.get("page").equals("selfservice")) {
                organizationStructuresDs.setQuery("select os from tsadv$OrganizationStructure os, " +
                        "   base$OrganizationExt oe, base$PositionExt pe " +
                        "   where :session$systemDate between os.startDate and os.endDate " +
                        "   and :session$systemDate between oe.startDate and oe.endDate " +
                        "   and pe.group.id=:custom$managerPositionGroupId" +
                        "   and os.path like concat('%',concat(pe.organizationGroupExt.id,'%')) " +
                        "   and oe.deleteTs is null ");
                organizationStructuresDs.refresh(params);
            }
        }
    }

    private void initFilterMap() {
        filterMap = new LinkedHashMap<>();

        filterMap.put("organization",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Organization"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", organizationGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "organization.organizationName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.organizationGroup.id ?")
        );

        filterMap.put("parentOrganization",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "PositionStructure.parentOrganizationGroup"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", organizationGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "organization.organizationName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.NOT_EMPTY)
                        .setQueryFilter("e.parentOrganizationGroup.id ?")
        );
    }
}