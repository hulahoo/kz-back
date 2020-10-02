package kz.uco.tsadv.modules.administration.importer;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.administration.importer.ImportLog;

import java.util.Map;

/**
 * @author veronika.buksha
 */
public interface ImporterAPI {

    /**
     * @param fileDescriptor Descriptor of the file to import from
     */
    void setFileDescriptor(FileDescriptor fileDescriptor);

    /**
     * @param params
     * @return Number of entities persisted
     */
    ImportLog doImport(ImportLog log, Map<String, Object> params);

    ImportHistory doImport(ImportHistory importHistory, Map<String, Object> params);

}