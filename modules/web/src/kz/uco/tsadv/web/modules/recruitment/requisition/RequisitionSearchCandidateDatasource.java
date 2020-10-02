package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.recruitment.model.RequisitionSearchCandidate;
import kz.uco.tsadv.modules.recruitment.model.RequisitionSearchCandidateResult;
import kz.uco.tsadv.service.RecruitmentService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author Adilbekov Yernar
 */
public class RequisitionSearchCandidateDatasource extends CustomCollectionDatasource<RequisitionSearchCandidateResult, UUID> {

    private RecruitmentService recruitmentService = AppBeans.get(RecruitmentService.class);

    @Override
    protected Collection<RequisitionSearchCandidateResult> getEntities(Map<String, Object> params) {
        if (params.containsKey(StaticVariable.REQUISITION_ID) && params.containsKey(StaticVariable.REQUISITION_SEARCH_CANDIDATE)) {
            UUID requisitionId = (UUID) params.get(StaticVariable.REQUISITION_ID);
            RequisitionSearchCandidate searchCandidate = (RequisitionSearchCandidate) params.get(StaticVariable.REQUISITION_SEARCH_CANDIDATE);
            String language = ((UserSessionSource) AppBeans.get(UserSessionSource.NAME)).getLocale().getLanguage();
            return recruitmentService.getRequisitionSearchCandidate(requisitionId, searchCandidate, language);
        }
        return Collections.emptyList();
    }
}
