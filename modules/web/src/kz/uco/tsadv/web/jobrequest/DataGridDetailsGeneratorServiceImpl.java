package kz.uco.tsadv.web.jobrequest;

import kz.uco.tsadv.modules.recruitment.model.Interview;


import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(DataGridDetailsGeneratorService.NAME)
public class DataGridDetailsGeneratorServiceImpl implements DataGridDetailsGeneratorService {

    @Inject
    private DataManager dataManager;

    @Override
    public List<Interview> loadInterviewsById(UUID jobRequestId) {
        LoadContext<Interview> lc = LoadContext.create(Interview.class);
        LoadContext.Query query =
                LoadContext.createQuery("select e from tsadv$Interview e where e.jobRequest.id = :jobRequestId")
                        .setParameter("jobRequestId", jobRequestId);
        lc.setView("interview-view-withquestianare");
        lc.setQuery(query);
        return dataManager.loadList(lc);
    }
}
