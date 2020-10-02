package kz.uco.tsadv.web.modules.performance.assessmentparticipant;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.performance.dictionary.DicParticipantType;
import kz.uco.tsadv.modules.performance.model.AssessmentParticipant;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.util.UUID;

public class AssessmentParticipantEdit extends AbstractEditor<AssessmentParticipant> {
    private static final String IMAGE_CELL_HEIGHT = "50px";

    AssessmentParticipant ap;

    @Inject
    private Datasource<AssessmentParticipant> assessmentParticipantDs;

    @Inject
    private CollectionDatasource<PersonGroupExt, UUID> personGroupsDs;
    @Inject
    private DataManager dataManager;

    @Override
    protected void postInit() {
        super.postInit();
        ap = assessmentParticipantDs.getItem();
        ap.setParticipantType(getParticipantType());
        ap.setGoalRating(0.0);
        ap.setCompetenceRating(0.0);
        ap.setOverallRating(0.0);
        personGroupsDs.addItemChangeListener(e -> ap.setParticipantPersonGroup(personGroupsDs.getItem()));
    }

    private DicParticipantType getParticipantType() {
        LoadContext<DicParticipantType> loadContext = LoadContext.create(DicParticipantType.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$DicParticipantType e where e.code = 'participant'"));
        return dataManager.load(loadContext);
    }

    public Component generateUserImageCell(PersonGroupExt entity) {
        return Utils.getPersonImageEmbedded(entity.getPerson(), IMAGE_CELL_HEIGHT, null);
    }
}