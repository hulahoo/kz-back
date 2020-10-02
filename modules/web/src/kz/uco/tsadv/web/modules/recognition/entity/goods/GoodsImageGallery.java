package kz.uco.tsadv.web.modules.recognition.entity.goods;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recognition.shop.GoodsImage;
import kz.uco.tsadv.service.RecognitionService;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class GoodsImageGallery extends AbstractWindow {

    @WindowParam
    private UUID goodsId;

    @WindowParam
    private UUID currentGoodsImageId;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private RecognitionService recognitionService;

    @Inject
    private Image goodsImagePreview;

    @Inject
    private HBoxLayout goodsImagesBox;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (goodsId != null) {
            List<GoodsImage> goodsImages = recognitionService.loadGoodsImages(goodsId);
            if (goodsImages != null && !goodsImages.isEmpty()) {
                for (GoodsImage goodsImage : goodsImages) {
                    createGoodsImagePreview(goodsImage);

                    if (currentGoodsImageId != null && goodsImage.getId().equals(currentGoodsImageId)
                            || BooleanUtils.isTrue(goodsImage.getPrimary())) {
                        showImagePreview(goodsImage);
                    }
                }
            } else {
                showImagePreview(null);
            }
        }
    }

    private void createGoodsImagePreview(GoodsImage goodsImage) {
        if (goodsImage != null) {
            FileDescriptor goodsImageFd = goodsImage.getImage();
            if (goodsImageFd != null) {
                Image image = componentsFactory.createComponent(Image.class);
                image.setScaleMode(Image.ScaleMode.SCALE_DOWN);
                image.setWidth("60px");
                image.setHeight("60px");
                image.setSource(FileDescriptorResource.class).setFileDescriptor(goodsImageFd);
                image.addClickListener(event -> showImagePreview(goodsImage));
                goodsImagesBox.add(image);
            }
        }
    }

    private void showImagePreview(GoodsImage goodsImage) {
        if (goodsImage == null) {
            goodsImagePreview.setSource(ThemeResource.class).setPath(GoodsBrowse.DEFAULT_GOODS_IMAGE);
        } else {
            FileDescriptor goodsImageFd = goodsImage.getImage();
            if (goodsImageFd != null) {
                goodsImagePreview.setSource(FileDescriptorResource.class).setFileDescriptor(goodsImageFd);
            }
        }
    }
}