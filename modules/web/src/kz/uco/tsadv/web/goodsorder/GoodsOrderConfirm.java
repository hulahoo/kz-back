package kz.uco.tsadv.web.goodsorder;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.recognition.enums.GoodsOrderStatus;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrder;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrderDetail;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GoodsOrderConfirm extends AbstractEditor<GoodsOrder> {

    @Inject
    private TextField discountTextField;
    @Named("fieldGroup.discount")
    private TextField discountField;
    @Inject
    private Button windowCommit, rejectOrder, excludeGoodsBtn, deliveredBtn;
    @Inject
    private Table<GoodsOrderDetail> detailsTable;
    @Inject
    private CollectionDatasource<GoodsOrderDetail, UUID> detailsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        removeAction("windowCommit");
        addAction(new BaseAction("windowCommit") {
            @Override
            public void actionPerform(Component component) {
                showOptionDialog(getMessage("goods.order.confirm.title"),
                        String.format(getMessage("goods.order.confirm.approve"), getItem().getOrderNumber()),
                        MessageType.CONFIRMATION_HTML,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        try {
                                            List<GoodsOrderDetail> details = getItem().getDetails();
                                            if (details == null || details.isEmpty()) {
                                                throw new RuntimeException(getMessage("goods.order.confirm.details.empty"));
                                            }

                                            int excludedCount = details.stream()
                                                    .filter(goodsOrderDetail -> BooleanUtils.isTrue(goodsOrderDetail.getExcluded()))
                                                    .mapToInt(value -> 1).sum();

                                            if (excludedCount == details.size()) {
                                                throw new RuntimeException(getMessage("goods.order.confirm.only.not.available.error"));
                                            }

                                            getItem().setStatus(GoodsOrderStatus.WAIT_DELIVERY);
                                            commitAndClose();
                                        } catch (Exception ex) {
                                            showNotification(
                                                    getMessage("goods.order.confirm.error.title"),
                                                    ex.getMessage(),
                                                    NotificationType.TRAY);
                                        }
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO)
                        });
            }
        });

        addAction(new BaseAction("rejectOrder") {
            @Override
            public void actionPerform(Component component) {
                showOptionDialog(getMessage("goods.order.confirm.title"),
                        String.format(getMessage("goods.order.confirm.reject"), getItem().getOrderNumber()),
                        MessageType.CONFIRMATION_HTML,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        getItem().setStatus(GoodsOrderStatus.REJECTED);
                                        commitAndClose();
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO)
                        });
            }
        });

        addAction(new BaseAction("deliveredOrder") {
            @Override
            public void actionPerform(Component component) {
                showOptionDialog(getMessage("goods.order.confirm.title"),
                        String.format(getMessage("goods.order.confirm.delivered"), getItem().getOrderNumber()),
                        MessageType.CONFIRMATION_HTML,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        getItem().setStatus(GoodsOrderStatus.DELIVERED);
                                        commitAndClose();
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO)
                        });
            }
        });

        detailsDs.addItemChangeListener(e -> excludeGoodsBtn.setEnabled(e.getItem() != null));
    }

    @Override
    protected void postInit() {
        super.postInit();
        GoodsOrder goodsOrder = getItem();

        int discount = goodsOrder.getDiscount();
        if (discount != 0) {
            discountTextField.setValue(discount + "%");
            discountField.setVisible(true);
        } else {
            discountField.setVisible(false);
        }
        initButtonsAccess();
    }

    /**
     * approve, reject are visible if GoodsOrderStatus equals ON_APPROVAL
     */
    protected void initButtonsAccess() {
        GoodsOrderStatus goodsOrderStatus = getItem().getStatus();

        boolean actionsAccess = goodsOrderStatus.equals(GoodsOrderStatus.ON_APPROVAL);
        windowCommit.setVisible(actionsAccess);
        rejectOrder.setVisible(actionsAccess);
        deliveredBtn.setVisible(goodsOrderStatus.equals(GoodsOrderStatus.WAIT_DELIVERY));
        detailsTable.getButtonsPanel().setVisible(actionsAccess);
    }

    public void excludeGoods() {
        GoodsOrderDetail goodsOrderDetail = detailsDs.getItem();
        if (goodsOrderDetail != null) {
            openEditor("goods-order-detail-exclude",
                    goodsOrderDetail,
                    WindowManager.OpenType.DIALOG,
                    detailsDs);
        }
    }
}