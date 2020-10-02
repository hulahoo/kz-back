package kz.uco.tsadv.web.positionSsView;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.entity.dbview.PositionSsView;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.Grade;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import kz.uco.tsadv.service.HierarchyService;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.haulmont.cuba.gui.components.Component;

public class PositionSsViewBrowse extends AbstractLookup {

    protected Date date;

    @Inject
    private GroupDatasource<PositionSsView, UUID> positionSsViewsDs;
    @Inject
    private HierarchyService hierarchyService;
    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        params.computeIfAbsent("date", s -> CommonUtils.getSystemDate());
        date = (Date) params.get("date");
        if (params.containsKey("hierarchy") && (boolean) params.get("hierarchy") &&
                params.containsKey("parentPositionGroupId") && params.get("parentPositionGroupId") != null) {
            List<UUID> list = hierarchyService.getPositionGroupIdChild((UUID) params.get("parentPositionGroupId"), (Date) params.get("date"));
            params.put("list", list);
            positionSsViewsDs.setQuery("select e from tsadv$PositionSsView e " +
                    "                where :param$date between e.startDate and e.endDate " +
                    "                      and e.positionGroup.id in :param$list " +
                    "order by e.id ");
        } else if (params.containsKey("posOrgJob") && (boolean) params.get("posOrgJob")) {
            positionSsViewsDs.setQuery(" select e " +
                    "                           from tsadv$PositionSsView e " +
                    "                           join e.positionGroup.list p " +
                    "                               on :param$date between p.startDate and p.endDate " +
                    (params.containsKey("jobGroupId") ? "  and ( p.jobGroup.id = COALESCE(:param$jobGroupId, p.jobGroup.id)) " : "") +
                    (params.containsKey("orgGroupId") ? " and ( p.organizationGroupExt.id = COALESCE(:param$orgGroupId, p.organizationGroupExt.id) ) " : "") +
                    "                           where :param$date between e.startDate and e.endDate " +
                    "                          order by e.id desc");
        }
        super.init(params);
    }

    public Component generateOrganizationCell(PositionSsView entity) {
        OrganizationExt organizationExt = entity.getOrganizationGroup() != null
                && entity.getOrganizationGroup().getList() != null
                ? entity.getOrganizationGroup().getList().stream()
                .filter(org -> !date.before(org.getStartDate()) && !date.after(org.getEndDate()))
                .findFirst().orElse(null) : null;
        return createLabel(organizationExt != null ? organizationExt.getOrganizationName() : null);
    }

    public Component generateGradeCell(PositionSsView entity) {
        Grade grade = entity.getGradeGroup() != null
                && entity.getGradeGroup().getList() != null
                ? entity.getGradeGroup().getList().stream()
                .filter(g -> !date.before(g.getStartDate()) && !date.after(g.getEndDate()))
                .findFirst().orElse(null) : null;
        return createLabel(grade != null ? grade.getGradeName() : null);
    }

    protected Label createLabel(String labelValue) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(labelValue);
        return label;
    }
}