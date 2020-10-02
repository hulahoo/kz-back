package kz.uco.tsadv.listener;

import com.haulmont.bali.db.QueryRunner;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.*;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.EmployeeConfig;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component("tsadv_PersonExtListener")
public class PersonExtListener implements
        AfterInsertEntityListener<PersonExt>,
        AfterUpdateEntityListener<PersonExt>,
        AfterDeleteEntityListener<PersonExt>,
        BeforeInsertEntityListener<PersonExt>,
        BeforeUpdateEntityListener<PersonExt> {

    @Inject
    protected IntegrationConfig integrationConfig;
    @Inject
    protected IntegrationService integrationService;
    @Inject
    protected EmployeeConfig employeeConfig;


    @Override
    public void onBeforeInsert(PersonExt person, EntityManager entityManager) {
        setFullNames(person);
    }

    @Override
    public void onBeforeUpdate(PersonExt person, EntityManager entityManager) {
        setFullNames(person);
    }

    @Override
    public void onAfterInsert(PersonExt entity, Connection connection) {
        UUID groupId = entity.getGroup().getId();
        UUID typeId = entity.getType().getId();
        if (typeId != null && groupId != null) {
            QueryRunner runner = new QueryRunner();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int update = 0;
            String personTypeCodesForListener = employeeConfig.getPersonTypeCodesSeparatedBySemicolonForListener();
            List<String> typeList = Arrays.asList(personTypeCodesForListener.split(";"));
            String checkSql = "select code from TSADV_DIC_PERSON_TYPE where id = '" + typeId.toString() + "';";
            String deleteSql = "delete " +
                    " from " +
                    " tsadv_security_person_list spl " +
                    " where " +
                    " spl.person_group_id = '" + groupId.toString() + "';";
            String insertSql = "insert " +
                    " into " +
                    "  tsadv_security_person_list( id, " +
                    "  \"version\", " +
                    "  create_ts, " +
                    "  transaction_date, " +
                    "  created_by, " +
                    "  security_group_id, " +
                    "  person_group_id ) select " +
                    "   newid(), " +
                    "   1, " +
                    "   to_date( '" + dateFormat.format(CommonUtils.getSystemDate()) + "', " +
                    "   'YYYY-MM-DD' ), " +
                    "   to_date( '" + dateFormat.format(CommonUtils.getSystemDate()) + "', " +
                    "   'YYYY-MM-DD' ), " +
                    "   'admin', " +
                    "   spt.security_group_id, " +
                    "   '" + groupId.toString() + "' " +
                    "  from " +
                    "   tsadv_security_person_type spt " +
                    "  where " +
                    "   spt.person_type_id = '" + typeId.toString() + "';";
            try {
                if (!typeList.isEmpty()) {
                    List<String> codes = runner.query(connection,checkSql, rs -> {
                        List<String> list = new ArrayList<>();
                        while (rs.next()) {
                            list.add(rs.getString("code"));
                        }
                        return list;
                    });
                    if (!codes.isEmpty() && typeList.contains(codes.get(0))) {
                        update = runner.update(connection, deleteSql);
                        update = runner.update(connection, insertSql);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createPerson(entity, connection);
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
    public void onAfterUpdate(PersonExt entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.updatePerson(entity, connection);
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
    public void onAfterDelete(PersonExt entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deletePerson(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }

    public void setFullNames(PersonExt person) {
        person.setFullNameCyrillic(person.calculateFullNameCyrillic());
        person.setFullNameNumberCyrillic(person.calculateFullNameNumberCyrillic());
    }


}