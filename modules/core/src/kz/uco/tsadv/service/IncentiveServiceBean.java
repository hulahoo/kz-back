package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.common.MultiLanguageUtils;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.pojo.IncentivePojo;
import kz.uco.tsadv.pojo.pagination.EntitiesPaginationResult;
import kz.uco.tsadv.pojo.pagination.PaginationPojo;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.StatusEnum;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(IncentiveService.NAME)
public class IncentiveServiceBean implements IncentiveService {

    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Persistence persistence;
    @Inject
    protected PositionService positionService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;

    protected final static Gson gson = new Gson();

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Override
    public EntitiesPaginationResult getIncentiveList(PaginationPojo paginationPojo) {
        PositionExt position = positionService.getPosition(new View(PositionExt.class).addProperty("group", new View(PositionGroupExt.class)));

        if (position == null || position.getGroup() == null)
            throw new PortalException("Позиция не найдено!");

        UUID positionGroupId = position.getGroup().getId();

        final String query = "with result as (\n" +
                "    select sum(score.total_score) as result,\n" +
                "           r.organization_group_id,\n" +
                "           to_char(r.period_date, 'yyyy-MM')                   as date_\n" +
                "    from tsadv_organization_incentive_result r\n" +
                "             left join TSADV_DIC_INCENTIVE_INDICATOR_SCORE_SETTING score\n" +
                "                       on score.indicator_id = r.indicator_id\n" +
                "                           and score.delete_ts is null\n" +
                "                           and r.result_ between score.min_percent and score.max_percent" +
                "    where r.delete_ts is null\n" +
                "    group by r.organization_group_id, to_char(r.period_date, 'yyyy-MM')\n" +
                "),\n" +
                "     INCENTIVE as (\n" +
                "         select d as date_,\n" +
                "                i.organization_group_id\n" +
                "         from TSADV_ORGANIZATION_INCENTIVE_INDICATORS i\n" +
                "                  join TSADV_ORGANIZATION_INCENTIVE_FLAG of\n" +
                "                       on of.organization_group_id = i.organization_group_id\n" +
                "                           and of.delete_ts is null\n" +
                "                           and of.date_to >= i.date_from\n" +
                "                           and of.date_from <= i.date_to\n" +
                "                  join generate_series(greatest(i.date_from, of.date_from), least(i.date_to, of.date_to), '1 month'::interval) d on 1 = 1 \n" +
                "         where i.delete_ts is null\n" +
                "           and i.RESPONSIBLE_POSITION_ID = #personGroupId\n" +
                "         group by d, i.organization_group_id\n" +
                "     )\n" +
                "select %s\n" +
                "from INCENTIVE i\n" +
                "         join base_organization o\n" +
                "              on o.group_id = i.organization_group_id\n" +
                "                  and o.delete_ts is null\n" +
                "                  and current_date between o.start_date and o.end_date\n" +
                "         left join result r\n" +
                "                   on r.organization_group_id = i.organization_group_id\n" +
                "                       and r.date_ = to_char(i.date_, 'yyyy-MM')\n" +
                "%s\n";

        final String fields = "i.organization_group_id, " +
                "i.date_ as date, " +
                "o.organization_name_lang1, " +
                "o.organization_name_lang2, " +
                "o.organization_name_lang3, " +
                "r.result";
        final String count = "count(*)";
        final String orderBy = "order by i.date_ desc, i.organization_group_id";

        return persistence.callInTransaction(em -> {
            EntitiesPaginationResult paginationResult = new EntitiesPaginationResult();

            paginationResult.setCount(Long.valueOf(em.createNativeQuery(String.format(query, count, ""))
                    .setParameter("personGroupId", positionGroupId)
                    .getFirstResult().toString()));

            List<IncentivePojo> pojoList = (List<IncentivePojo>) em.createNativeQuery(String.format(query, fields, orderBy))
                    .setParameter("personGroupId", positionGroupId)
                    .setFirstResult(paginationPojo.getOffset())
                    .setMaxResults(paginationPojo.getLimit())
                    .getResultList()
                    .stream()
                    .map(this::parseToIncentivePojo)
                    .collect(Collectors.toList());

            paginationResult.setEntities(gson.toJson(pojoList));

            return paginationResult;
        });
    }

    @Override
    public void saveMonthResult(String status, String comment, UUID incentiveMonthResultId) {
        OrganizationIncentiveMonthResult incentiveMonth = dataManager.load(OrganizationIncentiveMonthResult.class)
                .id(incentiveMonthResultId)
                .viewProperties("status", "comment")
                .one();

        incentiveMonth.setStatus(commonService.getEntity(DicIncentiveResultStatus.class, status));
        incentiveMonth.setComment(comment);

        List<Activity> activities = dataManager.load(Activity.class)
                .query("select e from uactivity$Activity e " +
                        "   where e.referenceId = :referenceId " +
                        "       and e.status = :status")
                .parameter("referenceId", incentiveMonthResultId)
                .parameter("status", StatusEnum.active)
                .list();

        CommitContext context = new CommitContext();
        context.addInstanceToCommit(incentiveMonth);
        activities.stream().peek(activity -> activity.setStatus(StatusEnum.done)).forEach(context::addInstanceToCommit);

        dataManager.commit(context);
    }

    private IncentivePojo parseToIncentivePojo(Object o) {
        Object[] row = (Object[]) o;
        int i = 0;
        return new IncentivePojo((UUID) row[i++], (Date) row[i++], MultiLanguageUtils.getCurrentLanguageValue((String) row[i++], (String) row[i++], (String) row[i++]), (Double) row[i]);
    }
}