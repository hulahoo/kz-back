package kz.uco.tsadv.web.bpmusersubstitution;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.BpmUserSubstitution;
import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.service.BpmUserSubstitutionService;

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

        startDate.addValueChangeListener(e -> endDate.setRangeStart(e.getValue()));
        endDate.addValueChangeListener(e -> startDate.setRangeEnd(e.getValue()));

        substitutedUserPickerField.getLookupAction().setLookupScreenParamsSupplier(() -> ParamsMap.of("EMPLOYEE", true));
        userPickerField.getLookupAction().setLookupScreenParamsSupplier(() -> ParamsMap.of("EMPLOYEE", true));

        substitutedUserPickerField.addValueChangeListener(e -> getItem().setSubstitutedUser(e.getValue() != null ? (TsadvUser) e.getValue() : null));
        userPickerField.addValueChangeListener(e -> getItem().setUser(e.getValue() != null ? (TsadvUser) e.getValue() : null));
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
        if (errors.isEmpty()) {
            try {
                bpmUserSubstitutionService.validate(getItem());
            } catch (PortalException e) {
                errors.add(e.getMessage());
            }
        }
    }
}