package kz.uco.tsadv.web.modules.personal.surcharge;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.OptionsGroup;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.model.SurCharge;
import kz.uco.tsadv.modules.personal.model.SurChargePeriod;
import kz.uco.tsadv.modules.personal.model.SurChargeType;

import javax.inject.Inject;
import java.util.Map;

public class SurChargeEditPosition extends AbstractEditor<SurCharge> {
    @Inject
    protected Datasource<SurCharge> surChargeDs;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        //set period
        OptionsGroup periodEnum = componentsFactory.createComponent(OptionsGroup.class);
        periodEnum.setOptionsEnum(SurChargePeriod.class);
        periodEnum.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        periodEnum.setDatasource(surChargeDs, "period");
        fieldGroup.getFieldNN("period").setComponent(periodEnum);
        //set type
        OptionsGroup typeEnum = componentsFactory.createComponent(OptionsGroup.class);
        typeEnum.setOptionsEnum(SurChargeType.class);
        typeEnum.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        typeEnum.setDatasource(surChargeDs, "type");
        fieldGroup.getFieldNN("type").setComponent(typeEnum);
        //set gross net
        OptionsGroup grossNet = componentsFactory.createComponent(OptionsGroup.class);
        grossNet.setOptionsEnum(SurChargeType.class);
        grossNet.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        grossNet.setDatasource(surChargeDs, "grossNet");
        fieldGroup.getFieldNN("grossNet").setComponent(grossNet);
    }

}