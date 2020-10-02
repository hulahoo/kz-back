package kz.uco.tsadv.web.modules.learning.learner;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import kz.uco.tsadv.modules.learning.model.Learner;
import kz.uco.tsadv.modules.learning.model.LearnerGroup;

import javax.inject.Inject;
import java.util.Map;

import static kz.uco.tsadv.modules.learning.model.LearnerGroup.PARAMETER_LEARNER_GROUP;

public class LearnerBrowse extends AbstractLookup {

    @WindowParam(name = PARAMETER_LEARNER_GROUP)
    protected LearnerGroup learnerGroup;

    @Inject
    protected GroupTable<Learner> learnersTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        customizeScreen();
    }

    protected void customizeScreen() {
        if (learnerGroup != null) {
            setCaption(getCaption() + ": " + learnerGroup.getCode());

            Table.Column groupColumn = learnersTable.getColumn("group");
            if (groupColumn != null) {
                learnersTable.removeColumn(groupColumn);
            }
        }
    }
}