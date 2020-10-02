package kz.uco.tsadv.importer.utils;

/**
 * @author aleksey.stukalov
 */

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.common.CollectionResult;
import kz.uco.base.importer.AbstractXlsImporter;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.importer.exception.PersistEntityException;
import kz.uco.tsadv.modules.administration.importer.ImportHistory;
import kz.uco.tsadv.modules.administration.importer.ImportLog;
import kz.uco.tsadv.modules.administration.importer.ImporterAPI;
import kz.uco.tsadv.service.ImportHistoryService;
import kz.uco.tsadv.service.JsonService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class XlsImporter extends AbstractXlsImporter implements ImporterAPI {

    protected ImportLogHelper logHelper;
    protected ImportHistoryService importHistoryService;
    protected ImportHistory importHistory;
    protected JsonService jsonService;
    protected boolean writeLog;

    @Override
    public CollectionResult doImport(Map<String, Object> params) throws Exception {
        init(params);

        try {
            for (currentRowIndex = firstDataRowIndex; !eof(sheet.getRow(currentRowIndex)); currentRowIndex += dataRowIncrement) {
                logger.info(String.format("Read row %s from xls ", currentRowIndex));
                Row row = sheet.getRow(currentRowIndex);
                try {
                    importXlsRow(params, row);
                } catch (PersistEntityException e) {
                    if (logHelper != null)
                        logHelper.error(String.format("Error while persisting entities for row: %s", currentRowIndex), e);
                    importHistoryService.error(importHistory, jsonService.convertToString(e.getEntitiesToPersist()),
                            String.format("Error while persisting entities for row: %s", currentRowIndex),
                            entitiesPersisted, e);
                } catch (Exception e) {
                    if (logHelper != null)
                        logHelper.error(String.format("Error while processing row: %s", currentRowIndex), e);
                    importHistoryService.error(importHistory, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            if (logHelper != null) logHelper.error(e.getMessage(), e);
            importHistoryService.error(importHistory, e.getMessage(), e);
        }

        return null;
    }

    public ImportLog doImport(ImportLog importLog, Map<String, Object> params) {
        logHelper = new ImportLogHelper(this.getClass(), importLog);

        try {
            doImport(params);
            logHelper.moreEntitiesProcessed(entitiesPersisted);
        } catch (FileStorageException | IOException e) {
            logHelper.error("Excel workbook was not initialized", e);
        } catch (Exception e) {
            logHelper.error(e.getMessage(), e);
        }

        return logHelper.getImportLog();
    }

    @Override
    public ImportHistory doImport(ImportHistory importHistory, Map<String, Object> params) {
        this.importHistory = importHistory;

        try {
            init(params);
        } catch (FileStorageException | IOException e) {
            importHistoryService.error(importHistory, "Excel workbook was not initialized", e);
            return importHistory;
        }

        int entitiesPersisted = 0;

        try {
            for (currentRowIndex = firstDataRowIndex; !eof(sheet.getRow(currentRowIndex)); currentRowIndex += dataRowIncrement) {
                try {
                    Map<String, Object> values = new HashMap<>();
                    Row row = sheet.getRow(currentRowIndex);
                    Object objValue;
                    Cell cell;
                    for (String attribute : attributesToColumns.keySet()) {
                        cell = row.getCell(attributesToColumns.get(attribute));
                        if (cell != null) {
                            objValue = XlsHelper.getCellValue(cell);
                            values.put(attribute, objValue);
                        }
                    }

                    if (defaultValues != null)
                        for (String attribute : defaultValues.keySet()) {
                            if (!values.keySet().contains(attribute)) {
                                values.put(attribute, defaultValues.get(attribute));
                            } else values.putIfAbsent(attribute, defaultValues.get(attribute));
                        }

                    List<BaseGenericIdEntity> entitiesToPersist = getEntitiesToPersist(values, params);
                    try {
                        persistEntities(entitiesToPersist);
                        entitiesPersisted += entitiesToPersist.size();
                        if (params.containsKey("writeLog") && ((boolean) params.get("writeLog"))) {
                            if (params.containsKey("customLogText")) {
                                importHistoryService.log(importHistory, jsonService.convertToString(entitiesToPersist),
                                        "With warning " + "[ " + params.get("customLogText") + " ]", entitiesPersisted);
                                params.remove("customLogText");
                            } else {
                                importHistoryService.info(importHistory, jsonService.convertToString(entitiesToPersist),
                                        "successfully", entitiesPersisted);
                            }
                        }
                    } catch (Exception e) {
                        importHistoryService.error(importHistory, jsonService.convertToString(entitiesToPersist), String.format("Error while persisting entities for row: %s", currentRowIndex),
                                entitiesPersisted, e);
                    }

                    afterEntitiesPersisted(entitiesToPersist);
                } catch (Throwable t) {
                    importHistoryService.error(importHistory, String.format("Error while processing row: %s", currentRowIndex), t);
                }
            }
        } catch (ImportFileEofEvaluationException e) {
            importHistoryService.error(importHistory, e.getMessage(), e);
        }

        return importHistory;
    }

    protected Map<String, Object> init(Map<String, Object> params) throws FileStorageException, IOException {
        super.init(params);

        writeLog = Boolean.TRUE.equals(params.get("writeLog"));

        importHistoryService = AppBeans.get(ImportHistoryService.class);
        jsonService = AppBeans.get(JsonService.class);

        firstDataRowIndex = 1;
        return params;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public ImportHistory getImportHistory() {
        return importHistory;
    }

    public void setImportHistory(ImportHistory importHistory) {
        this.importHistory = importHistory;
    }
}