package kz.uco.tsadv.web.trainer;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.performance.model.Trainer;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Optional;

public class TrainerLearningBrowse extends AbstractLookup {

    @Named("trainersTable.edit")
    private EditAction trainersTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        trainersTableEdit.setWindowId("tsadv$TrainerLearning.edit");
    }
}