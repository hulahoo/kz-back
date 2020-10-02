package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.recognition.exceptions.CheckoutCartException;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrder;
import kz.uco.tsadv.web.gui.components.WebRcgShop;
import kz.uco.tsadv.web.modules.recognition.entity.goods.GoodsCard;
import kz.uco.tsadv.web.toolkit.ui.rcgshopcomponent.RcgShopComponent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RcgCart extends AbstractRcgPage {

    @Override
    protected void loadPage(Map<String, Object> params) {
        removeAll();
        WebRcgShop rcgShop = componentsFactory.createComponent(WebRcgShop.class);
        rcgShop.setSizeFull();
        RcgShopComponent rcgShopComponent = (RcgShopComponent) rcgShop.getComponent();
        rcgShopComponent.setLanguage(localeName);
        rcgShopComponent.setPageName("cart");
        rcgShopComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());
        rcgShopComponent.setMessageBundle(messagesJson);
        rcgShopComponent.setAutomaticTranslate(isAutomaticTranslate() ? 1 : 0);

        rcgShopComponent.setGoodsCartCount(Math.toIntExact(recognitionService.goodsCartCount()));

        rcgShopComponent.setShowGoodsCardConsumer(goodsJson -> {
            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo("goods-card");

//            extWebWindowManager.setAdditionalClass(null); TODO переход на 7 версия
//            extWebWindowManager.openWindow(windowInfo,
//                    WindowManager.OpenType.DIALOG,
//                    ParamsMap.of(GoodsCard.GOODS_JSON, goodsJson));
        });

        rcgShopComponent.setOpenShopConsumer(nullableObject -> openFrame(getParent(), "rcg-shop"));

        rcgShopComponent.setBalance(recognitionService.getPersonBalance());

        boolean isHeartAward = recognitionService.checkHeartAward();
        rcgShopComponent.setHeartAward(isHeartAward ? 1 : 0);

        int discount = recognitionConfig.getHeartAwardDiscount();
        rcgShopComponent.setHeartAwardDiscount(discount);

        long cartTotalSum = recognitionService.getTotalSum(currentPersonGroupId);
        if (isHeartAward && discount != 0) {
            cartTotalSum = cartTotalSum - Math.round((double) (cartTotalSum * discount) / 100);
        }

        rcgShopComponent.setTotalSum(cartTotalSum);
        rcgShopComponent.setOpenConfirmCheckout(new Consumer<Integer>() {
            @Override
            public void accept(Integer totalSum) {
                recognitionHelper.showCheckoutConfirmWindow(totalSum, action -> {
                    if (action.equalsIgnoreCase("issue")) {
                        try {
                            List<GoodsOrder> goodsOrderList = recognitionService.checkoutCartList(recognitionHelper.getDicDeliveryAddress());

                            try {
                                recognitionService.sendCheckoutNotifications(goodsOrderList);
                            } catch (Exception ex) {
                                logger.error(ex.getMessage());
                            }

                            recognitionHelper.showSuccessCheckout(action1 -> {
                                showNotification(getMessage("msg.info.title"),
                                        getMessage("rcg.cart.checkout.success"),
                                        NotificationType.TRAY);

                                openFrame(getParent(), "rcg-shop");
                            });
                        } catch (CheckoutCartException checkoutCartException) {
                            showMessageDialog(getMessage("msg.warning.title"),
                                    checkoutCartException.getMessage(),
                                    MessageType.CONFIRMATION_HTML);
                        } catch (Exception ex) {
                            showNotification(getMessage("msg.warning.title"),
                                    ex.getMessage(),
                                    NotificationType.TRAY);
                        }
                    }
                });
            }
        });
        rcgShopComponent.setRemoveGoodsCartConsumer(new Consumer<String>() {
            @Override
            public void accept(String goodsId) {
//                extWebWindowManager.showOptionDialog(getMessage("confirm.remove.title"), TODO переход на 7 версия
//                        getMessage("confirm.remove.goods"),
//                        MessageType.CONFIRMATION,
//                        new Action[]{
//                                new DialogAction(DialogAction.Type.YES) {
//                                    @Override
//                                    public void actionPerform(Component component) {
//                                        try {
//                                            recognitionService.removeGoodsFromCart(UUID.fromString(goodsId));
//                                            rcgShopComponent.callFunction("removeGoodsCartInForm", goodsId);
//                                        } catch (Exception ex) {
//                                            showNotification(ex.getMessage());
//                                        }
//                                    }
//                                },
//                                new DialogAction(DialogAction.Type.NO)
//                        }
//                        );
            }
        });
        add(rcgShop);
    }
}