package kz.uco.tsadv.web.modules.personal.absence;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.entity.dbview.AbsenceBalanceV;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.service.CallStoredFunctionService;
import kz.uco.tsadv.web.modules.personal.person.frames.EditableFrame;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AbsenceBrowse extends EditableFrame {
    @Inject
    protected ButtonsPanel buttonsPanel;
    protected CollectionDatasource<Absence, UUID> absencesDs;
    @Inject
    protected CallStoredFunctionService callStoredFunctionService;
    @Named("absencesTable.remove")
    protected RemoveAction absencesTableRemove;
    @Named("absencesTable.create")
    protected CreateAction createAction;
    @Named("absencesTable.edit")
    protected EditAction editAction;
    @Inject
    protected Datasource<AssignmentExt> assignmentDs;

    protected CollectionDatasource<AbsenceBalanceV, UUID> absenceBalancesVDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        absencesDs = (CollectionDatasource<Absence, UUID>) getDsContext().get("absencesDs");
        absenceBalancesVDs = (CollectionDatasource<AbsenceBalanceV, UUID>) getDsContext().get("absenceBalancesVDs");
        Objects.requireNonNull(absencesDs).refresh();
        this.setHeight("100%");
        createAction.setInitialValues(Collections.singletonMap("personGroup", getDsContext().get("personGroupDs").getItem()));
        absencesTableRemove.setBeforeActionPerformedHandler(() -> {
            //absenceBalanceService.recountBalanceDays(absencesDs.getItem().getPersonGroup(), null, absencesDs.getItem(), null, null);
            return true;
        });
        absencesTableRemove.setAfterRemoveHandler(removedItems ->
                removedItems.stream().findFirst().ifPresent(removedItem -> callRefreshPersonBalanceSqlFunction(((Absence) removedItem).getPersonGroup().getId().toString())));
        createAction.setWindowParamsSupplier(() -> ParamsMap.of("assignmentDs", assignmentDs));
        createAction.setEditorCloseListener(actionId -> {
            absenceBalancesVDs.refresh();
            absencesDs.refresh();
        });
        editAction.setWindowParamsSupplier(() -> ParamsMap.of("assignmentDs", assignmentDs));
        editAction.setEditorCloseListener(actionId -> {
            absenceBalancesVDs.refresh();
            absencesDs.refresh();
        });
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
    }

    protected void callRefreshPersonBalanceSqlFunction(String personGroupId) {
        String sql = " select bal.refresh_person_balance('" + personGroupId + "')";
        callStoredFunctionService.execCallSqlFunction(sql);
    }


}