package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.global.entity.PageInfo;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.List;

@MetaClass(name = "tsadv$GoodsPageInfo")
public class GoodsPageInfo extends BaseUuidEntity {
    private static final long serialVersionUID = -4251981345857766357L;

    @MetaProperty
    protected PageInfo pageInfo;

    @MetaProperty
    protected List<GoodsPojo> goods;

    public List<GoodsPojo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsPojo> goods) {
        this.goods = goods;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }


}