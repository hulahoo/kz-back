package kz.uco.tsadv.gui.components;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author Adilbekov Yernar
 */
public class AbstractHrEditor<T extends AbstractTimeBasedEntity> extends AbstractEditor<T> {

    @Resource(name = "windowActions.windowCommitHistory")
    protected Button windowCommitHistory;

    protected Boolean editHistory = false;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("editHistory"))
            editHistory = (Boolean) params.get("editHistory");

        addAction(new BaseAction("windowCommitHistory") {
            @Override
            public void actionPerform(Component component) {
                if (getItem() != null) {
                    getItem().setWriteHistory(Boolean.TRUE);
                    commitAndClose();
                }
            }
        });

        setRightValidation(params);
    }

    protected void setRightValidation(Map<String, Object> params) {
        FieldGroup startEndDateFieldGroup = getStartEndDateFieldGroup();
        if (startEndDateFieldGroup != null) {

            FieldGroup.FieldConfig startDateConfig = startEndDateFieldGroup.getField("startDate");
            FieldGroup.FieldConfig endDateConfig = startEndDateFieldGroup.getField("endDate");

            if (params.containsKey("firstRow") && (Boolean) params.get("firstRow") && startDateConfig != null) {
                startDateConfig.setEditable(true);
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
