package kz.uco.tsadv.web.modules.learning.answer.v72;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.components.Image;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.components.ImgCropBean;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import kz.uco.tsadv.modules.learning.model.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@UiController("tsadv$Answer.edit")
@UiDescriptor("answer-edit.xml")
@EditedEntityContainer("answerDc")
@LoadDataBeforeShow
public class AnswerEdit extends StandardEditor<Answer> {

    protected static final Logger log = LoggerFactory.getLogger(AnswerEdit.class);

    protected QuestionType questionType;

    @Inject
    protected ImgCropBean imgCropBean;
    @Inject
    protected FileUploadField imageUpload;
    @Inject
    protected Image image;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected FileLoader fileLoader;

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    @SuppressWarnings("ConstantConditions")
    @Subscribe
    protected void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        if (imageUpload.getContentProvider() != null) {
            try {
                FileDescriptor fileDescriptor = imgCropBean.getFileDescriptor(imageUpload);
                getEditedEntity().setImage(fileDescriptor != null ? dataManager.commit(fileDescriptor) : null);
            } catch (FileStorageException e) {
                log.error("Error", e);
            }
        }
    }

    @Subscribe("imageUpload")
    public void onImageUploadUploadSucceed(FileUploadField.FileUploadSucceedEvent event) throws FileStorageException {
        FileDescriptor uploadValue = imageUpload.getValue();

        if (uploadValue != null)
            imgCropBean.crop(this, fileLoader.openStream(uploadValue), image, imageUpload);
    }

    @Subscribe("deleteImage")
    protected void onDeleteImageClick(Button.ClickEvent event) {
        getEditedEntity().setImage(null);
    }
}