package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.UUID;

@Component("tsadv_OrganizationExtChangedListener")
public class OrganizationExtChangedListener {

    @Inject
    private TransactionalDataManager txDataManager;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<OrganizationExt, UUID> event) {

        if (!event.getType().equals(EntityChangedEvent.Type.DELETED)) {
            Id<OrganizationExt, UUID> entityId = event.getEntityId();

            OrganizationExt organization = txDataManager.load(entityId).view("organization.edit").one();

            OrganizationGroupExt organizationGroup = txDataManager.load(OrganizationGroupExt.class)
                    .query("select j from base$OrganizationGroupExt j where j.id = :id")
                    .parameter("id", organization.getGroup().getId())
                    .view("organizationGroup.browse")
                    .one();

            if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
                if (organization.getEndDate().compareTo(BaseCommonUtils.getSystemDate()) >= 0
                        && organization.getStartDate().compareTo(BaseCommonUtils.getSystemDate()) <= 0){

                    saveOrganizationGroupNames(organizationGroup,organization);
                }
            }

            if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
                saveOrganizationGroupNames(organizationGroup,organization);
            }
        }

    }


    public void saveOrganizationGroupNames(OrganizationGroupExt organizationGroup, OrganizationExt organization) {

        organizationGroup.setOrganizationNameLang1(organization.getOrganizationNameLang1());
        organizationGroup.setOrganizationNameLang2(organization.getOrganizationNameLang2());
        organizationGroup.setOrganizationNameLang3(organization.getOrganizationNameLang3());
        organizationGroup.setOrganizationNameLang4(organization.getOrganizationNameLang4());
        organizationGroup.setOrganizationNameLang5(organization.getOrganizationNameLang5());

        txDataManager.save(organizationGroup);
    }
}