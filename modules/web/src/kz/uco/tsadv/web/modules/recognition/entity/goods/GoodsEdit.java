package kz.uco.tsadv.web.modules.recognition.entity.goods;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.app.core.file.FileUploadDialog;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recognition.shop.Goods;
import kz.uco.tsadv.modules.recognition.shop.GoodsImage;
import kz.uco.tsadv.modules.recognition.shop.GoodsImageForReport;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static kz.uco.tsadv.web.modules.recognition.entity.goods.GoodsBrowse.DEFAULT_GOODS_IMAGE;

public class GoodsEdit extends AbstractEditor<Goods> {

    @Inject
    protected Image goodsImagePreview;
    @Inject
    protected FlowBoxLayout goodsImagesBox;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected CollectionDatasource<GoodsImage, UUID> goodsImagesDs;
    @Inject
    protected CollectionDatasource<FileDescriptor, UUID> fileDescriptorsDs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected DataManager dataManager;

    @Inject
    protected Image goodsImageForReportPreview;
    @Inject
    protected FlowBoxLayout goodsImagesForReportBox;
    @Inject
    protected CollectionDatasource<GoodsImageForReport, UUID> goodsImagesForReportDs;

    protected GoodsImage goodsImage;
    protected GoodsImageForReport goodsImageForReport;
    @Override
    protected void postInit() {
        super.postInit();

        initGoodsImages();

        initGoodsImagesForReport();
    }

    protected void showPreviewImage(GoodsImage goodsImage) {
        if (goodsImage != null) {
            Optional.ofNullable(goodsImage.getImage())
                    .ifPresent(goodsImageFd ->
                            goodsImagePreview.setSource(FileDescriptorResource.class)
                                    .setFileDescriptor(goodsImageFd));
        } else {
            goodsImagePreview.setSource(ThemeResource.class).setPath(DEFAULT_GOODS_IMAGE);
        }
    }
    protected void showPreviewImageForReport(GoodsImageForReport goodsImageForReport) {
        if (goodsImageForReport != null) {
            Optional.ofNullable(goodsImageForReport.getImage())
                    .ifPresent(goodsImageForReportFd ->
                            goodsImageForReportPreview.setSource(FileDescriptorResource.class)
                                    .setFileDescriptor(goodsImageForReportFd));
        } else {
            goodsImageForReportPreview.setSource(ThemeResource.class).setPath(DEFAULT_GOODS_IMAGE);
        }
    }

    protected void initGoodsImages() {
        Collection<GoodsImage> goodsImages = goodsImagesDs.getItems();
        if (goodsImages != null && !goodsImages.isEmpty()) {
            for (GoodsImage goodsImage : goodsImages) {
                createGoodsImageBlock(goodsImage);

                if (BooleanUtils.isTrue(goodsImage.getPrimary())) {
                    showPreviewImage(goodsImage);
                }
            }
        } else {
            showPreviewImage(null);
        }
    }

    protected void initGoodsImagesForReport() {
        Collection<GoodsImageForReport> goodsImagesForReport = goodsImagesForReportDs.getItems();
        if (goodsImagesForReport != null && !goodsImagesForReport.isEmpty()) {
            for (GoodsImageForReport goodsImageForReport : goodsImagesForReport) {
                createGoodsImageForReportBlock(goodsImageForReport);

                if (BooleanUtils.isTrue(goodsImageForReport.getPrimary())) {
                    showPreviewImageForReport(goodsImageForReport);
                }
            }
        } else {
            showPreviewImageForReport(null);
        }
    }

