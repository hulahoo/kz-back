package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.tsadv.entity.RestIntegrationLog;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

@Service(RestIntegrationLogService.NAME)
public class RestIntegrationLogServiceBean implements RestIntegrationLogService {

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;

    @Override
    public void log(String requestId, String login, String methodName, String params, String message, Boolean success, Date dateTime) {
        RestIntegrationLog log = metadata.create(RestIntegrationLog.class);
        log.setLogin(login);
        log.setRequestId(requestId);
        log.setMethodName(methodName);
        log.setParams(params);
        log.setMessage(message);
        log.setSuccess(success);
        log.setDateTime(dateTime);

        dataManager.commit(log);
    }

    @Override
    public void log(String login, String methodName, String params, String message, Boolean success, Date dateTime) {
        RestIntegrationLog log = metadata.create(RestIntegrationLog.class);
        log.setLogin(login);
        log.setMethodName(methodName);
        log.setParams(params);
        log.setMessage(message);
        log.setSuccess(success);
        log.setDateTime(dateTime);

        dataManager.commit(log);
    }
}