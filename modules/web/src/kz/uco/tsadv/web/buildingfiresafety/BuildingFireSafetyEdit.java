package kz.uco.tsadv.web.buildingfiresafety;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.entity.tb.BuildingFireSafety;
import kz.uco.tsadv.entity.tb.FireWaterSupply;

import javax.inject.Inject;
import java.util.HashMap;

public class BuildingFireSafetyEdit extends AbstractEditor<BuildingFireSafety> {
    @Inject
    private Metadata metadata;
/*
    public void onCreate() {
        FireWaterSupply fireWaterSupply = metadata.create(FireWaterSupply.class);
        fireWaterSupply.setBuildingFireSafety(getItem());
        openEditor("tsadv$BuildingFireSafety.edit", fireWaterSupply, WindowManager.OpenType.THIS_TAB, new HashMap<>(), buildingFireSafetyDs);
    }*/
}