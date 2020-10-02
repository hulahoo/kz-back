package kz.uco.tsadv.modules.recognition.exceptions;

import com.haulmont.cuba.core.global.Logging;
import com.haulmont.cuba.core.global.SupportedByClient;

/**
 * @author adilbekov.yernar
 */
@SupportedByClient
@Logging(Logging.Type.NONE)
public class RecognitionException extends RuntimeException {

    public RecognitionException() {
    }

    public RecognitionException(String message) {
        super(message);
    }
}
