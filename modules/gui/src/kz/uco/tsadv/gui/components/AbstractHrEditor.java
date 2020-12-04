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

/**
 * @author Adilbekov Yernar
 */
public class AbstractHrEditor<T extends AbstractTimeBasedEntity & IGroupedEntity> extends AbstractEditor<T> {

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
                        IGroupedEntity item = getItem();
                        AbstractTimeBasedEntity latest = groupedEntityService.getLatest(item.getGroup());
                        AbstractTimeBasedEntity currentItem = getItem();
                        if (latest.getId().equals(currentItem.getId())) {
                                getItem().setWriteHistory(Boolean.TRUE);
                        }else {
                            getItem().setWriteHistory(Boolean.FALSE);
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

//    protected void setStartDateValidation(FieldGroup startEndDateFieldGroup) {
//        FieldGroup.FieldConfig startDateConfig = startEndDateFieldGroup.getField("startDate");
//        if (startDateConfig != null) {
//            if (!PersistenceHelper.isNew(getItem())) {
//                IGroupedEntity groupedEntity = getItem();
//                startDateConfig.setEditable(false);
//                AbstractTimeBasedEntity previousEntity = null;
//                AbstractTimeBasedEntity nextEntity = null;
//                try {
//                    previousEntity = groupedEntityService.getPreviousByStartDate(groupedEntity.getGroup(), getItem());
//                    nextEntity = groupedEntityService.getNextByStartDate(groupedEntity.getGroup(), getItem());
//                } catch (IllegalArgumentException ex) {
//                    ex.printStackTrace();
//                }
//                if (previousEntity != null) {
//                    AbstractTimeBasedEntity finalPreviousEntity = previousEntity;
//                    startDateConfig.addValidator(value -> {
//                        if (((Date) value).before(finalPreviousEntity.getStartDate())) {
//                            throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg.startDateEarlierThanPreviousStartDate"));
//                        }
//                    });
//                }
//                if (nextEntity != null) {
//                    AbstractTimeBasedEntity finalNextEntity = nextEntity;
//                    startDateConfig.addValidator(value -> {
//                        if (((Date) value).after(finalNextEntity.getStartDate())) {
//                            throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg.startDateBiggerThanNextStartDate"));
//                        }
//                    });
//                }
//            }
//            startDateConfig.addValidator(value -> {
//                if (value != null && getItem().getEndDate() != null) {
//                    Date startDate = (Date) value;
//                    if (startDate.after(getItem().getEndDate()))
//                        throw new ValidationException(getMessage("AbstractHrEditor.startDate.validatorMsg.startDateBiggerThanEndDate"));
//                }
//            });
//        } else {
//            if (startDateConfig != null) {
//                startDateConfig.setEditable(true);
//            }
//        }
//    }

    protected void setStartDateValidation(Map<String, Object> params, FieldGroup startEndDateFieldGroup) {
        FieldGroup.FieldConfig startDateConfig = startEndDateFieldGroup.getField("startDate");
        if (params.containsKey("firstRow") && (Boolean) params.get("firstRow") && startDateConfig != null) {
            if (!PersistenceHelper.isNew(getItem())) {
                IGroupedEntity groupedEntity = getItem();
                startDateConfig.setEditable(true);
                AbstractTimeBasedEntity previousEntity = null;
                AbstractTimeBasedEntity nextEntity = null;
                try {
                    previousEntity = groupedEntityService.getPreviousByStartDate(groupedEntity.getGroup(), getItem());
                    nextEntity = groupedEntityService.getNextByStartDate(groupedEntity.getGroup(), getItem());
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
                if (previousEntity != null) {
                    AbstractTimeBasedEntity finalPreviousEntity = previousEntity;
                    startDateConfig.addValidator(value -> {
                        if (((Date) value).before(finalPreviousEntity.getStartDate())) {
                            throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg.startDateEarlierThanPreviousStartDate"));
                        }
                    });
                }
                if (nextEntity != null) {
                    AbstractTimeBasedEntity finalNextEntity = nextEntity;
                    startDateConfig.addValidator(value -> {
                        if (((Date) value).after(finalNextEntity.getStartDate())) {
                            throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg.startDateBiggerThanNextStartDate"));
                        }
                    });
                }
            }
            startDateConfig.addValidator(value -> {
                if (value != null && getItem().getEndDate() != null) {
                    Date startDate = (Date) value;
                    if (startDate.after(getItem().getEndDate()))
                        throw new ValidationException(getMessage("AbstractHrEditor.startDate.validatorMsg.startDateBiggerThanEndDate"));
                }
            });
        } else {
            if (startDateConfig != null) {
                startDateConfig.setEditable(false);
            }
        }
    }

    protected void setEndDateValidation(FieldGroup startEndDateFieldGroup) {
        FieldGroup.FieldConfig endDateConfig = startEndDateFieldGroup.getField("endDate");
        if (endDateConfig != null) {
            endDateConfig.setEditable(false);
            endDateConfig.addValidator(value -> {
                if (value != null && getItem().getStartDate() != null) {
                    Date endDate = (Date) value;
                    if (endDate.before(getItem().getStartDate()))
                        throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg.endDateEarlierThanStartDate"));
                }
            });
        }
    }

    protected void setEntityValidation(Map<String, Object> params) {
        FieldGroup startEndDateFieldGroup = getStartEndDateFieldGroup();
        if (startEndDateFieldGroup != null) {
            setStartDateValidation(params, startEndDateFieldGroup);
            setEndDateValidation(startEndDateFieldGroup);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (windowCommitHistory != null) {
            windowCommitHistory.setVisible(!PersistenceHelper.isNew(getItem()) && !editHistory);
        }
    }

    protected FieldGroup getStartEndDateFieldGroup() {
        return null;
    }
}
