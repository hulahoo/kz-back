package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.base.cuba.actions.EditActionExt;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.web.screens.insurancecontract.InsuranceContractBrowse;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@UiController("tsadv$MyMICInsuredPerson.browse")
@UiDescriptor("my-mic-insured-person-browse.xml")
@LookupComponent("insuredPersonsTable")
@LoadDataBeforeShow
public class MyMICInsuredPersonBrowse extends StandardLookup<InsuredPerson> {

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
    private DataGrid<InsuredPerson> insuredPersonsTable;
    @Inject
    private CollectionLoader<InsuredPerson> insuredPersonsDl;
    @Named("insuredPersonsTable.joinMIC")
    private CreateActionExt insuredPersonsTableJoinMIC;
    @Inject
    private UiComponents uiComponents;
    @Named("insuredPersonsTable.joinFamilyMember")
    private EditActionExt insuredPersonsTableJoinFamilyMember;

    @Subscribe
    public void onInit(InitEvent event) {

        DataGrid.Column column = insuredPersonsTable.addGeneratedColumn("code", new DataGrid.ColumnGenerator<InsuredPerson, LinkButton>(){
            @Override
            public LinkButton getValue(DataGrid.ColumnGeneratorEvent<InsuredPerson> event){
                LinkButton linkButton = uiComponents.create(LinkButton.class);
                linkButton.setCaption(event.getItem().getIin());
                linkButton.setAction(new BaseAction("code").withHandler(e->{
                    InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                            .editEntity(event.getItem())
                            .build();
                    editorBuilder.setParameter("editHr");
                    editorBuilder.show();
                }));
                return linkButton;
            }

            @Override
            public Class<LinkButton> getType(){
                return LinkButton.class;
            }

        }, 1);
        column.setRenderer(insuredPersonsTable.createRenderer(DataGrid.ComponentRenderer.class));

        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null);

        insuredPersonsDl.setParameter("relativeType", RelativeType.EMPLOYEE);
        insuredPersonsDl.setParameter("employeeId", personGroupExt != null ? personGroupExt.getId() : null);

        DicCompany dicCompany = personGroupExt.getCurrentAssignment().getOrganizationGroup().getOrganization().getCompany();

        if (dicCompany != null){
            InsuranceContract contract = dataManager.load(InsuranceContract.class)
                    .query("select e from tsadv$InsuranceContract e where e.company.id = :companyId" +
                            " and current_date between e.availabilityPeriodFrom and e.availabilityPeriodTo")
                    .parameter("companyId", dicCompany.getId())
                    .view("insuranceContract-browseView")
                    .list().stream().findFirst().orElse(null);

            long assignedDay = TimeUnit.DAYS.convert(
                    Math.abs(timeSource.currentTimestamp().getTime() - personGroupExt.getCurrentAssignment().getAssignDate().getTime()),
                    TimeUnit.MILLISECONDS);
            if (contract != null && assignedDay > contract.getAttachingAnEmployee()){
                insuredPersonsTableJoinMIC.setEnabled(true);
            }else {
                insuredPersonsTableJoinMIC.setEnabled(false);
            }
            insuredPersonsTableJoinMIC.setEnabled(contract != null);
        }else {
            insuredPersonsTableJoinMIC.setEnabled(false);
        }
    }


    @Subscribe("insuredPersonsTable.joinMIC")
    public void onInsuredPersonsTableJoinMIC(Action.ActionPerformedEvent event) {
       joinMember("joinEmployee");
    }

    @Subscribe(id = "insuredPersonsDc", target = Target.DATA_CONTAINER)
    public void onInsuredPersonsDcItemChange(InstanceContainer.ItemChangeEvent<InsuredPerson> event) {
        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null);

        long assignedDay = TimeUnit.DAYS.convert(
                Math.abs(timeSource.currentTimestamp().getTime() - personGroupExt.getCurrentAssignment().getAssignDate().getTime()),
                TimeUnit.MILLISECONDS);

        if (event.getItem() != null && assignedDay < 45){
            insuredPersonsTableJoinFamilyMember.setEnabled(false);
        }
    }


    public void joinMember(String whichButton) {
        InsuredPerson item = dataManager.create(InsuredPerson.class);
        InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                .newEntity(chekType(item, whichButton))
                .build();

        editorBuilder.setParameter(whichButton);
        editorBuilder.show();
    }

    public InsuredPerson chekType(InsuredPerson insuredPerson, String whichButton){
        DicRelationshipType relationshipType = commonService.getEntity(DicRelationshipType.class, "PRIMARY");
        Date today = timeSource.currentTimestamp();
        insuredPerson.setAttachDate(today);
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
        if (whichButton.equals("joinEmployee")){
            insuredPerson.setStatusRequest(commonService.getEntity(DicMICAttachmentStatus.class, "DRAFT"));
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

        }else if (whichButton.equals("joinMember")){
            InsuredPerson singleSelected = insuredPersonsTable.getSingleSelected();
            assert singleSelected != null;
            isRelativeFamily(insuredPerson, singleSelected);
        }
        return insuredPerson;
    }

    public void isRelativeFamily(InsuredPerson person, InsuredPerson singleSelected) {
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
        person.setType(singleSelected.getType());
        person.setAttachDate(singleSelected.getAttachDate());
        person.setStatusRequest(commonService.getEntity(DicMICAttachmentStatus.class, "DRAFT"));
        person.setInsuranceProgram(singleSelected.getInsuranceProgram());
        person.setDocumentNumber(singleSelected.getDocumentNumber());
        person.setRegion(singleSelected.getRegion());
    }

}