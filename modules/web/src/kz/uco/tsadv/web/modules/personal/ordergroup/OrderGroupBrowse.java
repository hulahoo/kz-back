package kz.uco.tsadv.web.modules.personal.ordergroup;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderType;
import kz.uco.tsadv.modules.personal.group.OrderGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.Order;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.web.modules.personal.order.OrderEdit;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class OrderGroupBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<OrderGroup, UUID> orderGroupsDs;

    @Inject
    private CollectionDatasource<AssignmentExt, UUID> ordAssignmentDs;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    protected ReportGuiManager reportGuiManager;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private VBoxLayout filterBox;
    private Map<String, CustomFilter.Element> filterMap;
    private CustomFilter customFilter;

    @Inject
    private CollectionDatasource<DicOrderStatus, UUID> orderStatusesDs;
    @Inject
    private CollectionDatasource<DicOrderType, UUID> orderTypesDs;
    @Inject
    private CollectionDatasource<PersonGroupExt, UUID> approverPersonsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

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

    public void openOrder() {
        openOrderEditor(metadata.create(Order.class));
    }

    private void openOrderEditor(Order order) {
        OrderEdit orderEdit = (OrderEdit) openEditor("tsadv$Order.edit", order, WindowManager.OpenType.THIS_TAB);
        orderEdit.addCloseListener(actionId -> orderGroupsDs.refresh());
    }


    public void openOrderEdit() {
        OrderGroup orderGroup = orderGroupsDs.getItem();
        if (orderGroup != null) {
            Order order = orderGroup.getOrder();
            if (order != null) {
                openOrderEditor(order);
            }
        }
    }

    public void closeOrder() {
    }

    public void getReport() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderGroup", orderGroupsDs.getItem().getId());

        LoadContext<Report> reportLoadContext = LoadContext.create(Report.class)
                .setQuery(LoadContext.createQuery("select r from report$Report r where r.code = :code")
                        .setParameter("code", "REG_LEAVE_REPORT"))
                .setView("report.edit");
        Report report = dataManager.load(reportLoadContext);
        reportGuiManager.printReport(report, params);
    }

    public Component getOrderInfo(OrderGroup orderGroup) {
        Label label = componentsFactory.createComponent(Label.class);

        if (orderGroup != null && orderGroup.getOrder() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            label.setValue(String.format(getMessage("OrderInfo")
                    , orderGroup.getOrder().getOrderType().getLangValue()
                    , orderGroup.getOrder().getOrderNumber()
                    , sdf.format(orderGroup.getOrder().getOrderDate())
                    , orderGroup.getOrder().getOrderStatus().getLangValue()));
        }
        return label;

    }
}