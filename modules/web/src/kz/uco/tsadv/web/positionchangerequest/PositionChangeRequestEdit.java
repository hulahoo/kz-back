package kz.uco.tsadv.web.positionchangerequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.entity.dbview.OrganizationSsView;
import kz.uco.tsadv.entity.dbview.PositionSsView;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.enums.PositionChangeRequestType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.PositionChangeRequest;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.service.HierarchyService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import kz.uco.tsadv.web.bpm.editor.AbstractBpmEditor;

import javax.inject.Inject;
import java.util.List;

public class PositionChangeRequestEdit<T extends PositionChangeRequest> extends AbstractBpmEditor<T> {

    public static final String PROCESS_NAME = "positionRequest";
    protected boolean readOnly;

    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected GroupBoxLayout dataGroup;
    @Inject
    protected PickerField<Entity> positionGroup;
    @Inject
    protected HierarchyService hierarchyService;
    @Inject
    protected OrganizationHrUserService organizationHrUserService;
    @Inject
    protected PickerField<Entity> organizationGroupPicker;

    @Override
    protected void postInit() {
        readOnly = !isDraft();
        super.postInit();

        PositionChangeRequestType requestType = getItem().getRequestType();
        setCaption(messages.getMessage(requestType));

        if (getItem().getRequestType() != null)
            dataGroup.setCaption(messages.getMessage(getItem().getRequestType()));
    }

    @Override
    public void ready() {
        super.ready();

        positionGroup.setValue(getItem().getPositionGroup());
        organizationGroupPicker.setValue(getItem().getOrganizationGroup());

        addingListener();
        setLookupAction();
    }

    protected void setLookupAction() {
        PickerField.LookupAction lookupActionOrganization = new PickerField.LookupAction(organizationGroupPicker) {
            @Override
            public Entity transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                OrganizationSsView organizationSsView = (OrganizationSsView) valueFromLookupWindow;
                OrganizationGroupExt groupExt = organizationSsView.getOrganizationGroup();
                return groupExt != null ? dataManager.reload(groupExt, "organizationGroup.browse") : null;
            }
        };
        lookupActionOrganization.setLookupScreen("tsadv$OrganizationSsView.browse");
        organizationGroupPicker.addAction(lookupActionOrganization);

