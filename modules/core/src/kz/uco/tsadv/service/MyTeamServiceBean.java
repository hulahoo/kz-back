package kz.uco.tsadv.service;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.global.common.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

@Service(MyTeamService.NAME)
public class MyTeamServiceBean implements MyTeamService {

    @Inject
    private Persistence persistence;
    @Inject
    private CommonService commonService;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Messages messages;

    /**
     * Search in hierarchy myTeam
     *
     * @return info next position and person
     */
    @Override
    public List<Object[]> searchMyTeam(UUID hierarchyId, @Nullable UUID parentPositionGroupId, String searchFio,
                                       @Nullable String lastFindPathNumber, @Nullable UUID lastFindPersonGroupId,
                                       boolean onlyFirstElement) {

        String filterNextPerson = "";
        if (lastFindPathNumber != null) {
            filterNextPerson = String.format("  and ( pr.lastFindPathNumber > '%s' ", lastFindPathNumber);
            if (lastFindPersonGroupId != null) {
                filterNextPerson += String.format(" or ( pr.lastFindPathNumber = '%s' and p.group_id > '%s' )", lastFindPathNumber, lastFindPersonGroupId);
            }
            filterNextPerson += ")";
        }

        String sqlString = "with recursive PosReg as (\n" +
                "  select bhep.id,\n" +
                "         bhep.position_group_id,\n" +
                "         null::uuid                                   as parent_id,\n" +
                "         0                                            as lvl,\n" +
                "         bhep.position_group_id::text                 as path,\n" +
                "         repeat('0', 4 - (row_number() over (order by bhep.position_group_id)::text).length) ||\n" +
                "         (row_number() over (order by bhep.position_group_id))::text as lastFindPathNumber\n" +
                "  from base_hierarchy_element bhep\n" +
                "         join base_position bpos\n" +
                "              on bhep.position_group_id = bpos.group_id and bpos.delete_ts is null\n" +
                "                and ?1 between bpos.start_date and bpos.end_date\n" +
                "         join tsadv_dic_position_status tdps\n" +
                "              on bpos.position_status_id = tdps.id\n" +
                "                and tdps.delete_ts is null and tdps.code <> 'CLOSED'" +
                "  where bhep.delete_ts is null\n" +
                "    and ?1 between bhep.start_date and bhep.end_date\n" +
                (parentPositionGroupId != null
                        ? String.format("    and bhep.position_group_id = '%s'\n", parentPositionGroupId)
                        : "    and bhep.parent_id is null\n") +
                "  union\n" +
                "  select bhec.id,\n" +
                "         bhec.position_group_id,\n" +
                "         pr.position_group_id                                                     as parent_id,\n" +
                "         pr.lvl + 1                                                               as lvl,\n" +
                "         pr.path || '*' || bhec.position_group_id::text                           as path,\n" +
                "         pr.lastFindPathNumber || '/' ||\n" +
                "         repeat('0', 4 - ((row_number() over (partition by bhec.parent_id order by bhec.position_group_id))::text).length) ||\n" +
                "         (row_number() over (partition by bhec.parent_id order by bhec.position_group_id))::text as lastFindPathNumber\n" +
                "  from base_hierarchy_element bhec\n" +
                "         join PosReg pr\n" +
                "              on pr.id = bhec.parent_id\n" +
                "         join base_position bpos\n" +
                "              on bhec.position_group_id = bpos.group_id and bpos.delete_ts is null\n" +
                "                and ?1 between bpos.start_date and bpos.end_date\n" +
                "         join tsadv_dic_position_status tdps\n" +
                "              on bpos.position_status_id = tdps.id\n" +
                "                and tdps.delete_ts is null and tdps.code <> 'CLOSED'" +
                "  where ?1 between bhec.start_date and bhec.end_date\n" +
                "    and bhec.hierarchy_id = ?2\n" +
                "    and bhec.delete_ts is null)\n" +
                " select pr.lastFindPathNumber,\n" +
                "       pr.path,\n" +
                "       pr.position_group_id,\n" +
                "       p.group_id\n" +
                " from PosReg pr\n" +
                "       left join base_assignment ss\n" +
                "                 on pr.position_group_id = ss.position_group_id\n" +
                "                   and ?1 between ss.start_date and ss.end_date\n" +
                "                   and ss.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid\n" +
                "                   and ss.primary_flag is true\n" +
                "                   and ss.delete_ts is null\n" +
                "       left join base_person p\n" +
                "                 on p.group_id = ss.person_group_id\n" +
                "                   and ?1 between p.start_date and p.end_date\n" +
                "                   and p.delete_ts isnull\n" +
                " where ( lower(concat(p.last_name, ' ', p.first_name, ' ', p.middle_name, ' (', p.employee_number, ')' )) like ?3 \n" +
                "  or lower(concat(p.last_name_latin, ' ', p.first_name_latin, ' ', p.middle_name_latin, ' (', p.employee_number, ')')) like ?3 \n" +
                "  or (p.id is null and ('vacant position' like ?3 or 'позиция вакантна' like ?3 )))\n" +
                (parentPositionGroupId != null ? String.format(" and pr.position_group_id <> '%s' ", parentPositionGroupId) : "") +
                filterNextPerson +
                " order by pr.lastFindPathNumber, p.group_id \n" +
                (onlyFirstElement ? " limit 1" : "") + ";";

        return persistence.callInTransaction(em -> {
            Query query = em.createNativeQuery(sqlString)
                    .setParameter(1, CommonUtils.getSystemDate())
                    .setParameter(2, hierarchyId)
                    .setParameter(3, "%" + searchFio.toLowerCase() + "%");
            return (List<Object[]>) query.getResultList();
        });
    }

