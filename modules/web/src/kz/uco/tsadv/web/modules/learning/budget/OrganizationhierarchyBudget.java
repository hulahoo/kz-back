package kz.uco.tsadv.web.modules.learning.budget;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.widgets.CubaHorizontalSplitPanel;
import com.haulmont.cuba.web.widgets.client.split.SplitPanelDockMode;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbstractSplitPanel;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.AssignedEvent;
import kz.uco.tsadv.entity.tb.Buildings;
import kz.uco.tsadv.entity.tb.SafetyPlanEvent;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;

import javax.inject.Inject;
import java.util.*;

public class OrganizationhierarchyBudget extends AbstractWindow {
    @Inject
    private HierarchicalDatasource<HierarchyElementExt, UUID> hierarchyElementsDs;

    List<OrganizationGroupExt> organizationGroupList = new ArrayList<>();
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private CommonService commonService;
    @Inject
    private HBoxLayout hboxId;
    @Inject
    private SplitPanel splitter;
    @Inject
    private Messages messages;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Frame frame = null;
        if (params.containsKey("frameName")) {
            String frameName = (String) params.get("frameName");
            frame = openFrame(null, frameName);
            frame.setHeight("100%");
            frame.setWidth("100%");
            if ("tsadv$AssignedEvent.browse".equals(params.get("frameName"))) {
                setCaption(messages.getMessage("kz.uco.tsadv.web.assignedevent", "browseCaption"));
            }
        }
        fillBuildingsDs(organizationGroupList, null);
        hierarchyElementsDs.addItemChangeListener(e -> {
            fillBuildingsDs(organizationGroupList, e.getItem());
        });
        hboxId.add(frame);
    }

    private void fillBuildingsDs(List<OrganizationGroupExt> organizationGroupList, HierarchyElementExt hierarchyElement) {
        organizationGroupList.clear();
        for (DsContext dsContext : getDsContext().getChildren()) {
            for (Datasource datasource : dsContext.getAll()) {
                Map<String, Object> map = new HashMap<>();
                if (hierarchyElement != null) {
                    fillOrgList(organizationGroupList, hierarchyElement);
                }
                map.put("orgList", organizationGroupList);
                if ("buildingsesDs".equals(datasource.getId())) {
                    ((GroupDatasource<Buildings, UUID>) datasource).setQuery("select e from tsadv$Buildings e " +
                            " where e.organization in :custom$orgList");
                    ((GroupDatasource<Buildings, UUID>) datasource).refresh(map);
                } else if ("assignedEventsDs".equals(datasource.getId())) {
                    List<SafetyPlanEvent> safetyPlanEventList = commonService.getEntities(SafetyPlanEvent.class,
                            "select e from tsadv$SafetyPlanEvent e where e.organization in :orgList",
                            map, null);
                    map.clear();
                    map.put("speList", safetyPlanEventList);
                    ((GroupDatasource<AssignedEvent, UUID>) datasource).setQuery("select e from tsadv$AssignedEvent e where e.safetyPlanEvent in :custom$speList");
                    ((GroupDatasource<AssignedEvent, UUID>) datasource).refresh(map);
                } else if ("budgetRequestsDs".equals(datasource.getId())) {
                    List<BudgetRequest> budgetRequestList = commonService.getEntities(BudgetRequest.class,
                            "select e from tsadv$BudgetRequest e where e.organizationGroup in :orgList",
                            map, null);
                    map.clear();
                    map.put("speList", budgetRequestList);
                    ((GroupDatasource<BudgetRequest, UUID>) datasource).setQuery("select e from tsadv$BudgetRequest e where e.id in :custom$speList");
                    ((GroupDatasource<BudgetRequest, UUID>) datasource).refresh(map);
                }
            }
        }
    }

    private void fillOrgList(List<OrganizationGroupExt> organizationGroupList, HierarchyElementExt item) {
        organizationGroupList.add(item.getOrganizationGroup());
        if (item.getParent() != null) {
            fillOrgList(organizationGroupList, item.getParent());
        }
    }

    @Override
    public void ready() {
        super.ready();
        SplitPanel splitPanel = splitter;
        CubaHorizontalSplitPanel cubaHorizontalSplitPanel = splitPanel.unwrap(CubaHorizontalSplitPanel.class);
        cubaHorizontalSplitPanel.setDockMode(SplitPanelDockMode.LEFT);
        cubaHorizontalSplitPanel.setDockable(true);
        cubaHorizontalSplitPanel.setDefaultPosition("25%");
        cubaHorizontalSplitPanel.addSplitterClickListener(new AbstractSplitPanel.SplitterClickListener() {
            @Override
            public void splitterClick(AbstractSplitPanel.SplitterClickEvent event) {
                if (cubaHorizontalSplitPanel.getSplitPosition() > 100) {
                    cubaHorizontalSplitPanel.setSplitPosition(0);
//                            splitPanel.setMaxSplitPosition(0,0);
                } else {
                    cubaHorizontalSplitPanel.setSplitPosition(25, Sizeable.Unit.PERCENTAGE);
                    cubaHorizontalSplitPanel.setMaxSplitPosition(25, Sizeable.Unit.PERCENTAGE);
//                            splitPanel.setMaxSplitPosition(350,0);
                }

            }
        });
    }
}