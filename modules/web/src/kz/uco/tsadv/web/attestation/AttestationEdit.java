package kz.uco.tsadv.web.attestation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import kz.uco.tsadv.modules.learning.model.Attestation;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class AttestationEdit extends AbstractEditor<Attestation> {
    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected Frame windowActions;
    @Named("attestationOrganizationsTable.create")
    protected CreateAction attestationOrganizationsTableCreate;
    @Named("attestationJobsTable.create")
    protected CreateAction attestationJobsTableCreate;
    @Named("attestationPositionsTable.create")
    protected CreateAction attestationPositionsTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

    }

    @Override
    public void ready() {
        super.ready();
        if (PersistenceHelper.isNew(getItem())){
            tabSheet.setVisible(false);
            getFrame().expand(windowActions);
        }
        attestationOrganizationsTableCreate.setInitialValues(ParamsMap.of("attestation",getItem()));
        attestationJobsTableCreate.setInitialValues(ParamsMap.of("attestation",getItem()));
        attestationPositionsTableCreate.setInitialValues(ParamsMap.of("attestation",getItem()));
    }
}