    @Override
    public List<Object[]> getChildren(@Nullable UUID parentPositionGroupId, UUID positionStructureId) {
        String queryString = " with hierarchy_has_person as (\n" +
                "  select h.parent_id \n" +
                "  from base_hierarchy_element h\n" +
                "         join base_position p\n" +
                "              on h.position_group_id = p.group_id\n" +
                "                and p.delete_ts is null\n" +
                "                AND ?1 between p.start_date and p.end_date\n" +
                "         join tsadv_dic_position_status tdps\n" +
                "              on tdps.id = p.position_status_id\n" +
                "                and tdps.delete_ts is null\n" +
                "                and tdps.code <> 'CLOSED'\n" +
                "  where ?1 between h.start_date and h.end_date " +
                "         and h.delete_ts is null " +
                "  group by h.parent_id ) " +
                "SELECT bp.LAST_NAME, bp.FIRST_NAME, bp.MIDDLE_NAME, bp.LAST_NAME_LATIN, bp.FIRST_NAME_LATIN, bp.MIDDLE_NAME_LATIN,\n" +
                "       bhe.POSITION_GROUP_ID, bpos.POSITION_FULL_NAME_LANG1, bpos.POSITION_FULL_NAME_LANG2, \n" +
                "       bpos.POSITION_FULL_NAME_LANG3, bo.ORGANIZATION_NAME_LANG1, bo.ORGANIZATION_NAME_LANG2,\n" +
                "       bo.ORGANIZATION_NAME_LANG3, bp.person_group_id, bp.CODE, tg.GRADE_NAME," +
                "       bp.employee_number, hhp.parent_id is not null \n" +
                "FROM BASE_HIERARCHY_ELEMENT bhe\n" +
                "       JOIN BASE_POSITION bpos\n" +
                "            ON bpos.GROUP_ID = bhe.position_group_id and bpos.delete_ts is null\n" +
                "              and ?1 BETWEEN bpos.START_DATE AND bpos.END_DATE\n" +
                "       join tsadv_dic_position_status tdps\n" +
                "            on bpos.position_status_id = tdps.id\n" +
                "              and tdps.delete_ts is null and tdps.code <> 'CLOSED'\n" +
                "       LEFT JOIN BASE_HIERARCHY_ELEMENT bhep\n" +
                "            ON bhep.ID = bhe.PARENT_ID and bhep.delete_ts is null\n" +
                "              and ?1 between bhep.start_date and bhep.end_date\n" +
                "       LEFT JOIN (" +
                "           select bp.LAST_NAME, bp.FIRST_NAME, bp.MIDDLE_NAME, bp.LAST_NAME_LATIN, bp.FIRST_NAME_LATIN, bp.MIDDLE_NAME_LATIN,\n" +
                "                   ba.position_group_id, ba.person_group_id, tdas.CODE, bp.employee_number\n" +
                "           from BASE_ASSIGNMENT ba\n" +
                "               JOIN BASE_PERSON bp\n" +
                "                   ON bp.GROUP_ID = ba.person_group_id and bp.delete_ts is null\n" +
                "                       and ?1 BETWEEN bp.START_DATE AND bp.END_DATE\n" +
                "               join tsadv_dic_assignment_status tdas\n" +
                "                   on ba.assignment_status_id = tdas.id\n" +
                "                   and tdas.delete_ts is null\n" +
                "                   and tdas.code <> 'TERMINATED'\n" +
                "            where ?1 BETWEEN ba.START_DATE AND ba.END_DATE\n" +
                "               and ba.delete_ts is null\n" +
                "               AND ba.primary_flag = true ) bp \n" +
                "                   on bp.position_group_id = bhe.position_group_id \n" +
                "       LEFT JOIN BASE_ORGANIZATION bo\n" +
                "                 ON bo.GROUP_ID = bpos.organization_group_ext_id and bo.delete_ts is null\n" +
                "                   and ?1 BETWEEN bo.START_DATE AND bo.END_DATE\n" +
                "       LEFT JOIN TSADV_GRADE tg\n" +
                "                 ON tg.GROUP_ID = bpos.grade_group_id\n" +
                "                   and tg.delete_ts is null\n" +
                "       LEFT JOIN hierarchy_has_person hhp  on hhp.parent_id = bhe.id " +
                "where bhe.delete_ts is null\n" +
                "  AND bhe.hierarchy_id = ?2\n" +
                "  and ?1 between bhe.start_date and bhe.end_date\n" +
                (parentPositionGroupId != null
                        ? String.format("  AND bhep.position_group_id = '%s' \n", parentPositionGroupId)
                        : "  AND bhe.PARENT_ID is null \n") +
                "order by bhe.position_group_id, bp.person_group_id ";
        Map<Integer, Object> params = new HashMap<>();
        Date currentDate = CommonUtils.getSystemDate();
        params.put(1, currentDate);
        params.put(2, positionStructureId);
        List<Object[]> resultList = commonService.emNativeQueryResultList(queryString, params);
        return resultList;
    }

