package kz.uco.tsadv.exceptions;

import com.haulmont.cuba.core.global.Logging;
import com.haulmont.cuba.core.global.SupportedByClient;

@SupportedByClient
@Logging(Logging.Type.BRIEF)
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message) {
        super(message);
    }
}
