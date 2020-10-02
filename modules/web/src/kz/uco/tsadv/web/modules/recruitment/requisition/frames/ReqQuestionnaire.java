package kz.uco.tsadv.web.modules.recruitment.requisition.frames;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.modules.recruitment.model.RequisitionQuestionnaire;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author veronika.buksha
 */
public class ReqQuestionnaire extends AbstractFrame {

    @Inject
    private Metadata metadata;
    public CollectionDatasource<RequisitionQuestionnaire, UUID> questionnairesDs;
    public Datasource<Requisition> requisitionDs;

    @Inject
    private Table<RequisitionQuestionnaire> questionnairesTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        questionnairesDs = (CollectionDatasource<RequisitionQuestionnaire, UUID>) getDsContext().get("questionnairesDs");
        requisitionDs = (Datasource<Requisition>) getDsContext().get("requisitionDs");

        questionnairesDs.addItemPropertyChangeListener(e -> {
            if ("weight".equals(e.getProperty()))
                questionnairesDs.commit();
        });
    }

    public void addQuestionnaires() {
        Map<String, Object> params = new HashMap<>();
        params.put(WindowParams.MULTI_SELECT.toString(), Boolean.TRUE);
        params.put("validatePrescreening", Boolean.TRUE);
        List<RcQuestionnaire> rcQuestionnaireList = new ArrayList<>();
        questionnairesDs.getItems().forEach(rcQuestionnaires -> rcQuestionnaireList.add(rcQuestionnaires.getQuestionnaire()));
        params.put("alreadyExistRcQuestionnaires", rcQuestionnaireList);
        params.put("excludeRcQuestionnaireIds", questionnairesDs.getItems() != null ? questionnairesDs.getItems().stream().map(qq -> qq.getQuestionnaire().getId()).collect(Collectors.toList()) : null);

        openLookup("tsadv$RcQuestionnaire.lookup", items -> {
            for (Object item : items) {
                RcQuestionnaire questionnaire = (RcQuestionnaire) item;
                RequisitionQuestionnaire requisitionQuestionnaire = metadata.create(RequisitionQuestionnaire.class);
                requisitionQuestionnaire.setRequisition(requisitionDs.getItem());
                requisitionQuestionnaire.setQuestionnaire(questionnaire);
                questionnairesDs.addItem(requisitionQuestionnaire);
            }
            questionnairesDs.commit();
        }, WindowManager.OpenType.DIALOG, params);
    }
}
