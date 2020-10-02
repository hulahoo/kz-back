package kz.uco.tsadv.web.goodsorder;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrder;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class GoodsOrderBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<GoodsOrder, UUID> goodsOrdersDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    public void confirmGoodsOrder(GoodsOrder goodsOrder, String name) {
        AbstractEditor editor = openEditor("goods-order-confirm",
                goodsOrder,
                WindowManager.OpenType.THIS_TAB);

        editor.addCloseWithCommitListener(() -> goodsOrdersDs.refresh());
    }
}