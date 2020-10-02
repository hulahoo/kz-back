package kz.uco.tsadv.web.personalprotection;

import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personprotection.PersonalProtection;
import kz.uco.tsadv.modules.personprotection.dictionary.DicProtectionEquipmentCondition;
import kz.uco.tsadv.modules.personprotection.enums.PersonalProtectionEquipmentStatus;
import kz.uco.tsadv.modules.recruitment.config.CalcEndTrialPeriod;
import kz.uco.tsadv.modules.recruitment.enums.HS_Periods;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class PersonalProtectionEditIssue extends AbstractEditor<PersonalProtection> {

    @Inject
    protected Datasource<PersonalProtection> personalProtectionDs;
    @Named("fieldGroup.protectionEquipment")
    protected PickerField protectionEquipmentField;
    protected CalcEndTrialPeriod calcEndTrialPeriod;
    @Inject
    protected Configuration configuration;
    @Named("fieldGroup.dateOfIssue")
    protected DateField dateOfIssueField;
    @Inject
    protected CommonService commonService;

    @Override
    protected void initNewItem(PersonalProtection item) {
        super.initNewItem(item);
        item.setStatus(PersonalProtectionEquipmentStatus.ISSUED_BY);
        item.setIsAcceptedByPerson(true);
        item.setDateOfIssue(CommonUtils.getSystemDate());
        DicProtectionEquipmentCondition dicProtectionEquipmentCondition = commonService.getEntity(DicProtectionEquipmentCondition.class, "NEW");
        if (dicProtectionEquipmentCondition != null) {
            item.setCondition(dicProtectionEquipmentCondition);
        } else {
            showNotification(getMessage("missingValue"));
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        protectionEquipmentField.addValueChangeListener(e -> {
            if (getItem().getProtectionEquipment() != null) {
                getItem().getProtectionEquipment().setType(getItem().getProtectionEquipment().getType());
                getItem().getProtectionEquipment().setUnitOfMeasure(getItem().getProtectionEquipment().getUnitOfMeasure());
            }
        });

        calcEndTrialPeriod = configuration.getConfig(CalcEndTrialPeriod.class);
        dateOfIssueField.addValueChangeListener(e -> {
            if (calcEndTrialPeriod.getCalcCountProbationPeriod()) {
                if (getItem().getProtectionEquipment() != null) {
                    if (getItem().getProtectionEquipment().getReplacementUom() != null && getItem().getDateOfIssue() != null) {
                        getItem().setPlanChangeDate(getCountProbationPeriod(getItem().getDateOfIssue(), 1, ((HS_Periods) getItem().getProtectionEquipment().getReplacementUom()).getId()));
                    }
                }
            }
        });
    }

    protected Date getCountProbationPeriod(Date date, int count, int units) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        if (units == 10) {
            instance.add(Calendar.DAY_OF_WEEK, count);
        } else if (units == 20) {
            instance.add(Calendar.WEEK_OF_MONTH, count);
        } else if (units == 30) {
            for (int i = 0; i < count; ) {
                i++;
                instance.add(Calendar.MONTH, 3);
            }
        } else if (units == 40) {
            instance.add(Calendar.MONTH, count);
        } else if (units == 50) {
            for (int i = 0; i < count; ) {
                i++;
                instance.add(Calendar.MONTH, 6);
            }
        } else if (units == 60) {
            instance.add(Calendar.YEAR, count);
        }
        instance.add(Calendar.DAY_OF_WEEK, -1);
        return instance.getTime();
    }
}