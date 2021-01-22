package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.dictionary.DicVHIAttachmentStatus;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;

@UiController("tsadv$MyVHIInsuredPerson.browse")
@UiDescriptor("my-vhi-insured-person-browse.xml")
@LookupComponent("insuredPersonsTable")
@LoadDataBeforeShow
public class MyVhiInsuredPersonBrowse extends StandardLookup<InsuredPerson> {

    @Inject
    private UserSession userSession;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private DataManager dataManager;
    @Inject
    private CommonService commonService;
    @Inject
    private TimeSource timeSource;
    @Inject
    private GroupTable<InsuredPerson> insuredPersonsTable;
    @Inject
    private CollectionLoader<InsuredPerson> insuredPersonsDl;

    @Subscribe
    public void onInit(InitEvent event) {

        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null);

        insuredPersonsDl.setParameter("relativeType", RelativeType.EMPLOYEE);
        insuredPersonsDl.setParameter("employeeId", personGroupExt != null ? personGroupExt.getId() : null);
    }



    @Subscribe("insuredPersonsTable.joinVHI")
    public void onInsuredPersonsTableJoinVHI(Action.ActionPerformedEvent event) {
       joinMember(RelativeType.EMPLOYEE);
    }

    @Subscribe("insuredPersonsTable.joinFamilyMember")
    public void onInsuredPersonsTableJoinFamilyMember(Action.ActionPerformedEvent event) {
        joinMember(RelativeType.MEMBER);
    }

    public void joinMember(RelativeType type) {
        InsuredPerson item = dataManager.create(InsuredPerson.class);
        Date today = timeSource.currentTimestamp();
        item.setAttachDate(today);
        screenBuilders.editor(insuredPersonsTable)
                .newEntity(chekType(type, item))
                .build()
                .show();
    }

    public InsuredPerson chekType(RelativeType type, InsuredPerson insuredPerson){
        DicRelationshipType relationshipType = commonService.getEntity(DicRelationshipType.class, "PRIMARY");
        insuredPerson.setType(type);

        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null);

        DicCompany company = dataManager.load(DicCompany.class)
                .query("select o.company " +
                        "   from base$AssignmentExt a" +
                        " join a.assignmentStatus s " +
                        " join a.organizationGroup.list o " +
                        " where a.personGroup.id = :pg " +
                        "and current_date between a.startDate and a.endDate "+
                        "and a.primaryFlag = 'TRUE' " +
                        "and s.code in ('ACTIVE','SUSPENDED') " +
                        " and current_date between o.startDate and o.endDate")
                .parameter("pg", personGroupExt.getId()).view(View.LOCAL)
                .list().stream().findFirst().orElse(null);

        InsuranceContract contract = dataManager.load(InsuranceContract.class)
                .query("select e from tsadv$InsuranceContract e where e.company.id = :companyId ")
                .parameter("companyId", company.getId())
                .view("insuranceContract-editView")
                .list().stream().findFirst().orElse(null);

        PersonExt person = personGroupExt.getPerson();
        AssignmentExt assignment = personGroupExt.getCurrentAssignment();
        if (type == RelativeType.EMPLOYEE){
            insuredPerson.setStatusRequest(commonService.getEntity(DicVHIAttachmentStatus.class, "DRAFT"));
            if (contract != null){
                insuredPerson.setInsuranceContract(contract);
                insuredPerson.setInsuranceProgram(contract.getInsuranceProgram());
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

        }else if (type == RelativeType.MEMBER && insuredPersonsTable.getSingleSelected() != null){
            InsuredPerson singleSelected = insuredPersonsTable.getSingleSelected();
            isRelativeFamily(insuredPerson, singleSelected);
        }
        return insuredPerson;
    }

    public void isRelativeFamily(InsuredPerson person, InsuredPerson singleSelected){
        person.setEmployee(singleSelected.getEmployee());
        person.setIin(singleSelected.getIin());
        person.setSex(singleSelected.getSex());
        person.setBirthdate(singleSelected.getBirthdate());
        person.setRelative(singleSelected.getRelative());
        person.setDocumentType(singleSelected.getDocumentType());
        person.setDocumentNumber(singleSelected.getDocumentNumber());
        person.setAddressType(singleSelected.getAddressType());
        person.setAddress(singleSelected.getAddress());
        person.setCompany(singleSelected.getCompany());
        person.setJob(singleSelected.getJob());
        person.setInsuranceContract(singleSelected.getInsuranceContract());
        person.setAttachDate(singleSelected.getAttachDate());
        person.setStatusRequest(commonService.getEntity(DicVHIAttachmentStatus.class, "DRAFT"));
        person.setInsuranceProgram(singleSelected.getInsuranceProgram());
        person.setDocumentNumber(singleSelected.getDocumentNumber());
        person.setRegion(singleSelected.getRegion());

    }
}