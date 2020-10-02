package kz.uco.tsadv.web.toolkit.ui.rcgshopcomponent;

import kz.uco.tsadv.modules.recognition.dictionary.DicGoodsCategory;
import kz.uco.tsadv.modules.recognition.enums.GoodsOrderStatus;
import kz.uco.tsadv.web.toolkit.ui.RcgComponentState;

import java.util.List;
import java.util.Map;

public class RcgShopComponentState extends RcgComponentState {

    public int goodsCartCount;

    public long goodsOrdersCount;

    public long balance = 0;

    public long totalSum = 0;

    public int heartAward = 0;

    public int heartAwardDiscount = 0;

    public GoodsOrderStatus[] filterList;

    public Map<String, String> dicCategoryMap;
}