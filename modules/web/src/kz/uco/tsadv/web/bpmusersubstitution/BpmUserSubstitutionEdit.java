package kz.uco.tsadv.web.bpmusersubstitution;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.BpmUserSubstitution;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.service.BpmUserSubstitutionService;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

public class BpmUserSubstitutionEdit extends AbstractEditor<BpmUserSubstitution> {
    @Inject
    private DateField<Date> startDate;
    @Inject
    private DateField<Date> endDate;
    @Inject
    private HBoxLayout user;
    @Inject
    private BpmUserSubstitutionService bpmUserSubstitutionService;
    @Inject
    private PickerField<Entity> substitutedUserPickerField;
    @Inject
    private PickerField<Entity> userPickerField;
    @Inject
    private CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        user.setVisible(!params.containsKey("isUserEditable") || ((boolean) params.get("isUserEditable")));

        startDate.addValueChangeListener(e -> endDate.setRangeStart((Date) e.getValue()));
        endDate.addValueChangeListener(e -> startDate.setRangeEnd((Date) e.getValue()));

        substitutedUserPickerField.getLookupAction().setLookupScreenParamsSupplier(() -> ParamsMap.of("EMPLOYEE", true));
        userPickerField.getLookupAction().setLookupScreenParamsSupplier(() -> ParamsMap.of("EMPLOYEE", true));

        substitutedUserPickerField.addValueChangeListener(e -> getItem().setSubstitutedUser(e.getValue() != null ? (UserExt) e.getValue() : null));
        userPickerField.addValueChangeListener(e -> getItem().setUser(e.getValue() != null ? (UserExt) e.getValue() : null));
    }

    @Override
    public void ready() {
        super.ready();
        userPickerField.setValue(getItem().getUser());
        substitutedUserPickerField.setValue(getItem().getSubstitutedUser());
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (getItem().getUser() == null || getItem().getSubstitutedUser() == null) return;
        StringBuilder mes = new StringBuilder();
        if (getItem().getSubstitutedUser().getId().equals(getItem().getUser().getId())) {
            mes.append(getMessage("sameUser"));
        }
        if (getItem().getStartDate() == null || getItem().getEndDate() == null) return;
        if (bpmUserSubstitutionService.hasBpmUserSubstitution(getItem().getId(), getItem().getUser(), getItem().getStartDate(), getItem().getEndDate())) {
            mes.append(getMessage("haveBpmUserSubstitution"));
        }
        if (bpmUserSubstitutionService.isCycle(getItem())) {
            mes.append(!StringUtils.isEmpty(mes.toString()) ? '\n' : '\0').append(getMessage("cycle"));
        }
        if (!StringUtils.isEmpty(mes.toString())) {
            throw new ItemNotFoundException(mes.toString());
        }
    }
}