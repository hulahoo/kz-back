package kz.uco.tsadv.web.modules.recognition.entity.personpoint;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.PersonPoint;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class PersonPointBrowse extends AbstractLookup {

    @Inject
    private Metadata metadata;

    @Inject
    private GroupTable<PersonPoint> personPointsTable;

    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        personPointsTable.addAction(new BaseAction("insertPoints") {
            @Override
            public void actionPerform(Component component) {
                insertPoints();
            }
        });
    }

    private void insertPoints() {
        CommitContext commitContext = new CommitContext();
        for (PersonGroupExt personGroupExt : loadPersonGroups()) {
            PersonPoint personPoint = metadata.create(PersonPoint.class);
            personPoint.setPoints(1000L);
            personPoint.setPersonGroup(personGroupExt);
            commitContext.addInstanceToCommit(personPoint);
        }
        dataManager.commit(commitContext);
    }

    private List<PersonGroupExt> loadPersonGroups() {
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$PersonGroupExt e"));
        loadContext.setView(View.MINIMAL);
        return dataManager.loadList(loadContext);
    }
}