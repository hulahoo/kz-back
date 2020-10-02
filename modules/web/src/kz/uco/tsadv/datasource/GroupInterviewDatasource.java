package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author veronika.buksha
 */
public class GroupInterviewDatasource extends CustomCollectionDatasource<Interview, UUID> {
    private static final Logger log = LoggerFactory.getLogger(GroupInterviewDatasource.class);

    @Override
    protected Collection<Interview> getEntities(Map<String, Object> params) {
        DataManager dataManager = AppBeans.get(DataManager.NAME);
        CommonService commonService = AppBeans.get(CommonService.NAME);

        Collection<Interview> interviews = getCompiledLoadContext().getQuery() == null ?
                new ArrayList<>() :
                dataManager.loadList(getCompiledLoadContext());

        for (Interview interview : interviews) {
            Map<Integer, Object> queryParams = new HashMap<>();
            queryParams.put(1, interview.getId());

            List<Object[]> groupInterview = commonService.emNativeQueryResultList(
                    "select sum(case when i.interview_status = 10 then 1 else 0 end)," +
                            "         sum(case when i.interview_status = 20 then 1 else 0 end)," +
                            "         sum(case when i.interview_status = 30 then 1 else 0 end)," +
                            "         sum(case when i.interview_status = 40 then 1 else 0 end)," +
                            "         sum(case when i.interview_status = 50 then 1 else 0 end)," +
                            "         sum(case when i.interview_status = 60 then 1 else 0 end)," +
                            "         sum(case when i.interview_status = 70 then 1 else 0 end)" +
                            "    from tsadv_interview i  " +
                            "   where i.group_interview_id  = ?1 ", queryParams);

            if (groupInterview != null && groupInterview.size() > 0) {
                Object[] row = groupInterview.get(0);
                interview.setGroupDraftCount(((Long) row[0]).intValue());
                interview.setGroupOnApprovalCount(((Long) row[1]).intValue());
                interview.setGroupPlannedCount(((Long) row[2]).intValue());
                interview.setGroupCompletedCount(((Long) row[3]).intValue());
                interview.setGroupCancelledCount(((Long) row[4]).intValue());
                interview.setGroupFailedCount(((Long) row[5]).intValue());
                interview.setGroupOnCancellationCount(((Long) row[6]).intValue());
            }
        }
        return interviews;
    }
}
