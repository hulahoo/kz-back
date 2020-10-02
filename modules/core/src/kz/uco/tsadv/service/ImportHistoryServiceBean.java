package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.modules.administration.importer.ImportHistory;
import kz.uco.tsadv.modules.administration.importer.ImportHistoryLog;
import kz.uco.tsadv.modules.administration.importer.ImportLogLevel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

@Service(ImportHistoryService.NAME)
public class ImportHistoryServiceBean implements ImportHistoryService {

    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;
    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public void log(ImportHistory importHistory, String params, String message, int entitiesProcessed) {
        writeRecord(importHistory, userSessionSource.getUserSession().getUser().getLogin(),
                params, message, entitiesProcessed, true, new Date(), ImportLogLevel.LOG, null);
    }

    @Override
    public void log(ImportHistory importHistory,String login, String params, String message, int entitiesProcessed) {
        writeRecord(importHistory, login, params, message,
                entitiesProcessed, true, new Date(), ImportLogLevel.LOG, null);
    }

    @Override
    public void log(ImportHistory importHistory,String login, String params, String message, int entitiesProcessed,
                    Boolean success, Date dateTime) {
        writeRecord(importHistory, login, params, message,
                entitiesProcessed, success, dateTime, ImportLogLevel.LOG, null);
    }

    @Override
    public void error(ImportHistory importHistory, String login,
                      String params, String message, int entitiesProcessed, Throwable t) {
        writeRecord(importHistory, login, params, message,
                entitiesProcessed, false, new Date(), ImportLogLevel.ERROR, t);
    }

    @Override
    public void error(ImportHistory importHistory, String message, Throwable t) {
        writeRecord(importHistory, userSessionSource.getUserSession().getUser().getLogin(),
                null, message, 0, false, new Date(), ImportLogLevel.ERROR, t);
    }

    @Override
    public void error(ImportHistory importHistory, String params, String message, int entitiesProcessed, Throwable t) {
        writeRecord(importHistory, userSessionSource.getUserSession().getUser().getLogin(), params, message,
                entitiesProcessed, false, new Date(), ImportLogLevel.ERROR, t);
    }

    @Override
    public void error(ImportHistory importHistory, String params, String message, Throwable t) {
        writeRecord(importHistory, userSessionSource.getUserSession().getUser().getLogin(),
                params, message, 0, false, new Date(), ImportLogLevel.ERROR, t);
    }

    @Override
    public void error(ImportHistory importHistory,String login,
                      String params, String message, int entitiesProcessed, Throwable t, Date dateTime) {
        writeRecord(importHistory, login, params, message,
                entitiesProcessed, false, dateTime, ImportLogLevel.ERROR, t);
    }

    @Override
    public void info(ImportHistory importHistory, String params, String message, int entitiesProcessed) {
        writeRecord(importHistory, userSessionSource.getUserSession().getUser().getLogin(),
                params, message, entitiesProcessed, true, new Date(), ImportLogLevel.SUCCESS, null);
    }

    @Override
    public void info(ImportHistory importHistory,String login, String params, String message, int entitiesProcessed) {
        writeRecord(importHistory, login, params, message,
                entitiesProcessed, true, new Date(), ImportLogLevel.SUCCESS, null);
    }

    @Override
    public void info(ImportHistory importHistory,String login, String params, String message, int entitiesProcessed,
                        Boolean success, Date dateTime) {
        writeRecord(importHistory, login, params, message,
                entitiesProcessed, success, dateTime, ImportLogLevel.SUCCESS, null);
    }


    protected void writeRecord(ImportHistory importHistory, String login, String params, String message,
                               int entitiesProcessed,
                               Boolean success, Date dateTime, ImportLogLevel importLogLevel, Throwable t) {
        ImportHistoryLog importHistoryLog = metadata.create(ImportHistoryLog.class);
        importHistoryLog.setImportHistory(importHistory);
        importHistoryLog.setLogin(login);
        importHistoryLog.setParams(params);
        importHistoryLog.setMessage(message);
        importHistoryLog.setSuccess(success);
        importHistoryLog.setDateTime(dateTime);
        importHistoryLog.setLevel(importLogLevel);
        importHistoryLog.setEntitiesProcessed(entitiesProcessed);
        if (t != null) {
            importHistoryLog.setStacktrace(t.getLocalizedMessage()
                    + "\n" + ExceptionUtils.getStackTrace(t));
        }
        dataManager.commit(importHistoryLog);
    }
}