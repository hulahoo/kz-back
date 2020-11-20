package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.OrganizationStructureConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.entity.OrganizationTree;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.HrUserRole;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.personal.model.OrganizationStructure;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;


@Service(OrganizationService.NAME)
public class OrganizationServiceBean extends kz.uco.base.service.OrganizationServiceBean implements OrganizationService {

    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Persistence persistence;
    @Inject
    protected UserSessionSource userSessionSource;

    /**
     * Возвращает родительское подразделение для заданного подразделения в основной организационной иерархии
     */
    @Override
    public OrganizationGroupExt getParent(OrganizationGroupExt organizationGroup) {
        // устал бегать выяснять откуда взять (при помощи каких объектов) todo реализовать
        String query = "" +
                "select ";
        return null;
    }


    @Override
    public OrganizationGroupExt getOrganizationGroupByPositionGroupId(UUID positionGroupId, String viewName) {
        String queryString = "select distinct e.organizationGroup from tsadv$PositionStructure e" +
                "  where e.positionGroup.id = :positionGroupId" +
                "    and :systemDate between e.posStartDate and e.posEndDate " +
                "    and :systemDate between e.startDate and e.endDate";
        Map<String, Object> params = new HashMap<>();
        params.put("systemDate", CommonUtils.getSystemDate());
        params.put("positionGroupId", positionGroupId);
        List<OrganizationGroupExt> organizationGroupExtList = commonService.getEntities(OrganizationGroupExt.class,
                queryString,
                params,
                viewName != null ? viewName : "_minimal");
        if (!organizationGroupExtList.isEmpty()) {
            return organizationGroupExtList.get(0);
        } else {
            return null;
        }
    }


