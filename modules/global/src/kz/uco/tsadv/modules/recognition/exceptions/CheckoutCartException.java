package kz.uco.tsadv.modules.recognition.exceptions;

import com.haulmont.cuba.core.global.SupportedByClient;

/**
 * @author adilbekov.yernar
 */
@SupportedByClient
public class CheckoutCartException extends RuntimeException {

    public CheckoutCartException() {
        super();
    }

    public CheckoutCartException(String message) {
        super(message);
    }
}
