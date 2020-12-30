package kz.uco.tsadv.web.talentprogramrequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.TalentProgram;
import kz.uco.tsadv.entity.TalentProgramExceptions;
import kz.uco.tsadv.entity.TalentProgramRequest;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.UUID;

public class TalentProgramRequestBrowse extends AbstractLookup {


    @Inject
    protected GroupDatasource<TalentProgram, UUID> talentProgramsDs;
    @Inject
    protected Image image;
    @Inject
    protected GroupTable<TalentProgramRequest> talentProgramRequestsTable;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Button createBtn;
    @Inject
    protected Button editBtn;
    @Inject
    protected GroupDatasource<TalentProgramRequest, UUID> talentProgramRequestsDs;
    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected UserSession userSession;
    @Inject
    private Metadata metadata;
    @Inject
    private Link openIntraNet;

    protected TalentProgram talentProgram;

    @Override
    public void ready() {
        talentProgramRequestsDs.refresh();
        talentProgramsDs.refresh();

//        editBtn.setEnabled(false);
//        editBtn.setIcon(null);

        if (talentProgramsDs.getItems().isEmpty()) {
            showNotification(getMessage("noTalentPrograms"));
            createBtn.setEnabled(false);
            return;
        }

//        talentProgramRequestsDs.addItemChangeListener(e -> editBtn.setEnabled(e.getItem() != null));

        talentProgram = talentProgramsDs.getItems().iterator().next();

        if (talentProgramRequestsDs.getItems().stream().anyMatch(e -> talentProgram.equals(e.getTalentProgram())))
            createBtn.setEnabled(false);

        talentProgramRequestsDs.addCollectionChangeListener(ee -> {
            if (talentProgramRequestsDs.getItems().stream().anyMatch(e -> talentProgram.equals(e.getTalentProgram())))
                createBtn.setEnabled(false);
        });

        if (talentProgram.getBannerLang() != null)
            image.setSource(FileDescriptorResource.class).setFileDescriptor(talentProgram.getBannerLang());
        openIntraNet.setUrl(talentProgram.getWebLink());
        super.ready();
    }

    public Component talentProgramGenerator(TalentProgramRequest entity) {
        UUID InstanceId = entity.getId();
        TalentProgram program = entity.getValue("talentProgram");
        Link link = componentsFactory.createComponent(Link.class);
        if (program == null)
            return link;
        link.setCaption(program.getProgramNameLang());
        link.setUrl("http://kchrdev.air-astana.net/aa/open?screen=tsadv$TalentProgramRequest.edit&item=tsadv$TalentProgramRequest-"+InstanceId);
        link.setTarget("_blank");
        return link;

    }

    public void apply() {
        showOptionDialog(getMessage("msg.request.title"),
                talentProgram.getParticipationRuleLang(),
                MessageType.CONFIRMATION_HTML,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                if (isHasAccessToProgramRequest()) {
                                    TalentProgramRequest talentProgramRequest = talentProgramRequestsDs.getItems().stream()
                                            .filter(t -> t.getTalentProgram().equals(talentProgram))
                                            .findFirst().orElse(null);
                                    if (talentProgramRequest == null) {
                                        talentProgramRequest = metadata.create(TalentProgramRequest.class);
                                        talentProgramRequest.setTalentProgram(talentProgram);
                                    }
                                    openEditor(talentProgramRequest, WindowManager.OpenType.THIS_TAB).
                                            addCloseListener(actionId -> talentProgramRequestsDs.refresh());
                                } else {
                                    showMessageDialog(getMessage("noAccessTitle"), getMessage("noAccessToProgReq") + " " + talentProgram.getProgramNameLang(), MessageType.WARNING_HTML);
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });
    }

    protected boolean isHasAccessToProgramRequest() {
        PersonGroupExt personGroupExt = employeeService.getPersonGroupByUserId(userSession.getUser().getId());
        if (personGroupExt == null) return false;

        AssignmentExt assignmentExt = employeeService.getAssignmentExt(personGroupExt.getId(), CommonUtils.getSystemDate(), "assignment-for-talents");
        if (assignmentExt == null) return false;

        List<GradeGroup> gradeGroupList = commonService.getEntities(GradeGroup.class,
                "select e.gradeGroup from tsadv$TalentProgramGrade e where e.talentProgram.id=:talProgId",
                ParamsMap.of("talProgId", talentProgram.getId()), View.LOCAL);
        List<PersonGroupExt> personGroupExtsList = commonService.getEntities(PersonGroupExt.class,
                "select e.personGroupId from tsadv$TalentProgramExceptions e where e.talentProgram.id=:talProgId",
                ParamsMap.of("talProgId", talentProgram.getId()), View.LOCAL);



        return gradeGroupList.contains(assignmentExt.getGradeGroup()) || personGroupExtsList.contains(assignmentExt.getPersonGroup());
    }

}