    protected void createGoodsImageBlock(GoodsImage goodsImage) {
        FileDescriptor imageFileDescriptor = goodsImage.getImage();
        if (imageFileDescriptor != null) {
            CssLayout imageWrapper = componentsFactory.createComponent(CssLayout.class);
            imageWrapper.setId(goodsImage.getId().toString());
            imageWrapper.setStyleName("rcg-goods-image-pw");
            if (BooleanUtils.isTrue(goodsImage.getPrimary())) {
                imageWrapper.addStyleName("rcg-goods-image-primary");
            }

            Image image = componentsFactory.createComponent(Image.class);
            image.setWidth("60px");
            image.setHeight("60px");
            image.setStyleName("rcg-goods-image-pi");
            image.setScaleMode(Image.ScaleMode.CONTAIN);
            image.setSource(FileDescriptorResource.class).setFileDescriptor(imageFileDescriptor);
            image.addClickListener(event -> {
                if (event.isDoubleClick()) {
                    Collection<GoodsImage> goodsImages = goodsImagesDs.getItems();
                    if (goodsImages != null && !goodsImages.isEmpty()) {
                        goodsImages.stream()
                                .filter(goodsImage1 -> BooleanUtils.isTrue(goodsImage1.getPrimary()))
                                .findFirst()
                                .ifPresent(findGoodsImage -> {
                                    findGoodsImage.setPrimary(false);
                                    goodsImagesDs.modifyItem(findGoodsImage);

                                    Component component = goodsImagesBox.getComponent(findGoodsImage.getId().toString());
                                    if (component != null) {
                                        component.removeStyleName("rcg-goods-image-primary");
                                    }
                                });
                    }

                    Component currentComponent = goodsImagesBox.getComponent(goodsImage.getId().toString());
                    if (currentComponent != null) {
                        goodsImage.setPrimary(true);
                        goodsImagesDs.modifyItem(goodsImage);

                        currentComponent.addStyleName("rcg-goods-image-primary");
                    }
                } else {
                    showPreviewImage(goodsImage);
                }
            });
            imageWrapper.add(image);

            LinkButton removeLink = componentsFactory.createComponent(LinkButton.class);
            removeLink.setCaption("");
            removeLink.setStyleName("rcg-goods-image-prl");
            removeLink.setIcon("font-icon:TIMES_CIRCLE");
            imageWrapper.add(removeLink);
            removeLink.setAction(new BaseAction("remove") {
                @Override
                public void actionPerform(Component component) {
                    showOptionDialog("Подтверждение",
                            "Вы действительно хотите удалить картинку?",
                            MessageType.CONFIRMATION,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES) {
                                        @Override
                                        public void actionPerform(Component component) {
                                            goodsImagesDs.removeItem(goodsImage);
                                            goodsImagesBox.remove(imageWrapper);

                                            if (goodsImagesDs.size() > 0) {
                                                GoodsImage previousGoodsImage = goodsImagesDs.getItems().iterator().next();

                                                if (BooleanUtils.isTrue(goodsImage.getPrimary())) {
                                                    previousGoodsImage.setPrimary(true);
                                                    goodsImagesDs.modifyItem(previousGoodsImage);

                                                    Component findComponent = goodsImagesBox.getComponent(previousGoodsImage.getId().toString());
                                                    if (findComponent != null) {
                                                        findComponent.addStyleName("rcg-goods-image-primary");
                                                    }
                                                }

                                                showPreviewImage(previousGoodsImage);
                                            } else {
                                                showPreviewImage(null);
                                            }
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.NO)
                            });
                }
            });

            goodsImagesBox.add(imageWrapper);
        }
    }

 protected void createGoodsImageForReportBlock(GoodsImageForReport goodsImageForReport) {
        FileDescriptor imageFileDescriptor = goodsImageForReport.getImage();
        if (imageFileDescriptor != null) {
            CssLayout imageWrapper = componentsFactory.createComponent(CssLayout.class);
            imageWrapper.setId(goodsImageForReport.getId().toString());
            imageWrapper.setStyleName("rcg-goods-image-pw");
            if (BooleanUtils.isTrue(goodsImageForReport.getPrimary())) {
                imageWrapper.addStyleName("rcg-goods-image-primary");
            }

            Image image = componentsFactory.createComponent(Image.class);
            image.setWidth("60px");
            image.setHeight("60px");
            image.setStyleName("rcg-goods-image-pi");
            image.setScaleMode(Image.ScaleMode.CONTAIN);
            image.setSource(FileDescriptorResource.class).setFileDescriptor(imageFileDescriptor);
            image.addClickListener(event -> {
                if (event.isDoubleClick()) {
                    Collection<GoodsImageForReport> goodsImagesForReport = goodsImagesForReportDs.getItems();
                    if (goodsImagesForReport != null && !goodsImagesForReport.isEmpty()) {
                        goodsImagesForReport.stream()
                                .filter(goodsImageForREport1 -> BooleanUtils.isTrue(goodsImageForREport1.getPrimary()))
                                .findFirst()
                                .ifPresent(findGoodsImageForReport -> {
                                    findGoodsImageForReport.setPrimary(false);
                                    goodsImagesForReportDs.modifyItem(findGoodsImageForReport);

                                    Component component = goodsImagesForReportBox.getComponent(findGoodsImageForReport.getId().toString());
                                    if (component != null) {
                                        component.removeStyleName("rcg-goods-image-primary");
                                    }
                                });
                    }

                    Component currentComponent = goodsImagesForReportBox.getComponent(goodsImageForReport.getId().toString());
                    if (currentComponent != null) {
                        goodsImageForReport.setPrimary(true);
                        goodsImagesForReportDs.modifyItem(goodsImageForReport);

                        currentComponent.addStyleName("rcg-goods-image-primary");
                    }
                } else {
                    showPreviewImageForReport(goodsImageForReport);
                }
            });
            imageWrapper.add(image);

            LinkButton removeLink = componentsFactory.createComponent(LinkButton.class);
            removeLink.setCaption("");
            removeLink.setStyleName("rcg-goods-image-prl");
            removeLink.setIcon("font-icon:TIMES_CIRCLE");
            imageWrapper.add(removeLink);
            removeLink.setAction(new BaseAction("remove") {
                @Override
                public void actionPerform(Component component) {
                    showOptionDialog("Подтверждение",
                            "Вы действительно хотите удалить картинку?",
                            MessageType.CONFIRMATION,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES) {
                                        @Override
                                        public void actionPerform(Component component) {
                                            goodsImagesForReportDs.removeItem(goodsImageForReport);
                                            goodsImagesForReportBox.remove(imageWrapper);

                                            if (goodsImagesForReportDs.size() > 0) {
                                                GoodsImageForReport previousGoodsImageForReport = goodsImagesForReportDs.getItems().iterator().next();

                                                if (BooleanUtils.isTrue(goodsImageForReport.getPrimary())) {
                                                    previousGoodsImageForReport.setPrimary(true);
                                                    goodsImagesForReportDs.modifyItem(previousGoodsImageForReport);

                                                    Component findComponent = goodsImagesForReportBox.getComponent(previousGoodsImageForReport.getId().toString());
                                                    if (findComponent != null) {
                                                        findComponent.addStyleName("rcg-goods-image-primary");
                                                    }
                                                }

                                                showPreviewImageForReport(previousGoodsImageForReport);
                                            } else {
                                                showPreviewImageForReport(null);
                                            }
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.NO)
                            });
                }
            });

            goodsImagesForReportBox.add(imageWrapper);
        }
    }

    public void openFileUploader() {
        FileUploadDialog uploadDialog = (FileUploadDialog) openWindow("fileUploadDialog", WindowManager.OpenType.DIALOG);
        uploadDialog.addCloseListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)) {
                FileDescriptor fileDescriptor = fileUploadingAPI.getFileDescriptor(uploadDialog.getFileId(), uploadDialog.getFileName());
                try {
                    fileUploadingAPI.putFileIntoStorage(uploadDialog.getFileId(), fileDescriptor);

                    fileDescriptorsDs.addItem(fileDescriptor);

                    goodsImage = metadata.create(GoodsImage.class);
                    goodsImage.setGood(getItem());
                    goodsImage.setImage(fileDescriptor);
                    goodsImage.setPrimary(goodsImagesDs.size() == 0);

                    goodsImagePreview.setSource(FileDescriptorResource.class).setFileDescriptor(fileDescriptor);

                    createGoodsImageBlock(goodsImage);
                } catch (FileStorageException e) {
                    showNotification("Error saving file to FileStorage", NotificationType.ERROR);
                }
            }
        });
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (goodsImage != null) dataManager.commit(goodsImage);
        if (goodsImageForReport != null) dataManager.commit(goodsImageForReport);
        return super.postCommit(committed, close);
    }

    public void openImageForReportFileUploader() {
        FileUploadDialog uploadDialog = (FileUploadDialog) openWindow("fileUploadDialog", WindowManager.OpenType.DIALOG);
        uploadDialog.addCloseListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)) {
                FileDescriptor fileDescriptor = fileUploadingAPI.getFileDescriptor(uploadDialog.getFileId(), uploadDialog.getFileName());
                try {
                    fileUploadingAPI.putFileIntoStorage(uploadDialog.getFileId(), fileDescriptor);

                    fileDescriptorsDs.addItem(fileDescriptor);

                    goodsImageForReport = metadata.create(GoodsImageForReport.class);
                    goodsImageForReport.setGood(getItem());
                    goodsImageForReport.setImage(fileDescriptor);
                    goodsImageForReport.setPrimary(goodsImagesForReportDs.size() == 0);

                    goodsImageForReportPreview.setSource(FileDescriptorResource.class).setFileDescriptor(fileDescriptor);

                    createGoodsImageForReportBlock(goodsImageForReport);
                } catch (FileStorageException e) {
                    showNotification("Error saving file to FileStorage", NotificationType.ERROR);
                }
            }
        });
    }
}