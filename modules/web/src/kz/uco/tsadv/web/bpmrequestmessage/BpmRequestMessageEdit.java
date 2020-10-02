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
import kz.uco.tsadv.global.entity.UserExtPersonGroup;
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

    private boolean isNotApprover(UserExtPersonGroup assignedUser) {
        return assignedUser != null && assignedUser.getUserExt() != null
                && commonService.getCount(UserExtPersonGroup.class,
                "select e from tsadv$UserExtPersonGroup e " +
                        "   join bpm$ProcActor a on a.user.id = e.userExt.id " +
                        "   where a.procInstance.id = :procInstanceId " +
                        "   and e.userExt.id = :userId ",
                ParamsMap.of("procInstanceId", procInstanceId,
                        "userId", assignedUser.getUserExt().getId())) > 0;
    }

    @Nonnull
    protected UserExtPersonGroup getSessionUser() {
        UserExtPersonGroup userExtPersonGroup = commonService.getEntity(UserExtPersonGroup.class,
                "select e from tsadv$UserExtPersonGroup e where e.userExt.id = :userId",
                ParamsMap.of("userId", userSession.getUser().getId()),
                "userExtPersonGroup.edit");
        if (userExtPersonGroup == null) throw new ItemNotFoundException("user.do.not.have.person");
        return userExtPersonGroup;
    }
}