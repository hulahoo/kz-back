package kz.uco.tsadv.web.modules.learning.answer.v72;

import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.components.Image;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.components.ImgCropBean;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import kz.uco.tsadv.modules.learning.model.Answer;

import javax.inject.Inject;

@UiController("tsadv$Answer.edit")
@UiDescriptor("answer-edit.xml")
@EditedEntityContainer("answerDc")
@LoadDataBeforeShow
public class AnswerEdit extends StandardEditor<Answer> {

    protected QuestionType questionType;
    @Inject
    protected ImgCropBean imgCropBean;
    @Inject
    protected FileUploadField imageUpload;
    @Inject
    protected Image image;

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    @Subscribe("imageUpload")
    protected void onImageUploadFileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
        imgCropBean.crop(this, imageUpload.getFileContent(), image, imageUpload);
    }
}