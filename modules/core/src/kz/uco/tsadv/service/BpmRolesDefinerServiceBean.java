package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(BpmRolesDefinerService.NAME)
public class BpmRolesDefinerServiceBean implements BpmRolesDefinerService {

    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected DataManager dataManager;

    @Override
    public BpmRolesDefiner getBpmRolesDefiner(String processDefinitionKey, UUID employeePersonGroupId) {
        DicCompany dicCompany = employeeService.getCompanyByPersonGroupId(employeePersonGroupId);
        UUID company = dicCompany != null ? dicCompany.getId() : null;

        BpmRolesDefiner bpmRolesDefiner = null;

        @SuppressWarnings("ConstantConditions") List<BpmRolesDefiner> bpmRolesDefiners = dataManager.load(BpmRolesDefiner.class)
                .query("select e from tsadv$BpmRolesDefiner e where e.company.id = :companyId and e.processDefinitionKey = :processDefinitionKey ")
                .parameter("companyId", company)
                .parameter("processDefinitionKey", processDefinitionKey)
                .view("bpmRolesDefiner-view")
                .list();
        if (!bpmRolesDefiners.isEmpty()) bpmRolesDefiner = bpmRolesDefiners.get(0);

        if (bpmRolesDefiner == null)
            bpmRolesDefiners = dataManager.load(BpmRolesDefiner.class)
                    .query("select e from tsadv$BpmRolesDefiner e " +
                            "   where e.processDefinitionKey = :processDefinitionKey" +
                            "       and e.company is null ")
                    .parameter("processDefinitionKey", processDefinitionKey)
                    .view("bpmRolesDefiner-view")
                    .list();

        if (!bpmRolesDefiners.isEmpty()) bpmRolesDefiner = bpmRolesDefiners.get(0);
        if (bpmRolesDefiner == null) throw new PortalException("bpmRolesDefiner not found!");
        return bpmRolesDefiner;
    }
}