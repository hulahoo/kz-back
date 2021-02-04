package kz.uco.tsadv.web.bpmrequestmessage;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.model.BpmRequestMessage;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BpmRequestMessageEdit extends AbstractEditor<BpmRequestMessage> {

    @Inject
    protected TimeSource timeSource;
    @Inject
    protected UserSession userSession;
    @Named("fieldGroup.assignedUser")
    protected PickerField assignedUserField;
    @Inject
    protected CommonService commonService;
    @Inject
    protected LookupField<Integer> lookupField;

    protected Map<String, Object> assignedUserFieldParams;
    protected UUID procInstanceId;
    protected boolean isAssignedUserEditable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        isAssignedUserEditable = Boolean.FALSE.equals(params.get("isAssignedUserEditable"));
        procInstanceId = (UUID) params.getOrDefault("procInstanceId", null);
        assignedUserField.getLookupAction().setLookupScreenParamsSupplier(() -> assignedUserFieldParams);

        assignedUserFieldParams = new HashMap<>();
        assignedUserFieldParams.put("procInstanceId", procInstanceId);
    }

    @Override
    protected void initNewItem(BpmRequestMessage item) {
        super.initNewItem(item);
        item.setSendDate(timeSource.currentTimestamp());
        item.setAssignedBy(getSessionUser());
        if (item.getLvl() == null) {
            item.setLvl(0);
        }
    }

    @Override
    public void ready() {
        super.ready();
        Map<String, Integer> lookupMap = new HashMap<>();

        lookupMap.put(getMessage("bpm.actor.user"), 1);
        lookupMap.put(getMessage("any.user"), 2);

        lookupField.setOptionsMap(lookupMap);
        lookupField.setValue(isNotApprover(getItem().getAssignedUser()) ? 2 : 1);

        if (isAssignedUserEditable) {
            ((FieldGroup) getComponentNN("fieldGroup")).getFieldNN("assignedUser").setEditable(false);
            lookupField.setEditable(false);
        }

        lookupField.addValueChangeListener(e -> {
            if (Objects.equals(e.getValue(), 2)) {
                assignedUserFieldParams.put("EMPLOYEE", true);
            } else if (Objects.equals(e.getValue(), 1)) {
                assignedUserFieldParams.remove("EMPLOYEE");
            }
            assignedUserField.setValue(null);
        });
    }

    private boolean isNotApprover(TsadvUser assignedUser) {
        return assignedUser != null
                && commonService.getCount(TsadvUser.class,
                "select e from tsadv$UserExt e " +
                        "   join bpm$ProcActor a on a.user.id = e.id " +
                        "   where a.procInstance.id = :procInstanceId " +
                        "   and e.id = :userId ",
                ParamsMap.of("procInstanceId", procInstanceId,
                        "userId", assignedUser.getId())) > 0;
    }

    @Nonnull
    protected TsadvUser getSessionUser() {
        TsadvUser userExt = commonService.getEntity(TsadvUser.class,
                "select e from tsadv$UserExt e where e.id = :userId",
                ParamsMap.of("userId", userSession.getUser().getId()),
                "userExt.edit");
        if (userExt.getPersonGroup() == null) throw new ItemNotFoundException("user.do.not.have.person");
        return userExt;
    }
}