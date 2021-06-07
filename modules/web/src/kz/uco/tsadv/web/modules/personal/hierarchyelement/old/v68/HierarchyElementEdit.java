package kz.uco.tsadv.web.modules.personal.hierarchyelement.old.v68;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HierarchyElementEdit extends AbstractEditor<HierarchyElementExt> {

    @Inject
    @Named("fieldGroup.hierarchy")
    protected PickerField<Entity> hierarchy;
    @Inject
    @Named("fieldGroup.positionGroup")
    protected PickerField positionGroup;
    @Inject
    @Named("fieldGroup.organizationGroup")
    protected PickerField organizationGroup;
    @Inject
    @Named("fieldGroup.parent")
    protected PickerField<Entity> parent;
    @Inject
    @Named("fieldGroup.elementType")
    protected LookupField<Object> elementType;
    @Inject
    @Named("fieldGroup.endDate")
    protected DateField endDateField;
    @Named("fieldGroup.startDate")
    protected DateField startDateField;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected PositionStructureConfig positionStructureConfig;
    protected boolean openedFromHierarchyElementBrowse;
    protected boolean openedForCreateFromPositionEdit;
    protected boolean openedForEditFromPositionEdit;
    protected PositionGroupExt editedPositionGroup;
    private Map<String, Object> param;


    @Override
    protected void initNewItem(HierarchyElementExt item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param=params;

        if (params.containsKey("openedForEdit")) {
            startDateField.setEditable(false);
        }

        if (params.containsKey("openedFromHierarchyElementBrowse")) {
            openedFromHierarchyElementBrowse = true;
        }

        if (params.containsKey("openedForCreateFromPositionEdit")) {
            openedForCreateFromPositionEdit = true;
            if (params.containsKey("positionGroup")) {
                editedPositionGroup = (PositionGroupExt) params.get("positionGroup");
            }
        }

        if (params.containsKey("openedForCreateFromOrganizationEdit")){
            elementType.setEditable(false);
            hierarchy.setEditable(false);
            positionGroup.setEditable(false);
            organizationGroup.setEditable(false);
        }

        if (params.containsKey("openedForEditFromPositionEdit")) {
            openedForEditFromPositionEdit = true;
        }

        if (params.containsKey("close")) {
            positionGroup.setEditable(false);
            organizationGroup.setEditable(false);
            parent.setEditable(false);
            endDateField.setEditable(true);

            endDateField.addValidator(value -> {
                if (value != null) {
                    Date endDate = (Date) value;
                    if (endDate.before(getItem().getStartDate()))
                        throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg"));
                }
            });
        } else {
            customizeParentLookup();

            elementType.addValueChangeListener(e -> {
                customizeParentLookup();
                parent.setValue(null);
                if (e.getValue() != null) {
                    ElementType elementType = (ElementType) e.getValue();
                    switch (elementType) {
                        case ORGANIZATION:
                            if (params.containsKey("createFromHierarchyElementsTable")) {
                                organizationGroup.setEditable(true);
                            }
                            positionGroup.setValue(null);
                            positionGroup.setEditable(false);
                            organizationGroup.setRequired(true);
                            positionGroup.setRequired(false);

                            if (params.containsKey("openedForEdit")) {
                                organizationGroup.setEditable(false);
                            } else if (!params.containsKey("openedForCreateFromOrganizationEdit")){
                                organizationGroup.setEditable(true);
                            }
                            if (params.containsKey("reassignElement")) {
                                organizationGroup.setEditable(false);
                            }

                            break;
                        case POSITION:
                            organizationGroup.setRequired(false);
                            positionGroup.setRequired(true);

                            if (params.containsKey("openedForEdit")) {
                                positionGroup.setEditable(false);
                            } else {
                                positionGroup.setEditable(true);
                            }
                            if (params.containsKey("reassignElement")) {
                                positionGroup.setEditable(false);
                            }
                            organizationGroup.setValue(null);
                            organizationGroup.setEditable(false);
                            break;
                    }
                }
            });

            hierarchy.addValueChangeListener(e -> {
                customizeParentLookup();
                parent.setValue(null);
            });

            parent.addValidator(value -> {
                if (value != null) {
                    HierarchyElementExt parentElement = (HierarchyElementExt) value;
                    if (parentElement.getId().equals(getItem().getId()))
                        throw new ValidationException(getMessage("HierarchyElementExt.parentValidatorMsg"));
                }
            });
        }

        parent.addValueChangeListener((e) -> {
            if (e.getValue() != null) {
                HierarchyElementExt parentElement = (HierarchyElementExt) e.getValue();
                LoadContext<HierarchyElementExt> hierarchyElementLoadContext = LoadContext.create(HierarchyElementExt.class)
                        .setQuery(LoadContext.createQuery("select e from base$HierarchyElementExt e where e.id = :parentId")
                                .setParameter("parentId", parentElement.getId()))
                        .setView("hierarchyElement.edit");

                parentElement = dataManager.load(hierarchyElementLoadContext);
                getItem().setParentName(parentElement.getName());
                getItem().setParentGroup(parentElement.getGroup());
//                log.info(getItem().getParentName());
            } else {
                getItem().setParentName(null);
            }
        });

        organizationGroup.getLookupAction().setLookupScreenParams(ParamsMap.of("openedFromHierarchyElementEdit", ""));
        positionGroup.getLookupAction().setLookupScreenParams(ParamsMap.of("openedFromHierarchyElementEdit", ""));

        hierarchy.addValueChangeListener((e) -> {
            if (e.getValue() != null) {
                organizationGroup.getLookupAction().setLookupScreenParams(ParamsMap.of(
                        "openedFromHierarchyElementEdit", "",
                        "selectedHierarchy", e.getValue()));
                positionGroup.getLookupAction().setLookupScreenParams(ParamsMap.of(
                        "openedFromHierarchyElementEdit", "",
                        "selectedHierarchy", e.getValue()));
            } else {
                organizationGroup.getLookupAction().setLookupScreenParams(ParamsMap.of("openedFromHierarchyElementEdit", ""));
                positionGroup.getLookupAction().setLookupScreenParams(ParamsMap.of("openedFromHierarchyElementEdit", ""));
            }
        });

    }


    @Override
    protected void postInit() {
        super.postInit();
        elementType.setOptionsList(Arrays.asList(ElementType.ORGANIZATION, ElementType.POSITION));
        if (!PersistenceHelper.isNew(getItem())) {
            hierarchy.setEditable(false);
            elementType.setEditable(false);
            startDateField.setRangeStart(DateUtils.addDays(getItem().getStartDate(), 1));
        }

        if (openedFromHierarchyElementBrowse) {
            organizationGroup.setEditable(false);
            positionGroup.setEditable(false);
        }

        if (openedForCreateFromPositionEdit) {
            hierarchy.setValue(commonService.getEntity(Hierarchy.class, positionStructureConfig.getPositionStructureId()));
            hierarchy.setEditable(false);
            elementType.setValue(ElementType.POSITION);
            elementType.setEditable(false);
            if (editedPositionGroup != null) {
                positionGroup.setValue(editedPositionGroup);
                positionGroup.setEditable(false);
            }
            organizationGroup.setEditable(false);
        }

        if (openedForEditFromPositionEdit) {
            positionGroup.setEditable(false);
        }
    }

    @Override
    protected boolean preCommit() {
        if (param.containsKey("openedForEdit")) {
            getItem().setWriteHistory(true);
        }
        return super.preCommit();
    }

    private void customizeParentLookup() {
        Map<String, Object> params = new HashMap<>();

        if (hierarchy.getValue() != null)
            params.put("hierarchy", hierarchy.getValue());

        if (elementType.getValue() != null) {
            params.put("elementType", elementType.getValue());
        }

        if (startDateField.getValue() != null) {
            params.put("hierarchyStartDate", startDateField.getValue());
        }

        if (endDateField.getValue() != null) {
            params.put("hierarchyEndDate", endDateField.getValue());
        }

        Utils.customizeLookup(parent, "base$HierarchyElement.lookup", WindowManager.OpenType.DIALOG, params);
    }
}