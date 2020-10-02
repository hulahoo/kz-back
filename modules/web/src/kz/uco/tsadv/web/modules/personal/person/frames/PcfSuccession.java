package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.Successor;
import kz.uco.tsadv.web.modules.personal.successor.SuccessorpersonCard;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class PcfSuccession extends EditableFrame {

    @Inject
    private ButtonsPanel buttonsPanel;
    @Inject
    private Metadata metadata;
    public CollectionDatasource<Successor, UUID> successorsDs;
    private Datasource<PersonGroupExt> personGroupDs;
    private Datasource<AssignmentExt> assignmentDs;
    @Named("successionPlanningTable.edit")
    private EditAction successionPlanningTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        successionPlanningTableEdit.setWindowId("tsadv$Successor.editPersonCard");
    }
    public void createSuccessionPerson(){
        Successor item = metadata.create(Successor.class);
        item.setPersonGroup(assignmentDs.getItem().getPersonGroup());
        SuccessorpersonCard successorpersonCard = (SuccessorpersonCard) openEditor("tsadv$Successor.editPersonCard",item,WindowManager.OpenType.DIALOG);
        successorpersonCard.addCloseListener(s -> {
            successorsDs.refresh();
        });

    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        assignmentDs = getDsContext().get("assignmentDs");
        successorsDs = (CollectionDatasource<Successor, UUID>) getDsContext().get("successorsDs");
    }



}