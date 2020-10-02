package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import kz.uco.tsadv.modules.recognition.enums.PointOperationType;
import kz.uco.tsadv.modules.recognition.shop.GoodsReceipt;
import kz.uco.tsadv.service.RecognitionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

@Component("tsadv_GoodsReceiptListener")
public class GoodsReceiptListener implements AfterInsertEntityListener<GoodsReceipt> {

    @Inject
    private RecognitionService recognitionService;

    @Override
    public void onAfterInsert(GoodsReceipt goodsReceipt, Connection connection) {
        recognitionService.refreshWarehouse(goodsReceipt.getGoods(), goodsReceipt.getQuantity(), PointOperationType.RECEIPT);
    }
}