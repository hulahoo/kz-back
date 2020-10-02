package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import kz.uco.tsadv.modules.personal.model.PersonLearningHistory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class PcfPersonLearningHistory extends EditableFrame {

    @Named("personLearningHistoryTable.create")
    protected CreateAction personLearningHistoryTableCreate;
    @Named("learningExpenseTable.create")
    protected CreateAction learningExpenseTableCreate;
    @Named("learningExpenseTable.edit")
    protected EditAction learningExpenseTableEdit;
    @Inject
    private DataGrid learningExpenseTable;
    @Inject
    private DataGrid personLearningHistoryTable;
    @Inject
    protected ButtonsPanel buttonsPanel;

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personLearningHistoryTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);

        personLearningHistoryTable.addSelectionListener(event -> {
            personLearningHistorytableListener((DataGrid.SelectionEvent<PersonLearningHistory>) event);
        });

        learningExpenseTableCreate.setWindowId("tsadv$LearningExpense.edit.tsadv");
        learningExpenseTableEdit.setWindowId("tsadv$LearningExpense.edit.tsadv");
    }

    protected void personLearningHistorytableListener(DataGrid.SelectionEvent<PersonLearningHistory> event) {
        learningExpenseTableCreate.setEnabled(!personLearningHistoryTable.getSelected().isEmpty());
    }
}