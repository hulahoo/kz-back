package kz.uco.tsadv.web.modules.learning.questionnaire;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.performance.model.Questionnaire;

import javax.inject.Inject;
import java.util.UUID;

public class QuestionnaireBrowse extends AbstractLookup {
    @Inject
    private GroupDatasource<Questionnaire, UUID> questionnairesDs;

    public void esitmate() {
        openWindow("tsadv$PersonQuestionnaire.browse",
                WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("questionnaire", questionnairesDs.getItem()));
    }
}