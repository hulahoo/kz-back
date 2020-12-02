package kz.uco.tsadv.web.bpm.procactor;

/*import com.haulmont.bpm.entity.ProcActor;
import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.bpm.entity.ProcRole;
import com.haulmont.bpm.entity.ProcTask;*/
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.beans.BpmUtils;
import kz.uco.tsadv.entity.dbview.ProcInstanceV;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProcActorBrowse extends AbstractLookup {

    /*@Inject
    protected CollectionDatasource<ProcTask, UUID> procTasksDs;
    @Inject
    protected CollectionDatasource<ProcActor, UUID> procActorsDs;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    protected Metadata metadata;
    @Inject
    protected BpmUtils bpmUtils;
    @Inject
    protected Table<ProcActor> procActorsTable;

    protected ProcInstance procInstance;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        procInstance = (ProcInstance) params.get("procInstance");

        Assert.notNull(procInstance, "ProcInstance not found!");

        procInstance = dataSupplier.reload(procInstance, "procInstance-browse");

        procActorsTable.getColumn("startDate").setCaption(messages.getMessage(ProcTask.class, "ProcTask.startDate"));
        procActorsTable.getColumn("endDate").setCaption(messages.getMessage(ProcTask.class, "ProcTask.endDate"));

        procTasksDs.refresh();
    }

    @Override
    public void ready() {
        super.ready();

        ProcTask initiatorTask = bpmUtils.createInitiatorTask(procInstance);
        procTasksDs.addItem(initiatorTask);

        ProcRole procRole = metadata.create(ProcRole.class);
        procRole.setName(messages.getMessage(ProcInstanceV.class, initiatorTask.getName()));

        ProcActor procActor = initiatorTask.getProcActor();
        procActor.setProcRole(procRole);

        List<ProcActor> sortedCollect = new LinkedList<>(procActorsDs.getItems());
        procActorsDs.clear();
        sortedCollect.add(0, procActor);
        sortedCollect.forEach(procActorsDs::addItem);
    }

    public void close() {
        close(CLOSE_ACTION_ID);
    }

    public Component generateStartDate(ProcActor entity) {
        return getComponent(entity, "startDate");
    }

    public Component generateEndDate(ProcActor entity) {
        return getComponent(entity, "endDate");
    }

    protected Component getComponent(ProcActor entity, String property) {
        ProcTask procTask = getProcTask(entity);
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(procTask != null ? procTask.getValue(property) : "");
        return label;
    }

    @Nullable
    protected ProcTask getProcTask(ProcActor entity) {
        return procTasksDs.getItems().stream()
                .filter(procTask -> procTask.getProcActor().equals(entity))
                .findFirst().orElse(null);
    }*/
}