    @Override
    public List<Object[]> getMyTeamInPosition(UUID positionGroupId, UUID positionStructureId) {
        String queryString = " with hierarchy_has_person as (\n" +
                "  select h.parent_id \n" +
                "  from base_hierarchy_element h\n" +
                "         join base_position p\n" +
                "              on h.position_group_id = p.group_id\n" +
                "                and p.delete_ts is null\n" +
                "                AND ?1 between p.start_date and p.end_date\n" +
                "         join tsadv_dic_position_status tdps\n" +
                "              on tdps.id = p.position_status_id\n" +
                "                and tdps.delete_ts is null\n" +
                "                and tdps.code <> 'CLOSED'\n" +
                "  where ?1 between h.start_date and h.end_date " +
                "         and h.delete_ts is null " +
                "  group by h.parent_id ) " +
                "SELECT bp.LAST_NAME, bp.FIRST_NAME, bp.MIDDLE_NAME, bp.LAST_NAME_LATIN, bp.FIRST_NAME_LATIN, bp.MIDDLE_NAME_LATIN,\n" +
                "       bhe.POSITION_GROUP_ID, bpos.POSITION_FULL_NAME_LANG1, bpos.POSITION_FULL_NAME_LANG2, \n" +
                "       bpos.POSITION_FULL_NAME_LANG3, bo.ORGANIZATION_NAME_LANG1, bo.ORGANIZATION_NAME_LANG2,\n" +
                "       bo.ORGANIZATION_NAME_LANG3, bp.person_group_id, bp.CODE, tg.GRADE_NAME," +
                "       bp.employee_number, hhp.parent_id is not null \n" +
                "FROM BASE_HIERARCHY_ELEMENT bhe\n" +
                "       JOIN BASE_POSITION bpos\n" +
                "            ON bpos.GROUP_ID = bhe.position_group_id and bpos.delete_ts is null\n" +
                "              and ?1 BETWEEN bpos.START_DATE AND bpos.END_DATE\n" +
                "       join tsadv_dic_position_status tdps\n" +
                "            on bpos.position_status_id = tdps.id\n" +
                "              and tdps.delete_ts is null and tdps.code <> 'CLOSED'\n" +
                "       LEFT JOIN (" +
                "           select bp.LAST_NAME, bp.FIRST_NAME, bp.MIDDLE_NAME, bp.LAST_NAME_LATIN, bp.FIRST_NAME_LATIN, bp.MIDDLE_NAME_LATIN,\n" +
                "                   ba.position_group_id, ba.person_group_id, tdas.CODE, bp.employee_number\n" +
                "           from BASE_ASSIGNMENT ba\n" +
                "               JOIN BASE_PERSON bp\n" +
                "                   ON bp.GROUP_ID = ba.person_group_id and bp.delete_ts is null\n" +
                "                       and ?1 BETWEEN bp.START_DATE AND bp.END_DATE\n" +
                "               join tsadv_dic_assignment_status tdas\n" +
                "                   on ba.assignment_status_id = tdas.id\n" +
                "                   and tdas.delete_ts is null\n" +
                "                   and tdas.code <> 'TERMINATED'\n" +
                "            where ?1 BETWEEN ba.START_DATE AND ba.END_DATE\n" +
                "               and ba.delete_ts is null\n" +
                "               AND ba.primary_flag = true ) bp \n" +
                "                   on bp.position_group_id = bhe.position_group_id \n" +
                "       LEFT JOIN BASE_ORGANIZATION bo\n" +
                "                 ON bo.GROUP_ID = bpos.organization_group_ext_id and bo.delete_ts is null\n" +
                "                   and ?1 BETWEEN bo.START_DATE AND bo.END_DATE\n" +
                "       LEFT JOIN TSADV_GRADE tg\n" +
                "                 ON tg.GROUP_ID = bpos.grade_group_id\n" +
                "                   and tg.delete_ts is null\n" +
                "       LEFT JOIN hierarchy_has_person hhp  on hhp.parent_id = bhe.id " +
                "where bhe.delete_ts is null\n" +
                "  AND bhe.hierarchy_id = ?2\n" +
                "  and ?1 between bhe.start_date and bhe.end_date\n" +
                "  AND bhe.position_group_id = ?3 \n" +
                "order by bhe.position_group_id, bp.person_group_id ";
        Map<Integer, Object> params = new HashMap<>();
        Date currentDate = CommonUtils.getSystemDate();
        params.put(1, currentDate);
        params.put(2, positionStructureId);
        params.put(3, positionGroupId);
        List<Object[]> resultList = commonService.emNativeQueryResultList(queryString, params);
        return resultList;
    }


