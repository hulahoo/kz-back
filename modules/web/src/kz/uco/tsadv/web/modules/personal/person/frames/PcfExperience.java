package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.recruitment.model.PersonExperience;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class PcfExperience extends EditableFrame {

    @Inject
    private ButtonsPanel buttonsPanel;

    public CollectionDatasource<PersonExperience, UUID> personExperienceDs;

    @Named("personExperienceTable.create")
    private CreateAction personExperienceTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personExperienceTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        personExperienceDs = (CollectionDatasource<PersonExperience, UUID>) getDsContext().get("personExperienceDs");
    }
}