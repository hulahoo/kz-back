package kz.uco.tsadv.web.modules.personal.dismissal;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.EmployeeConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicDismissalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicLCArticle;
import kz.uco.tsadv.modules.personal.enums.ArticleAttribute;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.model.Dismissal;
import kz.uco.tsadv.modules.personal.model.Order;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.service.OrderNumberService;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DismissalEdit extends AbstractEditor<Dismissal> {

    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected CommonService commonService;
    @Inject
    protected AssignmentService assignmentService;
    @Inject
    protected BusinessRuleService businessRuleService;
    @Inject
    protected OrderNumberService orderNumberService;
    @Inject
    protected EmployeeConfig employeeConfig;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Datasource<Dismissal> dismissalDs;
    @Inject
    protected CollectionDatasource<AssignmentGroupExt, UUID> assignmentGroupsDs;
    @Inject
    protected DataManager dataManager;
    @Named("fieldGroup.order")
    protected PickerField<Entity> orderField;
    @Named("fieldGroup.orderNumber")
    protected TextField orderNumberField;
    @Named("fieldGroup.orderDate")
    protected DateField orderDateField;
    @Named("fieldGroup.lcArticle")
    protected LookupPickerField lcArticleField;
    @Named("fieldGroup.assignmentGroup")
    protected LookupPickerField assignmentGroupField;
    @Named("fieldGroup.dismissalDate")
    protected DateField dismissalDateField;
    @Inject
    protected CollectionDatasource<DicLCArticle, UUID> dicLCArticlesDs;

    protected boolean openedForEdit;

    protected boolean newItem = false;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("openedForEdit")) {
            openedForEdit = true;
        }
        lcArticleFilter();
    }

    @Override
    protected void postInit() {
        super.postInit();
        orderField.addValueChangeListener(e -> {
            Order order = (Order) e.getValue();
            if (order != null) {
                String orderNumber = order.getOrderNumber();
                Date orderDate = order.getOrderDate();
                orderNumberField.setValue(orderNumber);
                orderDateField.setValue(orderDate);
                orderNumberField.setEditable(false);
                orderDateField.setEditable(false);
            } else {
                orderNumberField.setEditable(true);
                orderDateField.setEditable(true);
                orderNumberField.setValue(null);
                orderDateField.setValue(null);
            }
        });

        if (getItem().getOrdAssignment() != null) {
            /*getItem().getOrderGroup().setList(new ArrayList<Order>());
            getItem().getOrderGroup().getList().add(getItem().getOrdAssignment().getOrder());*/
            fieldGroup.getFieldNN("status").setEditable(false);
            fieldGroup.getFieldNN("personGroup").setEditable(false);
            /*fieldGroup.getField("orderGroup").setVisible(false);
            fieldGroup.getField("orderGroup").setEnabled(false);*/
        } else {
            fieldGroup.getFieldNN("status").setEditable(true);
            fieldGroup.getFieldNN("personGroup").setEditable(false);
            /*fieldGroup.getField("orderGroup").setVisible(true);
            fieldGroup.getField("orderGroup").setEditable(false);*/
        }

        if (getItem().getStatus() != null && "APPROVED".equals(getItem().getStatus().getCode())) {
            fieldGroup.getFieldNN("dismissalDate").setEditable(false);
            fieldGroup.getFieldNN("requestDate").setEditable(false);
            fieldGroup.getFieldNN("lcArticle").setEditable(false);
            fieldGroup.getFieldNN("dismissalReason").setEditable(false);
        }

        if (!PersistenceHelper.isNew(getItem())
                && getItem().getDismissalDate() != null
                && DateUtils.addMonths(getItem().getDismissalDate(), 3).before(BaseCommonUtils.truncDate(new Date()))) {
            fieldGroup.getFieldNN("dismissalReason").setEditable(false);
        }

    }

    @Override
    protected void initNewItem(Dismissal item) {
        super.initNewItem(item);
        newItem = true;
        item.setStatus(commonService.getEntity(DicDismissalStatus.class, "ACTIVE"));
        item.setRequestDate(CommonUtils.getSystemDate());
        if (item.getOrdAssignment() != null) {
            item.setPersonGroup(item.getOrdAssignment().getAssignmentGroup().getAssignment().getPersonGroup());
            item.setOrderGroup(item.getOrdAssignment().getOrder().getGroup());
        }
    }

    @Override
    public void ready() {
        super.ready();
        if (assignmentGroupsDs.getItems().size() == 1) {
            assignmentGroupField.setValue(assignmentGroupsDs.getItems().iterator().next());
        }
        if (openedForEdit) {
            dismissalDateField.setEditable(false);
        }
        if (newItem && employeeConfig.getEnableOrderNumberAutogenerationForDismissals()) {
            orderNumberField.setValue((orderNumberService.getLastDismissalOrderNumber() + 1) + "");
        }
    }

    @Override
    protected boolean preCommit() {
        if (newItem) {
            if (assignmentService.isTransferInFutureExists(getItem().getPersonGroup(), getItem().getDismissalDate())) {
                showNotification(getMessage("Attention"), businessRuleService.getBusinessRuleMessage("transferInFutureExists"), NotificationType.TRAY);
                return false;
            }
        }
        if (assignmentService.isDismissalInFutureExists(getItem(), CommonUtils.getSystemDate())) {
            showNotification(getMessage("Attention"), businessRuleService.getBusinessRuleMessage("dismissalInFutureExists"), NotificationType.TRAY);
            return false;
        }
        return super.preCommit();
    }

    protected void lcArticleFilter() {
        Map<String, Object> customMap = new HashMap<>();
        dicLCArticlesDs.setQuery("select e from tsadv$DicLCArticle e where e.attribute = :custom$articleName order by e.article ASC, e.item ASC, e.subItem ASC");
        customMap.put("articleName", ArticleAttribute.DISMISSAL);
        dicLCArticlesDs.refresh(customMap);
    }
}