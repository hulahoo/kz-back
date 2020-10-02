package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.PopupButton;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderStatus;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.BusinessTrip;
import kz.uco.tsadv.modules.personal.model.BusinessTripLines;
import kz.uco.tsadv.web.modules.personal.businesstrip.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class PcfTrip extends EditableFrame {

    @Named("businessTripTable.create")
    public CreateAction businessTripTableCreate;
    @Named("businessTripTable.edit")
    public EditAction businessTripTableEdit;
    @Inject
    protected ButtonsPanel buttonsPanelTrip;
    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected CollectionDatasource<BusinessTrip, UUID> businessTripDs;
    @Inject
    protected Table businessTripTable;
    @Inject
    protected Metadata metadata;
    @Inject
    protected PopupButton popupButton;
    protected CollectionDatasource ordersDs;
    protected Datasource<AssignmentExt> assignmentDs;
    protected PersonGroupExt personGroupExt;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (getDsContext().get("personGroupDs") != null) {
            personGroupExt = (PersonGroupExt) getDsContext().get("personGroupDs").getItem();
        }
        businessTripTableCreate.setInitialValues(Collections.singletonMap("personGroup", personGroupExt));
        businessTripTableCreate.setEditorCloseListener(actionId -> {
            businessTripDs.refresh();
        });
        businessTripTableEdit.setEditorCloseListener(actionId -> {
            businessTripDs.refresh();
        });
        businessTripDs.addItemChangeListener(e -> {
            if (businessTripDs.getItem() == null) {
                popupButton.setEnabled(false);
            } else {
                popupButton.setEnabled(true);
            }
        });
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanelTrip.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        assignmentDs = getDsContext().get("assignmentDs");
        /*ordersDs = new DsBuilder(getDsContext())
                .setJavaClass(Order.class)
                .setViewName("order-view")
                .setId("ordersDs")
                .buildGroupDatasource();
        ordersDs.setQuery("select e from tsadv$Order e join " +
                "tsadv$OrdAssignment o on o.order.id = e.id " +
                "where o.assignmentGroup.id = :custom$assignmentGroupId");
        Map<String, Object> map = new HashMap<>();
        map.put("assignmentGroupId", assignmentDs.getItem().getGroup().getId());
        ordersDs.refresh(map);*/
    }


    public void cancel(Component source) {
        if (businessTripDs.getItems() != null) {
            BusinessTripEditCancel businessTripEditCancel = (BusinessTripEditCancel) openEditor("tsadv$BusinessTrip.Cancel",
                    businessTripDs.getItem(), WindowManager.OpenType.DIALOG, ParamsMap.of("assignmentDs", assignmentDs));
            businessTripEditCancel.addCloseListener(actionId -> {
                assignmentDs.refresh();
                businessTripDs.refresh();
            });
        } else {
            showNotification(getMessage("businessTripCancelError"), NotificationType.TRAY);
        }

    }


    public void comment(Component source) {
        BusinessTrip businessTripNew = getCopyBusinessTrip();
        businessTripNew.setBusinessTripLines(null);
        businessTripNew.setParentBusinessTrip(businessTripDs.getItem());
        BusinessTripEditComment businessTripEditComment = (BusinessTripEditComment) openEditor("tsadv$BusinessTrip.Сomment",
                businessTripNew, WindowManager.OpenType.DIALOG, ParamsMap.of("assignmentDs", assignmentDs, "ordersDs", ordersDs));
        businessTripEditComment.addCloseWithCommitListener(() -> {
            businessTripDs.getItem().setStatus(BusinessTripOrderStatus.CHANGED);
            businessTripDs.getItem().setTypeTrip(BusinessTripOrderType.RECALL);
            businessTripDs.commit();
            assignmentDs.refresh();
        });
    }

    protected BusinessTrip getCopyBusinessTrip() {
        BusinessTrip businessTrip = metadataTools.deepCopy(businessTripDs.getItem());
        businessTrip.setPersonGroup(personGroupExt);
        businessTrip.setUuid(UUID.randomUUID());
        return businessTrip;
    }

    public void transferring(Component source) {
        BusinessTrip businessTripNew = getCopyBusinessTrip();
        businessTripNew.setDateFrom(null);
        businessTripNew.setDateTo(null);

        businessTripNew.setBusinessTripLines(copyBusinessTripLines(businessTripDs.getItem().getBusinessTripLines()));
        businessTripNew.setParentBusinessTrip(businessTripDs.getItem());
        BusinessTripEditTransferring businessTripEditTransferring = (BusinessTripEditTransferring) openEditor("tsadv$BusinessTrip.Transferring",
                businessTripNew, WindowManager.OpenType.DIALOG, ParamsMap.of("assignmentDs", assignmentDs, "businessTripDs", businessTripDs));
        businessTripEditTransferring.addCloseWithCommitListener(() -> {
            businessTripDs.getItem().setStatus(BusinessTripOrderStatus.CHANGED);
            businessTripDs.getItem().setTypeTrip(BusinessTripOrderType.TRANSFER);
            businessTripDs.commit();
            businessTripDs.refresh();
        });

    }

    public void extension(Component source) {
        BusinessTrip businessTripNew = getCopyBusinessTrip();
        Calendar cal = Calendar.getInstance();
        cal.setTime(businessTripNew.getDateTo());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        businessTripNew.setDateFrom(cal.getTime());
        businessTripNew.setDateTo(null);
        //businessTripNew.setBusinessTripLines(null);
        businessTripNew.setBusinessTripLines(copyBusinessTripLines(businessTripDs.getItem().getBusinessTripLines()));
        businessTripNew.setParentBusinessTrip(businessTripDs.getItem());
        BusinessTripEditExtension businessTripEditExtention = (BusinessTripEditExtension) openEditor("tsadv$BusinessTrip.Extension",
                businessTripNew, WindowManager.OpenType.DIALOG, ParamsMap.of("assignmentDs", assignmentDs));
        businessTripEditExtention.addCloseWithCommitListener(() -> {
            businessTripDs.getItem().setStatus(BusinessTripOrderStatus.CHANGED);
            businessTripDs.getItem().setTypeTrip(BusinessTripOrderType.EXTENDED);
            businessTripDs.commit();
            businessTripDs.refresh();
        });
    }


    public void additionalchange(Component source) {
        BusinessTrip businessTripNew = getCopyBusinessTrip();
        businessTripNew.setParentBusinessTrip(businessTripDs.getItem());

        List<BusinessTripLines> linesList = businessTripNew.getBusinessTripLines();
        List<BusinessTripLines> linesListFinaly = new ArrayList<>();
        businessTripNew.setBusinessTripLines(null);
        for (BusinessTripLines linesItem : linesList) {
            linesItem.setUuid(UUID.randomUUID());
            linesListFinaly.add(linesItem);
        }
        businessTripNew.setBusinessTripLines(linesListFinaly);
        BusinessTripEditAdditionalChange businessTripEditAdditionalChange = (BusinessTripEditAdditionalChange) openEditor("tsadv$BusinessTrip.AdditionalChange",
                businessTripNew, WindowManager.OpenType.DIALOG, ParamsMap.of("assignmentDs", assignmentDs));
        businessTripEditAdditionalChange.addCloseWithCommitListener(() -> {
            businessTripDs.getItem().setStatus(BusinessTripOrderStatus.CHANGED);
            businessTripDs.getItem().setTypeTrip(BusinessTripOrderType.ADDITIONALCHANGE);
            businessTripDs.commit();
            businessTripDs.refresh();
        });
    }

    protected List<BusinessTripLines> copyBusinessTripLines(List<BusinessTripLines> businessTripLines) {
        List<BusinessTripLines> result = new ArrayList<>();
        if (businessTripLines == null || businessTripLines.isEmpty()) {
            return result;
        }
        for (BusinessTripLines businessTripLine : businessTripLines) {
            BusinessTripLines businessTripLineNew = metadataTools.deepCopy(businessTripLine);
            businessTripLineNew.setId(UUID.randomUUID());
            /*if (!businessTripLine.getBusinessTripCost().isEmpty()) {
                List<BusinessTripCost> businessTripCostsListNew = new ArrayList<>();
                for (BusinessTripCost businessTripCost : businessTripLine.getBusinessTripCost()) {
                    BusinessTripCost businessTripCostNew = metadataTools.deepCopy(businessTripCost);
                    businessTripCostNew.setId(UUID.randomUUID());
                    businessTripCostsListNew.add(businessTripCostNew);
                }
                businessTripLineNew.setBusinessTripCost(businessTripCostsListNew);
            }*/
            result.add(businessTripLineNew);
        }
        return result;
    }


}
