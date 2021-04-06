package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.config.TsadvGlobalConfig;
import kz.uco.tsadv.service.EmployeeService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public List<UUID> getCompaniesForLoadDictionary(UUID personGroupId) {
        DicCompany company = employeeService.getCompanyByPersonGroupId(personGroupId);
        UUID generalCompanyId = tsadvGlobalConfig.getGeneralCompanyId();

        List<UUID> companyIds = new ArrayList<>();
        if (company != null)
            companyIds.add(company.getId());
        if (generalCompanyId != null)
            companyIds.add
                    (generalCompanyId);
        return companyIds;
    }
}