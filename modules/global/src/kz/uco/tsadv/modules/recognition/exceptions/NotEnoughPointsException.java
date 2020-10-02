package kz.uco.tsadv.modules.recognition.exceptions;

import com.haulmont.cuba.core.global.Logging;
import com.haulmont.cuba.core.global.SupportedByClient;

/**
 * @author adilbekov.yernar
 */
@SupportedByClient
@Logging(Logging.Type.NONE)
public class NotEnoughPointsException extends RecognitionException {

    public NotEnoughPointsException(String message) {
        super(message);
    }

    public NotEnoughPointsException() {
        super();
    }
}
