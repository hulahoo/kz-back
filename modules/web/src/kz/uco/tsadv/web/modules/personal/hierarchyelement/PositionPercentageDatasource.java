package kz.uco.tsadv.web.modules.personal.hierarchyelement;

//import com.haulmont.bpm.web.App;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.model.PositionPercentage;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author Adilbekov Yernar
 *         for tab 'By Person' in position-person-competence.xml
 */
public class PositionPercentageDatasource extends CustomCollectionDatasource<PositionPercentage, UUID> {

    private EmployeeService employeeService = AppBeans.get(EmployeeService.NAME);

    @Override
    protected Collection<PositionPercentage> getEntities(Map<String, Object> params) {
        if (params.containsKey(StaticVariable.USER_PERSON_GROUP_ID)) {
            String personGroupId = String.valueOf(params.get(StaticVariable.USER_PERSON_GROUP_ID));

            String language = ((UserSessionSource) AppBeans.get(UserSessionSource.NAME)).getLocale().getLanguage();
            return employeeService.getPositionByPersonCompetence(params, language);
        }
        return Collections.emptyList();
    }
}
