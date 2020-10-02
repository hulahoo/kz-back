package kz.uco.tsadv.web.modules.recognition.entity.rcgquestionanswer;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.modules.recognition.RcgQuestionAnswer;
import kz.uco.tsadv.modules.recognition.enums.RcgAnswerType;

import javax.inject.Inject;
import java.util.Map;

public class RcgQuestionAnswerEdit extends AbstractEditor<RcgQuestionAnswer> {

    @WindowParam
    private RcgAnswerType rcgAnswerType;
    @Inject
    private FieldGroup textAnswers;
    @Inject
    private FieldGroup fieldGroup;
    @Inject
    private FileUploadField iconUploader;
    @Inject
    private DataSupplier dataSupplier;
    @Inject
    private Image iconImage;
    @Inject
    private FileUploadingAPI fileUploadingAPI;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        FieldGroup.FieldConfig fieldConfig = fieldGroup.getFieldNN("iconField");

        if (rcgAnswerType != null) {
            boolean isIcon = rcgAnswerType.equals(RcgAnswerType.ICON);
            fieldConfig.setVisible(isIcon);
            iconUploader.setRequired(isIcon);

            textAnswers.setVisible(!isIcon);
        }
    }

    @Override
    protected void initNewItem(RcgQuestionAnswer item) {
        super.initNewItem(item);
        item.setScore(0);
    }

    @Override
    protected void postInit() {
        super.postInit();

        iconUploader.addFileUploadSucceedListener(event -> {
            FileDescriptor fd = iconUploader.getFileDescriptor();
            try {
                fileUploadingAPI.putFileIntoStorage(iconUploader.getFileId(), fd);
            } catch (FileStorageException e) {
                throw new RuntimeException("Error saving file to FileStorage", e);
            }

            FileDescriptor committedImage = dataSupplier.commit(fd);
            iconImage.setSource(FileDescriptorResource.class).setFileDescriptor(committedImage);

            getItem().setIcon(committedImage);
        });

        iconUploader.addAfterValueClearListener(afterValueClearEvent -> {
            FileDescriptor currentImage = getItem().getIcon();
            if (currentImage != null) {
                try {
                    fileUploadingAPI.deleteFile(currentImage.getId());
                } catch (FileStorageException e) {
                    e.printStackTrace();
                }

                getItem().setIcon(null);
            }
        });
    }
}