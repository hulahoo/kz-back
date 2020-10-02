package kz.uco.tsadv.web.modules.selfservice.newbeautytree;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.Layout;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.HierarchyService;
import kz.uco.tsadv.web.toolkit.ui.orgchartnew.OrgChartNew;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class BeautyTreeNew extends AbstractWindow {
    protected String personGroupId;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected ScrollBoxLayout scrollBox;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected UserSession userSession;

    @Inject
    protected HierarchyService hierarchyService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (userSession.getAttribute(StaticVariable.POSITION_GROUP_ID) == null ||
                hierarchyService.isParent(userSession.getAttribute(StaticVariable.POSITION_GROUP_ID), null)) {
            throw new ItemNotFoundException(messages.getMainMessage("noSubordinateEmployees"));
        }

        Object personGroupIdObject = params.get("personGroupId");

        if (personGroupIdObject == null) {
            personGroupIdObject = userSession.getAttribute("userPersonGroupId");
        }

        if (personGroupIdObject != null) {
            this.personGroupId = String.valueOf(personGroupIdObject);
        }

        Component box = componentsFactory.createComponent(VBoxLayout.class);
        Layout vBox = (Layout) WebComponentsHelper.unwrap(box);
        vBox.addComponent(getOrgChartComponent());
        scrollBox.add(box);
    }

    protected OrgChartNew getOrgChartComponent() {
        OrgChartNew orgChartComponent = new OrgChartNew();
        orgChartComponent.setPersonGroupId(personGroupId);
        orgChartComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken()); //+ for rest api security
        orgChartComponent.setUrl("./rest/v2/services/tsadv_EmployeeService/generateOgrChart");
        orgChartComponent.addValueChangeListener(this::redirect);
        return orgChartComponent;
    }

    public void redirect(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        PersonGroupExt personGroup = getPersonGroup(UUID.fromString(id));
        if (personGroup != null) {
            AbstractEditor abstractEditor = openEditor("base$TeamMember", personGroup.getPerson(), WindowManager.OpenType.THIS_TAB);
//            abstractEditor.addCloseListener(actionId -> {
//                //renderChart();
//            });
        }
    }

    protected PersonGroupExt getPersonGroup(UUID id) {
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$PersonGroupExt e " +
                        "where e.id = :personGroupId")
                .setParameter("personGroupId", id))
                .setView("personGroup.browse");
        return dataManager.load(loadContext);
    }


}
