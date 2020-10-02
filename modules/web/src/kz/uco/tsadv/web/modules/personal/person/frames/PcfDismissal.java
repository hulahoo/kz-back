package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.Dismissal;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.web.modules.personal.dismissal.DismissalEdit;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.*;

@SuppressWarnings("all")
public class PcfDismissal extends EditableFrame {

    @Inject
    protected ButtonsPanel buttonsPanel;

    @Inject
    protected CommonService commonService;

    protected Datasource<AssignmentExt> assignmentDs;

    protected AssignmentExt assignmentExtNew;

    @Inject
    protected Metadata metadata;

    @Named("dismissalsTable.create")
    protected CreateAction dismissalsTableCreate;

    public CollectionDatasource<Dismissal, UUID> dismissalsDs;

    @Inject
    protected MetadataTools metadataTools;

    protected AssignmentExt assignmentExt;

    protected Calendar startDate = Calendar.getInstance();

    protected List<Entity> commitInstances = new ArrayList<>();

    @Inject
    protected Table dismissalsTable;
    @Inject
    protected Button DicRemove;
    @Inject
    protected BusinessRuleService businessRuleService;
    protected boolean checkDismissalCheck = false;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("notFromCard")) {
            dismissalsTable.getAction("remove").setVisible(true);
        } else {
            dismissalsTable.getAction("remove").setVisible(false);
        }

        dismissalsTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : ParamsMap.of("personGroup", params.get("personGroup")));
        dismissalsTableCreate.setWindowParamsSupplier(() ->
                ParamsMap.of("assignment", assignmentDs != null ? assignmentDs.getItem() : params.get("assignment")));

        dismissalsTableCreate.setAfterWindowClosedHandler((window, closeActionId) -> {
            if (assignmentDs != null) {
                assignmentDs.refresh();
            }
        });
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        dismissalsDs = (CollectionDatasource<Dismissal, UUID>) getDsContext().get("dismissalsDs");
        assignmentDs = (Datasource<AssignmentExt>) getDsContext().get("assignmentDs");
    }

    public void removeDismissal() {
        dismissalCheck();
        if (checkDismissalCheck) {
            if (!businessRuleService.getRuleStatus("dismissalCheck").equals(RuleStatus.DISABLE)) {
                showNotification(businessRuleService.getBusinessRuleMessage("dismissalCheck"), NotificationType.ERROR);
            }
        } else {
            showOptionDialog(
                    getMessage("msg.warning.title"),
                    getMessage("msg.remove.titlePcf"),
                    MessageType.WARNING,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    dismissalsDs.removeItem(dismissalsDs.getItem());
                                    dismissalsDs.commit();
                                    dismissalsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL)
                    });
        }
    }

    protected void dismissalCheck() {
        Date dismissalDatePlusOne = dismissalsDs.getItem().getDismissalDate();
        dismissalDatePlusOne = DateUtils.addDays(dismissalDatePlusOne, 1);
        List<PersonExt> personExts = commonService.getEntities(PersonExt.class,
                "select e from base$PersonExt e where e.group.id = :personGroupId and e.startDate > :dismissalDatePlusOne and e.deleteTs is null",
                ParamsMap.of("dismissalDatePlusOne", dismissalDatePlusOne, "personGroupId", dismissalsDs.getItem().getPersonGroup().getId()),
                "person.full");
        if (personExts.size() >= 1) {
            checkDismissalCheck = true;
        } else {
            checkDismissalCheck = false;
        }
    }

    public void edit() {
        Dismissal dismissal = dismissalsDs.getItem();
        if (dismissal != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("openedForEdit", "");
            openDismissalEditor(dismissal, params);
        }
    }

    protected void openDismissalEditor(Dismissal dismissal, Map<String, Object> params) {
        DismissalEdit dismissalEdit = (DismissalEdit) openEditor("tsadv$Dismissal.edit", dismissal, WindowManager.OpenType.DIALOG, params);
        dismissalEdit.addCloseListener(actionId -> {
            dismissalsDs.refresh();
        });
    }
}