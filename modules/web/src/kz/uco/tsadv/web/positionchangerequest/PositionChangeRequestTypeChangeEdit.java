package kz.uco.tsadv.web.positionchangerequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.entity.dbview.OrganizationSsView;
import kz.uco.tsadv.entity.dbview.PositionSsView;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.PositionChangeRequest;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.service.HierarchyService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import kz.uco.tsadv.service.SelfService;
import kz.uco.tsadv.web.bpm.editor.AbstractBpmEditor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

//import static kz.uco.tsadv.web.positionchangerequest.PositionChangeRequestEdit.PROCESS_NAME;

public class PositionChangeRequestTypeChangeEdit<T extends PositionChangeRequest> extends AbstractBpmEditor<T> {

    /*protected PositionGroupExt parentPositionGroup;
    protected PositionExt currentPositionExt;
    protected Job job;

    protected boolean readOnly;
    protected boolean isCommitted = false;

    @Inject
    protected Datasource<PositionChangeRequest> positionChangeRequestDs;
    @Inject
    protected GridLayout grid;
    @Inject
    protected HierarchyService hierarchyService;
    @Inject
    protected Datasource<PositionGroupExt> positionGroupExtDs;
    @Named("fieldGroup.positionGroup")
    protected PickerField<Entity> positionGroupField;
    @Inject
    protected PickerField<Entity> currentParentPositionGroup;
    @Inject
    protected Label labelOrganizationIcon;
    @Inject
    protected Label labelJobNameLang1Icon;
    @Inject
    protected Label labelJobNameLang2Icon;
    @Inject
    protected Label labelJobNameLang3Icon;
    @Inject
    protected Label labelLocationIcon;
    @Inject
    protected Label labelCostCentIcon;
    @Inject
    protected Label labelGradeGroupIcon;
    @Inject
    protected Label labelFteIcon;
    @Inject
    protected Label labelParentPositionGroupIcon;
    @Named("fieldGroup.effectiveDate")
    protected DateField effectiveDateField;
    @Inject
    protected FieldGroup fieldGroup2;
    @Inject
    protected FieldGroup fieldGroup3;
    @Inject
    protected PickerField<Entity> newCostCent;
    @Inject
    protected Label newValue;
    @Inject
    protected PickerField<Entity> newParentPositionGroupField;
    @Inject
    protected PickerField<Entity> newOrganization;
    @Inject
    protected TextField<String> newNameRu;
    @Inject
    protected TextField<String> newNameKz;
    @Inject
    protected TextField<String> newNameEN;
    @Inject
    protected TextField<String> newLocation;
    @Inject
    protected PickerField<Entity> newGradeGroup;
    @Inject
    protected GroupBoxLayout dataGroup;
    @Inject
    protected HBoxLayout procActionButtonHBox;
    @Inject
    protected TextField<Integer> newFte;
    @Named("fieldGroup2.mrgForm")
    protected FileUploadField mrgFormField;
    @Inject
    protected TextField currentFte;
    @Inject
    protected SelfService selfService;
    @Inject
    protected PickerField positionGroup;
    @Inject
    protected OrganizationHrUserService organizationHrUserService;

    protected void initLookupActions() {
        newParentPositionGroupField.getLookupAction().setAfterLookupSelectionHandler(items ->
                getItem().setParentPositionGroup(CollectionUtils.isEmpty(items) ? null : ((PositionSsView) items.iterator().next()).getPositionGroup()));

        PickerField.LookupAction lookupAction = new PickerField.LookupAction(positionGroup) {
            @Override
            public Entity transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                PositionSsView positionSsView = (PositionSsView) valueFromLookupWindow;
                PositionGroupExt groupExt = positionSsView.getPositionGroup();
                return groupExt != null ? dataManager.reload(groupExt, "positionGroup.change.request") : null;
            }
        };

        lookupAction.setLookupScreen("tsadv$PositionSsView.browse");
        positionGroup.addAction(lookupAction);

        PickerField.LookupAction lookupActionOrganization = new PickerField.LookupAction(newOrganization) {
            @Override
            public Entity transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                OrganizationSsView organizationSsView = (OrganizationSsView) valueFromLookupWindow;
                OrganizationGroupExt groupExt = organizationSsView.getOrganizationGroup();
                return groupExt != null ? dataManager.reload(groupExt, "organizationGroup.browse") : null;
            }
        };
        lookupActionOrganization.setLookupScreen("tsadv$OrganizationSsView.browse");

        newOrganization.addAction(lookupActionOrganization);
    }

    @Override
    protected void postInit() {
        readOnly = !isDraft();
        super.postInit();

        List<DicHrRole> list = organizationHrUserService.getDicHrRoles(userSession.getAttribute(StaticVariable.USER_EXT_ID));
        if (list.stream().noneMatch(hrRole -> hrRole.getCode().equalsIgnoreCase("HR_BUSINESS_PARTNER"))) {
            positionGroup.getLookupAction().setLookupScreenParamsSupplier(() -> ParamsMap.of("hierarchy", true,
                    "parentPositionGroupId", userSession.getAttribute(StaticVariable.POSITION_GROUP_ID)));
        }

    }

    @Override
    protected void initEditableFields() {
        if (readOnly) {
            for (FieldGroup.FieldConfig field : fieldGroup2.getFields()) {
                field.setEditable(false);
            }
            for (FieldGroup.FieldConfig field : fieldGroup3.getFields()) {
                field.setEditable(false);
            }
            positionGroupField.setEditable(false);
            effectiveDateField.setEditable(false);
            newFte.setEditable(false);
            newCostCent.setEditable(false);
            newValue.setEnabled(false);
            newParentPositionGroupField.setEditable(false);
            newOrganization.setEditable(false);
            newNameRu.setEditable(false);
            newNameKz.setEditable(false);
            newNameEN.setEditable(false);
            newLocation.setEditable(false);
            newGradeGroup.setEditable(false);
        }
    }

    @Override
    public void ready() {
        super.ready();

        positionGroupExtDs.setItem(getItem().getPositionGroup() != null ? dataManager.reload(getItem().getPositionGroup(), "positionGroup.change.request") : null);
        currentPositionExt = getItem().getPositionGroup() != null && getItem().getPositionGroup().getPosition() != null ?
                dataManager.reload(getItem().getPositionGroup().getPosition(), "position.edit") : null;

        newParentPositionGroupField.setValue(getItem().getParentPositionGroup() != null ?
                selfService.getPositionSsView(getItem().getParentPositionGroup(), CommonUtils.getSystemDate()) : null);
        parentPositionGroup = getItem().getPositionGroup() != null
                ? hierarchyService.getParentPosition(getItem().getPositionGroup(), "positionGroup.change.request")
                : null;
        currentParentPositionGroup.setValue(parentPositionGroup);

        job = currentPositionExt != null && currentPositionExt.getJobGroup() != null ? currentPositionExt.getJobGroup().getJob() : null;

        positionGroup.setValue(getItem().getPositionGroup());

        initListeners();
        setLabelCaption();
        setIcons();
    }

    protected void initListeners() {
        positionGroupExtDs.addItemChangeListener(e -> currentPositionExt = e.getItem() != null ? e.getItem().getPosition() : null);

        positionGroupField.addValueChangeListener(e -> {
            if (!isCommitted) {
                PositionGroupExt positionGroupExt = (PositionGroupExt) e.getValue();
                positionGroupExt = positionGroupExt != null ? dataManager.reload(positionGroupExt, "positionGroup.change.request") : null;
                parentPositionGroup = positionGroupExt != null ? hierarchyService.getParentPosition(positionGroupExt, "positionGroup.change.request") : null;
                currentParentPositionGroup.setValue(parentPositionGroup);
                PositionExt positionExt = positionGroupExt != null ? positionGroupExt.getPosition() : null;
                getItem().setParentPositionGroup(parentPositionGroup);
                getItem().setPositionGroup(positionGroupExt);
                newParentPositionGroupField.setValue(parentPositionGroup != null ?
                        selfService.getPositionSsView(parentPositionGroup, CommonUtils.getSystemDate()) : null);
                getItem().setOrganizationGroup(positionExt != null ? positionExt.getOrganizationGroupExt() : null);
                job = positionExt != null && positionExt.getJobGroup() != null ? positionExt.getJobGroup().getJob() : null;
                getItem().setJobNameLang3(job != null ? job.getJobNameLang3() : null);
                getItem().setJobNameLang2(job != null ? job.getJobNameLang2() : null);
                getItem().setJobNameLang1(job != null ? job.getJobNameLang1() : null);
                getItem().setFte(positionExt != null ? positionExt.getFte().intValue() : null);
                getItem().setGradeGroup(positionExt != null ? positionExt.getGradeGroup() : null);
                getItem().setCostCenter(positionExt != null ? positionExt.getCostCenter() : null);
                getItem().setLocation(positionExt != null ? positionExt.getBaza() : null);

                positionGroupExtDs.setItem(positionGroupExt);
                setIcons();
            }
            isCommitted = false;
        });

        newFte.addValueChangeListener(e -> {
            Integer fte = (Integer) e.getValue();
            mrgFormField.setRequired(fte != null && currentFte.getValue() != null && fte > (Double) currentFte.getValue());
            labelFteIcon.setIcon(ObjectUtils.nullSafeEquals(fte != null ? fte.doubleValue() : null, currentPositionExt != null
                    ? currentPositionExt.getFte() : null)
                    ? null : "images/check.png");
        });

        newCostCent.addValueChangeListener(e ->
                labelCostCentIcon.setIcon(ObjectUtils.nullSafeEquals(e.getValue(), currentPositionExt != null
                        ? currentPositionExt.getCostCenter() : null)
                        ? null : "images/check.png"));

        newGradeGroup.addValueChangeListener(e ->
                labelGradeGroupIcon.setIcon(ObjectUtils.nullSafeEquals(e.getValue(), currentPositionExt != null
                        ? currentPositionExt.getGradeGroup() : null)
                        ? null : "images/check.png"));

        newLocation.addValueChangeListener(e ->
                labelLocationIcon.setIcon(ObjectUtils.nullSafeEquals(e.getValue(), currentPositionExt != null
                        ? currentPositionExt.getBaza() : null)
                        ? null : "images/check.png"));

        newNameRu.addValueChangeListener(e ->
                labelJobNameLang1Icon.setIcon(ObjectUtils.nullSafeEquals(e.getValue(), job != null
                        ? job.getJobNameLang1() : null)
                        ? null : "images/check.png"));

        newNameKz.addValueChangeListener(e ->
                labelJobNameLang2Icon.setIcon(ObjectUtils.nullSafeEquals(e.getValue(), job != null
                        ? job.getJobNameLang2() : null)
                        ? null : "images/check.png"));

        newNameEN.addValueChangeListener(e ->
                labelJobNameLang3Icon.setIcon(ObjectUtils.nullSafeEquals(e.getValue(), job != null
                        ? job.getJobNameLang3() : null)
                        ? null : "images/check.png"));

        newOrganization.addValueChangeListener(e ->
                labelOrganizationIcon.setIcon(ObjectUtils.nullSafeEquals(e.getValue(), currentPositionExt != null
                        ? currentPositionExt.getOrganizationGroupExt() : null)
                        ? null : "images/check.png"));

        newParentPositionGroupField.addValueChangeListener(e -> {
            getItem().setParentPositionGroup(e.getValue() == null ? null : ((PositionSsView) e.getValue()).getPositionGroup());
            labelParentPositionGroupIcon.setIcon(ObjectUtils.nullSafeEquals(getItem().getParentPositionGroup(), parentPositionGroup)
                    ? null : "images/check.png");
        });
    }

    protected void setIcons() {
        labelFteIcon.setIcon(ObjectUtils.nullSafeEquals(getItem().getFte() != null ? getItem().getFte().doubleValue() : null, currentPositionExt != null
                ? currentPositionExt.getFte() : null)
                ? null : "images/check.png");

        labelCostCentIcon.setIcon(ObjectUtils.nullSafeEquals(getItem().getCostCenter(), currentPositionExt != null
                ? currentPositionExt.getCostCenter() : null)
                ? null : "images/check.png");

        labelGradeGroupIcon.setIcon(ObjectUtils.nullSafeEquals(getItem().getGradeGroup(), currentPositionExt != null
                ? currentPositionExt.getGradeGroup() : null)
                ? null : "images/check.png");

        labelLocationIcon.setIcon(ObjectUtils.nullSafeEquals(getItem().getLocation(), currentPositionExt != null
                ? currentPositionExt.getBaza() : null)
                ? null : "images/check.png");

        labelJobNameLang1Icon.setIcon(ObjectUtils.nullSafeEquals(getItem().getJobNameLang1(), job != null
                ? job.getJobNameLang1() : null)
                ? null : "images/check.png");

        labelJobNameLang2Icon.setIcon(ObjectUtils.nullSafeEquals(getItem().getJobNameLang2(), job != null
                ? job.getJobNameLang2() : null)
                ? null : "images/check.png");

        labelJobNameLang3Icon.setIcon(ObjectUtils.nullSafeEquals(getItem().getJobNameLang3(), job != null
                ? job.getJobNameLang3() : null)
                ? null : "images/check.png");

        labelOrganizationIcon.setIcon(ObjectUtils.nullSafeEquals(getItem().getOrganizationGroup(), currentPositionExt != null
                ? currentPositionExt.getOrganizationGroupExt() : null)
                ? null : "images/check.png");

        labelParentPositionGroupIcon.setIcon(ObjectUtils.nullSafeEquals(getItem().getParentPositionGroup(), parentPositionGroup)
                ? null : "images/check.png");
    }

    @Override
    protected boolean preCommit() {
        isCommitted = positionChangeRequestDs.isModified();
        return super.preCommit();
    }

    protected void setLabelCaption() {
        ((Label) grid.getComponentNN("labelOrganization")).setValue(getLabelCaption("organizationGroup"));
        ((Label) grid.getComponentNN("labelJobNameLang1")).setValue(getMessage("position.name.ru"));
        ((Label) grid.getComponentNN("labelJobNameLang2")).setValue(getMessage("position.name.kz"));
        ((Label) grid.getComponentNN("labelJobNameLang3")).setValue(getMessage("position.name.en"));
        ((Label) grid.getComponentNN("labelLocation")).setValue(getLabelCaption("location"));
        ((Label) grid.getComponentNN("labelCostCent")).setValue(getMessage("costCenter"));
        ((Label) grid.getComponentNN("labelGradeGroup")).setValue(getMessage("grade"));
        ((Label) grid.getComponentNN("labelFte")).setValue(getMessage("fte"));
        ((Label) grid.getComponentNN("labelParentPositionGroup")).setValue(getLabelCaption("parentPositionGroup"));

        dataGroup.setCaption(messages.getMessage(getItem().getRequestType()));
        this.setCaption(messages.getMessage(getItem().getRequestType()));
    }

    protected String getLabelCaption(String name) {
        return messages.getMessage(getItem().getClass(), "PositionChangeRequest." + name);
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }*/
}