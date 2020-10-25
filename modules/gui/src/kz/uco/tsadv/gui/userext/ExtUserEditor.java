package kz.uco.tsadv.gui.userext;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.SplitPanel;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.entity.UserExtPersonGroup;

import javax.inject.Inject;
import java.util.Map;

public class ExtUserEditor extends kz.uco.base.gui.userext.ExtUserEditor {

    @Inject
    protected GroupBoxLayout propertiesBox;

    @Inject
    protected SplitPanel split;

    @Inject
    protected SplitPanel vSplit;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    private UserExtPersonGroup oldUserExtPersonGroup = null;

    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        remove(propertiesBox);
        remove(split);

        vSplit.add(propertiesBox);
        vSplit.add(split);
    }

}