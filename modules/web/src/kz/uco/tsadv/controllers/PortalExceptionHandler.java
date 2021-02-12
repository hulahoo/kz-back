package kz.uco.tsadv.controllers;

import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.pojo.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Alibek Berdaulet
 */

@ControllerAdvice
public class PortalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(PortalExceptionHandler.class);

    @ExceptionHandler(PortalException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handlePortalException(PortalException e) {
        if (e.getCause() == null) {
            log.info("PortalException: {}", e.getMessage());
        }
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_GATEWAY.value(), e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_GATEWAY);
    }
}
