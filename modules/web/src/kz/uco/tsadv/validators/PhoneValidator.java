package kz.uco.tsadv.validators;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.ValidationException;
import kz.uco.tsadv.modules.personal.dictionary.DicOperatorCode;
import kz.uco.tsadv.service.UcoCommonService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adilbekov Yernar
 */
public class PhoneValidator implements Field.Validator {

    protected Messages messages = AppBeans.get(Messages.NAME);
    protected UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
    protected UcoCommonService ucoCommonService = AppBeans.get(UcoCommonService.class);

    private List<String> codes = new ArrayList<>();

    public PhoneValidator() {
        //TODO: realize method fillPhoneCode
        for (DicOperatorCode operatorCode : ucoCommonService.fillPhoneCodes()) {
            this.codes.add(operatorCode.getCode());
        }
    }

    @Override
    public void validate(Object value) throws ValidationException {
        if (codes.isEmpty()) {
            for (DicOperatorCode operatorCode : ucoCommonService.fillPhoneCodes()) {
                this.codes.add(operatorCode.getCode());
            }
        }

        boolean correct = false;
        String messageKey = "phone.validator.error";

        if (codes.isEmpty()) {
            messageKey = "phone.validator.codes.null";
        } else {
            if (value != null) {
                String phone = (String) value;
                if (phone.startsWith("7")) {
                    String code = phone.substring(1, 4);
                    if (codes.contains(code)) {
                        correct = true;
                    } else {
                        messageKey = "phone.validator.code.error";
                    }
                }
            }
        }

        if (!correct) throw new ValidationException(messages.getMainMessage(messageKey, userSessionSource.getLocale()));
    }
}
