package kz.uco.tsadv.web.modules.personal.absencerequest;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class AbsenceRequestBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<AbsenceRequest, UUID> absenceRequestsDs;
    @Named("absenceRequestsTable.remove")
    private RemoveAction absenceRequestsTableRemove;
    @Named("absenceRequestsTable.create")
    private CreateAction absenceRequestsTableCreate;
    @Named("absenceRequestsTable.edit")
    private EditAction absenceRequestsTableEdit;
    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public void init(Map<String, Object> params) {

        absenceRequestsDs.addItemChangeListener(e -> {
            if (absenceRequestsDs.getItem() == null)
                absenceRequestsTableRemove.setEnabled(false);
            else if ("DRAFT".equals(absenceRequestsDs.getItem().getStatus().getCode())
                    || "CANCELLED".equals(absenceRequestsDs.getItem().getStatus().getCode())) {
                absenceRequestsTableRemove.setEnabled(true);
            } else {
                absenceRequestsTableRemove.setEnabled(false);
            }
        });

        absenceRequestsTableEdit.setAfterCommitHandler(e -> absenceRequestsDs.refresh());
        absenceRequestsTableCreate.setEnabled(userSessionSource.getUserSession().getAttribute("userPersonGroupId") != null);
        super.init(params);
    }


    public void onBalanceAbsenceBtnClick() {
        openWindow("tsadv$myAbsenceBalance", WindowManager.OpenType.THIS_TAB);
    }
}