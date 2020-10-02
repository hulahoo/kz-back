package kz.uco.tsadv.web.modules.performance.calibrationsession;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.modules.performance.model.CalibrationComission;
import kz.uco.tsadv.modules.performance.model.CalibrationSession;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class CalibrationSessionEdit extends AbstractEditor<CalibrationSession> {

    @Inject
    private Metadata metadata;

    @Inject
    private CollectionDatasource<CalibrationComission, UUID> comissionsDs;

    @Inject
    private Table<CalibrationComission> comissionsTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        addAction(new BaseAction("addComission") {
            @Override
            public void actionPerform(Component component) {
                openLookup("base$PersonGroup.browse", new Lookup.Handler() {
                    @Override
                    public void handleLookup(Collection items) {
                        for (PersonGroupExt choose : (Collection<PersonGroupExt>) items) {
                            CalibrationComission comission = metadata.create(CalibrationComission.class);
                            comission.setCalibrationSession(getItem());
                            comission.setPerson(choose);
                            comissionsDs.addItem(comission);
                        }
                    }
                }, WindowManager.OpenType.DIALOG);
            }
        });
    }

    @Override
    public void ready() {
        super.ready();

        comissionsTable.getColumn("personImage").setWidth(65);
    }

    public Component generatePersonImage(CalibrationComission comission) {
        return Utils.getPersonImageEmbedded(comission.getPerson().getPerson(), "50px", null);
    }

}