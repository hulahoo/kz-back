package kz.uco.tsadv.web.modules.personal.personcontact;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.validators.EmailValidator;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.web.abstraction.AbstractDictionaryDatasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicPhoneType;
import kz.uco.tsadv.modules.personal.model.PersonContact;
import kz.uco.tsadv.validators.PhoneValidator;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class PersonContactEdit<T extends PersonContact> extends AbstractEditor<T> {
    protected FieldGroup.FieldConfig fieldConfig;
    protected EmailValidator emailValidator = new EmailValidator();
    protected PhoneValidator phoneValidator = new PhoneValidator();

    @Named("fieldGroup.contactValueId")
    protected TextField contactValueField;

    @Named("fieldGroup.typeId")
    protected PickerField typeField;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Datasource<PersonContact> personContactDs;
    @Inject
    private AbstractDictionaryDatasource contactTypeDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("noEmail") && (boolean) params.get("noEmail")) {
            contactTypeDs.setQuery("select e from tsadv$DicPhoneType e where e.code <> 'email' ");
        }
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }

    protected void initMaskedField() {

        MaskedField maskedField = componentsFactory.createComponent(MaskedField.class);
        maskedField.setRequired(true);
        maskedField.setValueMode(MaskedField.ValueMode.CLEAR);
        maskedField.setMask("#(###)###-##-##");
        maskedField.setDatasource(personContactDs, "contactValue");
        maskedField.addValidator(phoneValidator);
        fieldConfig = fieldGroup.getFieldNN("maskedFieldId");
        fieldConfig.setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "PersonContact.contactValue"));
        fieldConfig.setComponent(maskedField);
    }

    @Override
    public void ready() {
        super.ready();

        initMaskedField();

        setVisibleComponent(false);

        PersonContact personContact = getItem();
        if (personContact != null) {
            DicPhoneType phoneType = personContact.getType();
            if (phoneType != null) {
                if (phoneType.getCode() != null) {
                    String code = phoneType.getCode();
                    if (code.equalsIgnoreCase("mobile")) {
                        setVisibleComponent(true);
                    } else if (code.equalsIgnoreCase("email")) {
                        contactValueField.addValidator(emailValidator);
                    } else {
                        contactValueField.removeValidator(emailValidator);
                    }
                }
            }
        }

//        typeField.addValueChangeListener(this::typeFieldValueChangeListener);
    }

//    protected void typeFieldValueChangeListener(ValueChangeEvent e) {
//        DicPhoneType dicPhoneType = (DicPhoneType) e.getValue();
//        if (dicPhoneType != null) {
//            String code = dicPhoneType.getCode();
//            if (code != null) {
//                if (code.equalsIgnoreCase("email")) {
//                    contactValueField.addValidator(emailValidator);
//                } else {
//                    contactValueField.removeValidator(emailValidator);
//                }
//
//                setVisibleComponent(code.equalsIgnoreCase("mobile"));
//            } else {
//                contactValueField.removeValidator(emailValidator);
//                getItem().setContactValue(null);
//                setVisibleComponent(false);
//            }
//        } else {
//            contactValueField.removeValidator(emailValidator);
//            getItem().setContactValue(null);
//            setVisibleComponent(false);
//        }
//    }


    protected void setVisibleComponent(boolean isMobile) {
        contactValueField.setVisible(!isMobile);
        fieldConfig.setVisible(isMobile);
    }
}