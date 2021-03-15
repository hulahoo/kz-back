package kz.uco.tsadv.listener;

import com.haulmont.bali.db.QueryRunner;
import com.haulmont.bali.db.ResultSetHandler;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Component("tsadv_AssignmentExtListener")
public class AssignmentExtListener implements
        AfterUpdateEntityListener<AssignmentExt>,
        AfterDeleteEntityListener<AssignmentExt> {

    @Inject
    protected IntegrationConfig integrationConfig;
    @Inject
    protected Persistence persistence;
    @Inject
    protected IntegrationService integrationService;

    @Override
    public void onAfterDelete(AssignmentExt entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deleteAssignment(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }

    @Override
    public void onAfterUpdate(AssignmentExt entity, Connection connection) {
        /*if (persistence.getTools().isDirty(entity, "organizationGroup"))
            AppBeans.get(BpmService.class).notifyManagersAboutAccessInf(entity.getStartDate(),
                    entity.getPersonGroup(),
                    entity.getOrganizationGroup(),
                    (OrganizationGroupExt) persistence.getTools().getOldValue(entity, "organizationGroup"),
                    entity.getPositionGroup(),
                    (PositionGroupExt) persistence.getTools().getOldValue(entity, "positionGroup"),
                    false);
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.integrateAssignment(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }*/

        if(entity.getGroup().getId() != null){
            QueryRunner queryRunner = new QueryRunner();
            try {
                HashMap<String,String> assignmentDataMap = new HashMap<>();
                String organizationCompanyId = "";
                String assignmentGroupId = entity.getGroup().getId().toString();
                String personGroupIdKey = "person_group_id";
                String organizationGroupIdKey = "organization_group_id";
                String positionGroupIdKey = "position_group_id";
                String jobGroupIdKey = "job_group_id";
                String gradeGroupIdKey = "grade_group_id";
                String companyIdKey = "company_id";
                String selectAssignmentDataSql = "select person_group_id , organization_group_id , position_group_id , job_group_id , grade_group_id from base_assignment where group_id = '" + assignmentGroupId + "' order by start_date desc limit 1";

                assignmentDataMap = queryRunner.query(connection,
                        selectAssignmentDataSql,
                        rs -> {
                            HashMap<String,String> resultMap = new HashMap<>();
                            while (rs.next()){
                                resultMap.put(personGroupIdKey,rs.getString(personGroupIdKey));
                                resultMap.put(organizationGroupIdKey,rs.getString(organizationGroupIdKey));
                                resultMap.put(positionGroupIdKey,rs.getString(positionGroupIdKey));
                                resultMap.put(jobGroupIdKey,rs.getString(jobGroupIdKey));
                                resultMap.put(gradeGroupIdKey,rs.getString(gradeGroupIdKey));
                            }
                            return resultMap;
                        });

                String selectOrganizationCompanyIdSql = "select company_id from base_organization_group bog where id = '" + assignmentDataMap.get(organizationGroupIdKey) + "'";
                organizationCompanyId = queryRunner.query(connection,
                        selectOrganizationCompanyIdSql,
                        rs -> {
                            String companyId = "";
                            while (rs.next()){
                                companyId = rs.getString(companyIdKey);
                            }
                            return companyId;
                        });
                organizationCompanyId = ( (organizationCompanyId==null || organizationCompanyId.isEmpty()) ? "null" : "'" + organizationCompanyId +"'");

                String personGroupIdFromMap = assignmentDataMap.get(personGroupIdKey);
                String personGroupIdFormatted = ( (personGroupIdFromMap == null || personGroupIdFromMap.isEmpty()) ? "null" : "'" + personGroupIdFromMap + "'");
                String organizationGroupIdFromMap = assignmentDataMap.get(organizationGroupIdKey);
                String organizationGroupIdFormatted = ( (organizationGroupIdFromMap == null || organizationGroupIdFromMap.isEmpty()) ? "null" : "'" + organizationGroupIdFromMap + "'");
                String positionGroupIdFromMap = assignmentDataMap.get(positionGroupIdKey);
                String positionGroupFormatted = ( (positionGroupIdFromMap == null || positionGroupIdFromMap.isEmpty()) ? "null" : "'" + positionGroupIdFromMap + "'");
                String jobGroupIdFromMap = assignmentDataMap.get(jobGroupIdKey);
                String jobGroupIdFormatted = ( (jobGroupIdFromMap == null || jobGroupIdFromMap.isEmpty()) ? "null" : "'" + jobGroupIdFromMap + "'");
                String gradeGroupIdFromMap = assignmentDataMap.get(gradeGroupIdKey);
                String gradeGroupIdFormatted = ( (gradeGroupIdFromMap == null || gradeGroupIdFromMap.isEmpty()) ? "null" : "'" + gradeGroupIdFromMap + "'");

                String updateAssignmentGroupSql = "update base_assignment_group \n" +
                        "set person_group_id = " + personGroupIdFormatted + ",\n" +
                        "organization_group_id = " + organizationGroupIdFormatted + ",\n" +
                        "position_group_id = " + positionGroupFormatted + ",\n" +
                        "job_group_id = " + jobGroupIdFormatted + ",\n" +
                        "grade_group_id = " + gradeGroupIdFormatted + ",\n" +
                        "company_id = " + organizationCompanyId + "\n" +
                        "where id = '"+ assignmentGroupId +"'";

                queryRunner.update(connection,updateAssignmentGroupSql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}