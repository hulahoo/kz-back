package kz.uco.tsadv.web.modules.personal.businesstrip;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderStatus;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderType;
import kz.uco.tsadv.modules.personal.model.BusinessTrip;
import kz.uco.tsadv.modules.personal.model.BusinessTripLines;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class BusinessTripEdit extends AbstractEditor<BusinessTrip> {
    @Inject
    protected Datasource<BusinessTrip> businessTripDs;
    @Inject
    protected Table<BusinessTripLines> businessTripLinesTable;
    @Inject
    protected CollectionDatasource<BusinessTripLines, UUID> businessTripLinesDs;
    @Inject
    protected ButtonsPanel buttonsPanelBusinessTripCost;
    @Inject
    protected ButtonsPanel buttonsPanelBusinessTripLines;
    @Named("fieldGroup1.type")
    protected PickerField type;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        buttonsPanelBusinessTripCost.setEnabled(false);
        businessTripLinesDs.addItemChangeListener(e -> {
            if (businessTripLinesDs.getItem() == null) {
                buttonsPanelBusinessTripCost.setEnabled(false);
            } else {
                buttonsPanelBusinessTripCost.setEnabled(true);
            }
        });

        type.removeAllActions();
        type.addLookupAction();
        type.addClearAction();
    }

    @Override
    protected void initNewItem(BusinessTrip item) {
        super.initNewItem(item);
        item.setStatus(BusinessTripOrderStatus.PLANNED);
        if (item.getOrdAssignment() != null) {
            item.setPersonGroup(item.getOrdAssignment().getAssignmentGroup().getAssignment().getPersonGroup());
        }
        item.setOrderDate(CommonUtils.getSystemDate());
    }

    @Override
    protected void postInit() {
        super.postInit();
        if (getItem().getTypeTrip() == BusinessTripOrderType.ADDITIONALCHANGE &&
                getItem().getStatus() == BusinessTripOrderStatus.APPROVED) {
            buttonsPanelBusinessTripLines.setEnabled(false);
        }
        businessTripLinesTable.sortBy(businessTripLinesDs.getMetaClass().getPropertyPath("number"), true);

    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && close) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }
}