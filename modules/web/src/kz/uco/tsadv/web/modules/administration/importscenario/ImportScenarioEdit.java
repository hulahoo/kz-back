package kz.uco.tsadv.web.modules.administration.importscenario;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.administration.importer.ImportScenario;
import kz.uco.tsadv.modules.administration.importer.Importer;
import kz.uco.tsadv.service.ImporterService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportScenarioEdit extends AbstractEditor<ImportScenario> {

    @Inject
    private ImporterService importerService;

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private ComponentsFactory componentsFactory;

    private List<Importer> importerList = new ArrayList<>();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        importerList.addAll(importerService.getImporters());
    }

    @Override
    protected void postInit() {
        super.postInit();

        FieldGroup.FieldConfig importerConfig = fieldGroup.getField("importer");

        LookupField<Importer> importerLookup = componentsFactory.createComponent(LookupField.class);
        importerLookup.setOptionsList(importerList);
        importerConfig.setComponent(importerLookup);

        String importerBeanName = getItem().getImporterBeanName();
        if (importerBeanName != null) {
            for (Importer importer : importerList) {
                if (importer.getBeanName().equalsIgnoreCase(importerBeanName)) {
                    importerLookup.setValue(importer);
                    break;
                }
            }
        }

        importerLookup.addValueChangeListener(e -> {
            Object value = e.getValue();
            if (value != null) {
                getItem().setImporterBeanName(((Importer) value).getBeanName());
            } else {
                getItem().setImporterBeanName(null);
            }
        });
    }
}