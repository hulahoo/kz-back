package kz.uco.tsadv.web.modules.recruitment.hiringstep;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.recruitment.model.HiringStep;
import kz.uco.tsadv.modules.recruitment.model.HiringStepMember;
import kz.uco.tsadv.modules.recruitment.model.HiringStepQuestionnaire;
import kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire;
import kz.uco.tsadv.web.modules.recruitment.hiringstepmember.HiringStepMemberEdit;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HiringStepEdit extends AbstractEditor<HiringStep> {


    @Inject
    protected Metadata metadata;

    @Inject
    protected Datasource<HiringStep> hiringStepDs;

    @Inject
    protected CollectionDatasource<HiringStepQuestionnaire, UUID> questionnairesDs;

    @Inject
    protected CollectionDatasource<HiringStepMember, UUID> membersDs;

    @Inject
    protected Table<HiringStepMember> membersTable;

    @Inject
    protected CommonService commonService;

    @Override
    protected void initNewItem(HiringStep item) {
        super.initNewItem(item);
        item.setDefault_(false);
    }

    public void onCreate(Component source) {
        HiringStepMember hiringStepMember = metadata.create(HiringStepMember.class);
        hiringStepMember.setHiringStep(hiringStepDs.getItem());
        openEditorWithListener(hiringStepMember);
    }

    public void onEdit(Component source) {
        openEditorWithListener(membersDs.getItem());
    }

    protected void openEditorWithListener(HiringStepMember hiringStepMember) {
        HiringStepMemberEdit hiringStepMemberEdit = (HiringStepMemberEdit) openEditor("tsadv$HiringStepMember.edit", hiringStepMember, WindowManager.OpenType.DIALOG, membersDs);
        hiringStepMemberEdit.addCloseListener(actionId -> {
            if (hiringStepMemberEdit.getItem().getMainInterviewer()) {
                membersDs.getItems().stream().forEach(m -> {
                    if (!m.getId().equals(hiringStepMemberEdit.getItem().getId()))
                        membersDs.getItem(m.getId()).setMainInterviewer(false);
                });
            }
            membersTable.setSelected(hiringStepMemberEdit.getItem());
        });
    }

    public void addQuestionnaires() {
        Map<String, Object> params = new HashMap<>();
        params.put(WindowParams.MULTI_SELECT.toString(), Boolean.TRUE);
        params.put("excludeRcQuestionnaireIds", questionnairesDs.getItems() != null ? questionnairesDs.getItems().stream().map(qq -> qq.getQuestionnaire().getId()).collect(Collectors.toList()) : null);

        openLookup("tsadv$RcQuestionnaire.lookup", items -> {
            for (Object item : items) {
                RcQuestionnaire questionnaire = (RcQuestionnaire) item;
                HiringStepQuestionnaire hiringStepQuestionnaire = metadata.create(HiringStepQuestionnaire.class);
                hiringStepQuestionnaire.setHiringStep(getItem());
                hiringStepQuestionnaire.setQuestionnaire(questionnaire);
                questionnairesDs.addItem(hiringStepQuestionnaire);
            }
        }, WindowManager.OpenType.DIALOG, params);
    }
}