package kz.uco.tsadv.service;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.global.ViewRepository;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

@Service(PositionService.NAME)
public class PositionServiceBean implements PositionService {

    @Inject
    protected DataManager dataManager;
    @Inject
    private CommonService commonService;
    @Inject
    protected Persistence persistence;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected ViewRepository viewRepository;

    @Override
    public PositionExt getPosition(@Nullable View view) {
        return this.getPosition(userSessionSource.getUserSession().getUser().getId(), view);
    }

    @Override
    public PositionExt getPosition(UUID userId, @Nullable View view) {
        if (view == null) view = viewRepository.getView(PositionExt.class, View.MINIMAL);

        return dataManager.load(PositionExt.class)
                .query("select l from tsadv$UserExt e " +
                        "   join e.personGroup p " +
                        "   join p.assignments a " +
                        "   join a.positionGroup.list l " +
                        " where e.id = :userId" +
                        "   and a.primaryFlag = 'TRUE' " +
                        "   and a.assignmentStatus.code in ('ACTIVE', 'SUSPENDED') " +
                        "   and current_date between a.startDate and a.endDate " +
                        "   and current_date between l.startDate and l.endDate ")
                .parameter("userId", userId)
                .view(view)
                .one();
    }

    @Override
    public List<Job> getExistingJobsInactiveInNearFuture(PositionExt position) {
        List<Job> jobs = new ArrayList<>();
        if (position == null || position.getJobGroup() == null) {
            return jobs;
        }
        String queryString = "select e from tsadv$Job e " +
                "where e.endDate >= :positionStartDate and e.endDate < :positionEndDate and " +
                "e.group.id = :jobGroupId " +
                "order by e.endDate DESC";
        Map<String, Object> params = new HashMap<>();
        params.put("positionStartDate", position.getStartDate());
        params.put("positionEndDate", position.getEndDate());
        params.put("jobGroupId", position.getJobGroup().getId());
        jobs = commonService.getEntities(Job.class, queryString, params, View.LOCAL);
        return jobs;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public PositionGroupExt getManager(UUID positionGroupId) {
        return persistence.callInTransaction(em ->
                em.createNativeQuery("select p.* from base_hierarchy_element child " +
                        "   join base_hierarchy_element parent " +
                        "       on parent.GROUP_ID = child.PARENT_GROUP_ID " +
                        "       and #systemDate between parent.start_date and parent.end_date " +
                        "       and parent.delete_ts is null " +
                        "   join base_position_group p " +
                        "       on p.id = parent.position_group_id " +
                        "       and p.delete_ts is null " +
                        "   where child.position_group_id = #positionGroupId " +
                        "       and #systemDate between child.start_date and child.end_date " +
                        "       and child.delete_ts is null ", PositionGroupExt.class)
                        .setParameter("positionGroupId", positionGroupId)
                        .setParameter("systemDate", CommonUtils.getSystemDate())
                        .getFirstResult());
    }
}