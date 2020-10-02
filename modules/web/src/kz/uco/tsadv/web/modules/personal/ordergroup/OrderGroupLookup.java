package kz.uco.tsadv.web.modules.personal.ordergroup;

import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderType;
import kz.uco.tsadv.modules.personal.group.OrderGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import kz.uco.base.web.components.CustomFilter;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class OrderGroupLookup extends AbstractLookup {

    @Inject
    private GroupDatasource<OrderGroup, UUID> orderGroupsDs;

    @Inject
    private CollectionDatasource<PersonGroupExt, UUID> approverPersonsDs;
    @Inject
    private CollectionDatasource<DicOrderStatus, UUID> orderStatusesDs;
    @Inject
    private CollectionDatasource<DicOrderType, UUID> orderTypesDs;
    @Inject
    private VBoxLayout filterBox;
    private Map<String, CustomFilter.Element> filterMap;
    private CustomFilter customFilter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("assignmentGroupId")) {
            orderGroupsDs.setQuery(CustomFilter.getFilteredQuery(orderGroupsDs.getQuery(), " exists (select 1 from tsadv$OrdAssignment oa where oa.order.id = o.id and oa.assignmentGroup.id = :param$assignmentGroupId) "));
        }
        if (params.containsKey("orderTypeCode")) {
            orderGroupsDs.setQuery(CustomFilter.getFilteredQuery(orderGroupsDs.getQuery(), " o.orderType.code = :param$orderTypeCode "));
        }

        initFilterMap();

        customFilter = CustomFilter.init(orderGroupsDs, orderGroupsDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());

    }

    private void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("orderNumber",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Order.orderNumber"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat('',o.orderNumber)) ?")
        );

        filterMap.put("orderDate",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Order.orderDate"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("o.orderDate ?")
        );

        filterMap.put("approverPersonGroup",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Order.approverPersonGroup"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", approverPersonsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "person.fioWithEmployeeNumber")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("o.approverPersonGroup.id ?")
        );

        filterMap.put("orderType",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.dictionary", "DicOrderType"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", orderTypesDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("o.orderType.id ?")
        );

        filterMap.put("orderStatus",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.dictionary", "DicOrderStatus"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", orderStatusesDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("o.orderStatus.id ?")
        );

    }

}