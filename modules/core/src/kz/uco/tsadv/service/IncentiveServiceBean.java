package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.base.common.MultiLanguageUtils;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.pojo.IncentivePojo;
import kz.uco.tsadv.pojo.pagination.EntitiesPaginationResult;
import kz.uco.tsadv.pojo.pagination.PaginationPojo;
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

    protected final static Gson gson = new Gson();

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Override
    public EntitiesPaginationResult getIncentiveList(PaginationPojo paginationPojo) {
        UUID personGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);

        final String query = "with result as (\n" +
                "    select sum(coalesce(r.result_, 0) * coalesce(r.weight, 0) / 100) as result,\n" +
                "           r.organization_group_id,\n" +
                "           to_char(r.period_date, 'yyyy-MM')                   as date_\n" +
                "    from tsadv_organization_incentive_result r\n" +
                "    where r.delete_ts is null\n" +
                "    group by r.organization_group_id, to_char(r.period_date, 'yyyy-MM')\n" +
                "),\n" +
                "     INCENTIVE as (\n" +
                "         select greatest(i.date_from, of.date_from) as date_from,\n" +
                "                least(i.date_to, of.date_to)        as date_to,\n" +
                "                i.organization_group_id\n" +
                "         from TSADV_ORGANIZATION_INCENTIVE_INDICATORS i\n" +
                "                  join TSADV_ORGANIZATION_INCENTIVE_FLAG of\n" +
                "                       on of.organization_group_id = i.organization_group_id\n" +
                "                           and of.delete_ts is null\n" +
                "                           and of.date_to >= i.date_from\n" +
                "                           and of.date_from <= i.date_to\n" +
                "         where i.delete_ts is null\n" +
                "           and i.responsible_person_id = #personGroupId\n" +
                "     )\n" +
                "select %s\n" +
                "from INCENTIVE i\n" +
                "         join generate_series(i.date_from, i.date_to, '1 month'::interval) d on 1 = 1\n" +
                "         join base_organization o\n" +
                "              on o.group_id = i.organization_group_id\n" +
                "                  and o.delete_ts is null\n" +
                "                  and current_date between o.start_date and o.end_date\n" +
                "         left join result r\n" +
                "                   on r.organization_group_id = i.organization_group_id\n" +
                "                       and r.date_ = to_char(d, 'yyyy-MM')\n" +
                "%s\n";

        final String fields = "i.organization_group_id, " +
                "d as date, " +
                "o.organization_name_lang1, " +
                "o.organization_name_lang2, " +
                "o.organization_name_lang3, " +
                "r.result";
        final String count = "count(*)";
        final String orderBy = "order by d desc, i.organization_group_id";

        return persistence.callInTransaction(em -> {
            EntitiesPaginationResult paginationResult = new EntitiesPaginationResult();

            paginationResult.setCount(Long.valueOf(em.createNativeQuery(String.format(query, count, ""))
                    .setParameter("personGroupId", personGroupId)
                    .getFirstResult().toString()));

            List<IncentivePojo> pojoList = (List<IncentivePojo>) em.createNativeQuery(String.format(query, fields, orderBy))
                    .setParameter("personGroupId", personGroupId)
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

    private IncentivePojo parseToIncentivePojo(Object o) {
        Object[] row = (Object[]) o;
        int i = 0;
        return new IncentivePojo((UUID) row[i++], (Date) row[i++], MultiLanguageUtils.getCurrentLanguageValue((String) row[i++], (String) row[i++], (String) row[i++]), (Double) row[i]);
    }
}