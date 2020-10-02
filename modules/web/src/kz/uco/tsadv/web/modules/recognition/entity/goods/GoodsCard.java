package kz.uco.tsadv.web.modules.recognition.entity.goods;

import com.google.gson.Gson;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recognition.pojo.GoodsPojo;
import kz.uco.tsadv.modules.recognition.shop.GoodsImage;
import kz.uco.tsadv.service.RecognitionService;
import kz.uco.tsadv.web.toolkit.ui.rcgshopcomponent.RcgShopComponent;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GoodsCard extends AbstractWindow {

    private static Gson gson = new Gson();

    public static final String GOODS_JSON = "GC_GOODS_JSON";
    public static final String PARENT_COMPONENT = "PARENT_COMPONENT";
    public static final String RCG_SHOP_COMPONENT = "GC_RCG_SHOP_COMPONENT";

    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private Image goodsImagePreview;
    @Inject
    private ScrollBoxLayout goodsImagesBox;
    @Inject
    private HBoxLayout operationBlock;
    @Inject
    private LinkButton operationLink;
    @Inject
    private Label description;
    @Inject
    private Label priceLabel;
    @Inject
    private LinkButton addToWishList;
    @Inject
    private RecognitionService recognitionService;

    private Component parentComponent = null;

    /**
     * if it's null, then the request came from the RcgProfilePage
     */
    private RcgShopComponent rcgShopComponent = null;

    private UUID currentGoodsImageId = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        addToWishList.setCaption("");

        if (params.containsKey(GOODS_JSON)) {
            try {
                GoodsPojo goodsPojo = gson.fromJson(String.valueOf(params.get(GOODS_JSON)), GoodsPojo.class);
                if (goodsPojo == null) {
                    throw new RuntimeException(getMessage("goods.card.does.not.open"));
                }

                if (params.containsKey(PARENT_COMPONENT)) {
                    parentComponent = (Component) params.get(PARENT_COMPONENT);
                }

                if (params.containsKey(RCG_SHOP_COMPONENT)) {
                    rcgShopComponent = (RcgShopComponent) params.get(RCG_SHOP_COMPONENT);
                }

                renderGoodsCard(goodsPojo);

                goodsImagePreview.addClickListener(event -> {
                    if (goodsImagePreview.getSource() instanceof FileDescriptorResource) {
                        openWindow("goods-image-gallery",
                                WindowManager.OpenType.DIALOG,
                                ParamsMap.of("goodsId", goodsPojo.getId(),
                                        "currentGoodsImageId", currentGoodsImageId));
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void renderGoodsCard(GoodsPojo goodsPojo) {
        setCaption(goodsPojo.getName());
        description.setValue(goodsPojo.getDescription());

        List<GoodsImage> goodsImages = recognitionService.loadGoodsImages(goodsPojo.getId());
        if (goodsImages != null && !goodsImages.isEmpty()) {
            for (GoodsImage goodsImage : goodsImages) {
                createGoodsImagePreview(goodsImage);

                if (BooleanUtils.isTrue(goodsImage.getPrimary())) {
                    showImagePreview(goodsImage);
                }
            }
        } else {
            showImagePreview(null);
        }

        //goodsImageLabel.setValue("<img src=\"" + goodsPojo.getImage() + "\" class=\"rcg-sh-gc-image\"/>");
        priceLabel.setValue("<div class=\"rcg-sh-gc-price\"><i></i>" + goodsPojo.getPrice().intValue() + "<span> HC</span></div>");

        if (rcgShopComponent != null) {
            if (goodsPojo.getInCart() == 1) {
                initGoToCartLink();
            } else {
                if (goodsPojo.getQuantity() == 0) {
                    operationLink.setCaption(getMessage("goods.empty"));
                } else {
                    operationLink.setAction(new BaseAction("add-to-cart") {
                        @Override
                        public void actionPerform(Component component) {
                            UUID goodsId = goodsPojo.getId();
                            recognitionService.addGoodsToCart(goodsId);

                            goodsPojo.setInCart(1);

                            rcgShopComponent.callFunction("rePaintGoodsOperation", gson.toJson(goodsPojo));

                            initGoToCartLink();
                        }
                    });
                }
            }

            checkInWishList(goodsPojo);
        } else {
            operationBlock.setVisible(false);
        }
    }

    private void createGoodsImagePreview(GoodsImage goodsImage) {
        if (goodsImage != null) {
            FileDescriptor goodsImageFd = goodsImage.getImage();
            if (goodsImageFd != null) {
                Image image = componentsFactory.createComponent(Image.class);
                image.setScaleMode(Image.ScaleMode.SCALE_DOWN);
                image.setWidth("45px");
                image.setHeight("45px");
                image.setSource(FileDescriptorResource.class).setFileDescriptor(goodsImageFd);
                image.addClickListener(event -> showImagePreview(goodsImage));
                goodsImagesBox.add(image);
            }
        }
    }

    private void showImagePreview(GoodsImage goodsImage) {
        if (goodsImage == null) {
            goodsImagePreview.setSource(ThemeResource.class).setPath(GoodsBrowse.DEFAULT_GOODS_IMAGE);
            currentGoodsImageId = null;
        } else {
            FileDescriptor goodsImageFd = goodsImage.getImage();
            if (goodsImageFd != null) {
                goodsImagePreview.setSource(FileDescriptorResource.class).setFileDescriptor(goodsImageFd);
                currentGoodsImageId = goodsImage.getId();
            }
        }
    }

    private void checkInWishList(GoodsPojo goodsPojo) {
        if (goodsPojo.getInWishList() == 0) {
            addToWishList.removeStyleName("rcg-sh-gc-wl");
            addToWishList.addStyleName("rcg-sh-gc-wl-o");
            addToWishList.setIcon("font-icon:HEART_O");
        } else {
            addToWishList.removeStyleName("rcg-sh-gc-wl-o");
            addToWishList.addStyleName("rcg-sh-gc-wl");
            addToWishList.setIcon("font-icon:HEART");
        }

        addToWishList.setAction(new BaseAction("addToWishList") {
            @Override
            public void actionPerform(Component component) {
                boolean inWishList = goodsPojo.getInWishList() == 1;
                recognitionService.addToWishList(goodsPojo.getId());
                goodsPojo.setInWishList(inWishList ? 0 : 1);

                if (inWishList) {
                    addToWishList.setIcon("font-icon:HEART_O");
                    addToWishList.removeStyleName("rcg-sh-gc-wl");
                    addToWishList.addStyleName("rcg-sh-gc-wl-o");
                } else {
                    addToWishList.setIcon("font-icon:HEART");
                    addToWishList.removeStyleName("rcg-sh-gc-wl-o");
                    addToWishList.addStyleName("rcg-sh-gc-wl");
                }

                rcgShopComponent.callFunction("rePaintGoodsWishList", gson.toJson(goodsPojo));
            }
        });
    }

    private void initGoToCartLink() {
        operationLink.addStyleName("rcg-white-btn");
        operationLink.removeStyleName("rcg-blue-btn");
        operationLink.setCaption(getMessage("goods.in.cart"));
        operationLink.setAction(new BaseAction("go-to-cart") {
            @Override
            public void actionPerform(Component component) {
                if (parentComponent != null) {
                    close("close");
                    openFrame(parentComponent, "rcg-cart");
                } else {
                    showNotification("Parent component is null!", NotificationType.TRAY);
                }
            }
        });
    }
}