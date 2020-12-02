package kz.uco.tsadv.web.positionbpmrole;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.PositionBpmRole;
import kz.uco.tsadv.listener.PositionBpmRoleService;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PositionBpmRoleEdit extends AbstractEditor<PositionBpmRole> {

    @Inject
    private PositionBpmRoleService positionBpmRoleService;
    @Inject
    private CommonService commonService;
    @Inject
    private MetadataTools metadataTools;
    @Inject
    private DataManager dataManager;

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);

        /*if (getItem().getPositionGroup() == null || getItem().getProcModel() == null) return;

        if (positionBpmRoleService.isConstrain(getItem().getPositionGroup().getId(),
                getItem().getProcModel().getName(), getItem().getId())) {
            errors.add(getMessage("constrain"));
        }*/
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        /*List<BpmRolesLink> linkList = commonService.getEntities(BpmRolesLink.class,
                "select e from tsadv$BpmRolesLink e where e.bpmRolesDefiner.procModel.name = :procModelName",
                ParamsMap.of("procModelName", getItem().getProcModel().getName()), "bpmRolesLink-view");

        List<Entity> commitInstances = new ArrayList<>();
        for (BpmRolesLink bpmRolesLink : linkList) {
            BpmRolesLink link = metadataTools.deepCopy(bpmRolesLink);
            link.setId(UUID.randomUUID());
            link.setBpmRolesDefiner(null);
            link.setPositionBpmRole(getItem());
            commitInstances.add(link);
        }
        if (!commitInstances.isEmpty()) {
            dataManager.commit(new CommitContext(commitInstances));
        }*/
        return super.postCommit(committed, close);
    }
}