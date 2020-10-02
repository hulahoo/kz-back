package kz.uco.tsadv.web.modules.recruitment.requisition.frames;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.modules.recruitment.model.RequisitionMember;

import javax.inject.Inject;
import java.util.*;

/**
 * @author veronika.buksha
 */
public class ReqMember extends AbstractFrame {
    public CollectionDatasource<RequisitionMember, UUID> membersDs;
    public Datasource<Requisition> requisitionDs;

    @Inject
    protected Metadata metadata;

//    @Named("membersTable.create")
//    private CreateAction membersTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        membersDs = (CollectionDatasource<RequisitionMember, UUID>) getDsContext().get("membersDs");
        requisitionDs = (Datasource<Requisition>) getDsContext().get("requisitionDs");

//        membersTableCreate.setInitialValues(Collections.singletonMap("requisition", requisitionDs.getItem()));
    }

    public void customCreate() {
        RequisitionMember requisitionMember = metadata.create(RequisitionMember.class);
        requisitionMember.setRequisition(requisitionDs.getItem());
        Map<String, Object> params = new HashMap<>();
        List<PersonGroupExt> personGroups = new ArrayList<>();

        membersDs.getItems().forEach(requisitionMember1 -> personGroups.add(requisitionMember1.getPersonGroup()));
        params.put("alreadyExist", personGroups);

        Window.Editor editor = openEditor("tsadv$RequisitionMember.edit", requisitionMember, WindowManager.OpenType.DIALOG, params);
        editor.addCloseWithCommitListener(() -> membersDs.refresh());
    }
}
