package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import kz.uco.tsadv.modules.recognition.shop.GoodsIssue;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrderDetail;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("tsadv_GoodsOrderDetailListener")
public class GoodsOrderDetailListener implements BeforeInsertEntityListener<GoodsOrderDetail> {

    @Inject
    private Metadata metadata;

    @Override
    public void onBeforeInsert(GoodsOrderDetail goodsOrderDetail, EntityManager entityManager) {
        GoodsIssue goodsIssue = metadata.create(GoodsIssue.class);
        goodsIssue.setGoods(goodsOrderDetail.getGoods());
        goodsIssue.setOrder(goodsOrderDetail.getGoodsOrder());
        goodsIssue.setQuantity(goodsOrderDetail.getQuantity());
        entityManager.persist(goodsIssue);
    }
}