package kz.uco.tsadv.web.organalytics;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.service.HierarchyService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrgAnalyticsEdit extends AbstractEditor<OrgAnalytics> {

    protected List<Entity> commitInstances = new ArrayList<>();
    @Inject
    private DataManager dataManager;
    @Inject
    private HierarchyService hierarchyService;
    @Inject
    private CommonService commonService;
    @Inject
    private Metadata metadata;

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        setAndSaveWorkTomeForChildrenOrganizations();
        setPositionGroupLinkToMe();
        commitRelatedEntities();
        return super.postCommit(committed, close);
    }

    protected void setPositionGroupLinkToMe() {
        if (getItem().getPositionGroup() != null && getItem().getPositionGroup().getAnalytics() == null) {
            getItem().getPositionGroup().setAnalytics(getItem());
            commitInstances.add(getItem().getPositionGroup());
        }
    }

    protected void commitRelatedEntities() {
        if (!commitInstances.isEmpty()) {
            dataManager.commit(new CommitContext(commitInstances));
        }
        getDsContext().commit();
    }


    protected void setAndSaveWorkTomeForChildrenOrganizations() {
        if (getItem().getOrganizationGroup() != null) {
            setWorkTimeForChildrenOrganizations(getItem().getOrganizationGroup());
        }
    }

    protected void setWorkTimeForChildrenOrganizations(OrganizationGroupExt organizationGroup) {
        List<UUID> pathChild = hierarchyService.getOrganizationGroupIdChild(organizationGroup.getId());

        List<OrganizationGroupExt> childOrganizationGroupExtList = commonService.getEntities(OrganizationGroupExt.class,
                "SELECT e.group FROM base$OrganizationExt e " +
                        " where e.group.id IN :pathChild" +
                        "   and :systemDate between e.startDate and e.endDate ",
                ParamsMap.of("pathChild", pathChild, "systemDate", CommonUtils.getSystemDate()), "organization.analytic.update");
        childOrganizationGroupExtList.add(getItem().getOrganizationGroup());

        for (OrganizationGroupExt childOrganizationGroupExt : childOrganizationGroupExtList) {
            if (childOrganizationGroupExt != null && childOrganizationGroupExt.getAnalytics() != null
                    && getItem() != null) {
                setValuesForExistingAnalytics(childOrganizationGroupExt, getItem());
            } else if (getItem() != null) {
                createNewAnalitycsForChildOrganization(childOrganizationGroupExt, getItem());
            }
        }
    }

    protected void setValuesForExistingAnalytics(OrganizationGroupExt organizationGroup, OrgAnalytics currentAnalytics) {
        OrgAnalytics childOrganizationAnalytics = organizationGroup.getAnalytics();
        childOrganizationAnalytics.setCalendar(currentAnalytics.getCalendar());
        childOrganizationAnalytics.setOffset(currentAnalytics.getOffset());
        childOrganizationAnalytics.setWorkingCondition(currentAnalytics.getWorkingCondition());
        saveAnalytics(null, childOrganizationAnalytics);
    }

    protected void saveAnalytics(OrganizationGroupExt organizationGroup, OrgAnalytics childOrganizationAnalytics) {
        if (commitInstances.size() > 200) {
            dataManager.commit(new CommitContext(commitInstances));
            commitInstances.clear();
        } else {
            if (organizationGroup != null) {
                if (!commitInstances.contains(organizationGroup)) {
                    commitInstances.add(organizationGroup);
                }
            }
            if (!commitInstances.contains(childOrganizationAnalytics)) {
                commitInstances.add(childOrganizationAnalytics);
            }
        }
    }

    protected void createNewAnalitycsForChildOrganization(OrganizationGroupExt organizationGroup, OrgAnalytics currentAnalytics) {
        OrgAnalytics analitycsNew = metadata.create(OrgAnalytics.class);
        analitycsNew.setCalendar(currentAnalytics.getCalendar());
        analitycsNew.setOffset(currentAnalytics.getOffset());
        analitycsNew.setWorkingCondition(currentAnalytics.getWorkingCondition());
        analitycsNew.setOrganizationGroup(organizationGroup);
        organizationGroup.setAnalytics(analitycsNew);
        saveAnalytics(organizationGroup, analitycsNew);
    }
}