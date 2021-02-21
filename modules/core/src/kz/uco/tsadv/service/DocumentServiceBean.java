package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;

@Service(DocumentService.NAME)
public class DocumentServiceBean implements DocumentService {

    @Inject
    protected EmployeeService employeeService;
    @Inject
    private UserSession userSession;
    @Inject
    protected CommonService commonService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TimeSource timeSource;

    @Override
    public InsuredPerson getInsuredPerson(String type) {
        return RelativeType.EMPLOYEE.equals(RelativeType.fromId(type)) ? getInsuredPersonEmployee(type) : null;
    }

    private InsuredPerson getInsuredPersonEmployee(String type) {
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
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null);
    }


}