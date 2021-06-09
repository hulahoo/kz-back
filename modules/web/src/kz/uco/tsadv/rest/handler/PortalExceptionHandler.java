package kz.uco.tsadv.rest.handler;

import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.pojo.ExceptionResponse;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Alibek Berdaulet
 */
@ControllerAdvice
public class PortalExceptionHandler implements PriorityOrdered {

    @ExceptionHandler(PortalException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handlePortalException(PortalException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
