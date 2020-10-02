package kz.uco.tsadv.web.toolkit.ui.rcgshopcomponent;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.web.App;
import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.recognition.dictionary.DicGoodsCategory;
import kz.uco.tsadv.modules.recognition.enums.GoodsOrderStatus;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.toolkit.ui.RcgJavaScriptComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@JavaScript({"rcgshopcomponent-connector-v1.6.js"})
public class RcgShopComponent extends RcgJavaScriptComponent {

    private Consumer<String> showGoodsCardConsumer;
    private Consumer openCartConsumer;
    private Consumer openShopConsumer;
    private Consumer<String> removeGoodsCartConsumer;
    private Consumer<Integer> openConfirmCheckout;
    private Consumer<String> showGoodsOrderDetailConsumer;
    private Consumer<Map<String, Object>> printVoucherReport;

    public RcgShopComponent() {
        setFilterList(GoodsOrderStatus.values());
        setDicGoodsCategory(AppBeans.get(EmployeeService.class).getDicGoodsCategories());
        addStyleName("rcg-shop-widget");
        setPageName("shop");

        addFunction("showGoodsCard", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String goodsJson = arguments.getString(0);
                if (goodsJson != null && !goodsJson.equals("")) {
                    if (showGoodsCardConsumer != null) {
                        showGoodsCardConsumer.accept(goodsJson);
                    }
                }
            }
        });

        addFunction("openCart", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                if (openCartConsumer != null) {
                    openCartConsumer.accept(null);
                }
            }
        });

        addFunction("openShop", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                if (openShopConsumer != null) {
                    openShopConsumer.accept(null);
                }
            }
        });

        addFunction("removeGoodsCart", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String goodsId = arguments.getString(0);
                if (goodsId != null && !goodsId.trim().equalsIgnoreCase("")) {
                    if (removeGoodsCartConsumer != null) {
                        removeGoodsCartConsumer.accept(goodsId);
                    }
                }
            }
        });

        addFunction("openConfirmCheckout", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                Double totalSumObject = arguments.getNumber(0);

                if (openConfirmCheckout != null) {
                    openConfirmCheckout.accept(totalSumObject.intValue());
                }
            }
        });

        addFunction("showGoodsOrderDetail", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String goodsOrderNumber = arguments.getString(0);
                if (goodsOrderNumber != null && !goodsOrderNumber.trim().equalsIgnoreCase("")) {
                    if (showGoodsOrderDetailConsumer != null) {
                        showGoodsOrderDetailConsumer.accept(goodsOrderNumber);
                    }
                }
            }
        });

        addFunction("printVoucherReport", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                Map<String, Object> map = new HashMap<>();
                String goodsOrderId = arguments.getString(0);
                String personGroupId = arguments.getString(1);
                if (goodsOrderId != null) {
                    map.put("personGroupId", personGroupId);
                    map.put("goodsOrderId", goodsOrderId);
                    printVoucherReport.accept(map);
                }
            }
        });
    }

    public void setShowGoodsOrderDetailConsumer(Consumer<String> showGoodsOrderDetailConsumer) {
        this.showGoodsOrderDetailConsumer = showGoodsOrderDetailConsumer;
    }

    public void setOpenConfirmCheckout(Consumer<Integer> openConfirmCheckout) {
        this.openConfirmCheckout = openConfirmCheckout;
    }

    public void setRemoveGoodsCartConsumer(Consumer<String> removeGoodsCartConsumer) {
        this.removeGoodsCartConsumer = removeGoodsCartConsumer;
    }

    public void setShowGoodsCardConsumer(Consumer<String> showGoodsCardConsumer) {
        this.showGoodsCardConsumer = showGoodsCardConsumer;
    }

    public void setOpenCartConsumer(Consumer openCartConsumer) {
        this.openCartConsumer = openCartConsumer;
    }

    public void setOpenShopConsumer(Consumer openShopConsumer) {
        this.openShopConsumer = openShopConsumer;
    }

    @Override
    protected RcgShopComponentState getState() {
        return (RcgShopComponentState) super.getState();
    }

    public int getGoodsCartCount() {
        return getState().goodsCartCount;
    }

    public void setGoodsCartCount(int goodsCartCount) {
        getState().goodsCartCount = goodsCartCount;
    }

    public int getHeartAwardDiscount() {
        return getState().heartAwardDiscount;
    }

    public void setHeartAwardDiscount(int heartAwardDiscount) {
        getState().heartAwardDiscount = heartAwardDiscount;
    }

    public int getHeartAward() {
        return getState().heartAward;
    }

    public void setHeartAward(int heartAward) {
        getState().heartAward = heartAward;
    }

    public long getBalance() {
        return getState().balance;
    }

    public void setBalance(long balance) {
        getState().balance = balance;
    }

    public long getTotalSum() {
        return getState().totalSum;
    }

    public void setTotalSum(long totalSum) {
        getState().totalSum = totalSum;
    }

    public long getGoodsOrdersCount() {
        return getState().goodsOrdersCount;
    }

    public void setGoodsOrdersCount(long goodsOrdersCount) {
        getState().goodsOrdersCount = goodsOrdersCount;
    }

    public GoodsOrderStatus[] getFilterList() {
        return getState().filterList;
    }

    public void setFilterList(GoodsOrderStatus[] filterList) {
        getState().filterList = filterList;
    }


    public Map<String, String> getDicCategoryMap(){
        return getState().dicCategoryMap;
    }

    public void setDicGoodsCategory(Map<String, String> dicGoodsCategory) {
        getState().dicCategoryMap =  dicGoodsCategory;
    }

    public Consumer<Map<String, Object>> getPrintVoucherReport() {
        return printVoucherReport;
    }

    public void setPrintVoucherReport(Consumer<Map<String, Object>> printVoucherReport) {
        this.printVoucherReport = printVoucherReport;
    }
}