package kz.uco.tsadv.web.partyext;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.filter.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.learning.model.PartyExt;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyExtBrowse extends AbstractLookup {

    public static final String TRAINING_PROVIDER = "PEB_TRAINING_PROVIDER";

    @Named("partyExtsTable.create")
    private CreateAction createAction;
    @Named("partyExtsTable.edit")
    private EditAction editAction;
//    @Inject
//    protected Button contactTypesBtn;
    @Inject
    protected GroupTable<PartyExt> partyExtsTable;
    @Inject
    protected GroupDatasource<PartyExt, UUID> partyExtsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(TRAINING_PROVIDER)) {
            Condition filterCondition = new LogicalCondition("", LogicalOp.AND);
            filterCondition.getConditions().add(new Clause("", "e.trainingProvider = :custom$trainingProvider", null, null, null));

            partyExtsDs.setQueryFilter(new QueryFilter(filterCondition));

            Map<String, Object> param = new HashMap<>();
            param.put("trainingProvider", params.get(TRAINING_PROVIDER));
            partyExtsDs.refresh(param);
        }

//        partyExtsDs.addItemChangeListener(this::partyExtsDsItemChangeListener);

        createAction.setWindowId("base$PartyExt.edit");
        editAction.setWindowId("base$PartyExt.edit");
//        contactTypesBtn.setAction(new BaseAction("contacTypeAction") {
//            @Override
//            public void actionPerform(Component component) {
//                super.actionPerform(component);
//                if (partyExtsTable.getSingleSelected() != null) {
//                    AbstractWindow attestationWindow = openWindow("tsadv$PartyContactPerson.browse",
//                            WindowManager.OpenType.THIS_TAB, ParamsMap.of("partyExt",
//                                    partyExtsTable.getSingleSelected()));
//                    attestationWindow.addCloseListener(actionId -> {
//                        partyExtsDs.refresh();
//                    });
//                }
//            }
//        });
//        contactTypesBtn.setEnabled(false);
//    }
//
//    protected void partyExtsDsItemChangeListener(Datasource.ItemChangeEvent<PartyExt> e) {
//        contactTypesBtn.setEnabled(partyExtsTable.getSingleSelected() != null);
    }
}