package kz.uco.tsadv.web.modules.personal.address;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.entity.dictionary.DicCity;
import kz.uco.base.entity.dictionary.DicCountry;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAddressType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class AddressEdit extends AbstractEditor<Address> {
    private static final String KZ_ISO_NUMBER_CODE = "398";
    private static final Logger log = LoggerFactory.getLogger(AddressEdit.class);
    PersonGroupExt personGroup;


    @WindowParam(name = "address")
    protected Collection<Address> address;
    @Inject
    protected Datasource<Address> addressDs;
    @Inject
    protected CommonService commonService;

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    @Named("fieldGroup.startDate")
    protected DateField startDateField;

    @Inject
    @Named("fieldGroup.endDate")
    protected DateField endDateField;

    @Named("fieldGroup.addressType")
    protected PickerField addressTypeField;

    @Named("fieldGroup.city")
    protected PickerField<DicCity> cityField;
    private Map<String, Object> param;

    @Override
    protected void initNewItem(Address item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
        if (personGroup != null) {
            item.setPersonGroup(personGroup);
        }

        DicCountry country = commonService.getEntity(DicCountry.class, KZ_ISO_NUMBER_CODE);
        item.setCountry(country);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param = params;
        if (params.containsKey("personGroup")) {
            personGroup = (PersonGroupExt) params.get("personGroup");
        }
        addressTypeField.removeAction(PickerField.OpenAction.NAME);

        addressTypeField.addValidator(value -> {
            if (value != null) {
                DicAddressType addressType = (DicAddressType) value;
                if ("BIRTHPLACE".equals(addressType.getCode())) {
                    Map<String, Object> queryParams = new HashMap<>();
                    queryParams.put("personGroupId", getItem().getPersonGroup() != null ? getItem().getPersonGroup().getId() : null);
                    queryParams.put("id", getItem().getId());

                    List<Address> existed = commonService.getEntities(Address.class,
                            "select e " +
                                    " from tsadv$Address e, tsadv$DicAddressType at " +
                                    " where e.personGroupId.id = :personGroupId " +
                                    " and e.addressType.id = at.id " +
                                    " and at.code = 'BIRTHPLACE'" +
                                    " and e.id <> :id " +
                                    " and e.deleteTs is null",
                            queryParams,
                            View.LOCAL);
                    if (existed != null && existed.size() > 0)
                        throw new ValidationException(getMessage("AddressEdit.birthplace.validatorMsg"));
                }
            }
        });

        startDateField.addValidator(value -> {
            if (value != null && getItem().getEndDate() != null) {
                Date startDate = (Date) value;
                if (startDate.after(getItem().getEndDate()))
                    throw new ValidationException(getMessage("AbstractHrEditor.startDate.validatorMsg"));

               /* if (addressExists("startDate"))
                    throw new ValidationException(getMessage("AddressEdit.startEndDate.validatorMsg"));*/
            }
        });

        endDateField.addValidator(value -> {
            if (value != null && getItem().getStartDate() != null) {
                Date endDate = (Date) value;
                if (endDate.before(getItem().getStartDate()))
                    throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg"));

                /*if (addressExists("endDate"))
                    throw new ValidationException(getMessage("AddressEdit.startEndDate.validatorMsg"));*/
            }
        });

    }

    @Override
    public void ready() {
        super.ready();
        if (param.containsKey("fromRequisitionJobRequest")) {
            cityField.setRequired(true);
            List<FieldGroup.FieldConfig> fields = fieldGroup.getFields();
            for (FieldGroup.FieldConfig field : fields) {
                if (!field.getProperty().equals("city")) {
                    field.setVisible(false);
                } else {
                    ((TextField<String>) field.getComponentNN()).addValueChangeListener(e -> addressDs.getItem().setAddress(e.getValue()));
                }
            }
        }
    }

    protected boolean addressExists(String field) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", getItem().getId());
        params.put("personGroupId", getItem().getPersonGroup() != null ? getItem().getPersonGroup().getId() : null);
        params.put("addressTypeId", getItem().getAddressType().getId());
        params.put("startDate", getItem().getStartDate());
        params.put("endDate", getItem().getEndDate());

        List<Address> addresses = commonService.getEntities(Address.class, "select e " +
                        " from tsadv$Address e " +
                        " where e.personGroupId.id = :personGroupId " +
                        " and e.addressType.id = :addressTypeId " +
                        " and e.deleteTs is null " +
                        " and e.id <> :id " +
                        " and (" +
                        " (:" + field + " between e.startDate and e.endDate) or " +
                        " (e.startDate >= :startDate and e.endDate <= :endDate) " +
                        " )",
                params,
                View.LOCAL);
        return addresses != null && !addresses.isEmpty();

    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (errors.isEmpty() && hasAddres(getItem())) {
            errors.add(getMessage(String.format(getMessage("already.has.address"), getItem().getAddressType().getLangValue())));
        }
    }

    protected boolean hasAddres(Address address) {
        return commonService.getCount(Address.class,
                "select e from tsadv$Address e " +
                        " where e.addressType.id = :typeId " +
                        "   and e.personGroupId.id = :personGroupId " +
                        "   and e.startDate <= :endDate and e.endDate >= :startDate " +
                        "   and e.id <> :id",
                ParamsMap.of("personGroupId", address.getPersonGroup().getId(),
                        "typeId", address.getAddressType().getId(),
                        "startDate", address.getStartDate(),
                        "endDate", address.getEndDate(),
                        "id", address.getId())) > 0;
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && close) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }


}