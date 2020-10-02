package kz.uco.tsadv.web.talentprogramrequest;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.entity.TalentProgramPersonStep;
import kz.uco.tsadv.entity.TalentProgramRequest;
import kz.uco.tsadv.entity.TalentProgramStep;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.service.EmployeeService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class TalentProgramRequestView extends AbstractWindow {

    @Inject
    protected Datasource<TalentProgramRequest> talentProgramRequestDs;
    @Inject
    protected CollectionDatasource<TalentProgramPersonStep, UUID> talentProgramPersonStepsDs;
    @Inject
    protected CollectionDatasource<TalentProgramStep, UUID> steps;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Metadata metadata;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected CheckBox requirementsCheck;
    @Inject
    protected TextField experienceField;
    @Inject
    protected Label lblEssayHat;
    @Inject
    protected Table<TalentProgramStep> tableSteps;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        talentProgramRequestDs.setItem((TalentProgramRequest) params.get("talentProgramRequest"));
        requirementsCheck.setValue(Boolean.TRUE);
        experienceField.setValue(
                employeeService.getExperienceInCompany(talentProgramRequestDs.getItem().getPersonGroup().getId(),
                        CommonUtils.getSystemDate()));

        lblEssayHat.setValue(talentProgramRequestDs.getItem().getTalentProgram().getQuestionOfEssay());
        talentProgramPersonStepsDs.refresh();
    }

    public void close() {
        close(CLOSE_ACTION_ID);
    }

    @Nullable
    protected TalentProgramPersonStep getTalentProgramPersonStepByTalentProgramStep(@Nullable TalentProgramStep talentProgramStep) {
        if (talentProgramStep == null) return null;
        return talentProgramPersonStepsDs.getItems().stream()
                .filter(talentProgramPersonStep -> talentProgramPersonStep.getDicTalentProgramStep().equals(talentProgramStep.getStep()))
                .findFirst()
                .orElse(null);
    }

    public void openEditorPersonStep(TalentProgramStep talentProgramStep) {
        TalentProgramPersonStep talentProgramPersonStepByTalentProgramStep = getTalentProgramPersonStepByTalentProgramStep(talentProgramStep);
        if (talentProgramPersonStepByTalentProgramStep == null) {
            TalentProgramRequest talentProgramRequest = talentProgramRequestDs.getItem();

            talentProgramPersonStepByTalentProgramStep = metadata.create(TalentProgramPersonStep.class);
            talentProgramPersonStepByTalentProgramStep.setDicTalentProgramStep(talentProgramStep.getStep());
            talentProgramPersonStepByTalentProgramStep.setPersonGroup(talentProgramRequest.getPersonGroup());
            talentProgramPersonStepByTalentProgramStep.setTalentProgramRequest(talentProgramRequest);
        }

        openEditor(talentProgramPersonStepByTalentProgramStep, WindowManager.OpenType.THIS_TAB)
                .addCloseWithCommitListener(() -> {
                    talentProgramPersonStepsDs.refresh();
                    steps.refresh();
                });

    }

    public Component statusGenerator(Entity entity) {
        return getLabelWithValue(getTalentProgramPersonStepByTalentProgramStep((TalentProgramStep) entity), "status", "langValue");
    }

    public Component resultGenerator(Entity entity) {
        return getLabelWithValue(getTalentProgramPersonStepByTalentProgramStep((TalentProgramStep) entity), "result");
    }

    protected Label getLabelWithValue(TalentProgramPersonStep talentProgramPersonStep, String... property) {
        Label label = componentsFactory.createComponent(Label.class);
        Object item = talentProgramPersonStep;
        for (String s : property) {
            item = item instanceof Entity ? ((Entity) item).getValue(s) : null;
        }
        label.setValue(item);
        return label;
    }

    public Component commentGenerator(Entity entity) {
        return getLabelWithValue(getTalentProgramPersonStepByTalentProgramStep((TalentProgramStep) entity), "comment");
    }

    public Component fileGenerator(Entity entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        TalentProgramPersonStep talentProgramPersonStepByTalentProgramStep = getTalentProgramPersonStepByTalentProgramStep((TalentProgramStep) entity);
        FileDescriptor file = talentProgramPersonStepByTalentProgramStep != null ? talentProgramPersonStepByTalentProgramStep.getFile() : null;
        if (file != null) {
            linkButton.setCaption(file.getName());
            linkButton.setAction(new BaseAction("fileAction") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    AppBeans.get(ExportDisplay.class).show(file);
                }
            });
        }
        return linkButton;
    }

    public Component createOrEditStep(TalentProgramStep talentProgramStep) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(talentProgramStep.getStep() != null ? talentProgramStep.getStep().getLangValue() : null);
        linkButton.setAction(new BaseAction("createOrEditStep") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                openEditorPersonStep(talentProgramStep);
            }
        });
        return linkButton;
    }
}