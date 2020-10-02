package kz.uco.tsadv.importer.utils;

/**
 * @author aleksey.stukalov
 * @author veronika.buksha
 */

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.tsadv.modules.administration.importer.ImportLog;
import kz.uco.tsadv.modules.administration.importer.ImportLogRecord;
import kz.uco.tsadv.modules.administration.importer.LogRecordLevel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.UUID;

public class ImportLogHelper {

    protected ImportLog importLog;
    protected Log log;
    protected Metadata metadata;

    public ImportLogHelper(Class logClazz, ImportLog importLog) {
        this.importLog = importLog;
        metadata = AppBeans.get(Metadata.NAME);
        log = LogFactory.getLog(logClazz);
    }

    public void error(String message, Throwable t) {
        writeLogRecord(message, LogRecordLevel.ERROR, t);
        log.error(message, t);
    }

    public void warn(String message, Throwable t) {
        writeLogRecord(message, LogRecordLevel.WARN, t);
        log.warn(message, t);
    }

    public void debug(String message, Throwable t) {
        writeLogRecord(message, LogRecordLevel.DEBUG, t);
        log.debug(message, t);
    }

    public void error(String message, String userMessage, String personFullName, Throwable t) {
        writeLogRecord(message, userMessage, personFullName, false, LogRecordLevel.ERROR, t);
        log.error(message, t);
    }

    public void warn(String message, String userMessage, String personFullName, Throwable t) {
        writeLogRecord(message, userMessage, personFullName, true, LogRecordLevel.WARN, t);
        log.warn(message, t);
    }

    public void debug(String message, String userMessage, String personFullName, Throwable t) {
        writeLogRecord(message, userMessage, personFullName, true, LogRecordLevel.DEBUG, t);
        log.debug(message, t);
    }

    public void info(String message, String linkEntityName, String linkScreen, UUID linkEntityId) {
        ImportLogRecord record = writeLogRecord(message, LogRecordLevel.INFO, null);
        record.setLinkEntityName(linkEntityName);
        record.setLinkScreen(linkScreen);
        record.setLinkEntityId(linkEntityId);
    }

    private ImportLogRecord writeLogRecord(String message, LogRecordLevel level, Throwable t) {
        return writeLogRecord(metadata.create(ImportLogRecord.class), message, level, t);
    }

    private ImportLogRecord writeLogRecord(ImportLogRecord record, String message, LogRecordLevel level, Throwable t) {
        record.setImportLog(importLog);
        record.setLevel(level);
        record.setMessage(message);
        record.setTime(new Date());

        if (t != null) {
            record.setStacktrace(t.getLocalizedMessage()
                    + "\n" + ExceptionUtils.getStackTrace(t));
        }

        importLog.getRecords().add(record);

        return record;
    }

    private ImportLogRecord writeLogRecord(String message, String userMessage, String personFullName, boolean success,
                                           LogRecordLevel level, Throwable t) {
        ImportLogRecord record = metadata.create(ImportLogRecord.class);

        record.setUserMessage(userMessage);
        record.setFullName(personFullName);
        record.setSuccess(success);

        return writeLogRecord(record, message, level, t);
    }

    public void moreEntitiesProcessed(int entitiesPersisted) {
        importLog.setEntitiesProcessed(entitiesPersisted
                + (importLog.getEntitiesProcessed() != null ? importLog.getEntitiesProcessed() : 0));
    }

    public ImportLog getImportLog() {
        return importLog;
    }

}
