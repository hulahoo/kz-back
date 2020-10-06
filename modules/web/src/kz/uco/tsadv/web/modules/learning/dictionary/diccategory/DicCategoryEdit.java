package kz.uco.tsadv.web.modules.learning.dictionary.diccategory;

import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.common.IMAGE_SIZE;
import kz.uco.base.web.abstraction.six.AbstractDictionaryEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

public class DicCategoryEdit extends AbstractDictionaryEditor<DicCategory> {

    @Inject
    private FileUploadField imageUpload;

    @Inject
    private Embedded categoryImage;

    @Inject
    private Datasource<DicCategory> dicCategoryDs;

//    @Override
    public void init(Map<String, Object> params) {
        imageUpload.setUploadButtonCaption("");
        imageUpload.setClearButtonCaption("");

        imageUpload.addFileUploadSucceedListener(event -> {
            DicCategory dicCategory = dicCategoryDs.getItem();
            if (dicCategory != null && imageUpload.getFileContent() != null) {
                try {
                    dicCategory.setImage(CommonUtils.resize(imageUpload.getFileContent(), IMAGE_SIZE.XSS));
                    Utils.getCourseCategoryImageEmbedded(dicCategory, null, categoryImage);
                } catch (IOException e) {
//                    showNotification(getMessage("fileUploadErrorMessage"), NotificationType.ERROR);
                }
            }
            imageUpload.setValue(null);
        });

        imageUpload.addBeforeValueClearListener(beforeEvent -> {
            beforeEvent.preventClearAction();
//            showOptionDialog("", "Are you sure you want to delete this photo?", Dialogs.MessageType.CONFIRMATION,
//                    new Action[]{
//                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY) {
//                                public void actionPerform(Component component) {
//                                    dicCategoryDs.getItem().setImage(null);
//                                    categoryImage.resetSource();
//                                }
//                            },
//                            new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
//                    });
        });

//        super.init(params);
    }

    @SuppressWarnings("all")
//    @Override
    protected void postInit() {
        DicCategory dicCategory = dicCategoryDs.getItem();
        Utils.getCourseCategoryImageEmbedded(dicCategory, null, categoryImage);
    }

}