package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recognition.shop.Goods;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrder;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrderDetail;
import kz.uco.tsadv.service.RecognitionService;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class RcgCartDetail extends AbstractWindow {

    public static final String GOODS_ORDER_NUMBER = "RCD_GOODS_ORDER_NUMBER";

    @Inject
    protected Messages messages;

    @Inject
    private Label statusLabel, totalSumLabel;

    @Inject
    private ScrollBoxLayout scrollBox;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private RecognitionService recognitionService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(GOODS_ORDER_NUMBER)) {
            String goodsOrderNumber = (String) params.get(GOODS_ORDER_NUMBER);

            setCaption(getMessage("order.number") + goodsOrderNumber);

            loadPage(goodsOrderNumber);
        }
    }

    private void loadPage(String goodsOrderNumber) {
        try {
            GoodsOrder goodsOrder = recognitionService.loadGoodsOrder(goodsOrderNumber);
            if (goodsOrder != null) {
                List<GoodsOrderDetail> details = goodsOrder.getDetails();
                long totalSum = 0;
                if (details != null && !details.isEmpty()) {
                    GridLayout gridLayout = componentsFactory.createComponent(GridLayout.class);
                    gridLayout.setSpacing(true);
                    gridLayout.setStyleName("rcg-sh-go-detail-grid");
                    gridLayout.setColumns(3);
                    gridLayout.setRows(details.size());
                    gridLayout.setSizeFull();
                    gridLayout.setColumnExpandRatio(1, 2);

                    scrollBox.add(gridLayout);

                    int row = 0;
                    for (GoodsOrderDetail detail : details) {
                        addGoodsOrderDetail(detail, gridLayout, row);
                        if (BooleanUtils.isFalse(detail.getExcluded())) {
                            totalSum += detail.getQuantity() * detail.getGoods().getPrice();
                        }

                        row++;
                    }
                }

                int discount = goodsOrder.getDiscount();
                String totalText = getMessage("order.detail.total");

                if (discount != 0) {
                    totalSum = totalSum - Math.round((double) (totalSum * discount) / 100);

                    totalText += " (-" + discount + "%)";
                }

                statusLabel.setValue(getMessage("order.detail.status") + " <span class=\"rcg-cart-detail-status\" status-code=\"" + goodsOrder.getStatus().getId() + "\">" + messages.getMessage(goodsOrder.getStatus()) + "</span>");
                totalSumLabel.setValue(totalText + " : " + " <span class=\"rcg-cart-detail-total\"><i></i>" + totalSum + "<span> HC</span></span>");
            }
        } catch (Exception ex) {
            showNotification(
                    getMessage("msg.warning.title"),
                    ex.getMessage(),
                    NotificationType.TRAY);
        }
    }

    private void addGoodsOrderDetail(GoodsOrderDetail detail, GridLayout gridLayout, int row) {
        boolean excluded = BooleanUtils.isTrue(detail.getExcluded());

        Goods goods = detail.getGoods();

        Label imageLabel = componentsFactory.createComponent(Label.class);
        imageLabel.setWidth("160px");
        imageLabel.setHtmlEnabled(true);
        imageLabel.setValue(String.format("<img src=\"./dispatch/goods_image/%s\" class=\"rcg-sh-go-grid-image\"/>", goods.getId().toString()));
        gridLayout.add(imageLabel, 0, row);

        Label bodyWrapper = componentsFactory.createComponent(Label.class);
        bodyWrapper.setWidthFull();
        bodyWrapper.setHtmlEnabled(true);
        boolean isVoucher = detail.getGoods().getCategory().getCode() != null && detail.getGoods().getCategory().getCode().equalsIgnoreCase("VOUCHER");
        bodyWrapper.setValue(String.format(
                "<div class=\"rcg-sh-go-grid-nq %s\">" +
                        "<div class=\"rcg-sh-go-grid-n\">%s</div>" +
                        "<div class=\"rcg-sh-go-grid-q\">%s</div>" +
                        "<div class=\"rcg-sh-go-grid-exc\">%s</div>" +
                        "<div class=\"rcg-sh-go-grid-exc\">%s</div>" +
                        "</div>",
                excluded ? "excluded" : "",
                goods.getName(),
                detail.getQuantity() + " " + getMessage("order.detail.pc"),
                isVoucher ? getMessage("orderDetail.voucherUsed") + " " + getMessage("orderDetail.voucher." + detail.getVoucherUsed()) : "",
                isVoucher ? getMessage("orderDetail.qrCode") + " " + detail.getQrCode() : "",
                excluded ? "<span>" + getMessage("detail.goods.excluded") + "</span> " + detail.getComment() : ""));
        gridLayout.add(bodyWrapper, 1, row);

        Label priceLabel = componentsFactory.createComponent(Label.class);
        priceLabel.setValue(String.format(
                "<span class=\"rcg-sh-go-grid-price %s\">" +
                        "<i></i>%d <span>HC</span>" +
                        "</span>",
                excluded ? "excluded" : "",
                goods.getPrice().longValue()));
        priceLabel.setHtmlEnabled(true);
        gridLayout.add(priceLabel, 2, row);
    }

}