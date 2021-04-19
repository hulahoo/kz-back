package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.LeavingVacationRequest;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component("tsadv_LeavingVacationRequestListener")
public class LeavingVacationRequestListener implements BeforeUpdateEntityListener<LeavingVacationRequest> {


    protected String APPROVED_STATUS = "APPROVED";
    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    protected String MATERNITY_RECALL_API_URL = "http://10.2.200.101:8290/api/ahruco/maternity/recall/request";

    @Override
    public void onBeforeUpdate(LeavingVacationRequest entity, EntityManager entityManager) {

    }

    protected boolean isApproved(LeavingVacationRequest entity, EntityManager entityManager) {
        DicRequestStatus status = entity.getStatus();
        if (status == null) return false;
        return APPROVED_STATUS.equals(entityManager.reloadNN(status, View.LOCAL).getCode());
    }

    protected String getApiUrl(){
        return MATERNITY_RECALL_API_URL;
    }
}