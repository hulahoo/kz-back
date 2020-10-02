package kz.uco.tsadv.web.goodsorderdetail;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrderDetail;

import javax.inject.Inject;
import java.util.UUID;

public class GoodsOrderDetailWithVoucherBrowse extends AbstractLookup {
    @Inject
    protected GroupDatasource<GoodsOrderDetail, UUID> goodsOrderDetailWithVouchersDs;

    @Inject
    protected Button useVoucherBtn;

    protected boolean useVoucher;
    @Override
    public void ready() {
        super.ready();
        goodsOrderDetailWithVouchersDs.addItemChangeListener(e -> {
            useVoucherBtn.setEnabled(e.getItem() != null);
            if (e.getItem().getVoucherUsed()) {
                useVoucherBtn.setCaption(getMessage("unUsedVoucherBtnCaption"));
                useVoucher = false;
            }else {
                useVoucherBtn.setCaption(getMessage("useVoucherBtnCaption"));
                useVoucher = true;
            }
        });
    }

    public void useVoucher() {
        goodsOrderDetailWithVouchersDs.getItem().setVoucherUsed(useVoucher);
        goodsOrderDetailWithVouchersDs.commit();
    }
}