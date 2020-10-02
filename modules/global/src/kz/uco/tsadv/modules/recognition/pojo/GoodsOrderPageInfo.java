package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.List;
import kz.uco.tsadv.global.entity.PageInfo;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$GoodsOrderPageInfo")
public class GoodsOrderPageInfo extends BaseUuidEntity {
    private static final long serialVersionUID = 6955652515522671169L;

    @MetaProperty
    protected PageInfo pageInfo;

    @MetaProperty
    protected List<GoodsOrderPojo> goodsOrders;

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setGoodsOrders(List<GoodsOrderPojo> goodsOrders) {
        this.goodsOrders = goodsOrders;
    }

    public List<GoodsOrderPojo> getGoodsOrders() {
        return goodsOrders;
    }


}