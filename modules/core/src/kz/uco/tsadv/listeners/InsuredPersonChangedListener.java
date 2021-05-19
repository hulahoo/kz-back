package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.ContractConditions;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.service.DocumentService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("tsadv_InsuredPersonChangedListener")
public class InsuredPersonChangedListener {

    @Inject
    protected Resources resources;
    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected Metadata metadata;
    @Inject
    private TransactionalDataManager txDataManager;
    @Inject
    private DataManager dataManager;
    @Inject
    private EmployeeService employeeService;
    protected SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    @Inject
    protected NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    protected DocumentService documentService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<InsuredPerson, UUID> event) {

        if (event.getType().equals(EntityChangedEvent.Type.DELETED)) {
            Id<InsuredPerson, UUID> entityId = event.getEntityId();
            InsuredPerson person = dataManager.load(entityId).view("insuredPerson-editView").one();

            if (!person.getRelative().getCode().equals("PRIMARY")) {
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
                    if (person.getAmount().signum() > 0) {
                        BigDecimal temp = employee.getTotalAmount();
                        employee.setTotalAmount(temp.subtract(person.getAmount()));
                        txDataManager.save(employee);
                    } else {
                        for (InsuredPerson insuredPerson : personList) {
                            int age = employeeService.calculateAge(insuredPerson.getBirthdate());
                            for (ContractConditions conditions : employee.getInsuranceContract().getProgramConditions()) {
                                if (insuredPerson.getAmount().signum() > 0 && !insuredPerson.getId().equals(person.getId())
                                        && conditions.getIsFree() && conditions.getAgeMin() <= age && conditions.getAgeMax() >= age) {
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

            if ("DRAFT".equals(person.getStatusRequest().getCode())) {
                Map<String, Object> params = new HashMap<>();
                params.put("mainFullName", person.getEmployee().getFioWithEmployeeNumber());
                params.put("job", person.getEmployee().getCurrentAssignment() != null
                        && person.getEmployee().getCurrentAssignment().getJobGroup() != null
                        ? person.getEmployee().getCurrentAssignment().getJobGroup().getJobName()
                        : "");
                params.put("organization", person.getEmployee().getCurrentAssignment() != null
                        && person.getEmployee().getCurrentAssignment().getOrganizationGroup() != null
                        && person.getEmployee().getCurrentAssignment().getOrganizationGroup().getOrganization() != null
                        ? person.getEmployee().getCurrentAssignment().getOrganizationGroup().getOrganization().getOrganizationName()
                        : "");

                params.put("contractNum", person.getInsuranceContract().getContract());
                params.put("attachDate", person.getAttachDate() != null
                        ? format.format(person.getAttachDate())
                        : "");
                params.put("linkRu", "<a href=\"" + globalConfig.getWebAppUrl() + "\">ссылке</a>");
                params.put("tableRu", !"PRIMARY".equals(person.getRelative().getCode())
                        ? getTable(person)
                        : "");
                List<PersonGroupExt> personGroupExtList = dataManager.load(PersonGroupExt.class)
                        .query("select e.employee from tsadv$InsuranceContractAdministrator e " +
                                " where e.insuranceContract = :insuranceContract " +
                                " and e.notifyAboutNewAttachments = 'TRUE'")
                        .parameter("insuranceContract", person.getInsuranceContract())
                        .list();
                List<TsadvUser> tsadvUsers = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e " +
                                " where e.personGroup in :personGroupList ")
                        .parameter("personGroupList", personGroupExtList)
                        .view("")
                        .list();
                ActivityType activityType = dataManager.load(ActivityType.class)
                        .query("select e from uactivity$ActivityType e where e.code = :code")
                        .parameter("code", "NOTIFICATION")
                        .view(new View(ActivityType.class)
                                .addProperty("code").addProperty("windowProperty"))
                        .one();
                tsadvUsers.forEach(tsadvUser -> {
                    notificationSenderAPIService.sendParametrizedNotification("insurance.person.dms",
                            tsadvUser, params);
                    activityService.createActivity(tsadvUser, tsadvUser, activityType, StatusEnum.active,
                            "description", null, new Date(), null, null,
                            person.getId(), "insurance.person.dms", params);
                });
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {

            Id<InsuredPerson, UUID> entityId = event.getEntityId();
            InsuredPerson person = txDataManager.load(entityId).view("insuredPerson-editView").one();

            if (event.getChanges().isChanged("insuranceContract")) {
                documentService.getInsuredPersonMembersWithNewContract(entityId.getValue(), person.getInsuranceContract().getId())
                        .forEach(txDataManager::save);
            }

            if (event.getChanges().isChanged("amount") && (event.getChanges().isChanged("birthdate") || event.getChanges().isChanged("relative"))) {
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

                if (oldVal != null && oldVal.signum() == 0) {
                    boolean changed = true;
                    for (InsuredPerson insuredPerson : personList) {
                        int age = employeeService.calculateAge(insuredPerson.getBirthdate());
                        if (changed && !insuredPerson.getId().equals(person.getId()) && insuredPerson.getAmount().signum() > 0) {
                            for (ContractConditions conditions : employee.getInsuranceContract().getProgramConditions()) {
                                if (conditions.getIsFree() && conditions.getAgeMin() <= age && conditions.getAgeMax() >= age) {
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

    protected String getTable(InsuredPerson insuredPerson) {
        return "<table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:500px\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td>Родственник</td>\n" +
                "        <td>ФИО</td>\n" +
                "        <td>ИИН</td>\n" +
                "        <td>Дата прикрепления</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>" +
                insuredPerson.getRelative().getLangValue() +
                "        </td>\n" +
                "        <td>" +
                insuredPerson.getFirstName() + " "
                + insuredPerson.getSecondName() + " " + insuredPerson.getMiddleName() +
                "        </td>\n" +
                "        <td>" +
                insuredPerson.getIin() +
                "         </td>\n" +
                "        <td>" +
                format.format(insuredPerson.getAttachDate()) +
                "        </td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>";
    }

}