    protected String getFullName(Object[] entity) {
        String lastName;
        String firstName;
        String middleName;
        if (userSessionSource.getLocale().getLanguage().equals("en")) {
            if (StringUtils.isBlank((String) entity[3]) &&
                    StringUtils.isBlank((String) entity[4]) &&
                    StringUtils.isBlank((String) entity[5])) {
                lastName = (String) entity[0];
                firstName = (String) entity[1];
                middleName = (String) entity[2];
            } else {
                lastName = (String) entity[3];
                firstName = (String) entity[4];
                middleName = (String) entity[5];
            }
        } else {
            lastName = (String) entity[0];
            firstName = (String) entity[1];
            middleName = (String) entity[2];
        }
        return (lastName != null ? lastName + " " : "") +
                (firstName != null ? firstName + " " : "") +
                (middleName != null ? middleName : "") +
                (entity[16] != null ? String.format(" (%s)", entity[16]) : "");
    }

    protected String getPositionName(Object[] entity) {
        switch (userSessionSource.getLocale().getLanguage()) {
            case "ru":
                return (String) entity[7];
            case "kz":
                return (String) entity[8];
            case "en":
                return (String) entity[9];
            default:
                return (String) entity[7];
        }
    }

    protected String getOrganizationName(Object[] entity) {
        switch (userSessionSource.getLocale().getLanguage()) {
            case "ru":
                return (String) entity[10];
            case "kz":
                return (String) entity[11];
            case "en":
                return (String) entity[12];
            default:
                return (String) entity[10];
        }
    }

