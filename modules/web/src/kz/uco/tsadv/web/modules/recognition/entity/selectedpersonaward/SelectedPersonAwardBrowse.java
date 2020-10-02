package kz.uco.tsadv.web.modules.recognition.entity.selectedpersonaward;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recognition.SelectedPersonAward;
import kz.uco.tsadv.service.RecognitionService;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class SelectedPersonAwardBrowse extends AbstractLookup {

    @Inject
    private RecognitionService recognitionService;

    @Inject
    private DataGrid<SelectedPersonAward> selectedPersonAwardsTable;

    @Inject
    private GroupDatasource<SelectedPersonAward, UUID> selectedPersonAwardsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        BaseAction nominateAction = new BaseAction("nominate") {
            @Override
            public void actionPerform(Component component) {
                nominate();
            }

            @Override
            public String getCaption() {
                return getMessage("nominate.btn");
            }
        };
        nominateAction.setEnabled(false);
        selectedPersonAwardsTable.addAction(nominateAction);
        selectedPersonAwardsTable.addSelectionListener(selectedPersonAwardSelectionEvent -> {
            List<SelectedPersonAward> selected = (List<SelectedPersonAward>) selectedPersonAwardSelectionEvent.getSelected();
            boolean enable = false;
            if (selected != null && !selected.isEmpty()) {
                enable = selected.stream().noneMatch(new Predicate<SelectedPersonAward>() {
                    @Override
                    public boolean test(SelectedPersonAward selectedPersonAward) {
                        return BooleanUtils.isTrue(selectedPersonAward.getAwarded());
                    }
                });
            }
            nominateAction.setEnabled(enable);
        });
    }

    private void nominate() {
        Set<SelectedPersonAward> selectedPersonAwardSet = selectedPersonAwardsTable.getSelected();
        if (selectedPersonAwardSet != null && !selectedPersonAwardSet.isEmpty()) {
            showOptionDialog(getMessage("confirm.title"),
                    getMessage("confirm.body"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    recognitionService.nominate(selectedPersonAwardSet);
                                    selectedPersonAwardsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });
        }
    }
}