package kz.uco.tsadv.web.modules.recruitment.requisitioncompetence;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.OptionsGroup;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recruitment.enums.CompetenceCriticalness;
import kz.uco.tsadv.modules.recruitment.model.RequisitionCompetence;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class RequisitionCompetenceEdit extends AbstractEditor<RequisitionCompetence> {

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Datasource<RequisitionCompetence> requisitionCompetenceDs;
    @Named("fieldGroup.competenceGroup")
    protected PickerField competenceGroupField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        FieldGroup.FieldConfig criticalnessConfig = fieldGroup.getField("criticalness");
        OptionsGroup criticalness = componentsFactory.createComponent(OptionsGroup.class);
        criticalness.setDatasource(requisitionCompetenceDs, "criticalness");
        criticalness.setOptionsEnum(CompetenceCriticalness.class);
        criticalness.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        criticalnessConfig.setComponent(criticalness);

        Map<String, Object> competenceParams = new HashMap<>();
        competenceParams.put("rcAvailableFilter", Boolean.TRUE);
        if (params.containsKey("excludeCompetence")) {
            competenceParams.put("excludeCompetence", params.get("excludeCompetence"));

        }
        Utils.customizeLookup(fieldGroup.getField("competenceGroup").getComponent(),
                "tsadv$CompetenceGroup.lookup",
                WindowManager.OpenType.DIALOG,
                competenceParams);

    }

    @Override
    protected void initNewItem(RequisitionCompetence item) {
        super.initNewItem(item);
        item.setCriticalness(CompetenceCriticalness.CRITICAL);
    }
}