package kz.uco.tsadv.web.modules.personal.positionstructure;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class PositionStructureBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<PositionExt, UUID> positionExtDs;
    @Inject
    protected VBoxLayout filterBox;
    @Inject
    protected ComponentsFactory componentsFactory;

    protected Map<String, CustomFilter.Element> filterMap;

    protected CustomFilter customFilter;

    protected Map<String, Object> param;
    @Inject
    protected UserSession userSession;
    @Inject
    protected RecruitmentConfig recruitmentConfig;
    @Inject
    protected DataGrid<PositionExt> positionExtTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param = params;
        if (params.containsKey("fromRequisition") && Boolean.TRUE.equals(params.get("fromRequisition"))) {
            positionExtDs.setQuery("select pe\n" +
                    "from base$PositionExt pe\n" +
                    "where pe.organizationGroupExt.id in\n" +
                    "      (select oe.group.id\n" +
                    "       from base$OrganizationExt oe\n" +
                    "       where (:session$systemDate between oe.startDate and oe.endDate\n" +
                    "         or (oe.startDate > :session$systemDate and\n" +
                    "             oe.startDate in (select min(p.startDate) from base$OrganizationExt p where p.group.id = oe.group.id))))\n" +
                    "  and (:session$systemDate between pe.startDate and pe.endDate\n" +
                    "  or (pe.startDate > :session$systemDate and\n" +
                    "      pe.startDate in (select min(p.startDate) from base$PositionExt p where p.group.id = pe.group.id)))\n" +
                    "  and pe.fte > 0\n" +
                    "  and pe.deleteTs is null");
        }
        if (recruitmentConfig.getOrganizationGenerationType().name().equals("MANAGER") && ((param.get("page") != null) && param.get("page").equals("selfservice"))) {
            positionExtDs.setQuery("select pe\n" +
                    "from base$PositionExt pe\n" +
                    "where pe.organizationGroupExt.id in\n" +
                    "      (select os.organizationGroup.id\n" +
                    "       from tsadv$OrganizationStructure os\n" +
                    "       where os.path like concat('%', concat(\n" +
                    "           (select ppe.organizationGroupExt.id\n" +
                    "            from base$PositionExt ppe\n" +
                    "            where ppe.group.id = '" + userSession.getAttribute(StaticVariable.POSITION_GROUP_ID) + "' \n" +
                    "              and :session$systemDate between ppe.startDate and ppe.endDate), '%'))\n" +
                    "         and (:session$systemDate between os.startDate and os.endDate\n" +
                    "         or (os.startDate > :session$systemDate and\n" +
                    "             os.startDate in (select min(p.startDate) from base$OrganizationExt p where p.group.id = os.organizationGroup.id))))\n" +
                    "  and (:session$systemDate between pe.startDate and pe.endDate\n" +
                    "  or (pe.startDate > :session$systemDate and\n" +
                    "      pe.startDate in (select min(p.startDate) from base$PositionExt p where p.group.id = pe.group.id)))\n" +
                    "  and pe.fte > 0\n" +
                    "  and pe.deleteTs is null");
        }
//        if (params.containsKey("personGroupConfigMap")){
//            positionExtTable.getColumn("managerFlag").setVisible(false);
//        }
//
//        initFilterMap();
//
//        customFilter = CustomFilter.init(positionExtDs, positionExtDs.getQuery(), filterMap);
//        filterBox.add(customFilter.getFilterComponent());
    }

//    protected void initFilterMap() {
//        filterMap = new LinkedHashMap<>();
//
//        filterMap.put("organization",
//                CustomFilter.Element
//                        .initElement()
//                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Organization"))
//                        .setComponentClass(LookupPickerField.class)
//                        .addComponentAttribute("optionsDatasource", organizationGroupsDs)
//                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
//                        .addComponentAttribute("captionProperty", "organization.organizationName")
//                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
//                        .setQueryFilter("e.organizationGroup.id ?")
//        );
//
//        filterMap.put("position",
//                CustomFilter.Element
//                        .initElement()
//                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position"))
//                        .setComponentClass(LookupPickerField.class)
//                        .addComponentAttribute("optionsDatasource", positionGroupsDs)
//                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
//                        .addComponentAttribute("captionProperty", "position.positionName")
//                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
//                        .setQueryFilter("e.positionGroup.id ?")
//        );
//
//        filterMap.put("parentPosition",
//                CustomFilter.Element
//                        .initElement()
//                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "PositionStructure.parentPositionGroup"))
//                        .setComponentClass(LookupPickerField.class)
//                        .addComponentAttribute("optionsDatasource", positionGroupsDs)
//                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
//                        .addComponentAttribute("captionProperty", "position.positionName")
//                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.NOT_EMPTY)
//                        .setQueryFilter("e.parentPositionGroup.id ?")
//        );
//
//
//        filterMap.put("managerFlag",
//                CustomFilter.Element
//                        .initElement()
//                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "PositionStructure.managerFlag"))
//                        .setComponentClass(CheckBox.class)
//                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
//                        .setQueryFilter("e.managerFlag ?")
//        );
//    }

}