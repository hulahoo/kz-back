package kz.uco.tsadv.web.modules.recognition.entity.personcoin;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.PersonCoin;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class PersonCoinBrowse extends AbstractLookup {

    @Inject
    private Metadata metadata;

    @Inject
    private GroupTable<PersonCoin> personCoinsTable;

    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        personCoinsTable.addAction(new BaseAction("insertCoins") {
            @Override
            public void actionPerform(Component component) {
                insertCoins();
            }
        });
    }

    private void insertCoins() {
        CommitContext commitContext = new CommitContext();
        for (PersonGroupExt personGroupExt : loadPersonGroups()) {
            PersonCoin personCoin = metadata.create(PersonCoin.class);
            personCoin.setCoins(1000L);
            personCoin.setPersonGroup(personGroupExt);
            commitContext.addInstanceToCommit(personCoin);
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