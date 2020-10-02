package kz.uco.tsadv.web.individualdevelopmentplan;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.enums.IdpStatus;
import kz.uco.tsadv.modules.learning.model.IdpDetail;
import kz.uco.tsadv.modules.learning.model.IndividualDevelopmentPlan;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class IndividualDevelopmentPlanEdit extends AbstractEditor<IndividualDevelopmentPlan> {
    @Named("idpDetailTable.create")
    protected CreateAction idpDetailTableCreate;
    @Named("idpDetailTable.edit")
    protected EditAction idpDetailTableEdit;
    @Named("idpDetailTable.remove")
    protected RemoveAction idpDetailTableRemove;

    @Inject
    protected CommonService commonService;
    @Inject
    protected Table<IdpDetail> idpDetailTable;
    @Inject
    protected Datasource<IndividualDevelopmentPlan> individualDevelopmentPlanDs;
    @Inject
    protected CollectionDatasource<IdpDetail, UUID> idpDetailDs;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        idpDetailDs.addItemChangeListener(e -> {

        });
        idpDetailTable.addStyleProvider((entity, property) -> {
            boolean checkRed = !entity.getDone() &&
                    entity.getTargetDate().before(CommonUtils.getSystemDate());
            if (checkRed) {
                return "check-color-red";
            }
            return null;
        });
    }

    @Override
    protected void postInit() {
        super.postInit();
        setAccessible(IdpStatus.DRAFT.equals(getItem().getStatus()));
        individualDevelopmentPlanDs.addItemPropertyChangeListener(e -> {
            if ("status".equals(e.getProperty())) {
                setAccessible(IdpStatus.DRAFT.equals(e.getValue()));
            }
        });
    }

    protected void setAccessible(boolean enabled) {
        idpDetailTable.getAction("create").setEnabled(enabled);
        idpDetailTable.getAction("edit").setEnabled(enabled);
        idpDetailTable.getAction("remove").setEnabled(enabled);
    }
}