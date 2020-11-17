package kz.uco.tsadv.gui.components;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.base.service.GroupedEntityService;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author Adilbekov Yernar
 */
public class AbstractHrEditor<T extends AbstractTimeBasedEntity> extends AbstractEditor<T> {

    @Resource(name = "windowActions.windowCommitHistory")
    protected Button windowCommitHistory;

    protected Boolean editHistory = false;

    protected Map<String, Object> screenParams;

    @Inject
    private GroupedEntityService groupedEntityService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("editHistory"))
            editHistory = (Boolean) params.get("editHistory");
        removeAction("windowCommit");
        addAction(new BaseAction("windowCommit") {
            @Override
            public void actionPerform(Component component) {
                if (getItem() != null) {
                    if (PersistenceHelper.isNew(getItem())) {
                        getItem().setWriteHistory(Boolean.TRUE);
                    } else if (IGroupedEntity.class.isAssignableFrom(getItem().getClass())) {
                        IGroupedEntity item = (IGroupedEntity) getItem();
                        UUID latestId = ((AbstractTimeBasedEntity) groupedEntityService.getLatest(item.getGroup())).getId();
                        UUID currentItemId = getItem().getId();
                        if (latestId.equals(currentItemId)) {
                            getItem().setWriteHistory(Boolean.TRUE);
                        }
                    }
                    commitAndClose();
                }
            }
        });
        screenParams = params;

    }

    @Override
    public void ready() {
        super.ready();
        setEntityValidation(screenParams);
    }

    protected void setStartDateValidation(FieldGroup startEndDateFieldGroup) {
        FieldGroup.FieldConfig startDateConfig = startEndDateFieldGroup.getField("startDate");
        if (startDateConfig != null) {
            if (!PersistenceHelper.isNew(getItem())) {
                IGroupedEntity groupedEntity = (IGroupedEntity) getItem();
                startDateConfig.setEditable(true);
                IGroupedEntity previousEntity = null;
                IGroupedEntity nextEntity = null;
                try {
                    previousEntity = groupedEntityService.getPreviousByStartDate(groupedEntity.getGroup(), groupedEntity);
                    nextEntity = groupedEntityService.getNextByStartDate(groupedEntity.getGroup(), groupedEntity);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
                if (previousEntity != null) {
                    AbstractTimeBasedEntity finalPreviousEntity = (AbstractTimeBasedEntity) previousEntity;
                    startDateConfig.addValidator(value -> {
                        if (((Date) value).before(finalPreviousEntity.getStartDate())) {
                            throw new ValidationException("Start date must be greater than previous entity's start date from this group.");
                        }
                    });
                }
                if (nextEntity != null) {
                    AbstractTimeBasedEntity finalNextEntity = (AbstractTimeBasedEntity) nextEntity;
                    startDateConfig.addValidator(value -> {
                        if (((Date) value).after(finalNextEntity.getStartDate())) {
                            throw new ValidationException("Start date must be lower than next entity's start date from this group.");
                        }
                    });
                }
            }
            startDateConfig.addValidator(value -> {
                if (value != null && getItem().getEndDate() != null) {
                    Date startDate = (Date) value;
                    if (startDate.after(getItem().getEndDate()))
                        throw new ValidationException(getMessage("AbstractHrEditor.startDate.validatorMsg"));
                }
            });
        } else {
            if (startDateConfig != null) {
                startDateConfig.setEditable(false);
            }
        }
    }

    protected void setEndDateValidation(Map<String, Object> params, FieldGroup startEndDateFieldGroup) {
        FieldGroup.FieldConfig endDateConfig = startEndDateFieldGroup.getField("endDate");
        if (params.containsKey("lastRow") && (Boolean) params.get("lastRow") && endDateConfig != null) {
            endDateConfig.setEditable(true);
            endDateConfig.addValidator(value -> {
                if (value != null && getItem().getStartDate() != null) {
                    Date endDate = (Date) value;
                    if (endDate.before(getItem().getStartDate()))
                        throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg"));
                }
            });
        } else {
            if (endDateConfig != null) {
                endDateConfig.setEditable(false);
            }
        }
    }

    protected void setEntityValidation(Map<String, Object> params) {
        FieldGroup startEndDateFieldGroup = getStartEndDateFieldGroup();
        if (startEndDateFieldGroup != null) {
            setStartDateValidation(startEndDateFieldGroup);
            setEndDateValidation(params, startEndDateFieldGroup);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (windowCommitHistory != null) {
            windowCommitHistory.setVisible(!PersistenceHelper.isNew(getItem()) && !editHistory);
        }
    }

//    protected T isFirst() {
//        T item = getItem();
//        return item == item
//                .getGroup()
//                .getList()
//                .stream()
//                .min((firstOrganization, secondOrganization) ->
//                        firstOrganization.getStartDate().before(secondOrganization.getStartDate()) ? -1 : 1)
//                .get();
//    }

    protected FieldGroup getStartEndDateFieldGroup() {
        return null;
    }
}
