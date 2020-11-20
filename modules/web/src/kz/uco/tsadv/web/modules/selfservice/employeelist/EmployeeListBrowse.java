package kz.uco.tsadv.web.modules.selfservice.employeelist;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EmployeeListBrowse extends AbstractLookup {
    private static final String IMAGE_CELL_HEIGHT = "100px";

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected GroupDatasource<AssignmentGroupExt, UUID> assignmentGroupsDs;

    @Inject
    protected CollectionDatasource<AssignmentExt, UUID> listDs;

    @Inject
    protected Metadata metadata;

    @Inject
    protected GroupTable<AssignmentExt> assignmentGroupsTable;

    @Inject
    protected DataManager dataManager;

    protected Map<String, CustomFilter.Element> filterMap;
    protected CustomFilter customFilter;

    @Inject
    protected VBoxLayout filterBox;

    @Inject
    protected CollectionDatasource<GradeGroup, UUID> gradeGroupsDs;
    @Inject
    protected CollectionDatasource<JobGroup, UUID> jobGroupsDs;
    @Inject
    protected CollectionDatasource<DicLocation, UUID> locationsDs;
    @Inject
    protected CollectionDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;
    @Inject
    protected CollectionDatasource<PositionGroupExt, UUID> positionGroupsDs;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected Button transfer;
    @Inject
    protected Button dismissal;
    @Inject
    protected Button history;
    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("restrictToLeader")) {
            UserExt leader = (UserExt) params.get("restrictToLeader");

            PersonGroupExt currentPersonGroup = employeeService.getPersonGroupByUserId(leader.getId());
            if (currentPersonGroup != null) {
                assignmentGroupsDs.setQuery("select e\n" +
                        "                           from base$AssignmentGroupExt e\n" +
                        "                           join base$AssignmentExt a on a.group.id = e.id\n" +
                        "                           join base$PersonExt p on p.group.id =  a.personGroup.id\n" +
                        "                           join tsadv$MyTeam m on m.personGroup.id = a.personGroup.id\n" +
                        "                          where :session$systemDate between a.startDate and a.endDate\n" +
                        "                            and :session$systemDate between p.startDate and p.endDate " +
                        "and m.personPath like :custom$like and m.personGroup.id <> :custom$personGroup");
                Map<String, Object> map = new HashMap<>();
                String like = "%" + currentPersonGroup.getId().toString() + "%";
                map.put("like", like);
                map.put("personGroup", currentPersonGroup.getId());
                assignmentGroupsDs.refresh(map);
            }
        }
    }

}