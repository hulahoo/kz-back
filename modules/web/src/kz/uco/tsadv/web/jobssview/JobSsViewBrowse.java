package kz.uco.tsadv.web.jobssview;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.dbview.JobSsView;
import kz.uco.tsadv.global.common.CommonUtils;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class JobSsViewBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<JobSsView, UUID> jobSsViewsDs;

    @Override
    public void init(Map<String, Object> params) {
        params.putIfAbsent("date", CommonUtils.getSystemDate());

        if (params.get("posOrgJob") != null && (boolean) params.get("posOrgJob")) {
            jobSsViewsDs.setQuery("select j " +
                    "                           from tsadv$JobSsView j " +
                    "                          where :param$date between j.startDate and j.endDate " +
                    "                                and j.jobGroup.id = COALESCE(:param$jobGroupId, j.jobGroup.id ) " +
                    "                                and ( j.jobGroup.id = COALESCE(:param$orgGroupId, j.jobGroup.id) or" +
                    "                                       j.jobGroup.id in ( select p.jobGroup.id from base$PositionExt p " +
                    "                                                    where p.organizationGroupExt.id = COALESCE(:param$orgGroupId, p.organizationGroupExt.id) " +
                    "                                                           and :param$date between p.startDate and p.endDate )) " +
                    "                           order by j.id");
        }

        super.init(params);
    }
}