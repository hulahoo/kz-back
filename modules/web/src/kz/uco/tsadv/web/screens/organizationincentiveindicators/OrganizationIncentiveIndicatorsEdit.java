package kz.uco.tsadv.web.screens.organizationincentiveindicators;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveFlag;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveIndicators;
import kz.uco.tsadv.service.HierarchyService;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@UiController("tsadv_OrganizationIncentiveIndicators.edit")
@UiDescriptor("organization-incentive-indicators-edit.xml")
@EditedEntityContainer("organizationIncentiveIndicatorsDc")
@LoadDataBeforeShow
public class OrganizationIncentiveIndicatorsEdit extends StandardEditor<OrganizationIncentiveIndicators> {

//    @Inject
//    protected Notifications notifications;
//    @Inject
//    protected HierarchyService hierarchyService;
//    @Inject
//    protected MetadataTools metadataTools;
//    @Inject
//    protected Metadata metadata;
//    @Inject
//    protected DataManager dataManager;
//
//    protected boolean isOrgSecondLvl = false;
//
//    public boolean isOrgSecondLvl() {
//        return isOrgSecondLvl;
//    }
//
//    public void setOrgSecondLvl(boolean orgSecondLvl) {
//        isOrgSecondLvl = orgSecondLvl;
//    }

    /*@SuppressWarnings("ResultOfMethodCallIgnored")
    @Subscribe
    protected void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        OrganizationIncentiveIndicators indicator = getEditedEntity();

        indicator.setDateTo(DateUtils.setDays(indicator.getDateTo(), 1));
        if (indicator.getIndicator().getGeneral() && isOrgSecondLvl) {
            List<UUID> pathChild = hierarchyService.getOrganizationGroupIdChild(indicator.getOrganizationGroup().getId());

            List<OrganizationIncentiveFlag> flags = dataManager.load(OrganizationIncentiveFlag.class)
                    .query("select e from tsadv_OrganizationIncentiveFlag e " +
                            "   where e.organizationGroup in :organizationGroupIds" +
                            "       and e.isIncentive = 'TRUE'" +
                            "       and e.dateFrom <= :dateTo" +
                            "       and e.dateTo >= :dateFrom")
                    .parameter("organizationGroupIds", pathChild)
                    .parameter("dateTo", DateUtils.addMonths(DateUtils.addDays(DateUtils.setDays(indicator.getDateTo(), 1), -1), 1))
                    .parameter("dateFrom", DateUtils.setDays(indicator.getDateFrom(), 1))
                    .viewProperties("organizationGroup")
                    .list();

            flags.stream()
                    .map(OrganizationIncentiveFlag::getOrganizationGroup)
                    .map(this::copyIndicator)
                    .forEach(event.getDataContext()::merge);

        }
    }

    protected OrganizationIncentiveIndicators copyIndicator(OrganizationGroupExt organizationGroupExt) {
        OrganizationIncentiveIndicators organizationIncentiveIndicators = metadataTools.copy(getEditedEntity());
        organizationIncentiveIndicators.setId(UUID.randomUUID());
        organizationIncentiveIndicators.setOrganizationGroup(organizationGroupExt);
        return organizationIncentiveIndicators;
    }*/

}