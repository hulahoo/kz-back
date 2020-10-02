package kz.uco.tsadv.web.successor;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.model.Successor;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class SuccessorHistoryBrowse extends AbstractLookup {

    @Named("successorsTable.edit")
    protected EditAction successorsTableEdit;
    @Named("successorsTable.remove")
    protected RemoveAction successorsTableRemove;
    @Inject
    protected GroupDatasource<Successor, UUID> successorsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        successorsTableEdit.setWindowParamsSupplier(() ->
                ParamsMap.of("fromHistory", true,
                        "previousRecord", getPreviousRecord(successorsDs.getItem()),
                        "nextRecord", getNextRecord(successorsDs.getItem())));
        successorsTableEdit.setAfterCommitHandler(entity -> successorsDs.refresh());
    }

    @Override
    public void ready() {
        super.ready();
    }

    public void removeEntity() {
        Successor nextRecord = getNextRecord(successorsDs.getItem());
        Successor previousRecord = getPreviousRecord(successorsDs.getItem());
        if (nextRecord != null) {
            nextRecord.setStartDate(successorsDs.getItem().getStartDate());
        } else {
            if (previousRecord != null) {
                previousRecord.setEndDate(successorsDs.getItem().getEndDate());
            }
        }
        successorsDs.removeItem(successorsDs.getItem());
        successorsDs.commit();
        successorsDs.refresh();
    }

    protected Successor getPreviousRecord(Successor currentSuccessor) {
        for (Successor successor : successorsDs.getItems()) {
            if (successor.getEndDate().getTime() == currentSuccessor.getStartDate().getTime() - 24 * 60 * 60 * 1000) {
                return successor;
            }
        }
        return null;
    }

    protected Successor getNextRecord(Successor currentSuccessor) {
        for (Successor successor : successorsDs.getItems()) {
            if (successor.getStartDate().getTime() == currentSuccessor.getEndDate().getTime() + 24 * 60 * 60 * 1000) {
                return successor;
            }
        }
        return null;
    }

    public void close() {
        close("cancel");
    }
}