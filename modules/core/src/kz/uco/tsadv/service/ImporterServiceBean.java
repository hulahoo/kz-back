package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.tsadv.modules.administration.importer.ImportHistory;
import kz.uco.tsadv.modules.administration.importer.ImportLog;
import kz.uco.tsadv.modules.administration.importer.Importer;
import kz.uco.tsadv.modules.administration.importer.ImporterAPI;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service(ImporterService.NAME)
public class ImporterServiceBean implements ImporterService {

    @Inject
    protected Metadata metadata;

    @Inject
    protected Persistence persistence;

    @Override
    public Set<Importer> getImporters() {
        Set<Importer> result = new LinkedHashSet<>();

        Map<String, ImporterAPI> importerBeans = AppBeans.getAll(ImporterAPI.class);

        for (String beanName : importerBeans.keySet()) {
            Importer importer = metadata.create(Importer.class);
            importer.setBeanName(beanName);
            result.add(importer);
        }

        return result;
    }

    @Override
    public ImportLog doImport(ImportLog log, Map<String, Object> params, boolean doPersistLog) {
        log.setStarted(new Date());
        try {
            ImporterAPI importerAPI = AppBeans.get(log.getImportScenario().getImporterBeanName());
            importerAPI.setFileDescriptor(log.getFile());

            log = importerAPI.doImport(log, params);

        } finally {
            log.setFinished(new Date());

            if (doPersistLog)
                persistLog(log);
        }
        return log;
    }

    @Override
    public ImportHistory doImport(ImportHistory importHistory, Map<String, Object> params, boolean doPersistLog) {
        importHistory.setStarted(new Date());
        try {
            ImporterAPI importerAPI = AppBeans.get(importHistory.getImportScenario().getImporterBeanName());
            importerAPI.setFileDescriptor(importHistory.getFile());

            importHistory = importerAPI.doImport(importHistory, params);

        } finally {
            importHistory.setFinished(new Date());

            if (doPersistLog)
                persistImportHistory(importHistory);
        }
        return importHistory;
    }

    protected ImportLog persistLog(ImportLog log) {
        ImportLog result = log;
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();

            if (PersistenceHelper.isNew(log))
                em.persist(log);
            else
                result = em.merge(log);

            log.getRecords().forEach(em::persist);
            tx.commit();
        }
        return result;
    }
    protected ImportHistory persistImportHistory(ImportHistory log) {
        ImportHistory result = log;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            if (PersistenceHelper.isNew(log))
                em.persist(log);
            else
                result = em.merge(log);

            log.getRecords().forEach(em::persist);
            tx.commit();
        }finally {
            tx.end();
        }
        return result;
    }
}