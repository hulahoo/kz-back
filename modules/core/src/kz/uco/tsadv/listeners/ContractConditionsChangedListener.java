package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.tsadv.modules.personal.model.ContractConditions;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.service.EmployeeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component("tsadv_ContractConditionsChangedListener")
public class ContractConditionsChangedListener {

    @Inject
    private DataManager dataManager;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private TransactionalDataManager txDataManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<ContractConditions, UUID> event) {

        if (event.getType().equals(EntityChangedEvent.Type.UPDATED)){
            Id<ContractConditions, UUID> entityId = event.getEntityId();

            ContractConditions contractConditions = dataManager.load(entityId).view("contractConditions-editView").one();

            if (event.getChanges().isChanged("costInKzt")
                    && !event.getChanges().isChanged("ageMax")
                    && !event.getChanges().isChanged("ageMin")){
                BigDecimal oldValue = event.getChanges().getOldValue("costInKzt");
                List<InsuredPerson> personList = dataManager.load(InsuredPerson.class).query("select e " +
                        "from tsadv$InsuredPerson e " +
                        " where e.relative.id = :relativeId " +
                        " and e.insuranceContract.id = :insuranceContractId")
                        .parameter("insuranceContractId", contractConditions.getInsuranceContract().getId())
                        .parameter("relativeId", contractConditions.getRelationshipType().getId())
                        .view("insuredPerson-editView")
                        .list();



                for (InsuredPerson person : personList){
                    InsuredPerson employee = dataManager.load(InsuredPerson.class).query("select e " +
                            "from tsadv$InsuredPerson e " +
                            " where e.insuranceContract.id = :insuranceContractId" +
                            " and e.employee.id = :employeeId " +
                            " and e.relative.code = 'PRIMARY'")
                            .parameter("insuranceContractId", person.getInsuranceContract().getId())
                            .parameter("employeeId", person.getEmployee().getId())
                            .view("insuredPerson-editView")
                            .list().stream().findFirst().orElse(null);

                    int age = employeeService.calculateAge(person.getBirthdate());
                    if (contractConditions.getAgeMin() <= age
                            && contractConditions.getAgeMax() >= age
                            && person.getAmount().signum() > 0){
                        person.setAmount(contractConditions.getCostInKzt());
                        employee.setTotalAmount((employee.getTotalAmount().subtract(oldValue).add(person.getAmount())));
                        dataManager.commit(person);
                    }
                }
            }

        }
    }
}