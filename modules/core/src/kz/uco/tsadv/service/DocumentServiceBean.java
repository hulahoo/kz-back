package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.*;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service(DocumentService.NAME)
public class DocumentServiceBean implements DocumentService {

    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected CommonService commonService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TimeSource timeSource;

    @Override
    public List<InsuredPerson> getMyInsuraces() {
        return dataManager.load(InsuredPerson.class)
                .query("select e from tsadv$InsuredPerson e where e.type = :type and e.employee.id = :pgId")
                .parameter("type",RelativeType.EMPLOYEE.getId())
                .parameter("pgId",getPersonGroupExt().getId())
                .view("insuredPerson-browseView")
                .list();
    }

    @Override
    public InsuredPerson getInsuredPerson(String type) {
        return RelativeType.EMPLOYEE.equals(RelativeType.fromId(type)) ? getInsuredPersonEmployee() : getInsuredPersonMember();
    }

    private InsuredPerson getInsuredPersonMember() {
        InsuredPerson insuredPerson = dataManager.create(InsuredPerson.class);
        Date today = timeSource.currentTimestamp();
        insuredPerson.setAttachDate(today);
        insuredPerson.setType(RelativeType.MEMBER);
        PersonGroupExt personGroupExt = getPersonGroupExt();

        DicCompany company = personGroupExt.getCurrentAssignment().getOrganizationGroup().getCompany();

        InsuranceContract contract = dataManager.load(InsuranceContract.class)
                .query("select e from tsadv$InsuranceContract e where e.company.id = :companyId ")
                .parameter("companyId", company.getId())
                .view("insuranceContract-editView")
                .list().stream().findFirst().orElse(null);
        insuredPerson.setStatusRequest(commonService.getEntity(DicMICAttachmentStatus.class, "DRAFT"));
        if (contract != null) {
            insuredPerson.setInsuranceContract(contract);
            insuredPerson.setInsuranceProgram(contract.getInsuranceProgram());
        }

        boolean isEmptyDocument = personGroupExt.getPersonDocuments().isEmpty();
        if (isEmptyDocument) {
            insuredPerson.setDocumentType(contract.getDefaultDocumentType());
        } else {
            boolean isSetDocument = false;
            for (PersonDocument document : personGroupExt.getPersonDocuments()) {
                if (document.getDocumentType().getId().equals(contract.getDefaultDocumentType().getId())) {
                    insuredPerson.setDocumentType(document.getDocumentType());
                    insuredPerson.setDocumentNumber(document.getDocumentNumber());
                    isEmptyDocument = true;
                    break;
                }
            }
            if (!isEmptyDocument) {
                insuredPerson.setDocumentType(personGroupExt.getPersonDocuments().get(0).getDocumentType());
                insuredPerson.setDocumentNumber(personGroupExt.getPersonDocuments().get(0).getDocumentNumber());
            }
        }

        if (!personGroupExt.getAddresses().isEmpty()) {
            boolean isSetAddress = false;
            for (Address a : personGroupExt.getAddresses()) {
                if (a.getAddressType().getId().equals(contract.getDefaultAddress().getId())) {
                    insuredPerson.setAddressType(a);
                    isSetAddress = true;
                    break;
                }
            }
            if (!isSetAddress) {
                insuredPerson.setAddressType(personGroupExt.getAddresses().get(0));
            }
        }
        insuredPerson.setEmployee(personGroupExt);
        insuredPerson.setCompany(company);
        insuredPerson.setTotalAmount(new BigDecimal(0));
        insuredPerson.setAmount(new BigDecimal(0));

        return insuredPerson;
    }

    @Override
    public List<InsuredPerson> getInsuredPersonMembers(UUID insuredPersonId) {
        FluentLoader<InsuredPerson, UUID> fluentLoader = dataManager.load(InsuredPerson.class).view("insuredPerson-browseView");
        InsuredPerson insuredPerson = fluentLoader.id(insuredPersonId).one();
        return fluentLoader
                .query("select e from tsadv$InsuredPerson e where  e.insuranceContract.id =:employeeContractId and e.employee.id =:employeeId and e.type = :relativeType")
                .parameter("employeeContractId", insuredPerson.getInsuranceContract().getId())
                .parameter("employeeId", insuredPerson.getEmployee().getId())
                .parameter("relativeType", RelativeType.MEMBER)
                .list();
    }

    @Override
    public Boolean checkPersonInsure(UUID personGroupId, UUID contractId) {
        InsuredPerson person = dataManager.load(InsuredPerson.class).query("select e " +
                "from tsadv$InsuredPerson e " +
                "where e.insuranceContract.id = :contractId " +
                " and e.employee.id = :employeeId " +
                " and e.relative.code = 'PRIMARY' ")
                .parameter("contractId", contractId)
                .parameter("employeeId", personGroupId)
                .view("insuredPerson-editView")
                .list().stream().findFirst().orElse(null);

        return person != null;
    }

    @Override
    public BigDecimal calcAmount(UUID insuranceContractId,
                                 UUID personGroupExtId,
                                 Date bith,
                                 UUID relativeTypeId) {
        InsuranceContract insuranceContract = dataManager.load(InsuranceContract.class).id(insuranceContractId).view("insuranceContract-editView").one();
        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).id(personGroupExtId).view("personGroupExt-view").one();
        DicRelationshipType relativeType = dataManager.load(DicRelationshipType.class).id(relativeTypeId).view(View.LOCAL).one();
        List<ContractConditions> conditionsList = new ArrayList<>();

