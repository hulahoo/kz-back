package kz.uco.tsadv.service;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import kz.uco.tsadv.entity.tb.BpmUserSubstitution;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.TsadvUser;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service(BpmUserSubstitutionService.NAME)
public class BpmUserSubstitutionServiceBean implements BpmUserSubstitutionService {

    @Inject
    private Persistence persistence;

    @Override
    public String getBpmUserSubstitution(TsadvUser userExt, Date date, boolean path) {
        Object res = persistence.callInTransaction(em ->
                em.createNativeQuery("with recursive SubstitutedUser as ( " +
                        "  select e.user_id                   as current_user_id, " +
                        "         e.substituted_user_id, " +
                        "         e.substituted_user_id::text as path_substituted_user_id, " +
                        "         1                           as lvl " +
                        "  from tsadv_bpm_user_substitution e " +
                        "  where e.delete_ts is null " +
                        "    and ?2 between e.start_date and e.end_date " +
                        "    and e.user_id = ?1 " +
                        "  UNION ALL " +
                        "  select SubstitutedUser.current_user_id, " +
                        "         e.substituted_user_id, " +
                        "         SubstitutedUser.path_substituted_user_id || '*' || e.substituted_user_id::text, " +
                        "         SubstitutedUser.lvl + 1 as lvl " +
                        "  from SubstitutedUser " +
                        "         join tsadv_bpm_user_substitution e " +
                        "              on SubstitutedUser.substituted_user_id = e.user_id " +
                        "                and SubstitutedUser.path_substituted_user_id not like '%' || e.substituted_user_id || '%' " +
                        "                and e.delete_ts is null " +
                        "                and ?2 between e.start_date and e.end_date) " +
                        "select " + (path ? "current_user_id::text || '*' || path_substituted_user_id" : "substituted_user_id") + " " +
                        "from SubstitutedUser " +
                        "where lvl = (select max(lvl) from SubstitutedUser)")
                        .setParameter(1, userExt.getId())
                        .setParameter(2, date)
                        .getFirstResult());
        return res != null ? res.toString() : null;
    }

    @Override
    public String getCurrentBpmUserSubstitution(TsadvUser userExt, boolean path) {
        return getBpmUserSubstitution(userExt, CommonUtils.getSystemDate(), path);
    }

    @Override
    public BpmUserSubstitution reloadBpmUserSubstitution(String id) {
        return persistence.callInTransaction(em -> em.find(BpmUserSubstitution.class, UUID.fromString(id), "bpmUserSubstitution-view"));
    }

    @Override
    public List<BpmUserSubstitution> getBpmUserSubstitutionList(TsadvUser userExt) {
        return persistence.callInTransaction(em ->
                em.createQuery("select e from tsadv$BpmUserSubstitution e where e.user.id = :userId",
                        BpmUserSubstitution.class)
                        .setParameter("userId", userExt.getId())
                        .setViewName("bpmUserSubstitution-view")
                        .getResultList());
    }

    @Override
    public boolean hasBpmUserSubstitution(UUID entityId, TsadvUser user, Date startDate, Date endDate) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(" select count(e) from tsadv$BpmUserSubstitution e " +
                    " where e.user.id = :userId " +
                    (entityId != null ? " and e.id <> :id " : "") +
                    " and NOT ( e.startDate > :endDate or e.endDate < :startDate )", Long.class)
                    .setParameter("userId", user.getId())
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate);
            if (entityId != null) {
                query.setParameter("id", entityId);
            }
            return ((Long) query.getSingleResult()) > 0;
        });
    }

    @Override
    public boolean isCycle(BpmUserSubstitution bpmUserSubstitution) {
        List res = persistence.callInTransaction(em -> em.createNativeQuery("with recursive SubstitutedUser as ( " +
                "  select ?1::uuid as current_user_id, " +
                "         ?2::uuid as substituted_user_id, " +
                "         ?3::date as start_date, " +
                "         ?4::date as end_date, " +
                "         ?5::text as path_substituted_user_id, " +
                "         1        as lvl " +
                "  UNION ALL " +
                "  select SubstitutedUser.current_user_id, " +
                "         e.substituted_user_id, " +
                "         greatest(case when e.id <> ?6 then e.start_date else ?3 end, SubstitutedUser.start_date) as start_date, " +
                "         least(case when e.id <> ?6 then e.end_date else ?4 end, SubstitutedUser.end_date)        as end_date, " +
                "         SubstitutedUser.path_substituted_user_id || '*' || e.substituted_user_id::text, " +
                "         SubstitutedUser.lvl + 1                                                                  as lvl " +
                "  from SubstitutedUser " +
                "         join tsadv_bpm_user_substitution e " +
                "              on SubstitutedUser.substituted_user_id = e.user_id " +
                "                and e.delete_ts is null " +
                "                and not (e.end_date < SubstitutedUser.start_date or SubstitutedUser.end_date < e.start_date) " +
                "                and e.id <> ?6 " +
                "                and SubstitutedUser.path_substituted_user_id not like '%' || e.user_id || '%' || e.user_id || '%') " +
                "select path_substituted_user_id " +
                "from SubstitutedUser " +
                "order by lvl desc")
                .setParameter(1, bpmUserSubstitution.getUser().getId())
                .setParameter(2, bpmUserSubstitution.getSubstitutedUser().getId())
                .setParameter(3, bpmUserSubstitution.getStartDate())
                .setParameter(4, bpmUserSubstitution.getEndDate())
                .setParameter(5, bpmUserSubstitution.getUser().getId().toString())
                .setParameter(6, bpmUserSubstitution.getId())
                .getResultList());
        for (Object o : res) {
            String[] arr = ((String) o).split("\\*");
            HashSet<String> stringSet = new HashSet<>();
            for (String s : arr) {
                if (stringSet.contains(s)) {
                    return true;
                }
                stringSet.add(s);
            }
        }
        return false;
    }


}