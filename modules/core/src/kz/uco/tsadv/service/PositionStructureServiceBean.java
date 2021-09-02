package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.models.PositionGroupDetails;
import kz.uco.tsadv.entity.models.PositionHierarchy;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import org.springframework.stereotype.Service;


import javax.inject.Inject;
import java.util.*;

@Service(PositionStructureService.NAME)
public class PositionStructureServiceBean implements PositionStructureService {


    @Inject
    protected CommonService commonService;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected HierarchyService hierarchyService;

    @Override
    public List<PositionHierarchy> getChildren(UUID parentId) {
        String sql = "select  bhe2.position_group_id " +
                ", case when lower(?2) = 'ru' then bp2.position_full_name_lang1  " +
                "when lower(?2) = 'kz' then bp2.position_full_name_lang2 " +
                "when lower(?2) = 'en' then bp2.position_full_name_lang3 " +
                "else bp2.position_full_name_lang1 end position_name2 " +
                ", bhe3.position_group_id  " +
                ",case when lower(?2) = 'ru' then bp3.position_full_name_lang1  " +
                "when lower(?2) = 'kz' then bp3.position_full_name_lang2 " +
                "when lower(?2) = 'en' then bp3.position_full_name_lang3 " +
                "else bp3.position_full_name_lang1 end position_name3 " +
                "from base_hierarchy_element bhe  " +
                "left join base_position bp on bp.group_id =bhe.position_group_id   " +
                "and ?3 between bp.start_date and bp.end_date and bp.delete_ts is null " +
                "join base_hierarchy_element bhe2 on bhe2.parent_id = bhe.id  " +
                "and ?3 between bhe2.start_date and bhe2.end_date  " +
                "and bhe2.delete_ts is null " +
                "and bhe2.element_type = 2 " +
                "left join base_position bp2 on bp2.group_id =bhe2.position_group_id   " +
                "and ?3 between bp2.start_date and bp2.end_date and bp2.delete_ts is null " +
                "left join base_hierarchy_element bhe3 on bhe3.parent_id = bhe2.id  " +
                "and ?3 between bhe3.start_date and bhe3.end_date  " +
                "and bhe3.element_type = 2 " +
                "and bhe3.delete_ts is null " +
                "left join base_position bp3 on bp3.group_id =bhe3.position_group_id   " +
                "and ?3 between bp3.start_date and bp3.end_date and bp3.delete_ts is null " +
                "where bhe.delete_ts is null  " +
                "and ?3 between bhe.start_date and bhe.end_date  " +
                "and bhe.element_type = 2 " +
                "and ((?1 is null and bhe.parent_group_id is null)  or ?1 = bhe.position_group_id ) " +
                "order by position_name2, bhe2.position_group_id, position_name2, bhe3.position_group_id ";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, parentId);
        params.put(2, userSessionSource.getUserSession().getLocale().toLanguageTag().toLowerCase());
        params.put(3, CommonUtils.getSystemDate());
        List<PositionHierarchy> resultHierarchy = new ArrayList<>();
        List<Object[]> result = commonService.emNativeQueryResultList(
                sql, params);
        for (Object[] objects : result) {
            if (objects[2] != null) {
                PositionHierarchy grandChild = metadata.create(PositionHierarchy.class);
                grandChild.setId((UUID) objects[2]);
                grandChild.setPositionName((String) objects[3]);
                PositionHierarchy childParent = resultHierarchy.stream().filter(child ->
                        ((UUID) objects[0]).equals(child.getId())).findFirst().orElse(null);
                if (childParent == null) {
                    childParent = metadata.create(PositionHierarchy.class);
                    childParent.setPositionName((String) objects[1]);
                    childParent.setId((UUID) objects[0]);
                    childParent.setChildren(new ArrayList<>());
                    resultHierarchy.add(childParent);
                }
                grandChild.setParent(childParent);
                childParent.setHaveChildren(true);
                childParent.getChildren().add(grandChild);
            } else {
                PositionHierarchy grandChild = resultHierarchy.stream().filter(child ->
                        child.getId().equals((UUID) objects[0])).findFirst().orElse(null);
                if (grandChild == null) {
                    grandChild = metadata.create(PositionHierarchy.class);
                    grandChild.setId((UUID) objects[0]);
                    grandChild.setPositionName((String) objects[1]);
                    grandChild.setHaveChildren(false);
                    grandChild.setChildren(new ArrayList<>());
                    resultHierarchy.add(grandChild);
                }
            }


        }
        return resultHierarchy;
    }

    @Override
    public List<PositionHierarchy> getStartData() {
        UUID userId = userSessionSource.getUserSession().getUser().getId();
        String sql = "select bhe2.position_group_id " +
                ", case when lower(?2) = 'ru' then bp2.position_full_name_lang1  " +
                "when lower(?2) = 'kz' then bp2.position_full_name_lang2 " +
                "when lower(?2) = 'en' then bp2.position_full_name_lang3 " +
                "else bp2.position_full_name_lang1 end position_name2 " +
                ", bhe3.position_group_id  " +
                ",case when lower(?2) = 'ru' then bp3.position_full_name_lang1  " +
                "when lower(?2) = 'kz' then bp3.position_full_name_lang2 " +
                "when lower(?2) = 'en' then bp3.position_full_name_lang3 " +
                "else bp3.position_full_name_lang1 end position_name3 " +
                "from base_hierarchy_element bhe  " +
                "join base_assignment ba on ba.position_group_id =bhe.position_group_id  " +
                "left join base_position bp on bp.group_id =bhe.position_group_id   " +
                "and ?3 between bp.start_date and bp.end_date and bp.delete_ts is null " +
                "join base_hierarchy_element bhe2 on bhe2.parent_id = bhe.id  " +
                "and ?3 between bhe2.start_date and bhe2.end_date  " +
                "and bhe2.delete_ts is null " +
                "and bhe2.element_type = 2 " +
                "left join base_position bp2 on bp2.group_id =bhe2.position_group_id   " +
                "and ?3 between bp2.start_date and bp2.end_date and bp2.delete_ts is null " +
                "left join base_hierarchy_element bhe3 on bhe3.parent_id = bhe2.id  " +
                "and ?3 between bhe3.start_date and bhe3.end_date  " +
                "and bhe3.element_type = 2 " +
                "and bhe3.delete_ts is null " +
                "left join base_position bp3 on bp3.group_id =bhe3.position_group_id   " +
                "and ?3 between bp3.start_date and bp3.end_date and bp3.delete_ts is null " +
                "where bhe.delete_ts is null  " +
                "and ?3 between bhe.start_date and bhe.end_date  " +
                "and bhe.element_type = 2 " +
                "and bhe.position_group_id in  " +
                "(select ba2.position_group_id from sec_user su  " +
                "join base_assignment ba2 on su.person_group_id  = ba2.person_group_id  " +
                "and ba2.delete_ts is null and ba2.delete_ts is null  " +
                "and ?3 between ba2.start_date and ba2.end_date  " +
                "where su.id = ?1 and su.delete_ts is null) " +
                "order by position_name2, bhe2.position_group_id, position_name2, bhe3.position_group_id ";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, userId);
        params.put(2, userSessionSource.getUserSession().getLocale().toLanguageTag().toLowerCase());
        params.put(3, CommonUtils.getSystemDate());
        List<PositionHierarchy> resultHierarchy = new ArrayList<>();
        List<Object[]> result = commonService.emNativeQueryResultList(
                sql, params);
        for (Object[] objects : result) {
            if (objects[2] != null) {
                PositionHierarchy grandChild = metadata.create(PositionHierarchy.class);
                grandChild.setId((UUID) objects[2]);
                grandChild.setPositionName((String) objects[3]);
                PositionHierarchy childParent = resultHierarchy.stream().filter(child ->
                        ((UUID) objects[0]).equals(child.getId())).findFirst().orElse(null);
                if (childParent == null) {
                    childParent = metadata.create(PositionHierarchy.class);
                    childParent.setId((UUID) objects[0]);
                    childParent.setPositionName((String) objects[1]);
                    childParent.setChildren(new ArrayList<>());
                    resultHierarchy.add(childParent);
                }
                grandChild.setParent(childParent);
                childParent.setHaveChildren(true);
                childParent.getChildren().add(grandChild);
            } else {
                PositionHierarchy grandChild = resultHierarchy.stream().filter(child ->
                        child.getId().equals((UUID) objects[0])).findFirst().orElse(null);
                if (grandChild == null) {
                    grandChild = metadata.create(PositionHierarchy.class);
                    grandChild.setId((UUID) objects[0]);
                    grandChild.setPositionName((String) objects[1]);
                    grandChild.setHaveChildren(false);
                    grandChild.setChildren(new ArrayList<>());
                    resultHierarchy.add(grandChild);
                }
            }
        }
        return resultHierarchy;
    }

    @Override
    public PositionGroupDetails getPositionDetails(UUID positionGroupId) {
        PositionGroupDetails positionGroupDetails = metadata.create(PositionGroupDetails.class);
        if (positionGroupId != null) {
            positionGroupDetails.setId(positionGroupId);
            PositionGroupExt positionGroupExt = dataManager.load(Id.of(positionGroupId, PositionGroupExt.class))
                    .view("positionGroupExt.for.requisition").one();
            positionGroupDetails.setPositionGroupName(positionGroupExt.getPositionName());
            if (positionGroupExt.getPosition().getFunctionalManagerPositionGroup() != null) {
                positionGroupDetails.setFunctionalManagerId(positionGroupExt.getPosition().getFunctionalManagerPositionGroup().getId().toString());
                positionGroupDetails.setFunctionalManagerName(positionGroupExt.getPosition().getFunctionalManagerPositionGroup().getFullName());
            }
            PositionGroupExt parentPosition = hierarchyService.getParentPosition(positionGroupExt, "positionGroupExt-for-position-details");
            positionGroupDetails.setAdministrativeManagerId(parentPosition.getId().toString());
            positionGroupDetails.setAdministrativeManagerName(parentPosition.getFullName());
            positionGroupDetails.setStructuralOrganizationsTree(getOrganizationHierarchyTree(
                    positionGroupExt.getPosition().getOrganizationGroupExt().getId()));
        }
        return positionGroupDetails;
    }

    public String getOrganizationHierarchyTree(UUID organizationGroupId) {
        OrganizationService organizationService = AppBeans.get(OrganizationService.class);

        StringBuilder structOrganization = new StringBuilder();
        if (organizationGroupId != null) {
            OrganizationGroupExt organizationGroupExt = dataManager.load(Id.of(
                    organizationGroupId, OrganizationGroupExt.class))
                    .view("organizationGroupExt-for-struct-path").optional().orElse(null);
            List<OrganizationGroupExt> organizationList = new ArrayList<>();
            while (organizationGroupExt != null && organizationGroupExt.getOrganizationName() != null) {
                organizationList.add(organizationGroupExt);
                organizationGroupExt = organizationService.getParent(organizationGroupExt);
            }
            Collections.reverse(organizationList);
            if (organizationList.size() > 0) {
                organizationList.remove(0);
            }
            int i = 0;
            for (OrganizationGroupExt groupExt : organizationList) {
                for (int j = 0; j < organizationList.indexOf(groupExt) + 1; j++) {
                    structOrganization.append("- ");
                }
                structOrganization.append(groupExt.getOrganizationName()).append("\r\n");
            }
        }
        return structOrganization.toString();
    }

}