    @Override
    public MyTeamNew parseMyTeamNewObject(Object[] entity, String vacantPosition) {
        MyTeamNew myTeamNewObject = new MyTeamNew();
        myTeamNewObject.setLinkEnabled(true);
        myTeamNewObject.setFullName(getFullName(entity));
        myTeamNewObject.setPositionGroupId((UUID) entity[6]);
        myTeamNewObject.setPositionNameLang1(getPositionName(entity));
        myTeamNewObject.setOrganizationNameLang1(getOrganizationName(entity));
        myTeamNewObject.setPersonGroupId((UUID) entity[13]);
        myTeamNewObject.setGradeName((String) entity[15]);
        myTeamNewObject.setHasChild(Boolean.TRUE.equals(entity[17]));
        String assignmentStatusCode = (String) entity[14];
        if ("TERMINATED".equals(assignmentStatusCode)
                || StringUtils.isBlank(assignmentStatusCode)
                || StringUtils.isBlank(myTeamNewObject.getFullName())) {
            myTeamNewObject.setFullName(vacantPosition);
            myTeamNewObject.setLinkEnabled(false);
        }
        return myTeamNewObject;
    }

    @Override
    public MyTeamNew createFakeChild(@Nullable MyTeamNew parent) {
        MyTeamNew fakeChild = new MyTeamNew();
        fakeChild.setId(UUID.randomUUID());
        fakeChild.setFullName("FORDELETE!!");
        fakeChild.setParent(parent);
        return fakeChild;
    }

    @Nullable
    @Override
    public List<UUID> getChildPositionIdList(@Nullable UUID parentPositionGroupId, @Nullable UUID positionStructureId) {
        if (parentPositionGroupId == null || positionStructureId == null) return null;
        return persistence.callInTransaction(em -> em.createQuery(" select e.positionGroup.id from base$HierarchyElementExt e " +
                " join e.parent r " +
                " join e.positionGroup.list p " +
                " where r.positionGroup.id = :parentPositionGroupId " +
                "       and p.positionStatus.code <> 'CLOSED' " +
                "       and e.hierarchy.id = :positionStructureId " +
                "       and r.hierarchy.id = :positionStructureId " +
                "       and :sysDate between p.startDate and p.endDate " +
                "       and :sysDate between r.startDate and r.endDate " +
                "       and :sysDate between e.startDate and e.endDate ", UUID.class)
                .setParameter("parentPositionGroupId", parentPositionGroupId)
                .setParameter("positionStructureId", positionStructureId)
                .setParameter("sysDate", CommonUtils.getSystemDate())
                .getResultList());
    }
}