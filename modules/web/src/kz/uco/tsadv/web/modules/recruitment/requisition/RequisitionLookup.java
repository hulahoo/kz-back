package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recruitment.model.Requisition;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class RequisitionLookup extends AbstractLookup {

    @Inject
    private GroupDatasource<Requisition, UUID> requisitionsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("status")) {
            requisitionsDs.setQuery("select e from tsadv$Requisition e where e.requisitionType = :param$requisitionType " +
                            " and e.requisitionStatus = :param$status" +
                    " and e.id not in (select j.requisition.id from tsadv$JobRequest j " +
                                 " where j.candidatePersonGroup.id = :param$candidatePersonGroupId)");
        }
        if (params.containsKey("requisitionType"))
            switch ((Integer) params.get("requisitionType")) {
                case 1:
                    this.setCaption(getMessage("Requisition.browseCaption"));
                    break;
                case 2:
                    this.setCaption(getMessage("Requisition.templateCaption"));
                    break;
            }
    }
}