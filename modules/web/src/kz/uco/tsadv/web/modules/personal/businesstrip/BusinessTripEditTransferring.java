package kz.uco.tsadv.web.modules.personal.businesstrip;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderStatus;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderType;
import kz.uco.tsadv.modules.personal.model.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class BusinessTripEditTransferring extends AbstractEditor<BusinessTrip> {

    @Named("businessTripFieldGroup.order")
    private LookupField order;

    @Inject
    private Datasource<BusinessTrip> businessTripNewDs;

    @Inject
    protected CollectionDatasource ordersDs;

    @WindowParam
    private Datasource<AssignmentExt> assignmentDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (assignmentDs!=null) {
            ordersDs.setQuery("select e from tsadv$Order e join " +
                    "tsadv$OrdAssignment o on o.order.id = e.id " +
                    "where o.assignmentGroup.id = :custom$assignmentGroupId");
            Map<String, Object> map = new HashMap<>();
            map.put("assignmentGroupId", assignmentDs.getItem().getGroup().getId());
            ordersDs.refresh(map);
        }

            order.addValueChangeListener(e -> {
            if (order.getValue()!=null){
                businessTripNewDs.getItem().setOrderNum(((Order)order.getValue()).getOrderNumber());
                businessTripNewDs.getItem().setOrderDate(((Order)order.getValue()).getOrderDate());
            }
            else{
                businessTripNewDs.getItem().setOrderNum(null);
                businessTripNewDs.getItem().setOrderDate(null);
            }
        });
    }

    @Override
    public void ready() {
        super.ready();
        businessTripNewDs.getItem().setOrder(null);
        businessTripNewDs.getItem().setOrderNum(null);
        businessTripNewDs.getItem().setOrder(null);
    }

    @Override
    protected boolean preCommit() {
        businessTripNewDs.getItem().setStatus(BusinessTripOrderStatus.APPROVED);
        businessTripNewDs.getItem().setTypeTrip(BusinessTripOrderType.TRANSFER);
        return super.preCommit();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        //copyBusinessTripCosts();
        if (committed && close){
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }
}