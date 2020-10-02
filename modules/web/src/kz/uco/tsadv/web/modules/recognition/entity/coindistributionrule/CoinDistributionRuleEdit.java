package kz.uco.tsadv.web.modules.recognition.entity.coindistributionrule;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.TabSheet;
import kz.uco.tsadv.modules.recognition.CoinDistributionRule;

import javax.inject.Inject;
import java.util.Map;

public class CoinDistributionRuleEdit extends AbstractEditor<CoinDistributionRule> {
    @Inject
    private TabSheet tabSheet;
    private boolean isCreateAction;
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("createAction")) {
            isCreateAction = true;
            tabSheet.setVisible(false);
        }
    }


    @Override
    public void commitAndClose() {
        if (isCreateAction && validateAll()) {
            commit();
            isCreateAction = false;
            tabSheet.setVisible(true);
        }else {
            super.commitAndClose();
        }
    }


    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && !close) {
            if (showSaveNotification) {
                showNotification(getMessage("saved.successfully"), NotificationType.TRAY);
            }
            postInit();
        }
        return true;
    }
}