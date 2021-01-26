package kz.uco.tsadv.web.modules.personal.successor;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.Successor;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class SuccessorEdit<T extends Successor> extends AbstractEditor<T> {
    @Named("fieldGroup.personFioWithEmployeeNumber")
    protected PickerField personGroupField;

    @Inject
    protected DataManager dataManager;

    @Named("fieldGroup.succession")
    protected PickerField successionField;
    @Inject
    protected CommonService commonService;
    protected boolean isFromPromote;
    protected boolean isFromHistory;
    @Inject
    protected Metadata metadata;
    @Named("fieldGroup.endDate")
    protected DateField endDateField;
    protected Successor oldSuccessor;
    protected Successor previousSuccessor;
    protected Successor nextSuccessor;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        PickerField.LookupAction lookupAction = personGroupField.addLookupAction();
        lookupAction.setLookupScreen("base$PersonGroup.browse.all.record");

        if (params.containsKey("fromSuccessorPlanning")) {
            successionField.setVisible(false);
        }
        if (params.containsKey("fromPromote")) {
            isFromPromote = true;
            successionField.setVisible(false);
            personGroupField.setVisible(false);
            endDateField.setVisible(false);
        }
        if (params.containsKey("fromHistory")) {
            successionField.setVisible(false);
            personGroupField.setVisible(false);
            isFromHistory = true;
            previousSuccessor = (Successor) params.get("previousRecord");
            nextSuccessor = (Successor) params.get("nextRecord");
        }
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }

    @Override
    protected void postInit() {
        super.postInit();
        if (isFromPromote) {
            oldSuccessor = metadata.create(Successor.class);
            oldSuccessor.setPersonGroup(getItem().getPersonGroup());
            oldSuccessor.setSuccession(getItem().getSuccession());
            oldSuccessor.setStartDate(getItem().getStartDate());
            oldSuccessor.setEndDate(getItem().getEndDate());
            oldSuccessor.setReadinessLevel(getItem().getReadinessLevel());
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (personGroupField.getValue() != null) {
            if (getItem().getStartDate().after(getItem().getEndDate())) {
                errors.add(getMessage("error.startDate.must.be.before.or.equals.endDate"));
            }
            if (isFromPromote) {
                if (!getItem().getStartDate().after(oldSuccessor.getStartDate())) {
                    errors.add(getMessage("error.startDate.must.be.after.previous.record.startDate"));
                }
            } else if (isFromHistory) {
                if (previousSuccessor != null &&
                        !getItem().getStartDate().after(previousSuccessor.getStartDate())) {
                    errors.add(getMessage("error.startDate.must.be.after.previous.record.startDate"));
                }
                if (nextSuccessor != null &&
                        !getItem().getEndDate().before(nextSuccessor.getStartDate())) {
                    errors.add(getMessage("error.endDate.must.be.before.next.record.startDate"));
                }
            } else if (hasDuplicate()) {
                errors.add(getMessage("error.duplicate.succession.and.personGroupId"));
            }
        } else {
            personGroupField.setRequiredMessage(getMessage("fillField"));
        }
    }

    private boolean hasDuplicate() {
        return commonService.getCount(Successor.class, "select e from tsadv$Successor e " +
                "where e.succession.id = :successionId" +
                "  and e.personGroup.id = :personGroupId" +
                " and e.id <> :successorId", ParamsMap.of("successionId", getItem().getSuccession().getId(),
                "personGroupId", getItem().getPersonGroup().getId(),
                "successorId",getItem().getId())) > 0;
    }

    @Override
    protected boolean preCommit() {
        if (isFromPromote) {
            oldSuccessor.setEndDate(new Date(getItem().getStartDate().getTime() - 24 * 60 * 60 * 1000));
            getItem().setStartDate(CommonUtils.getSystemDate());
            dataManager.commit(oldSuccessor);
        }
        if (isFromHistory) {
            if (previousSuccessor != null) {
                previousSuccessor.setEndDate(new Date(getItem().getStartDate().getTime() - 24 * 60 * 60 * 1000));
                dataManager.commit(previousSuccessor);
            }
            if (nextSuccessor != null) {
                nextSuccessor.setStartDate(new Date(getItem().getEndDate().getTime() + 24 * 60 * 60 * 1000));
                dataManager.commit(nextSuccessor);
            }
        }
        getItem().setPersonGroup(dataManager.reload(getItem().getPersonGroup(), "personGroup.listInfo"));
        return super.preCommit();
    }
}