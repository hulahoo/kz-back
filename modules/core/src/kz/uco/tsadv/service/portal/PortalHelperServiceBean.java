package kz.uco.tsadv.service.portal;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.config.TsadvGlobalConfig;
import kz.uco.tsadv.service.EmployeeService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service(PortalHelperService.NAME)
public class PortalHelperServiceBean implements PortalHelperService {

    @Inject
    protected Metadata metadata;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TsadvGlobalConfig tsadvGlobalConfig;

    @Override
    public <T extends BaseGenericIdEntity<K>, K> T newEntity(String entityName) {
        @SuppressWarnings("unchecked") T entity = (T) metadata.create(entityName);
        entity.setId(null);
        return entity;
    }

    @Override
    public <T extends AbstractDictionary> List<T> loadDictionaries(String dictionaryName,
                                                                   UUID personGroupId) {
        DicCompany company = employeeService.getCompanyByPersonGroupId(personGroupId);
        MetaClass metaClass = metadata.getClassNN(dictionaryName);
        Class<T> javaClass = metaClass.getJavaClass();

        UUID generalCompanyId = tsadvGlobalConfig.getGeneralCompanyId();

        List<UUID> companyIds = new ArrayList<>();
        if (company != null)
            companyIds.add(company.getId());
        if (generalCompanyId != null)
            companyIds.add(generalCompanyId);

        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("companyIds", companyIds);

        String queryString = "select e from " + dictionaryName + " e where e.company.id in :companyIds ";

        return dataManager.load(javaClass)
                .query(queryString)
                .setParameters(queryParameters)
                .view(View.LOCAL)
                .list();
    }
}