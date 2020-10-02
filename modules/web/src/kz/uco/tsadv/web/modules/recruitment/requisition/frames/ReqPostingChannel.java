package kz.uco.tsadv.web.modules.recruitment.requisition.frames;

import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.modules.recruitment.model.RequisitionPostingChannel;

import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author veronika.buksha
 */
public class ReqPostingChannel extends AbstractFrame {
    public CollectionDatasource<RequisitionPostingChannel, UUID> postingChannelsDs;
    public Datasource<Requisition> requisitionDs;

    @Named("postingChannelsTable.create")
    private CreateAction postingChannelsTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        postingChannelsDs = (CollectionDatasource<RequisitionPostingChannel, UUID>) getDsContext().get("postingChannelsDs");
        requisitionDs = (Datasource<Requisition>) getDsContext().get("requisitionDs");

        postingChannelsTableCreate.setInitialValues(Collections.singletonMap("requisition", requisitionDs.getItem()));
    }
}
