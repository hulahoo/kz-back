package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.ContractConditions;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.service.DocumentService;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.*;
import java.util.Calendar;

@UiController("tsadv$InsuredPerson.MemberEdit")
@UiDescriptor("insured-person-member-edit.xml")
@EditedEntityContainer("insuredPersonDc")
@LoadDataBeforeShow
public class InsuredPersonMemberEdit extends StandardEditor<InsuredPerson> {

    @Inject
    private CollectionLoader<DicRelationshipType> relativeDl;
    @Inject
    private DateField<Date> birthdateField;
    @Inject
    private LookupField<DicRelationshipType> relativeField;
    @Inject
    private PickerField<InsuranceContract> insuranceContractField;
    @Inject
    private TextField<BigDecimal> amountField;
    @Inject
    private TimeSource timeSource;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private CommonService commonService;
    @Inject
    private FileUploadingAPI fileUploadingAPI;
    @Inject
    private FileUploadField upload;
    @Inject
    private DataManager dataManager;
    @Inject
    private InstanceContainer<InsuredPerson> insuredPersonDc;
    @Inject
    private CollectionPropertyContainer<FileDescriptor> attachmentsDc;
    @Inject
    private PickerField<PersonGroupExt> employeeField;
    @Inject
    private DocumentService documentService;


    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        relativeDl.setParameter("employee", "PRIMARY");
    }

    @Subscribe("birthdateField")
    public void onBirthdateFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        if (isNewOrChangedInsuredPerson()  && birthdateField.getValue() != null && employeeField.getValue() != null
                && relativeField.getValue() != null && insuranceContractField.getValue() != null){
            amountField.setValue(documentService.calcAmount(
                    insuranceContractField.getValue().getId(),
                    employeeField.getValue().getId(),
                    birthdateField.getValue(),
                    relativeField.getValue().getId()));
        }
    }

    public InsuredPerson getPerson(){
        return dataManager.load(InsuredPerson.class).
                query("select e from tsadv$InsuredPerson e where e.id =:id")
                .parameter("id", getEditedEntity().getId())
                .view("insuredPerson-editView")
                .list().stream().findFirst().orElse(null);
    }

    public boolean isNewOrChangedInsuredPerson(){
        boolean result = true;
        if (getPerson() != null
                && birthdateField.getValue() != null
                && birthdateField.getValue().equals(getPerson().getBirthdate())
                && relativeField.getValue() != null
                && relativeField.getValue().getCode().equals(getPerson().getRelative().getCode())){

            result = false;
        }

        return result;
    }

    @Subscribe("relativeField")
    public void onRelativeFieldValueChange(HasValue.ValueChangeEvent<DicRelationshipType> event) {
        if (isNewOrChangedInsuredPerson()  && birthdateField.getValue() != null && employeeField.getValue() != null
                && relativeField.getValue() != null && insuranceContractField.getValue() != null){
            amountField.setValue(documentService.calcAmount(
                    insuranceContractField.getValue().getId(),
                    employeeField.getValue().getId(),
                    birthdateField.getValue(),
                    relativeField.getValue().getId()));
        }
    }


    protected void calculatedAmount(){
        List<ContractConditions> conditionsList = new ArrayList<>();
        if (insuranceContractField.getValue() != null &&
                employeeField.getValue() != null) {
            int age = employeeService.calculateAge(birthdateField.getValue());
            List<InsuredPerson> personList = dataManager.load(InsuredPerson.class).query("select e " +
                    "from tsadv$InsuredPerson e " +
                    " where e.insuranceContract.id = :insuranceContractId" +
                    " and e.employee.id = :employeeId " +
                    " and e.amount = :amount " +
                    " and e.relative.code <> 'PRIMARY'")
                    .parameter("insuranceContractId", insuranceContractField.getValue().getId())
                    .parameter("employeeId", employeeField.getValue().getId())
                    .parameter("amount", BigDecimal.valueOf(0))
                    .view("insuredPerson-editView")
                    .list();

            for (ContractConditions condition : insuranceContractField.getValue().getProgramConditions()) {
                if (condition.getRelationshipType().getId().equals(relativeField.getValue().getId())) {
                    if (condition.getAgeMin() <= age && condition.getAgeMax() >= age) {
                        conditionsList.add(condition);
                    }
                }
            }


            if (conditionsList.size() > 1) {
                if (insuranceContractField.getValue().getCountOfFreeMembers() > personList.size()){
                    amountField.setValue(BigDecimal.ZERO);
                }else {
                    for (ContractConditions condition : conditionsList){
                        if (!condition.getIsFree()){
                            amountField.setValue(condition.getCostInKzt());
                            break;
                        }
                    }
                }
            } else {
                for (ContractConditions condition : conditionsList){
                    if (!condition.getIsFree()){
                        amountField.setValue(condition.getCostInKzt());
                        break;
                    }
                }
            }
        }

    }


    @Subscribe("upload")
    protected void onUploadFileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
        File file = fileUploadingAPI.getFile(upload.getFileId());
        FileDescriptor fd = upload.getFileDescriptor();
        try {
            fileUploadingAPI.putFileIntoStorage(upload.getFileId(), fd);
        } catch (FileStorageException e) {
            throw new RuntimeException("Error saving file to FileStorage", e);
        }
        dataManager.commit(fd);
        if (insuredPersonDc.getItem().getFile() == null) {
            insuredPersonDc.getItem().setFile(new ArrayList<FileDescriptor>());
        }
        attachmentsDc.getDisconnectedItems().add(fd);
        insuredPersonDc.getItem().getFile().add(fd);
    }


    public Component generatorName(FileDescriptor fd) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(fd.getName());
        linkButton.setAction(new BaseAction("export") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                exportDisplay.show(fd, ExportFormat.OCTET_STREAM);
            }
        });
        return linkButton;
    }
}