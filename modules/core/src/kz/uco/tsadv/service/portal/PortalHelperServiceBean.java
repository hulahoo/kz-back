package kz.uco.tsadv.service.portal;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.entity.dictionary.DicCompany;
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

    @Override
    public <T extends BaseGenericIdEntity<K>, K> T newEntity(String entityName) {
        @SuppressWarnings("unchecked") T entity = (T) metadata.create(entityName);
        entity.setId(null);
        return entity;
    }

    //todo
    @Override
    public <T extends AbstractDictionary> List<T> loadDictionary(String dictionaryName, UUID personGroupId) {
        DicCompany company = employeeService.getCompanyByPersonGroupId(personGroupId);
        MetaClass metaClass = metadata.getClassNN(dictionaryName);
        Class<T> javaClass = metaClass.getJavaClass();

        List<UUID> companyIds = new ArrayList<>();
        if (company != null)
            companyIds.add(company.getId());

        return dataManager.load(javaClass)
                .query(String.format("select e from %s e where e.company.id in :companyIds", metaClass.getName()))
                .parameter("companyIds", companyIds)
                .view(View.LOCAL)
                .list();
    }
}