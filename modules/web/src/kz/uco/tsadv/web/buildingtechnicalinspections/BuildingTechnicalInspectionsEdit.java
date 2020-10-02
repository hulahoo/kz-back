package kz.uco.tsadv.web.buildingtechnicalinspections;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.entity.tb.BuildingFireSafety;
import kz.uco.tsadv.entity.tb.BuildingTechnicalInspections;
import com.haulmont.cuba.gui.components.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.UUID;

public class BuildingTechnicalInspectionsEdit extends AbstractEditor<BuildingTechnicalInspections> {
    @Inject
    private CollectionDatasource<BuildingFireSafety, UUID> buildingFireSafetyDs;
    @Inject
    private Metadata metadata;

    /*public void onCreate(Component source) {
        BuildingFireSafety buildingFireSafety = metadata.create(BuildingFireSafety.class);
        buildingFireSafety.setBuildingTechnicalInspections(getItem());
        openEditor("tsadv$BuildingFireSafety.edit", buildingFireSafety, WindowManager.OpenType.THIS_TAB, new HashMap<>(), buildingFireSafetyDs);
    }*/
}