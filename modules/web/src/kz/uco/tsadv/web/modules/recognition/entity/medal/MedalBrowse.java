package kz.uco.tsadv.web.modules.recognition.entity.medal;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recognition.Medal;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MedalBrowse extends AbstractLookup {

    @Named("qualityTable.create")
    protected CreateAction createQualityAction;
    @Named("childTable.create")
    protected CreateAction createChildTableAction;
    @Named("qualityTable.edit")
    protected EditAction editQualityAction;
    @Named("childTable.edit")
    protected EditAction editChildTableAction;
    @Inject
    protected GroupDatasource<Medal, UUID> medalsDs;
    @Inject
    protected TabSheet tabSheet;
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("hideTabs")) {
            tabSheet.setVisible(false);
        }
        Map<String, Object> qualityParam = new HashMap<>();
        Map<String, Object> childParam = new HashMap<>();
        medalsDs.addItemChangeListener(e -> {
            createQualityAction.setEnabled(e != null);
            createChildTableAction.setEnabled(e != null);
            qualityParam.put("medal", e.getItem());
            childParam.put("medal", e.getItem());
        });
        qualityParam.put("inVisibleChildFields", true);
        childParam.put("inVisibleQualityFields", true);
        createQualityAction.setWindowParams(qualityParam);
        editQualityAction.setWindowParams(qualityParam);
        createChildTableAction.setWindowParams(childParam);
        editChildTableAction.setWindowParams(childParam);
    }
}