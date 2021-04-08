package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.base.cuba.actions.EditActionExt;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;

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

        DataGrid.Column column = insuredPersonsTable.addGeneratedColumn("contractField", new DataGrid.ColumnGenerator<InsuredPerson, LinkButton>() {
            @Override
            public LinkButton getValue(DataGrid.ColumnGeneratorEvent<InsuredPerson> event) {
                LinkButton linkButton = uiComponents.create(LinkButton.class);
                linkButton.setCaption(event.getItem().getInsuranceContract().getContract());
                linkButton.setAction(new BaseAction("contractField").withHandler(e -> {
                    InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                            .editEntity(event.getItem())
                            .build();
                    editorBuilder.setParameter("openEmployee");
                    editorBuilder.show();
                }));
                return linkButton;
            }

            @Override
            public Class<LinkButton> getType() {
                return LinkButton.class;
            }

        }, 0);

        column.setRenderer(insuredPersonsTable.createRenderer(DataGrid.ComponentRenderer.class));

        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null);

        insuredPersonsDl.setParameter("relativeType", RelativeType.EMPLOYEE);
        insuredPersonsDl.setParameter("employeeId", personGroupExt != null ? personGroupExt.getId() : null);

        DicCompany dicCompany = personGroupExt.getCurrentAssignment().getOrganizationGroup().getCompany();

        if (dicCompany != null) {
            InsuranceContract contract = dataManager.load(InsuranceContract.class)
                    .query("select e from tsadv$InsuranceContract e where e.company.id = :companyId" +
                            " and current_date between e.availabilityPeriodFrom and e.availabilityPeriodTo")
                    .parameter("companyId", dicCompany.getId())
                    .view("insuranceContract-browseView")
                    .list().stream().findFirst().orElse(null);

            long assignedDay = TimeUnit.DAYS.convert(
                    Math.abs(timeSource.currentTimestamp().getTime() - personGroupExt.getCurrentAssignment().getAssignDate().getTime()),
                    TimeUnit.MILLISECONDS);
            insuredPersonsTableJoinMIC.setEnabled(contract != null && assignedDay > contract.getAttachingAnEmployee());
        }
    }

    @Subscribe("insuredPersonsTable.joinMIC")
    public void onInsuredPersonsTableJoinMIC(Action.ActionPerformedEvent event) {
        joinEmployee("joinEmployee");
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

        if (event.getItem() != null && assignedDay < 45) {
            insuredPersonsTableJoinFamilyMember.setEnabled(false);
        }
    }


    public void joinEmployee(String whichButton) {
        InsuredPerson item = dataManager.create(InsuredPerson.class);
        InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                .newEntity(chekType(item, whichButton))
                .build();

        editorBuilder.setParameter(whichButton);
        editorBuilder.show();
    }

    public InsuredPerson chekType(InsuredPerson insuredPerson, String whichButton) {
        DicRelationshipType relationshipType = commonService.getEntity(DicRelationshipType.class, "PRIMARY");
        Date today = timeSource.currentTimestamp();
        insuredPerson.setAttachDate(today);
        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null);

        DicCompany company = personGroupExt.getCurrentAssignment().getOrganizationGroup().getCompany();

        InsuranceContract contract = dataManager.load(InsuranceContract.class)
                .query("select e from tsadv$InsuranceContract e where e.company.id = :companyId ")
                .parameter("companyId", company.getId())
                .view("insuranceContract-editView")
                .list().stream().findFirst().orElse(null);

        PersonExt person = personGroupExt.getPerson();
        AssignmentExt assignment = personGroupExt.getCurrentAssignment();
        if (whichButton.equals("joinEmployee")) {
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

           if (contract.getDefaultDocumentType() != null) {
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

        }
        return insuredPerson;
    }

}