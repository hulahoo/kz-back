package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import kz.uco.tsadv.modules.recognition.enums.PointOperationType;
import kz.uco.tsadv.modules.recognition.shop.GoodsIssue;
import kz.uco.tsadv.service.RecognitionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

@Component("tsadv_GoodsIssueListener")
public class GoodsIssueListener implements AfterInsertEntityListener<GoodsIssue>, AfterDeleteEntityListener<GoodsIssue> {

    @Inject
    private RecognitionService recognitionService;

    @Override
    public void onAfterInsert(GoodsIssue goodsIssue, Connection connection) {
        recognitionService.refreshWarehouse(goodsIssue.getGoods(), goodsIssue.getQuantity(), PointOperationType.ISSUE);
    }

    @Override
    public void onAfterDelete(GoodsIssue goodsIssue, Connection connection) {
        recognitionService.refreshWarehouse(goodsIssue.getGoods(), goodsIssue.getQuantity(), PointOperationType.RECEIPT);
    }
}