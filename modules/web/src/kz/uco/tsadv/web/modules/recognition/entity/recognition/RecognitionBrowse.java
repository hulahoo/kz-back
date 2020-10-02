package kz.uco.tsadv.web.modules.recognition.entity.recognition;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recognition.Recognition;
import kz.uco.tsadv.service.RecognitionService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class RecognitionBrowse extends AbstractLookup {

    @Inject
    protected Button removeBtn;

    @Inject
    protected GroupDatasource<Recognition, UUID> recognitionsDs;

    @Inject
    protected RecognitionService recognitionService;

    @Named("recognitionsTable.create")
    protected CreateAction recognitionsTableCreate;
    @Named("recognitionsTable.edit")
    protected EditAction recognitionsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        recognitionsDs.addItemChangeListener(e -> removeBtn.setEnabled(e.getItem() != null));

        recognitionsTableCreate.setAfterCommitHandler(entity -> {
            recognitionService.addBadges((Recognition) entity);
        });

        recognitionsTableEdit.setAfterCommitHandler(entity -> {
            recognitionService.addBadges((Recognition) entity);
        });
    }

    public void removeRecognition() {
        showOptionDialog(getMessage("remove.confirm"),
                getMessage("remove.confirm.text"),
                MessageType.CONFIRMATION_HTML,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                recognitionService.removeRecognition(recognitionsDs.getItem());
                                recognitionsDs.refresh();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });
    }
}