    @Override
    public List<OrganizationTree> loadOrganizationTree(UUID parentId, UUID hierarchyId, String searchText) {
        List<OrganizationTree> resultList = new LinkedList<>();

        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            int languageIndex = languageIndex();

            if (StringUtils.isBlank(searchText)) {
                Query query = em.createNativeQuery(String.format(
                        "SELECT " +
                                "  he.id, " +
                                "  he.parent_id, " +
                                "  he.organization_group_id, " +
                                "  org.id org_id," +
                                "  org.organization_name_lang%d," +
                                "  org.organization_name_lang1," +
                                "  org.organization_name_lang2," +
                                "  org.organization_name_lang3," +
                                "  org.organization_name_lang4," +
                                "  org.organization_name_lang5," +
                                "  org.start_date," +
                                "  org.end_date," +
                                "  (SELECT count(*) " +
                                "   FROM base_hierarchy_element bhe " +
                                "   WHERE bhe.delete_ts IS NULL AND bhe.element_type = 1 AND bhe.parent_id = he.id) cnt " +
                                "FROM base_hierarchy h " +
                                "  JOIN base_hierarchy_element he " +
                                "    ON he.hierarchy_id = h.id " +
                                "  JOIN base_organization org " +
                                "    ON org.group_id = he.organization_group_id " +
                                "WHERE he.element_type = 1 " +
                                "      AND h.delete_ts IS NULL " +
                                "      AND he.delete_ts IS NULL " +
                                "      AND current_date between he.start_date and he.end_date " +
                                "      %s " +
                                "      AND he.element_type = 1 " +
                                "      AND org.delete_ts IS NULL " +
                                "      AND current_date between org.start_date and org.end_date " +
                                "      AND he.parent_id %s",
                        languageIndex,
                        hierarchyId == null ? "AND h.primary_flag = TRUE" : "AND h.id = ?1",
                        parentId != null ? "= ?2" : "IS NULL"));

                if (hierarchyId != null) {
                    query.setParameter(1, hierarchyId);
                }
                if (parentId != null) {
                    query.setParameter(2, parentId);
                }

                List<Object[]> rows = query.getResultList();
                if (rows != null && !rows.isEmpty()) {
                    for (Object[] row : rows) {
                        resultList.add(parseOrganizationTree(row));
                    }
                }
            } else {
                search(searchText, resultList, em, hierarchyId, languageIndex);
            }
        } finally {
            tx.end();
        }
        return resultList;
    }

    @Override
    public List<UUID> getAllNestedOrganizationIds(UUID organizationGroupId, UUID hierarchyId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            String hierarchyFilter = "h.primary_flag = True";
            if (hierarchyId != null) {
                hierarchyFilter = "h.id = '" + hierarchyId + "'";
            }

            Query query = em.createNativeQuery(
                    "SELECT he.organization_group_id as orgs\n" +
                            "FROM base_hierarchy_element he\n" +
                            " join base_hierarchy h on h.id = he.hierarchy_id " +
                            "  and " + hierarchyFilter + " " +
                            "WHERE he.organization_group_id IN (SELECT og.id\n" +
                            "                                   FROM base_organization_group og\n" +
                            "                                     JOIN tsadv_organization_structure os\n" +
                            "                                       ON os.organization_group_id = og.id\n" +
                            "                                   WHERE os.path LIKE ?1)\n" +
                            "and he.delete_ts is NULL\n" +
                            "  and current_date BETWEEN he.start_date and he.end_date");
            query.setParameter(1, "%" + organizationGroupId.toString() + "%");
            return query.getResultList();
        }
    }

    @Override
    public DicPayroll getPayroll() {
        DicPayroll payroll = null;
        Configuration configuration = AppBeans.get(Configuration.NAME);
        OrganizationStructureConfig config = configuration.getConfig(OrganizationStructureConfig.class);
        if (!config.getFillPayrollOnOrgStructure().equals("")) {
            UUID payrollId = UUID.fromString(config.getFillPayrollOnOrgStructure());
            payroll = commonService.getEntity(DicPayroll.class, payrollId);
        }
        return payroll;
    }

    private OrganizationTree parseOrganizationTree(Object[] row) {
        OrganizationTree organizationTree = metadata.create(OrganizationTree.class);
        organizationTree.setId((UUID) row[0]);

        UUID parentId = (UUID) row[1];
        if (parentId != null) {
            OrganizationTree parent = metadata.create(OrganizationTree.class);
            parent.setId(parentId);
            organizationTree.setParent(parent);
        }

        UUID organizationGroupId = (UUID) row[2];
        organizationTree.setOrganizationGroupId(organizationGroupId);
        organizationTree.setOrganizationName((String) row[4]);
        organizationTree.setHasChild(((Long) row[12]) > 0);

        /**
         * create and fill OrganizationGroupExt
         * */
        OrganizationGroupExt organizationGroupExt = metadata.create(OrganizationGroupExt.class);
        organizationGroupExt.setId(organizationGroupId);

        OrganizationExt organizationExt = metadata.create(OrganizationExt.class);
        organizationExt.setId((UUID) row[3]);
        organizationExt.setOrganizationNameLang1((String) row[5]);
        organizationExt.setOrganizationNameLang2((String) row[6]);
        organizationExt.setOrganizationNameLang3((String) row[7]);
        organizationExt.setOrganizationNameLang4((String) row[8]);
        organizationExt.setOrganizationNameLang5((String) row[9]);
        organizationExt.setStartDate((Date) row[10]);
        organizationExt.setEndDate((Date) row[11]);
        organizationExt.setGroup(organizationGroupExt);
        organizationGroupExt.setList(Collections.singletonList(organizationExt));
        organizationTree.setOrganizationGroupExt(organizationGroupExt);

        return organizationTree;
    }

    @Override
    public String getOrganizationPathToHint(UUID organizationGroupId, Date date) {
        StringBuilder orgNamesPath = new StringBuilder();
        List<OrganizationStructure> organizationStructures = commonService.getEntities(OrganizationStructure.class,
                "select e from tsadv$OrganizationStructure e where e.organizationGroup.id = :organizationGroupId" +
                        " and e.startDate <= :date and e.endDate >= :date",
                ParamsMap.of("organizationGroupId", organizationGroupId, "date", date), View.LOCAL);
        OrganizationStructure organizationStructure = null;
        if (!organizationStructures.isEmpty()) {
            organizationStructure = organizationStructures.get(0);
        }
        if (organizationStructure == null) {
            return null;
        }
        String idPath = organizationStructure.getPathOrgName1();
        String[] split = idPath.split("->");

        for (int i = 0; i < split.length; i++) {
            StringBuilder spaces = new StringBuilder();
            if (i != 0) {
                spaces.append("<br/>");
                for (int j = 0; j < i; j++) {
                    spaces.append("&nbsp;&nbsp;");
                }
            }
            String organizationName = split[i];
            String organizationNameShort = "";
            if (organizationName != null) {
                organizationNameShort = organizationName.substring(0, Math.min(organizationName.length(), 50));
                if (organizationName.length() > 50) {
                    organizationNameShort += "..";
                }
                organizationNameShort = "<font size='-1'>" + organizationNameShort + "</font>";
            }

            orgNamesPath.append(organizationName != null ? spaces + organizationNameShort : "");
        }
        return orgNamesPath.toString();
    }

    @Override
    public Map<UUID, String> getOrganizationPathToHintForList(List<UUID> organizationGroupIds, Date date) {
        Map<UUID, String> hints = new HashMap<>();
        StringBuilder orgNamesPath = new StringBuilder();
        List<OrganizationStructure> organizationStructures = commonService.getEntities(OrganizationStructure.class,
                "select e from tsadv$OrganizationStructure e where e.organizationGroup.id in :organizationGroupIds" +
                        " and e.startDate <= :date and e.endDate >= :date",
                ParamsMap.of("organizationGroupIds", organizationGroupIds, "date", date),
                "organizationStructure.for.orgService");
        OrganizationStructure organizationStructure = null;

        for (UUID organizationGroupId : organizationGroupIds) {
            if (!organizationStructures.isEmpty()) {
                organizationStructure = organizationStructures.stream().filter(organizationStructure1 ->
                        organizationStructure1.getOrganizationGroup().getId().equals(organizationGroupId)
                ).findFirst().orElse(null);
            }
            if (organizationStructure == null) {
                continue;
            }
            String idPath = organizationStructure.getPathOrgName1();
            String[] split = idPath.split("->");

            for (int i = 0; i < split.length; i++) {
                StringBuilder spaces = new StringBuilder();
                if (i != 0) {
                    spaces.append("<br/>");
                    for (int j = 0; j < i; j++) {
                        spaces.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    }
                }
                String organizationName = split[i];
                String organizationNameShort = "";
                if (organizationName != null) {
                    organizationNameShort = "<font size='-1'>" + (i > 0 ? "└─" : "") + organizationName + "</font>";
                }

                orgNamesPath.append(organizationName != null ? spaces + organizationNameShort : "");
            }

            hints.put(organizationGroupId, orgNamesPath.toString());
            orgNamesPath.delete(0, orgNamesPath.length());
            organizationStructure = null;
        }
        return hints;
    }

    @Nonnull
    @Override
    public Set<OrganizationGroupExt> getOrganizationsWhereUserIsHr(UserExt userExt) {
        Set<OrganizationGroupExt> organizationsWhereUserIsHr = new HashSet<>();
        HrUserRole timekeeperRoleForUser = commonService.getEntity(HrUserRole.class,
                "select e from tsadv$HrUserRole e where e.role.code = 'TIMEKEEPER' and e.user.id = :userExtId and :currentDate <= e.dateTo and :currentDate >= e.dateFrom",
                ParamsMap.of("userExtId", userExt.getId(), "currentDate", CommonUtils.getSystemDate()), View.LOCAL);
        if (timekeeperRoleForUser == null) {
            return organizationsWhereUserIsHr;
        }

        List<OrganizationHrUser> hrUsers = commonService.getEntities(OrganizationHrUser.class,
                "select e from tsadv$OrganizationHrUser e where e.user.id = :userExtId and :currentDate <= e.dateTo and :currentDate >= e.dateFrom",
                ParamsMap.of("userExtId", userExt.getId(), "currentDate", CommonUtils.getSystemDate()), "organizationHrUser.view");

        hrUsers.forEach(e -> organizationsWhereUserIsHr.add(e.getOrganizationGroup()));
        return organizationsWhereUserIsHr;
    }

    private void search(String searchText, List<OrganizationTree> resultList, EntityManager em, UUID hierarchyId, int languageIndex) {
        List<UUID> filteredIds = getFilteredList(searchText, em, hierarchyId);
        if (filteredIds != null && !filteredIds.isEmpty()) {
            String sqlIds = filteredIds.stream()
                    .map(uuid -> "'" + uuid.toString() + "'")
                    .collect(Collectors.joining(","));

            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "  he.id, " +
                            "  he.parent_id, " +
                            "  he.organization_group_id, " +
                            "  org.id org_id," +
                            "  org.organization_name_lang%d," +
                            "  org.organization_name_lang1," +
                            "  org.organization_name_lang2," +
                            "  org.organization_name_lang3," +
                            "  org.organization_name_lang4," +
                            "  org.organization_name_lang5," +
                            "  org.start_date," +
                            "  org.end_date," +
                            "  (SELECT count(*) " +
                            "   FROM base_hierarchy_element bhe " +
                            "   WHERE bhe.delete_ts IS NULL AND bhe.element_type = 1 AND bhe.parent_id = he.id) cnt " +
                            "FROM base_hierarchy h " +
                            "  JOIN base_hierarchy_element he " +
                            "    ON he.hierarchy_id = h.id " +
                            "  JOIN base_organization org " +
                            "    ON org.group_id = he.organization_group_id " +
                            "WHERE he.id in (%s) " +
                            "      AND he.element_type = 1 " +
                            "      AND h.delete_ts IS NULL " +
                            "      AND he.delete_ts IS NULL " +
                            "      AND current_date between he.start_date and he.end_date " +
                            "      %s " +
                            "      AND he.element_type = 1 " +
                            "      AND org.delete_ts IS NULL " +
                            "      AND current_date between org.start_date and org.end_date ",
                    languageIndex,
                    sqlIds,
                    hierarchyId == null ? "AND h.primary_flag = TRUE" : "AND h.id = ?1"));

            if (hierarchyId != null) {
                query.setParameter(1, hierarchyId);
            }
            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    resultList.add(parseOrganizationTree(row));
                }
            }
        }
    }

    private List<UUID> getFilteredList(String searchText, EntityManager em, UUID hierarchyId) {
        Query query = em.createNativeQuery(String.format(
                "SELECT t.id " +
                        "FROM (WITH RECURSIVE he(lvl, id, parent_id) AS ( " +
                        "  WITH elements AS ( " +
                        "      SELECT " +
                        "        he.id, " +
                        "        he.parent_id, " +
                        "        he.organization_group_id " +
                        "      FROM base_hierarchy h " +
                        "        JOIN base_hierarchy_element he " +
                        "          ON he.hierarchy_id = h.id " +
                        "      WHERE h.delete_ts IS NULL " +
                        "            AND he.delete_ts IS NULL " +
                        "            %s " +
                        "            AND he.element_type = 1 " +
                        "  ) " +
                        "  SELECT " +
                        "    CASE WHEN he.parent_id IS NULL " +
                        "      THEN 0 " +
                        "    ELSE 1 END, " +
                        "    he.id, " +
                        "    he.parent_id " +
                        "  FROM elements he " +
                        "    JOIN base_organization org " +
                        "      ON org.group_id = he.organization_group_id " +
                        "         AND org.delete_ts IS NULL " +
                        "         AND current_date BETWEEN org.start_date AND org.end_date " +
                        "  WHERE lower(org.organization_name_lang1) LIKE ?2 " +
                        "  UNION ALL " +
                        "  SELECT " +
                        "    lvl + 1, " +
                        "    g.id, " +
                        "    g.parent_id " +
                        "  FROM elements g, he sg " +
                        "  WHERE g.id = sg.parent_id " +
                        ") " +
                        "SELECT " +
                        "  heh.id, " +
                        "  heh.lvl " +
                        "FROM he heh " +
                        "ORDER BY heh.lvl DESC) t " +
                        "GROUP BY t.id",
                hierarchyId == null ? "AND h.primary_flag = TRUE" : "AND h.id = ?1"));

        if (hierarchyId != null) {
            query.setParameter(1, hierarchyId);
        }
        query.setParameter(2, ("%" + searchText + "%").toLowerCase());
        return query.getResultList();
    }

    private int languageIndex() {
        Locale locale = userSessionSource.getLocale();
        String language = locale.getLanguage().toLowerCase();
        int languageIndex = 1;
        switch (language) {
            case "ru": {
                languageIndex = 1;
                break;
            }
            case "kz": {
                languageIndex = 2;
                break;
            }
            case "en": {
                languageIndex = 3;
                break;
            }
        }
        return languageIndex;
    }
}