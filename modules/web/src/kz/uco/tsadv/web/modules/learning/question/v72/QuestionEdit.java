package kz.uco.tsadv.web.modules.learning.question.v72;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.actions.list.CreateAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.components.ImgCropBean;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.web.modules.learning.answer.v72.AnswerEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@UiController("tsadv$Question.edit")
@UiDescriptor("question-edit.xml")
@EditedEntityContainer("questionDc")
@LoadDataBeforeShow
public class QuestionEdit extends StandardEditor<Question> {

    protected static final Logger log = LoggerFactory.getLogger(QuestionEdit.class);
    @Inject
    protected Image image;
    @Inject
    protected FileUploadField imageUpload;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Notifications notifications;
    @Named("answersTable.create")
    protected CreateAction<Answer> answersTableCreate;
    @Inject
    protected CollectionPropertyContainer<Answer> answersDc;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected ImgCropBean imgCropBean;
    @Inject
    protected FileLoader fileLoader;

    @Subscribe("imageUpload")
    protected void onImageUploadFileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) throws FileStorageException {
        FileDescriptor uploadValue = imageUpload.getValue();
        if (uploadValue != null)
            imgCropBean.crop(this,
                    fileLoader.openStream(uploadValue),
                    image,
                    imageUpload,
                    this::setImage);
    }

    protected void setImage(byte[] imageContent) {
        try {
            FileDescriptor fileDescriptor = imgCropBean.getFileDescriptor(imageUpload);
            getEditedEntity().setImage(fileDescriptor != null ? dataManager.commit(fileDescriptor) : null);
        } catch (FileStorageException e) {
            throw new RuntimeException("Error after crop", e);
        }
    }

    @Subscribe("typeField")
    protected void onTypeFieldValueChange(HasValue.ValueChangeEvent<QuestionType> event) {
        QuestionType questionTypeObject = event.getValue();
        answersTableCreate.setEnabled(questionTypeObject != null);

        if (questionTypeObject != null) {
            switch (questionTypeObject) {
                case ONE:
                case MANY: {
                    break;
                }
                case NUM:
                case TEXT: {
                    answersDc.getMutableItems().clear();
                    break;
                }
            }
        }
    }

    @Override
    protected ValidationErrors validateScreen() {
        ValidationErrors validationErrors = super.validateScreen();
        if (validationErrors.isEmpty()) {
            QuestionType questionType = getEditedEntity().getType();
            switch (questionType) {
                case ONE:
                case MANY:
                case NUM: {
                    String message = validate(questionType);
                    if (message != null) validationErrors.add(messageBundle.getMessage(message));
                }
            }
        }
        return validationErrors;
    }

    protected String validate(QuestionType questionType) {
        int answerSize = answersDc.getItems().size();
        if (answerSize == 0) return "add.question.answer.empty";

        if (questionType.equals(QuestionType.NUM) && answerSize > 1)
            return "add.question.answer.single.right";

        if ((questionType.equals(QuestionType.ONE) || questionType.equals(QuestionType.MANY)) && answerSize == 1) {
            /**
             * Required minimum two answer
             * */
            return "add.question.answer.two.answer";
        }

        int rightAnswerCount = 0;
        for (Answer answer : answersDc.getItems()) {
            if (answer.getCorrect() != null && answer.getCorrect()) {
                rightAnswerCount++;
            }
        }

        if (rightAnswerCount == 0) {
            return questionType.equals(QuestionType.MANY) ? "add.question.answer.min.not.right.2" : "add.question.answer.min.not.right";
        } else if (rightAnswerCount > 1) {
            if (questionType.equals(QuestionType.ONE)) {
                /**
                 * Required single correct answer
                 * */
                return "add.question.answer.not.right";
            }
        }

        return null;
    }

    @Install(to = "answersTable.create", subject = "screenConfigurer")
    protected void answersTableCreateScreenConfigurer(Screen screen) {
        ((AnswerEdit) screen).setQuestionType(getEditedEntity().getType());
    }

    @Install(to = "answersTable.edit", subject = "screenConfigurer")
    protected void answersTableEditScreenConfigurer(Screen screen) {
        ((AnswerEdit) screen).setQuestionType(getEditedEntity().getType());
    }

    @Install(to = "answersTable.edit", subject = "transformation")
    protected Answer answersTableEditTransformation(Answer answer) {
        return answer;
    }

    @Subscribe("deleteImage")
    protected void onDeleteImageClick(Button.ClickEvent event) {
        getEditedEntity().setImage(null);
    }
}