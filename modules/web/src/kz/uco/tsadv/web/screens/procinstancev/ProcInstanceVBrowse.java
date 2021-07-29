package kz.uco.tsadv.web.screens.procinstancev;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.entity.dbview.ProcInstanceV;
import kz.uco.tsadv.web.screens.exttaskdata.ExtTaskDataBrowse;

import javax.inject.Inject;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv$ProcInstanceV.browse")
@UiDescriptor("proc-instance-v-browse.xml")
@LookupComponent("procInstanceVsTable")
@LoadDataBeforeShow
public class ProcInstanceVBrowse extends StandardLookup<ProcInstanceV> {

    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected CollectionContainer<ProcInstanceV> procInstanceVsDc;

    @Subscribe("procInstanceVsTable.showProcessParticipants")
    protected void onProcInstanceVsTableShowProcessParticipants(Action.ActionPerformedEvent event) {
        ExtTaskDataBrowse build = screenBuilders.lookup(ExtTaskData.class, this)
                .withScreenClass(ExtTaskDataBrowse.class)
                .build();
        build.setProcInstanceV(procInstanceVsDc.getItem());
        build.show();
    }

    public void openEntity(ProcInstanceV item, String columnId) {
        AbstractBprocRequest request = item.getEntity();
        screenBuilders.editor((Class<AbstractBprocRequest>) request.getClass(), this)
                .withOpenMode(OpenMode.THIS_TAB)
                .editEntity(request)
                .build()
                .show();
    }
}