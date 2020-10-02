package kz.uco.tsadv.web.individualdevelopmentplan;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.IdpDetail;
import kz.uco.tsadv.modules.learning.model.IndividualDevelopmentPlan;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IndividualDevelopmentPlanBrowse extends AbstractLookup {
    @Named("individualDevelopmentPlansTable.create")
    protected CreateAction individualDevelopmentPlansTableCreate;
    @Named("individualDevelopmentPlansTable.edit")
    protected EditAction individualDevelopmentPlansTableEdit;
    @Named("individualDevelopmentPlansTable.remove")
    protected RemoveAction individualDevelopmentPlansTableRemove;

    @Inject
    protected GroupTable<IndividualDevelopmentPlan> individualDevelopmentPlansTable;
//    @Inject
//    private Button detail;
    @Inject
    protected GroupDatasource<IndividualDevelopmentPlan, UUID> individualDevelopmentPlansDs;

    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        individualDevelopmentPlansDs.addItemChangeListener(e -> {
            IndividualDevelopmentPlan individualDevelopmentPlan = e.getItem();
            if (individualDevelopmentPlan != null) {
                List<IdpDetail> idpDetails = getIdpDetails(individualDevelopmentPlan);
                if (!idpDetails.isEmpty()) {
                    individualDevelopmentPlansTableRemove.setEnabled(false);
                } else {
                    individualDevelopmentPlansTableRemove.setEnabled(true);
                }
            }
//            if (individualDevelopmentPlansTable.getSingleSelected() != null) {
//                detail.setEnabled(true);
//            } else {
//                detail.setEnabled(false);
//            }
        });
    }

    protected List<IdpDetail> getIdpDetails(IndividualDevelopmentPlan individualDevelopmentPlan) {
        return commonService.getEntities(IdpDetail.class,
                "select e from tsadv$IdpDetail e where e.idp.id = :idpId",
                ParamsMap.of("idpId", individualDevelopmentPlan.getId()), "idpDetail.edit");
    }

//    public void goToDetail() {
//        IndividualDevelopmentPlan individualDevelopmentPlan = individualDevelopmentPlansTable.getSingleSelected();
//        Window browse = openWindow("tsadv$IdpDetail.browse", WindowManager.OpenType.THIS_TAB,
//                ParamsMap.of("individualDevelopmentPlan", individualDevelopmentPlan));
//    }
}