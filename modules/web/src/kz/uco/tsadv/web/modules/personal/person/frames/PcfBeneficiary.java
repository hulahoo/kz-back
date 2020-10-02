package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.Beneficiary;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class PcfBeneficiary extends EditableFrame {

    @Inject
    protected ButtonsPanel buttonsPanel;
    @Inject
    protected Metadata metadata;

    public CollectionDatasource<Beneficiary, UUID> beneficiariesDs;
    protected Datasource<AssignmentExt> assignmentDs;

    @Named("beneficiariesTable.create")
    protected CreateAction beneficiariesTableCreate;

    @Named("beneficiariesTable.edit")
    protected EditAction beneficiariesTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        beneficiariesTableEdit.setWindowId("tsadv$BeneficiaryView");
/*        beneficiariesTableEdit.setAfterCommitHandler(entity ->
                showNotification(getMessage("person.card.commit.msg"), NotificationType.TRAY));*/

        beneficiariesTableCreate.setOpenType(WindowManager.OpenType.DIALOG);
        beneficiariesTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroupParent", getDsContext().get("personGroupDs").getItem())
                        : null);
    }


    public void removeBeneficiary() {
        showOptionDialog(
                "Подтверждение",
                "Вы действительно хотите удалить выбранные элементы?",
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                LoadContext<Beneficiary> loadContext = LoadContext.create(Beneficiary.class);
                                loadContext.setQuery(LoadContext.createQuery("select e from tsadv$Beneficiary e " +
                                        "where e.personGroupParent.id = :childPersonGroupId and e.personGroupChild.id = :parentPersonGroupId")
                                        .setParameter("childPersonGroupId", beneficiariesDs.getItem().getPersonGroupChild().getId())
                                        .setParameter("parentPersonGroupId", beneficiariesDs.getItem().getPersonGroupParent().getId()))
                                        .setView("beneficiaryView");
                                Beneficiary beneficiary = dataManager.load(loadContext);
                                beneficiariesDs.removeItem(beneficiary);
                                beneficiariesDs.commit();
                                beneficiariesDs.removeItem(beneficiariesDs.getItem());
                                beneficiariesDs.commit();

                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                }
        );

    }


    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        assignmentDs = getDsContext().get("assignmentDs");
        beneficiariesDs = (CollectionDatasource<Beneficiary, UUID>) getDsContext().get("beneficiariesDs");
    }
}