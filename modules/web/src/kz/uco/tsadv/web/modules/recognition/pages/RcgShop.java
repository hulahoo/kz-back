package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrder;
import kz.uco.tsadv.web.gui.components.WebRcgShop;
import kz.uco.tsadv.web.modules.recognition.entity.goods.GoodsCard;
import kz.uco.tsadv.web.toolkit.ui.rcgshopcomponent.RcgShopComponent;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RcgShop extends AbstractRcgPage {

    @Inject
    protected CommonService commonService;

    @Override
    protected void loadPage(Map<String, Object> params) {
        removeAll();
        showNotification(getMessage("shop.technical.work"));
        WebRcgShop rcgShop = componentsFactory.createComponent(WebRcgShop.class);
        rcgShop.setSizeFull();
        RcgShopComponent rcgShopComponent = (RcgShopComponent) rcgShop.getComponent();
        rcgShopComponent.setLanguage(localeName);
        rcgShopComponent.setPageName("shop");
        rcgShopComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());
        rcgShopComponent.setGoodsCartCount(Math.toIntExact(recognitionService.goodsCartCount()));
        rcgShopComponent.setGoodsOrdersCount(recognitionService.goodsOrdersCount());
        rcgShopComponent.setBalance(recognitionService.getPersonBalance());
        rcgShopComponent.setMessageBundle(messagesJson);
        rcgShopComponent.setAutomaticTranslate(isAutomaticTranslate() ? 1 : 0);

        rcgShopComponent.setShowGoodsCardConsumer(goodsJson -> {
            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo("goods-card");

//            extWebWindowManager.setAdditionalClass(null);
//            extWebWindowManager.openWindow(windowInfo,
//                    WindowManager.OpenType.DIALOG,
//                    ParamsMap.of(GoodsCard.GOODS_JSON, goodsJson,
//                            GoodsCard.PARENT_COMPONENT, getParent(),
//                            GoodsCard.RCG_SHOP_COMPONENT, rcgShopComponent));
        });
        rcgShopComponent.setOpenCartConsumer(new Consumer() {
            @Override
            public void accept(Object nullableObject) {
                openFrame(getParent(), "rcg-cart");
            }
        });

        rcgShopComponent.setShowGoodsOrderDetailConsumer(new Consumer<String>() {
            @Override
            public void accept(String goodsOrderNumber) {
                WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
                WindowInfo windowInfo = windowConfig.getWindowInfo("rcg-cart-detail");

//                extWebWindowManager.setAdditionalClass(null);
//                extWebWindowManager.openWindow(windowInfo,
//                        WindowManager.OpenType.DIALOG,
//                        ParamsMap.of(RcgCartDetail.GOODS_ORDER_NUMBER, goodsOrderNumber));
            }
        });


        rcgShopComponent.setPrintVoucherReport(new Consumer<Map<String, Object>>() {
            @Override
            public void accept(Map<String, Object> map) {
                if (map != null && map.size() > 1) {
                    Report report = null;
                    if (userSession.getLocale().getLanguage().equalsIgnoreCase("en")) {
                        report = commonService.getEntity(Report.class, "KC_VOUCHER_EN");
                    }else {
                        report = commonService.getEntity(Report.class, "KC_VOUCHER_RU");
                    }
                    PersonGroupExt personGroupExt = dataManager.load(LoadContext.create(PersonGroupExt.class).setId(UUID.fromString((String) map.get("personGroupId"))));
                    GoodsOrder goodsOrder = dataManager.load(LoadContext.create(GoodsOrder.class).setId(UUID.fromString((String) map.get("goodsOrderId"))));
                    if (report != null) {
                        AppBeans.get(ReportGuiManager.class).printReport(report, ParamsMap.of("personGroupId", personGroupExt, "goodsOrderId", goodsOrder), null);
                    }
                }
            }
        });

        add(rcgShop);
    }
}