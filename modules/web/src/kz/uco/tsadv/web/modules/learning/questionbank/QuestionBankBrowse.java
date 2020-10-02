package kz.uco.tsadv.web.modules.learning.questionbank;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.learning.model.QuestionBank;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class QuestionBankBrowse extends AbstractLookup {
    @Inject
    protected Table<QuestionBank> questionBanksTable;
    @Named("questionBanksTable.create")
    protected CreateAction questionBanksTableCreate;
    @Named("questionBanksTable.edit")
    protected EditAction questionBanksTableEdit;
    @Inject
    protected CollectionDatasource<QuestionBank, UUID> questionBanksDs;
    @Inject
    protected DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        questionBanksTableEdit.setWindowParamsSupplier(() ->
                ParamsMap.of("item", dataManager.reload(
                        questionBanksDs.getItem(),
                        "questionBank.edit")));
        super.init(params);

    }
}