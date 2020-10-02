package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(CallStoredFunctionService.NAME)
public class CallStoredFunctionServiceBean implements CallStoredFunctionService {
    @Inject
    protected Persistence persistence;
    @Inject
    protected Logger log;

    public void execSqlCallFunction(String sql) {
        Object singleResult = null;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(sql);
            singleResult = query.getSingleResult();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (singleResult != null) {
            log.debug(singleResult.toString());
        }
    }

    public void execSqlCallProcedure(String sql) {
        int i = -1;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(sql);
            i = query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (i != -1) {
            log.debug(Integer.toString(i));
        }
    }

    @Override
    public Object execCallSqlFunction(String sql) {
        Object singleResult = null;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(sql);
            singleResult = query.getSingleResult();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return singleResult;
    }

    @Override
    public List<Object> execSqlCallProcedureList(String sql) {
        List<Object> resultList = null;
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(sql);
            resultList = query.getResultList();
            tx.commit();
            tx.end();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resultList != null) {
            log.debug(resultList.toString());
        }
        return resultList;
    }
}