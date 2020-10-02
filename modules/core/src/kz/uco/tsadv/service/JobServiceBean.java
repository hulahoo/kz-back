package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(JobService.NAME)
public class JobServiceBean implements JobService {

    @Inject
    protected CommonService commonService;

    @Override
    public boolean checkForExistingPositionsInSameDate(JobGroup jobGroup) {
        return !getExistingPositionsInSameDate(jobGroup, View.MINIMAL).isEmpty();
    }

    @Override
    public List<PositionExt> getExistingPositionsInSameDate(JobGroup jobGroup, String view) {
        String queryString = "select distinct e from base$PositionExt e where e.jobGroup.id = :jobGroupId";
        Map<String, Object> params = new HashMap<>();
        params.put("jobGroupId", jobGroup.getId());
        return commonService.getEntities(PositionExt.class, queryString, params, view);
    }
}