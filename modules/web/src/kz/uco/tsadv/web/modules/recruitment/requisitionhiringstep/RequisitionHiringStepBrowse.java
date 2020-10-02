package kz.uco.tsadv.web.modules.recruitment.requisitionhiringstep;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.recruitment.model.RequisitionHiringStep;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class RequisitionHiringStepBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<RequisitionHiringStep, UUID> requisitionHiringStepsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("jobRequestId"))
            requisitionHiringStepsDs.setQuery(
                    CustomFilter.getFilteredQuery(requisitionHiringStepsDs.getQuery(), "  and not exists (select 1 " +
                    "  from tsadv$Interview i " +
                    " where i.jobRequest.id = :param$jobRequestId " +
                    "   and i.requisitionHiringStep.id = e.id " +
                    "   and i.interviewStatus <> 50) " +
                    "   and not exists (select 1 " +
                    "  from tsadv$Interview i, tsadv$RequisitionHiringStep hs" +
                    " where i.jobRequest.id = :param$jobRequestId" +
                    "   and hs.id = i.requisitionHiringStep.id " +
                    "   and i.interviewStatus in (10, 20, 30, 60, 70)) "));

        if (params.containsKey("jobRequestIds"))
            requisitionHiringStepsDs.setQuery(
                    CustomFilter.getFilteredQuery(requisitionHiringStepsDs.getQuery(), "  and not exists (select 1 " +
                    "  from tsadv$Interview i " +
                    " where i.jobRequest.id in :param$jobRequestIds " +
                    "   and i.requisitionHiringStep.id = e.id " +
                    "   and i.interviewStatus <> 50) " +
                    "   and not exists (select 1 " +
                    "  from tsadv$Interview i, tsadv$RequisitionHiringStep hs" +
                    " where i.jobRequest.id in :param$jobRequestIds" +
                    "   and hs.id = i.requisitionHiringStep.id " +
                    "   and i.interviewStatus in (10, 20, 30, 60, 70)) "));

    }
}