        PickerField.LookupAction lookupAction = new PickerField.LookupAction(positionGroup) {
            @Override
            public Entity transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                PositionSsView positionSsView = (PositionSsView) valueFromLookupWindow;
                PositionGroupExt groupExt = positionSsView.getPositionGroup();
                return groupExt != null ? dataManager.reload(groupExt, "positionGroup.change.request") : null;
            }
        };

        lookupAction.setLookupScreen("tsadv$PositionSsView.browse");
        List<DicHrRole> list = organizationHrUserService.getDicHrRoles(userSession.getAttribute(StaticVariable.USER_EXT_ID));
        if (list.stream().noneMatch(hrRole -> hrRole.getCode().equalsIgnoreCase("HR_BUSINESS_PARTNER"))) {
            lookupAction.setLookupScreenParamsSupplier(() -> ParamsMap.of("hierarchy", true,
                    "parentPositionGroupId", userSession.getAttribute(StaticVariable.POSITION_GROUP_ID)));
        }
        positionGroup.addAction(lookupAction);
    }

    protected void addingListener() {
        positionGroup.addValueChangeListener(e -> {
            PositionGroupExt positionGroup = (PositionGroupExt) e.getValue();
            PositionGroupExt parentPositionGroup = positionGroup != null ? hierarchyService.getParentPosition(positionGroup, "positionGroup.change.request") : null;
            PositionExt positionExt = positionGroup != null ? positionGroup.getPosition() : null;
            positionExt = positionExt != null ? dataManager.reload(positionExt, "position.edit") : null;
            getItem().setParentPositionGroup(parentPositionGroup);
            getItem().setPositionGroup(positionGroup);
            getItem().setOrganizationGroup(positionExt != null ? positionExt.getOrganizationGroupExt() : null);
            Job job = positionExt != null && positionExt.getJobGroup() != null ? positionExt.getJobGroup().getJob() : null;
            getItem().setJobNameLang1(job != null ? job.getJobNameLang1() : null);
            getItem().setJobNameLang2(job != null ? job.getJobNameLang2() : null);
            getItem().setJobNameLang3(job != null ? job.getJobNameLang3() : null);
            getItem().setGradeGroup(positionExt != null ? positionExt.getGradeGroup() : null);
            getItem().setFte(positionExt != null ? positionExt.getFte().intValue() : null);
            getItem().setCostCenter(positionExt != null ? positionExt.getCostCenter() : null);
            getItem().setLocation(positionExt != null ? positionExt.getBaza() : null);
        });

        organizationGroupPicker.addValueChangeListener(e -> getItem().setOrganizationGroup((OrganizationGroupExt) e.getValue()));
    }

    @Override
    protected void initVisibleFields() {
        super.initVisibleFields();
        FieldGroup.FieldConfig organizationGroupFc = fieldGroup.getFieldNN("organizationGroup");
        FieldGroup.FieldConfig positionGroupFc = fieldGroup.getFieldNN("positionGroup");
        FieldGroup.FieldConfig jobNameLang1Fc = fieldGroup.getFieldNN("jobNameLang1");
        FieldGroup.FieldConfig jobNameLang2Fc = fieldGroup.getFieldNN("jobNameLang2");
        FieldGroup.FieldConfig jobNameLang3Fc = fieldGroup.getFieldNN("jobNameLang3");
        FieldGroup.FieldConfig locationFc = fieldGroup.getFieldNN("location");
        FieldGroup.FieldConfig gradeGroupFc = fieldGroup.getFieldNN("gradeGroup");
        FieldGroup.FieldConfig costCenterFc = fieldGroup.getFieldNN("costCenter");
        FieldGroup.FieldConfig fteFc = fieldGroup.getFieldNN("fte");
        FieldGroup.FieldConfig effectiveDateFc = fieldGroup.getFieldNN("effectiveDate");
        FieldGroup.FieldConfig commentsFc = fieldGroup.getFieldNN("comments");
        FieldGroup.FieldConfig parentPositionGroupFc = fieldGroup.getFieldNN("parentPositionGroup");
        FieldGroup.FieldConfig mrgFormFc = fieldGroup.getFieldNN("mrgForm");
        FieldGroup.FieldConfig jobInstructionFc = fieldGroup.getFieldNN("jobInstruction");
        FieldGroup.FieldConfig attachmentFc = fieldGroup.getFieldNN("attachment");
        FieldGroup.FieldConfig materialLiabilityAgreementRequiredFc = fieldGroup.getFieldNN("materialLiabilityAgreementRequired");

        switch (getItem().getRequestType()) {
            case NEW: {

                organizationGroupFc.setRequired(true);
                positionGroupFc.setVisible(false);
                jobNameLang1Fc.setRequired(true);
                jobNameLang2Fc.setRequired(true);
                jobNameLang3Fc.setRequired(true);
                locationFc.setRequired(true);
                gradeGroupFc.setRequired(true);
                costCenterFc.setRequired(true);
                fteFc.setRequired(true);
                commentsFc.setRequired(true);
                effectiveDateFc.setRequired(true);
                parentPositionGroupFc.setRequired(true);
                mrgFormFc.setRequired(true);
                jobInstructionFc.setRequired(false);
                attachmentFc.setVisible(false);
                break;
            }
            case CHANGE: {
                positionGroupFc.setRequired(true);

                organizationGroupFc.setEditable(false);
                fieldGroup.getFieldNN("oldPositionNameLang1").setVisible(true);
                fieldGroup.getFieldNN("oldPositionNameLang2").setVisible(true);
                fieldGroup.getFieldNN("oldPositionNameLang3").setVisible(true);
                fieldGroup.getFieldNN("oldBaza").setVisible(true);
                locationFc.setCaption(getMessage("locationChange"));

                jobNameLang1Fc.setRequired(true);
                jobNameLang2Fc.setRequired(true);
                jobNameLang3Fc.setRequired(true);
                locationFc.setRequired(true);
                gradeGroupFc.setRequired(true);
                costCenterFc.setRequired(true);
                fteFc.setRequired(true);
                commentsFc.setRequired(true);
                effectiveDateFc.setRequired(true);
                parentPositionGroupFc.setRequired(true);
                mrgFormFc.setRequired(true);
                jobInstructionFc.setRequired(false);
                attachmentFc.setVisible(false);
                break;
            }
            case CLOSE: {
                organizationGroupFc.setVisible(true);
                organizationGroupFc.setEditable(false);
                positionGroupFc.setRequired(true);
                jobNameLang1Fc.setEditable(false);
                jobNameLang1Fc.setCaption(getMessage("position.name.ru"));
                jobNameLang2Fc.setEditable(false);
                jobNameLang2Fc.setCaption(getMessage("position.name.kz"));
                jobNameLang3Fc.setEditable(false);
                jobNameLang3Fc.setCaption(getMessage("position.name.en"));
                locationFc.setVisible(false);
                gradeGroupFc.setEditable(false);
                gradeGroupFc.setCaption(getMessage("grade"));
                costCenterFc.setEditable(false);
                costCenterFc.setCaption(getMessage("costCenter"));
                fteFc.setEditable(false);
                fteFc.setCaption(getMessage("fte"));
                commentsFc.setRequired(true);
                effectiveDateFc.setRequired(true);
                parentPositionGroupFc.setVisible(false);
                mrgFormFc.setVisible(false);
                jobInstructionFc.setVisible(false);
                attachmentFc.setVisible(true);
                materialLiabilityAgreementRequiredFc.setVisible(false);
                organizationGroupPicker.setVisible(false);
                break;
            }
        }

        fieldGroup.setEditable(!readOnly);
        bpmActorsVBox.setVisible(readOnly);
        positionGroup.setEditable(!readOnly);
        organizationGroupPicker.setEditable(!readOnly);
    }

    @Override
    public boolean close(String actionId) {
        return super.close(actionId, readOnly);
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }

}