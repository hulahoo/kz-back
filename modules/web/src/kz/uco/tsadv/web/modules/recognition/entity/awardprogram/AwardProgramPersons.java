package kz.uco.tsadv.web.modules.recognition.entity.awardprogram;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.recognition.AwardProgram;
import kz.uco.tsadv.modules.recognition.pojo.AwardProgramPerson;
import kz.uco.tsadv.service.RecognitionService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AwardProgramPersons extends AbstractWindow {

    @Inject
    private RecognitionService recognitionService;

    @Inject
    private Button addToShortListBtn;
    @Inject
    private DataManager dataManager;

    @Inject
    private CollectionDatasource<AwardProgramPerson, UUID> programPersonAwardsDs;

    @Inject
    private DataGrid<AwardProgramPerson> personDataGrid;

    @Inject
    private TextField<String> filterText;
    private AwardProgram awardProgram;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("awardProgram")) {
            programPersonAwardsDs.refresh(params);
            awardProgram = (AwardProgram) params.get("awardProgram");
        }

        personDataGrid.addSelectionListener(event -> {
            List<AwardProgramPerson> selected = (List<AwardProgramPerson>) event.getSelected();
            addToShortListBtn.setEnabled(selected != null && !selected.isEmpty());
        });
        filterText.addValueChangeListener(e -> {
            programPersonAwardsDs.refresh(ParamsMap.of("awardProgram", awardProgram, "filter", e.getValue()));
        });

    }

    public void addToShortList() {
        Set<AwardProgramPerson> selectedItems = personDataGrid.getSelected();
        if (selectedItems != null && !selectedItems.isEmpty()) {
            showOptionDialog(getMessage("confirm.title"),
                    getMessage("confirm.body"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    recognitionService.addShortList(selectedItems);
                                    programPersonAwardsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });
        }
    }
}