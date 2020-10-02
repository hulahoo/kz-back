package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import kz.uco.tsadv.modules.personal.model.Disability;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class PcfDisability extends EditableFrame {

    CollectionDatasource<Disability, UUID> disabilityDs;

    @Inject
    private ExportDisplay exportDisplay;
    @Named("disabilityTable.create")
    private CreateAction disabilityTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        disabilityTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroupExt", getDsContext().get("personGroupDs").getItem())
                        : null);
    }

    @Override
    public void editable(boolean editable) {
    }

    @Override
    public void initDatasource() {
        disabilityDs = (CollectionDatasource<Disability, UUID>) getDsContext().get("disabilityDs");
    }


    public void downloadAttachment(Entity item, String columnId) {
        Disability disability = disabilityDs.getItem();
        if (disability != null) {
            LoadContext<Disability> loadContext = LoadContext.create(Disability.class);
            loadContext.setQuery(LoadContext.createQuery("select e from tsadv$Disability e where e.id = :jId")
                    .setParameter("jId", disability.getId()))
                    .setView("disability.all");
            Disability downloadDisability = dataManager.load(loadContext);
            if (downloadDisability != null) {
                exportDisplay.show(new ByteArrayDataProvider(downloadDisability.getAttachment()), downloadDisability.getAttachmentName());
            }
        }
    }
}
