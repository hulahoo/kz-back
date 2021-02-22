package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.tsadv.modules.personal.model.ContractConditions;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.service.EmployeeService;
import org.docx4j.wml.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("tsadv_InsuredPersonChangedListener")
public class InsuredPersonChangedListener {

    @Inject
    private TransactionalDataManager txDataManager;
    @Inject
    private DataManager dataManager;
    @Inject
    private EmployeeService employeeService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<InsuredPerson, UUID> event) {



        if (event.getType().equals(EntityChangedEvent.Type.DELETED)){
            Id<InsuredPerson, UUID> entityId = event.getEntityId();
            InsuredPerson person = dataManager.load(entityId).view("insuredPerson-editView").one();

            if (!person.getRelative().getCode().equals("PRIMARY")){
                List<InsuredPerson> personList = dataManager.load(InsuredPerson.class).query("select e " +
                        "from tsadv$InsuredPerson e " +
                        " where e.insuranceContract.id = :insuranceContractId" +
                        " and e.employee.id = :employeeId " +
                        " and e.relative.code <> 'PRIMARY' " +
                        " ORDER BY e.attachDate")
                        .parameter("insuranceContractId", person.getInsuranceContract().getId())
                        .parameter("employeeId", person.getEmployee().getId())
                        .view("insuredPerson-editView")
                        .list();

                InsuredPerson employee = dataManager.load(InsuredPerson.class).query("select e " +
                        "from tsadv$InsuredPerson e " +
                        " where e.insuranceContract.id = :insuranceContractId" +
                        " and e.employee.id = :employeeId " +
                        " and e.relative.code = 'PRIMARY'")
                        .parameter("insuranceContractId", person.getInsuranceContract().getId())
                        .parameter("employeeId", person.getEmployee().getId())
                        .view("insuredPerson-editView")
                        .list().stream().findFirst().orElse(null);

                if (employee != null) {
                    if (person.getAmount().signum() > 0){
                        BigDecimal temp = employee.getTotalAmount();
                        employee.setTotalAmount(temp.subtract(person.getAmount()));
                        txDataManager.save(employee);
                    }
                    else {
                        for (InsuredPerson insuredPerson : personList) {
                            int age = employeeService.calculateAge(insuredPerson.getBirthdate());
                            for (ContractConditions conditions : employee.getInsuranceContract().getProgramConditions()) {
                                if (insuredPerson.getAmount().signum() > 0 && !insuredPerson.getId().equals(person.getId())
                                        && conditions.getIsFree() && conditions.getAgeMin() <= age && conditions.getAgeMax() >= age){
                                    BigDecimal minusAmount = insuredPerson.getAmount();
                                    BigDecimal temp = employee.getTotalAmount();

                                    insuredPerson.setAmount(BigDecimal.valueOf(0));
                                    txDataManager.save(insuredPerson);
                                    employee.setTotalAmount(temp.subtract(minusAmount));
                                    txDataManager.save(employee);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {

            Id<InsuredPerson, UUID> entityId = event.getEntityId();
            InsuredPerson person = txDataManager.load(entityId).view("insuredPerson-editView").one();

            InsuredPerson employee = dataManager.load(InsuredPerson.class).query("select e " +
                    "from tsadv$InsuredPerson e " +
                    " where e.insuranceContract.id = :insuranceContractId" +
                    " and e.employee.id = :employeeId " +
                    " and e.relative.code = 'PRIMARY'")
                    .parameter("insuranceContractId", person.getInsuranceContract().getId())
                    .parameter("employeeId", person.getEmployee().getId())
                    .view("insuredPerson-editView")
                    .list().stream().findFirst().orElse(null);


            if (employee != null && person.getAmount() != null && person.getAmount().signum() > 0) {
                employee.setTotalAmount(employee.getTotalAmount().add(person.getAmount()));
                txDataManager.save(employee);
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            if (event.getChanges().isChanged("amount") && (event.getChanges().isChanged("birthdate") || event.getChanges().isChanged("relative"))) {
                Id<InsuredPerson, UUID> entityId = event.getEntityId();
                InsuredPerson person = txDataManager.load(entityId).view("insuredPerson-editView").one();
                BigDecimal oldVal = event.getChanges().getOldValue("amount");

                List<InsuredPerson> personList = dataManager.load(InsuredPerson.class).query("select e " +
                        "from tsadv$InsuredPerson e " +
                        " where e.insuranceContract.id = :insuranceContractId" +
                        " and e.employee.id = :employeeId " +
                        " and e.relative.code <> 'PRIMARY' " +
                        " ORDER BY e.attachDate")
                        .parameter("insuranceContractId", person.getInsuranceContract().getId())
                        .parameter("employeeId", person.getEmployee().getId())
                        .view("insuredPerson-editView")
                        .list();

                InsuredPerson employee = dataManager.load(InsuredPerson.class).query("select e " +
                        "from tsadv$InsuredPerson e " +
                        " where e.insuranceContract.id = :insuranceContractId" +
                        " and e.employee.id = :employeeId " +
                        " and e.relative.code = 'PRIMARY'")
                        .parameter("insuranceContractId", person.getInsuranceContract().getId())
                        .parameter("employeeId", person.getEmployee().getId())
                        .view("insuredPerson-editView")
                        .list().stream().findFirst().orElse(null);

                if (oldVal != null && oldVal.signum() == 0){
                    boolean changed = true;
                    for (InsuredPerson insuredPerson : personList){
                        int age = employeeService.calculateAge(insuredPerson.getBirthdate());
                        if (changed && !insuredPerson.getId().equals(person.getId()) && insuredPerson.getAmount().signum() > 0){
                            for (ContractConditions conditions : employee.getInsuranceContract().getProgramConditions()) {
                                if (changed && conditions.getIsFree() && conditions.getAgeMin() <= age && conditions.getAgeMax() >= age){
                                    BigDecimal minusAmount = insuredPerson.getAmount();

                                    insuredPerson.setAmount(BigDecimal.valueOf(0));
                                    txDataManager.save(insuredPerson);
                                    employee.setTotalAmount((employee.getTotalAmount().subtract(minusAmount)).add(person.getAmount()));
                                    txDataManager.save(employee);
                                    changed = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        }
    }

}