package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionType;
import kz.uco.tsadv.modules.recruitment.model.Requisition;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequisitionBrowseSelf extends RequisitionBrowse {
    @Inject
    private DataGrid<Requisition> requisitionsTable;

    @Override
    public void init(Map<String, Object> params) {
        isSelfService=true;

        super.init(params);

        requisitionsTableCreate.setWindowId("tsadv$Requisition.editSelf");
        requisitionsTableEdit.setWindowId("tsadv$Requisition.editSelf");

        paramsMap.put("fromRequisitionBrowseSelf", true);
        requisitionsTableCreate.setWindowParams(paramsMap);
        requisitionsTableEdit.setWindowParams(paramsMap);
    }

//    @Override
//    protected void initFilterMap() {
//        // not remove this override method, it need for reset CustomFilter
//
//        filterMap = new LinkedHashMap<>();
//    }


    @Override
    protected void setQuery() {
        String orderBy = "e.startDate desc";
        PersonGroupExt personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
        if (recruitmentConfig.getSortingCVDate()) {
            orderBy = "e.finalCollectDate asc";
        }
        requisitionsDs.setQuery(String.format(
                "select e from tsadv$Requisition e " +
                        "where '%s' <> 'null' and e.managerPersonGroup.id = '%s' and e.requisitionType = %d " +
//                        "and e.requisitionStatus not in (2, 3, 5) " +
                        "order by " + orderBy,
                personGroup != null ? personGroup.getId() : null,
                personGroup != null ? personGroup.getId() : null,
                RequisitionType.STANDARD.getId()));

    }

    @Override
    protected void applyFilter() {
        // not remove this override method, it need for reset CustomFilter
        customFilter.selectFilter("requisitionStatus",RequisitionStatus.OPEN);
        customFilter.applyFilter();
    }

    @Override
    protected boolean isRecruiter() {
        return false;
    }
    protected void editRequisition(Component component, Entity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("isRecruiter", isRecruiter());
        RequisitionEditSelf requisitionEditSelf = (RequisitionEditSelf) openEditor("tsadv$Requisition.editSelf", entity, WindowManager.OpenType.THIS_TAB, map);
        requisitionEditSelf.addCloseListener(actionId -> {
            requisitionsDs.refresh();
        });
    }
}