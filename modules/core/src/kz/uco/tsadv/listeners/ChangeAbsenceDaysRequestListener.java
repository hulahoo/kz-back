package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kz.uco.tsadv.modules.personal.model.ChangeAbsenceDaysRequest;
import org.springframework.stereotype.Component;

@Component("tsadv_ChangeAbsenceDaysRequestListener")
public class ChangeAbsenceDaysRequestListener implements BeforeUpdateEntityListener<ChangeAbsenceDaysRequest> {

    @Override
    public void onBeforeUpdate(ChangeAbsenceDaysRequest entity, EntityManager entityManager) {

    }
}