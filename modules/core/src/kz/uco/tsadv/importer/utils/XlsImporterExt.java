package kz.uco.tsadv.importer.utils;

import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.importer.exception.PersistEntityException;
import kz.uco.tsadv.modules.administration.importer.ImportLog;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.EmployeeService;
import org.apache.poi.ss.usermodel.Row;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

public abstract class XlsImporterExt extends XlsImporter {
    @Inject
    protected EmployeeService employeeService;

    protected PersonGroupExt personGroupExt;
    protected int currentColumnIndex;

    @Override
    public ImportLog doImport(ImportLog importLog, Map<String, Object> params) {
        logHelper = new ImportLogHelper(this.getClass(), importLog);

        try {
            init(params);
        } catch (FileStorageException | IOException e) {
            logHelper.error("Excel workbook was not initialized", e);
            return logHelper.getImportLog();
        }

        int entitiesPersisted = 0;
        try {
            for (currentRowIndex = firstDataRowIndex; !eof(getSheet().getRow(currentRowIndex)); currentRowIndex += dataRowIncrement) {
                try {
                    Row row = getSheet().getRow(currentRowIndex);
                    importXlsRow(params, row);

                } catch (PersistEntityException e) {
                    logHelper.error(String.format("Error while persisting entities for row: %s", currentRowIndex + 1), e);
                } catch (ClassCastException e) {
                    logHelper.error(e.getMessage(), e.getMessage() +
                                    "\n Не правильный формат данных! " + String.format("\n Строка №: %d, колонка №: %d", (currentRowIndex + 1),
                            (getSheet().getRow(currentRowIndex).getCell(currentColumnIndex).getAddress().getColumn() + 1)),
                            personGroupExt != null ? personGroupExt.getFioWithEmployeeNumber() : "personGroup is null", e);
                } catch (NullPointerException e) {
                    logHelper.error("NullPointerException", e.getMessage(), personGroupExt != null
                            ? personGroupExt.getFioWithEmployeeNumber() : "PersonGroup not found", e);
                } catch (RuntimeException e) {
                    logHelper.error("RuntimeException",
                            e.getMessage(), personGroupExt != null
                                    ? personGroupExt.getFioWithEmployeeNumber() : "PersonGroup not found", e);
                } catch (Exception t) {
                    logHelper.error(String.format("Error while processing row: %s", currentRowIndex), t);
                }
            }
        } catch (ImportFileEofEvaluationException e) {
            logHelper.error(e.getMessage(), e);
        }
        logHelper.moreEntitiesProcessed(entitiesPersisted);

        return logHelper.getImportLog();
    }
}