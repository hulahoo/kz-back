package kz.uco.tsadv.web.modules.personal.businesstrip;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderStatus;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.BusinessTrip;
import kz.uco.tsadv.modules.personal.model.Order;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class BusinessTripEditCancel extends AbstractEditor<BusinessTrip> {

    @Named("businessTripFieldGroup.order")
    private LookupField cancelOrderField;

    @Inject
    private Datasource<BusinessTrip> businessTripDs;

    @Inject
    protected UserSession userSession;

    @Inject
    protected CollectionDatasource ordersDs;

    @WindowParam
    private Datasource<AssignmentExt> assignmentDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (assignmentDs != null) {
            ordersDs.setQuery("select e from tsadv$Order e join " +
                    "tsadv$OrdAssignment o on o.order.id = e.id " +
                    "where o.assignmentGroup.id = :custom$assignmentGroupId");
            Map<String, Object> map = new HashMap<>();
            map.put("assignmentGroupId", assignmentDs.getItem().getGroup().getId());
            ordersDs.refresh(map);
        }

        cancelOrderField.addValueChangeListener(e -> {
            if (cancelOrderField.getValue() != null) {
                getItem().setCancelOrderNumber(((Order) cancelOrderField.getValue()).getOrderNumber());
                getItem().setCancelOrderDate(((Order) cancelOrderField.getValue()).getOrderDate());
            } else {
                getItem().setCancelOrderNumber(null);
                getItem().setCancelOrderDate(null);
            }
        });
    }

    @Override
    public void ready() {
        super.ready();
        cancelOrderField.setValue(null);
        //getItem().setOrder(null);
        getItem().setCancelOrderDate(null);
        getItem().setCancelOrderNumber(null);
    }

    @Override
    protected boolean preCommit() {
        if (businessTripDs.isModified()) {
            getItem().setStatus(BusinessTripOrderStatus.CANCELED);
        }
        return super.preCommit();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && close) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }
}