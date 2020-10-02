package kz.uco.tsadv.web.modules.recruitment.offer;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.enums.OfferStatus;
import kz.uco.tsadv.modules.recruitment.model.Offer;
import kz.uco.base.common.StaticVariable;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OfferBrowse extends AbstractLookup {

    @Named("offersTable.create")
    private CreateAction offersTableCreate;
    @Named("offersTable.edit")
    private EditAction offersTableEdit;
    @Named("offersTable.remove")
    private RemoveAction offersTableRemove;
    @Inject
    private CollectionDatasource<Offer, UUID> offersDs;
    @Inject
    private UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        offersTableCreate.setEditorCloseListener(actionId -> offersDs.refresh());
        offersTableEdit.setEditorCloseListener(actionId -> offersDs.refresh());
        if (params.get("idJobRequest")!=null){
            offersDs.setQuery("select e from tsadv$Offer e where e.jobRequest.id = :param$idJobRequest");
        }
        if (params.get("myOffers") != null) {
            offersTableCreate.setVisible(false);
            offersTableRemove.setVisible(false);
            offersDs.setQuery(String.format(
                    "select e from tsadv$Offer e " +
                            "join e.jobRequest jr " +
                            "where jr.candidatePersonGroup.id = :session$userPersonGroupId " +
                            "and e.status <> %d " +
                            "and e.status <> %d",
                    OfferStatus.DRAFT.getId(),
                    OfferStatus.ONAPPROVAL.getId()));
            Map<String, Object> map = new HashMap<>();
            map.put("fromMyOffer", null);
            offersTableEdit.setWindowParams(map);
        }

        if (isSelfService()) {
            PersonGroupExt personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
            if (personGroup != null) {
                offersDs.setQuery(String.format(
                        "select e from tsadv$Offer e " +
                                "join e.jobRequest jr " +
                                "join jr.requisition r " +
                                "join r.managerPersonGroup mp " +
                                "where mp.id = '%s' " +
                                "and e.status <> %d",
                        personGroup.getId(),
                        OfferStatus.DRAFT.getId()));
            }
        }

        super.init(params);
    }

    protected boolean isSelfService() {
        return false;
    }
}