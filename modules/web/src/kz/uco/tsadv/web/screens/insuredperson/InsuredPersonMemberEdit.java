package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.entity.FileDescriptor;
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
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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


    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        relativeDl.setParameter("employee", "PRIMARY");
    }

    @Subscribe("birthdateField")
    public void onBirthdateFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        if (event.getValue() != null && relativeField.getValue() != null && insuranceContractField.getValue() != null){
            calculatedAmount();
        }
    }


    @Subscribe("relativeField")
    public void onRelativeFieldValueChange(HasValue.ValueChangeEvent<DicRelationshipType> event) {
        if (event.getValue() != null && birthdateField.getValue() != null && insuranceContractField.getValue() != null){
            calculatedAmount();
        }
    }


    protected void calculatedAmount(){

        int age = employeeService.calculateAge(birthdateField.getValue());
        List<InsuredPerson> personList = dataManager.load(InsuredPerson.class).query("select e " +
                "from tsadv$InsuredPerson e " +
                " where e.insuranceContract.id = :insuranceContractId" +
                " and e.employee.id = :employeeId " +
                " and e.relative.code <> 'PRIMARY'")
                .parameter("insuranceContractId", insuranceContractField.getValue().getId())
                .parameter("employeeId", employeeField.getValue().getId())
                .view("insuredPerson-editView")
                .list();

        if (insuranceContractField.getValue().getCountOfFreeMembers() > personList.size()){
            amountField.setValue(BigDecimal.valueOf(0));
        }else {
            for (ContractConditions condition : insuranceContractField.getValue().getProgramConditions()){
                if (condition.getRelationshipType().getId() == relativeField.getValue().getId()){
                    if (condition.getAgeMin() <= age && condition.getAgeMax() >= age){
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