package kz.uco.tsadv.web.modules.personal.personexperience;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recruitment.model.PersonExperience;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class PersonExperienceEdit extends AbstractEditor<PersonExperience> {
    @Named("fieldGroup.untilNow")
    private CheckBox untilNow;

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Datasource<PersonExperience> personExperienceDs;



    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

//        FieldGroup.FieldConfig startMonth = fieldGroup.getField("startMonth");
//        DatePicker startMonthComponent = componentsFactory.createComponent(DatePicker.class);
//        startMonthComponent.setResolution(DatePicker.Resolution.MONTH);
//        startMonthComponent.setDatasource(personExperienceDs, "startMonth");
//        startMonth.setComponent(startMonthComponent);
//
//        FieldGroup.FieldConfig endMonth = fieldGroup.getField("endMonth");
//        DatePicker endMonthComponent = componentsFactory.createComponent(DatePicker.class);
//        endMonthComponent.setResolution(DatePicker.Resolution.MONTH);
//        endMonthComponent.setDatasource(personExperienceDs, "endMonth");
//        endMonth.setComponent(endMonthComponent);
//
//        untilNow.addValueChangeListener(e -> {
//            if (e.getValue().equals(true)){
//                endMonthComponent.setVisible(false);
//                endMonthComponent.setValue(null);
//                personExperienceDs.commit();
//            }
//            else
//                endMonthComponent.setVisible(true);
//        });

    }

    @Override
    public void ready() {
        super.ready();

    }
}