        int age = employeeService.calculateAge(bith);
        List<InsuredPerson> personList = dataManager.load(InsuredPerson.class).query("select e " +
                "from tsadv$InsuredPerson e " +
                " where e.insuranceContract.id = :insuranceContractId" +
                " and e.employee.id = :employeeId " +
                " and e.amount = :amount " +
                " and e.relative.code <> 'PRIMARY'")
                .parameter("insuranceContractId", insuranceContract.getId())
                .parameter("employeeId", personGroupExt.getId())
                .parameter("amount", BigDecimal.valueOf(0))
                .view("insuredPerson-editView")
                .list();

        for (ContractConditions condition : insuranceContract.getProgramConditions()) {
            if (condition.getRelationshipType().getId().equals(relativeType.getId()) ) {
                if (condition.getAgeMin() <= age && condition.getAgeMax() >= age) {
                    conditionsList.add(condition);
                }
            }
        }

        if (conditionsList.size() > 1) {
            if (insuranceContract.getCountOfFreeMembers() > personList.size()){
                return BigDecimal.ZERO;
            }else {
                for (ContractConditions condition : conditionsList){
                    if (!condition.getIsFree()){
                        return condition.getCostInKzt();
                    }
                }
            }
        } else {
            for (ContractConditions condition : conditionsList) {
                if (!condition.getIsFree()) {
                    return condition.getCostInKzt();
                }
            }
        }

        return BigDecimal.ZERO;
    }

    @Override
    public List<ScheduleOffsetsRequest> getOffsetRequestsByPgId(UUID personGroupExtId) {
        return dataManager.load(ScheduleOffsetsRequest.class)
                .query("select e from tsadv_ScheduleOffsetsRequest e where e.personGroup.id = :pgId")
                .parameter("pgId",personGroupExtId)
                .view("scheduleOffsetsRequest-for-my-team")
                .list();
    }

    private InsuredPerson getInsuredPersonEmployee() {
        InsuredPerson insuredPerson = dataManager.create(InsuredPerson.class);
        DicRelationshipType relationshipType = commonService.getEntity(DicRelationshipType.class, "PRIMARY");
        Date today = timeSource.currentTimestamp();
        insuredPerson.setAttachDate(today);
        insuredPerson.setType(RelativeType.EMPLOYEE);
        PersonGroupExt personGroupExt = getPersonGroupExt();


        DicCompany company = personGroupExt.getCurrentAssignment().getOrganizationGroup().getCompany();

        InsuranceContract contract = dataManager.load(InsuranceContract.class)
                .query("select e from tsadv$InsuranceContract e where e.company.id = :companyId ")
                .parameter("companyId", company.getId())
                .view("insuranceContract-editView")
                .list().stream().findFirst().orElse(null);

        PersonExt person = personGroupExt.getPerson();
        AssignmentExt assignment = personGroupExt.getCurrentAssignment();

        insuredPerson.setStatusRequest(commonService.getEntity(DicMICAttachmentStatus.class, "DRAFT"));
        if (contract != null) {
            insuredPerson.setInsuranceContract(contract);
            insuredPerson.setInsuranceProgram(contract.getInsuranceProgram());
        }

        Address address = dataManager.load(Address.class).query("select e " +
                "from tsadv$Address e " +
                "where e.personGroup.id = :personGroupId " +
                " and current_date between e.startDate and e.endDate")
                .parameter("personGroupId", personGroupExt.getId())
                .view("address.view")
                .list().stream().findFirst().orElse(null);

        boolean isEmptyDocument = personGroupExt.getPersonDocuments().isEmpty();
        if (isEmptyDocument) {
            insuredPerson.setDocumentType(contract.getDefaultDocumentType());
        } else {
            boolean isSetDocument = false;
            for (PersonDocument document : personGroupExt.getPersonDocuments()) {
                if (document.getDocumentType().getId().equals(contract.getDefaultDocumentType().getId())) {
                    insuredPerson.setDocumentType(document.getDocumentType());
                    insuredPerson.setDocumentNumber(document.getDocumentNumber());
                    isEmptyDocument = true;
                    break;
                }
            }
            if (!isEmptyDocument) {
                insuredPerson.setDocumentType(personGroupExt.getPersonDocuments().get(0).getDocumentType());
                insuredPerson.setDocumentNumber(personGroupExt.getPersonDocuments().get(0).getDocumentNumber());
            }
        }

        if (!personGroupExt.getAddresses().isEmpty()) {
            boolean isSetAddress = false;
            for (Address a : personGroupExt.getAddresses()) {
                if (a.getAddressType().getId().equals(contract.getDefaultAddress().getId())) {
                    insuredPerson.setAddressType(a);
                    isSetAddress = true;
                    break;
                }
            }
            if (!isSetAddress) {
                insuredPerson.setAddressType(personGroupExt.getAddresses().get(0));
            }
        }
        insuredPerson.setEmployee(personGroupExt);
        insuredPerson.setFirstName(person.getFirstName());
        insuredPerson.setSecondName(person.getLastName());
        insuredPerson.setMiddleName(person.getMiddleName());
        insuredPerson.setIin(person.getNationalIdentifier());
        insuredPerson.setBirthdate(person.getDateOfBirth());
        insuredPerson.setSex(person.getSex());
        insuredPerson.setRelative(relationshipType);
        insuredPerson.setCompany(company);
        insuredPerson.setJob(assignment.getJobGroup());
        insuredPerson.setTotalAmount(new BigDecimal(0));

        return insuredPerson;
    }

    private PersonGroupExt getPersonGroupExt() {
        return dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSessionSource.getUserSession().getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null);
    }


}