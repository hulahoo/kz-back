package kz.uco.tsadv.web.modules.personal.transferrequest;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.model.TransferRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class TransferRequestBrowse extends AbstractLookup {

    @Named("transferRequestsTable.edit")
    private EditAction transferRequestsTableEdit;
    @Named("transferRequestsTable.create")
    private CreateAction transferRequestsTableCreate;

    @Inject
    private GroupDatasource<TransferRequest, UUID> transferRequestsDs;

    @Override
    public void init(Map<String, Object> params) {
        transferRequestsTableEdit.setAfterCommitHandler(entity -> transferRequestsDs.refresh());
        transferRequestsTableCreate.setAfterCommitHandler(entity -> transferRequestsDs.refresh());
    }

}