package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.components.GroupsComponent;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.service.OrganizationService;

import javax.inject.Inject;
import java.util.*;

@SuppressWarnings("all")
public class PcfAssignment extends EditableFrame {

    protected Datasource<AssignmentExt> assignmentDs;
    protected CollectionDatasource<AssignmentExt, UUID> assignmentHistoryDs;

    @Inject
    protected Table historyTable;

    protected GroupsComponent groupsComponent = AppBeans.get(GroupsComponent.class);
    protected OrganizationService organizationService = AppBeans.get(OrganizationService.class);
    protected Date oracleEndDate = getOracleEndTime();

    @Override
    public void editable(boolean editable) {

    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        AssignmentExt selected = assignmentDs.getItem();

        historyTable.setSelected(selected);

        assignmentHistoryDs.addItemChangeListener(e -> groupsComponent.setInDateGroups(e.getItem(), getEndDate(e.getItem().getEndDate())));

        historyTable.addGeneratedColumn("organizationGroup.organization.organizationName", this::generateOrgPath);

        groupsComponent.setInDateGroups(selected, getEndDate(selected.getEndDate()));
    }

    protected Date getEndDate(Date endDate) {
        return endDate.after(oracleEndDate) ? oracleEndDate : endDate;
    }

    protected Date getOracleEndTime() {
        Calendar cal = java.util.Calendar.getInstance();
        cal.set(4712, Calendar.DECEMBER, 30, 0, 0, 0);
        return cal.getTime();
    }

    public Component generateOrgPath(Entity assignment) {
        AssignmentExt assignmentExt = (AssignmentExt) assignment;
        Label label = componentsFactory.createComponent(Label.class);
        OrganizationGroupExt organizationGroup = assignmentExt.getOrganizationGroup();
        String organizationName = organizationGroup.getOrganizationName();
        label.setValue(organizationName);

        String organizationPathToHint = organizationService.getOrganizationPathToHint(organizationGroup.getId(), CommonUtils.getSystemDate());
        label.setDescription(organizationPathToHint);
        return label;
    }

    @Override
    public void initDatasource() {
        assignmentDs = getDsContext().get("assignmentDs");
        assignmentHistoryDs = (CollectionDatasource<AssignmentExt, UUID>) getDsContext().get("assignmentHistoryDs");
    }

    public Component generateDate(AssignmentExt assignmentExt) {

        Date endDate = getEndDate(assignmentExt.getEndDate());

        groupsComponent.setInDateGroups(assignmentExt, endDate);
        Optional.ofNullable(assignmentExt.getPositionGroup()).map(PositionGroupExt::getPosition)
                .map(PositionExt::getJobGroup).ifPresent(jobGroup -> jobGroup.getJobInDate(endDate));

        Label startDateLbl = componentsFactory.createComponent(Label.class);
        startDateLbl.setValue(assignmentExt.getStartDate());
        return startDateLbl;
    }
}