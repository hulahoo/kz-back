package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.tsadv.service.ImporterService;
import kz.uco.tsadv.modules.administration.importer.Importer;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * @author aleksey.stukalov
 */
public class ImportersDatasource extends CustomCollectionDatasource<Importer, UUID> {

    private ImporterService importerService = AppBeans.get(ImporterService.NAME);

    @Override
    protected Collection<Importer> getEntities(Map<String, Object> params) {
        return importerService.getImporters();
    }
}
