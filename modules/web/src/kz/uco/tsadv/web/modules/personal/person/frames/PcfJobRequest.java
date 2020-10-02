package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class PcfJobRequest extends EditableFrame {

    @Inject
    private ButtonsPanel buttonsPanel;

    public CollectionDatasource<JobRequest, UUID> jobRequestsDs;

    @Named("jobRequestsTable.create")
    private CreateAction jobRequestsTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        jobRequestsTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("candidatePersonGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        jobRequestsDs = (CollectionDatasource<JobRequest, UUID>) getDsContext().get("jobRequestsDs");
    }
}