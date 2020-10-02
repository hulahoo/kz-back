package kz.uco.tsadv.web.modules.recognition.entity.rcgquestion;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recognition.RcgQuestion;
import kz.uco.tsadv.service.RecognitionService;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class RcgQuestionBrowse extends AbstractLookup {

    @Named("rcgQuestionsTable.create")
    private CreateAction createAction;
    @Named("rcgQuestionsTable.edit")
    private EditAction editAction;
    @Inject
    private Button activateBtn;
    @Inject
    private Button deactivateBtn;
    @Inject
    private GroupDatasource<RcgQuestion, UUID> rcgQuestionsDs;
    @Inject
    private RecognitionService recognitionService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        rcgQuestionsDs.addItemChangeListener(e -> {
            boolean activate = e.getItem() != null && BooleanUtils.isFalse(e.getItem().getActive());
            activateBtn.setEnabled(activate);
            deactivateBtn.setEnabled(!activate);
        });
        createAction.setAfterCommitHandler(entity -> rcgQuestionsDs.refresh());
        editAction.setAfterCommitHandler(entity -> rcgQuestionsDs.refresh());
    }

    public void activate() {
        showOptionDialog(
                getMessage("activate.confirm.title"),
                getMessage("activate.confirm.body"),
                MessageType.CONFIRMATION_HTML,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                try {
                                    RcgQuestion rcgQuestion = rcgQuestionsDs.getItem();
                                    if (rcgQuestion == null) {
                                        throw new NullPointerException("Please, select question");
                                    }
                                    recognitionService.resetActiveQuestions(rcgQuestion.getId(), true);
                                    rcgQuestionsDs.refresh();
                                } catch (Exception ex) {
                                    showNotification(
                                            getMessage("activate.error.title"),
                                            ex.getMessage(),
                                            NotificationType.TRAY);
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL)
                });
    }

    public void deactivate() {
        showOptionDialog(
                getMessage("activate.confirm.title"),
                getMessage("activate.confirm.da.body"),
                MessageType.CONFIRMATION_HTML,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                try {
                                    RcgQuestion rcgQuestion = rcgQuestionsDs.getItem();
                                    if (rcgQuestion == null) {
                                        throw new NullPointerException("Please, select question");
                                    }
                                    recognitionService.resetActiveQuestions(rcgQuestion.getId(), false);
                                    rcgQuestionsDs.refresh();
                                } catch (Exception ex) {
                                    showNotification(
                                            getMessage("activate.error.title"),
                                            ex.getMessage(),
                                            NotificationType.TRAY);
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL)
                });
    }
}