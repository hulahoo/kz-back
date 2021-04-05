package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kz.uco.tsadv.modules.personal.model.AbsenceRvdRequest;
import org.springframework.stereotype.Component;

@Component("tsadv_AbsenceRvdRequestListener")
public class AbsenceRvdRequestListener implements BeforeUpdateEntityListener<AbsenceRvdRequest> {

    @Override
    public void onBeforeUpdate(AbsenceRvdRequest entity, EntityManager entityManager) {

    }
}