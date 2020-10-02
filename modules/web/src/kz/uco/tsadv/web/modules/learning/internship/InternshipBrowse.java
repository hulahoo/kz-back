package kz.uco.tsadv.web.modules.learning.internship;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.Internship;
import kz.uco.tsadv.modules.learning.model.InternshipExpenses;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InternshipBrowse extends AbstractWindow {
    @Named("internshipsTable.create")
    protected CreateAction internshipsTableCreate;
    @Named("internshipsTable.edit")
    protected EditAction internshipsTableEdit;
    @Named("internshipsTable.remove")
    protected RemoveAction internshipsTableRemove;
    protected PersonGroupExt personGroupExt;

    @Inject
    protected GroupDatasource<Internship, UUID> internshipsDs;
    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("personGroupExt")) {
            personGroupExt = (PersonGroupExt) params.get("personGroupExt");
        }
        internshipsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of(
                "personGroup", personGroupExt));

        internshipsDs.addItemChangeListener(e -> {
            Internship internship = e.getItem();
            if (internship != null) {
                List<InternshipExpenses> internshipExpenses = getInternshipExpences(internship);
                if (!internshipExpenses.isEmpty()) {
                    internshipsTableRemove.setEnabled(false);
                } else {
                    internshipsTableRemove.setEnabled(true);
                }
            }
        });
        internshipsTableCreate.setAfterCommitHandler(entity -> internshipsDs.refresh());
        internshipsTableEdit.setAfterCommitHandler(entity -> internshipsDs.refresh());
    }

    protected List<InternshipExpenses> getInternshipExpences(Internship internship) {
        return commonService.getEntities(InternshipExpenses.class,
                "select e from tsadv$InternshipExpenses e where e.internship.id = :internshipId",
                ParamsMap.of("internshipId", internship.getId()), "internshipExpenses.edit");
    }

}