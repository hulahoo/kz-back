package kz.uco.tsadv.web.modules.personal.salary;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.EmployeeConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.tsadv.modules.personal.enums.SalaryType;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.service.SalariesPeriodChangerService;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class SalaryEdit extends AbstractEditor<Salary> {

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    protected Datasource<Salary> salaryDs;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected CommonService commonService;

    @Inject
    protected SalariesPeriodChangerService salariesPeriodChangerService;

    @Inject
    protected CollectionDatasource reasonDs;

    @Inject
    @Named("fieldGroup.assignmentGroup")
    protected PickerField<Entity> assignmentGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        FieldGroup.FieldConfig netGrossConfig = fieldGroup.getFieldNN("netGross");
        OptionsGroup netGross = componentsFactory.createComponent(OptionsGroup.class);
        netGross.setDatasource(salaryDs, "netGross");
        netGross.setOptionsEnum(GrossNet.class);
        netGross.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        netGrossConfig.setComponent(netGross);

        /*FieldGroup.FieldConfig fieldConfig = fieldGroup.getField("reason");
        LookupPickerField pickerField = componentsFactory.createComponent(LookupPickerField.class);
        pickerField.setDatasource(salaryDs, "reason");
        pickerField.setOptionsDatasource(reasonDs);
        fieldConfig.setComponent(pickerField);*/

        fieldGroup.getFieldNN("startDate").addValidator(value -> {
            if (value != null && getItem().getEndDate() != null) {
                Date startDate = (Date) value;
                if (startDate.after(getItem().getEndDate()))
                    throw new ValidationException(getMessage("AbstractHrEditor.startDate.validatorMsg"));
            }
        });

        fieldGroup.getFieldNN("endDate").addValidator(value -> {
            if (value != null && getItem().getStartDate() != null) {
                Date endDate = (Date) value;
                if (endDate.before(getItem().getStartDate()))
                    throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg"));
            }
        });

        assignmentGroup.addValueChangeListener((e) -> {
            if (e.getPrevValue() != null) {
                getItem().setOrderGroup(null);
                getItem().setAgreement(null);
            }
            customizeLookups();
        });
    }

    @Override
    protected void initNewItem(Salary item) {
        super.initNewItem(item);
        item.setCurrency(commonService.getEntity(DicCurrency.class, "KZT"));
        item.setNetGross(GrossNet.GROSS);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
        item.setType(SalaryType.monthlyRate);
    }

    @Override
    public void ready() {
        super.ready();
        if (getItem().getAssignmentGroup() != null)
            fieldGroup.getFieldNN("assignmentGroup").setEditable(false);

        customizeLookups();
    }

    @Override
    public void commitAndClose() {
        List<Salary> list = salariesPeriodChangerService.getExistingSalaries(getItem());
        if (list.isEmpty()) {
            super.commitAndClose();
        } else if (list.size() == 1 && list.get(0).equals(getItem())) {
            super.commitAndClose();
        } else {
            showOptionDialog(getMessage("Salary.overwriteExistingCaption"),
                    getMessage("Salary.overwriteExisting"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    SalaryEdit.super.commitAndClose();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO) {
                                @Override
                                public void actionPerform(Component component) {
                                }
                            }
                    });
        }

    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        return super.postCommit(committed, close);
    }

    protected void customizeLookups() {
        Map<String, Object> agreementLookupParams = new HashMap<>();
        agreementLookupParams.put("personGroupId", getItem().getAssignmentGroup() == null ? null : getItem().getAssignmentGroup().getAssignment().getPersonGroup().getId());
        Utils.customizeLookup(fieldGroup.getFieldNN("agreement").getComponent(), "tsadv$Agreement.lookup", WindowManager.OpenType.DIALOG, agreementLookupParams);

        Map<String, Object> orderGroupLookupParams = new HashMap<>();
        orderGroupLookupParams.put("orderTypeCode", "SALARY");
        Utils.customizeLookup(fieldGroup.getFieldNN("orderGroup").getComponent(), "tsadv$OrderGroup.lookup", WindowManager.OpenType.DIALOG, orderGroupLookupParams);
    }


}