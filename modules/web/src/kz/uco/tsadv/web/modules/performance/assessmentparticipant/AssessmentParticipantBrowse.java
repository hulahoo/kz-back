package kz.uco.tsadv.web.modules.performance.assessmentparticipant;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.performance.model.AssessmentParticipant;
import kz.uco.tsadv.web.modules.performance.assessment.AssessmentEdit;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class AssessmentParticipantBrowse extends AbstractLookup {
    private static final String IMAGE_CELL_HEIGHT = "50px";
    @Inject
    private GroupDatasource<AssessmentParticipant, UUID> assessmentParticipantsDs;
    @Inject
    private UserSession userSession;
    @Inject
    private GroupTable<AssessmentParticipant> assessmentParticipantsTable;
    @Named("assessmentParticipantsTable.edit")
    private Action assessmentParticipantsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        assessmentParticipantsTableEdit.setEnabled(false);
        assessmentParticipantsDs.addItemChangeListener(e -> {
            if (e.getItem() != null)
                assessmentParticipantsTableEdit.setEnabled(true);
            else
                assessmentParticipantsTableEdit.setEnabled(true);
        });
    }

    public void onAssessButonClick() {
        AssessmentEdit editor = (AssessmentEdit) openEditor("tsadv$Assessment.edit", assessmentParticipantsDs.getItem().getAssessment(), WindowManager.OpenType.DIALOG);
        editor.addCloseListener(actionId -> {
            assessmentParticipantsDs.refresh();
        });
    }

    public Component generateUserImageCell(AssessmentParticipant entity) {
        return Utils.getPersonImageEmbedded(entity.getAssessment().getEmployeePersonGroup().getPerson(), IMAGE_CELL_HEIGHT, null);
    }
}