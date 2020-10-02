package kz.uco.tsadv.web.organizationssview;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.dbview.OrganizationSsView;
import kz.uco.tsadv.global.common.CommonUtils;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class OrganizationSsViewBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<OrganizationSsView, UUID> organizationSsViewsDs;

    @Override
    public void init(Map<String, Object> params) {
        params.computeIfAbsent("date", s -> CommonUtils.getSystemDate());

        if (params.get("posOrgJob") != null && (boolean) params.get("posOrgJob")) {
            organizationSsViewsDs.setQuery("select e " +
                    "                           from tsadv$OrganizationSsView e " +
                    "                           join e.organizationGroup org " +
                    "                           join e.organizationGroup.list o " +
                    "                               on :param$date between o.startDate and o.endDate " +
                    (params.containsKey("orgGroupId") ? " and org.id = COALESCE(:param$orgGroupId, org.id) " : "") +
                    (params.containsKey("jobGroupId") ? (" and ( org.id = COALESCE(:param$jobGroupId, org.id) or " +
                            "                                       org.id in ( select p.organizationGroupExt.id from base$PositionExt p " +
                            "                                                    where p.jobGroup.id = COALESCE(:param$jobGroupId, p.jobGroup.id) " +
                            "                                                        and :param$date between p.startDate and p.endDate ) )") :
                            ""));
        }

        super.init(params);
    }
}