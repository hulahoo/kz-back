package kz.uco.tsadv.controllers;

import com.haulmont.addon.restapi.api.controllers.ServicesController;
import com.haulmont.cuba.core.global.RemoteException;
import kz.uco.tsadv.exceptions.PortalException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Alibek Berdaulet
 */
public class ExtServicesController extends ServicesController {

    @Override
    @PostMapping("/{serviceName}/{methodName}")
    public ResponseEntity<String> invokeServiceMethodPost(@PathVariable String serviceName,
                                                          @PathVariable String methodName,
                                                          @RequestParam(required = false) String modelVersion,
                                                          @RequestBody(required = false) String paramsJson) throws Throwable {
        return callOnTryCatch(() -> super.invokeServiceMethodPost(serviceName, methodName, modelVersion, paramsJson));
    }

    @Override
    @GetMapping("/{serviceName}/{methodName}")
    public ResponseEntity<String> invokeServiceMethodGet(@PathVariable String serviceName,
                                                         @PathVariable String methodName,
                                                         @RequestParam(required = false) String modelVersion,
                                                         @RequestParam Map<String, String> paramsMap) throws Throwable {
        return callOnTryCatch(() -> super.invokeServiceMethodGet(serviceName, methodName, modelVersion, paramsMap));
    }

    /**
     * if Throwable contain PortalException then PortalException will be throw
     */
    protected <T> T callOnTryCatch(Call<T> call) throws Throwable {
        try {
            return call.call();
        } catch (RemoteException e) {
            for (RemoteException.Cause cause : e.getCauses()) {
                if (cause.getClassName().equals(PortalException.class.getName())) {
                    throw new PortalException(cause.getMessage());
                }
            }
            throw e;
        }
    }

    @FunctionalInterface
    interface Call<T> {
        T call() throws Throwable;
    }
}
