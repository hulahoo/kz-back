package kz.uco.tsadv.web.modules.personal.hierarchyelement;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.model.PersonPercentage;
import kz.uco.tsadv.service.EmployeeService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author Adilbekov Yernar
 *         for tab 'By Position' in position-person-competence.xml
 */
public class PersonPercentageDatasource extends CustomCollectionDatasource<PersonPercentage, UUID> {

    private EmployeeService employeeService = AppBeans.get(EmployeeService.NAME);

    @Override
    protected Collection<PersonPercentage> getEntities(Map<String, Object> params) {
        if (params.containsKey(StaticVariable.POSITION_GROUP_ID)) {
            String positionGroupId = String.valueOf(params.get(StaticVariable.POSITION_GROUP_ID));

            return employeeService.getPersonByPositionCompetence(params);
        }
        return Collections.emptyList();
    }
}