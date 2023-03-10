package kz.uco.tsadv.exceptions;

import com.haulmont.cuba.core.global.Logging;
import com.haulmont.cuba.core.global.SupportedByClient;
import com.haulmont.cuba.core.global.validation.CustomValidationException;

/**
 * @author Alibek Berdaulet
 */
@SupportedByClient
@Logging(Logging.Type.BRIEF)
public class PortalException extends RuntimeException {

    public PortalException(String message) {
        super(message);
    }
}
