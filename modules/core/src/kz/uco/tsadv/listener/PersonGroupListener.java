package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.PersonCoin;
import kz.uco.tsadv.modules.recognition.PersonPoint;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("tsadv_PersonGroupListener")
public class PersonGroupListener implements BeforeInsertEntityListener<PersonGroupExt> {

    @Inject
    private Metadata metadata;

    @Override
    public void onBeforeInsert(PersonGroupExt personGroup, EntityManager entityManager) {
        PersonPoint personPoint = metadata.create(PersonPoint.class);
        personPoint.setPersonGroup(personGroup);
        personPoint.setPoints(0L);
        entityManager.persist(personPoint);

        PersonCoin personCoin = metadata.create(PersonCoin.class);
        personCoin.setPersonGroup(personGroup);
        personCoin.setCoins(0L);
        entityManager.persist(personCoin);
    }
}