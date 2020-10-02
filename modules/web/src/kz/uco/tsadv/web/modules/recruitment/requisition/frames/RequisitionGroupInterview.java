package kz.uco.tsadv.web.modules.recruitment.requisition.frames;

import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.recruitment.model.Interview;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

/**
 * @author veronika.buksha
 */
public class RequisitionGroupInterview extends AbstractFrame {

    private CollectionDatasource<Interview, UUID> groupInterviewsDs;
    private CollectionDatasource<Interview, UUID> groupInterviewChildrenDs;

    @Inject
    private Table<Interview> groupInterviewsTable;

    @Named("groupInterviewsTable.edit")
    private EditAction groupInterviewsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        groupInterviewsDs = (CollectionDatasource<Interview, UUID>) getDsContext().get("groupInterviewsDs");
        groupInterviewChildrenDs = (CollectionDatasource<Interview, UUID>) getDsContext().get("groupInterviewChildrenDs");

        groupInterviewsTableEdit.setWindowId("tsadv$GroupInterview.edit");

    }
}
