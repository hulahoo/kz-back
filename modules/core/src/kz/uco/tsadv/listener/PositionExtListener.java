package kz.uco.tsadv.listener;

import com.haulmont.bali.db.QueryRunner;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import kz.uco.tsadv.modules.personal.model.PositionExt;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;

import javax.inject.Inject;

@Component("tsadv_PositionExtListener")
public class PositionExtListener implements AfterDeleteEntityListener<PositionExt>, AfterInsertEntityListener<PositionExt>, AfterUpdateEntityListener<PositionExt> {

    @Inject
    private IntegrationConfig integrationConfig;

    @Inject
    private IntegrationService integrationService;

    @Override
    public void onAfterDelete(PositionExt entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deletePosition(entity, connection);
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
    public void onAfterInsert(PositionExt entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createPosition(entity, connection);
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
    public void onAfterUpdate(PositionExt entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.updatePosition(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }

        if(entity.getGroup().getId() != null){
            QueryRunner queryRunner = new QueryRunner();
            String positionGroupId = entity.getGroup().getId().toString();

            HashMap<String,String> positionDataMap = new HashMap<>();
            String organizationGroupCompanyId = "";
            String organizationGroupIdKey = "organization_group_ext_id";
            String jobGroupIdKey = "job_group_id";
            String gradeGroupIdKey = "grade_group_id";
            String companyIdKey = "company_id";
            String selectPositionDataSql = "select organization_group_ext_id , job_group_id , grade_group_id from base_position bp where group_id = '" + positionGroupId +"' order by start_date desc limit 1;";
            try {
                positionDataMap = queryRunner.query(connection,
                        selectPositionDataSql,
                        rs -> {
                            HashMap<String,String> resultMap = new HashMap<>();
                            while (rs.next()){
                                resultMap.put(organizationGroupIdKey,rs.getString(organizationGroupIdKey));
                                resultMap.put(jobGroupIdKey,rs.getString(jobGroupIdKey));
                                resultMap.put(gradeGroupIdKey,rs.getString(gradeGroupIdKey));
                            }
                            return resultMap;
                        });

                String selectOrganizationCompanyIdSql = "select company_id from base_organization_group bog where id = '" + positionDataMap.get(organizationGroupIdKey) + "'";
                organizationGroupCompanyId = queryRunner.query(connection,
                        selectOrganizationCompanyIdSql,
                        rs -> {
                            String companyId = "";
                            while (rs.next()){
                                companyId = rs.getString(companyIdKey);
                            }
                            return companyId;
                        });

                String organizationGroupIdFromMap = positionDataMap.get(organizationGroupIdKey);
                String organizationGroupIdFormatted = ( (organizationGroupIdFromMap == null || organizationGroupIdFromMap.isEmpty()) ? "null" : "'" + organizationGroupIdFromMap + "'");
                String jobGroupIdFromMap = positionDataMap.get(jobGroupIdKey);
                String jobGroupIdFormatted = ( (jobGroupIdFromMap == null || jobGroupIdFromMap.isEmpty()) ? "null" : "'" + jobGroupIdFromMap + "'");
                String gradeGroupIdFromMap = positionDataMap.get(gradeGroupIdKey);
                String gradeGroupIdFormatted = ( (gradeGroupIdFromMap == null || gradeGroupIdFromMap.isEmpty()) ? "null" : "'" + gradeGroupIdFromMap + "'");
                String organizationGroupCompanyIdFormatted = ( (organizationGroupCompanyId==null || organizationGroupCompanyId.isEmpty()) ? "null" : "'" + organizationGroupCompanyId +"'");

                String updatePositionGroupSql = "update base_position_group \n" +
                        "set organization_group_id = " + organizationGroupIdFormatted + ",\n" +
                        "job_group_id = " + jobGroupIdFormatted + ",\n" +
                        "grade_group_id = " + gradeGroupIdFormatted + ",\n" +
                        "company_id = " + organizationGroupCompanyIdFormatted + "\n" +
                        "where id = '" + positionGroupId + "';";

                queryRunner.update(connection,updatePositionGroupSql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}