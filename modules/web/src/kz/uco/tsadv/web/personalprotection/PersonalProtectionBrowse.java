package kz.uco.tsadv.web.personalprotection;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personprotection.JobProtectionEquipment;
import kz.uco.tsadv.modules.personprotection.PersonalProtection;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PersonalProtectionBrowse extends AbstractLookup {

    @Inject
    private Button siz;
    @Inject
    private Button removeBtn;
    @Inject
    private GroupDatasource<PersonGroupExt, UUID> personGroupExtDs;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        BaseAction removeAction = new BaseAction("remove-siz") {
            @Override
            public void actionPerform(Component component) {
                showOptionDialog(getMessage("confirmation"),
                        getMessage("remove"),
                        MessageType.CONFIRMATION,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        removeSiz();
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO)
                        });
            }
        };

        removeBtn.setAction(removeAction);
        removeAction.setEnabled(false);
        personGroupExtDs.addItemChangeListener(e -> {
            siz.setEnabled(e.getItem() != null);
            removeAction.setEnabled(e.getItem() != null);
        });
    }

    private void removeSiz() {
        LoadContext<PersonalProtection> loadContext = LoadContext.create(PersonalProtection.class);
        LoadContext.Query query = LoadContext.createQuery("select e from tsadv$PersonalProtection e where e.employee.id = :emId");
        query.setParameter("emId", personGroupExtDs.getItem().getId());
        loadContext.setQuery(query);
        loadContext.setView(View.MINIMAL);
        List<PersonalProtection> list = dataManager.loadList(loadContext);
        if (list != null && !list.isEmpty()) {
            CommitContext removeContext = new CommitContext();
            for (PersonalProtection personalProtection : list) {
                removeContext.addInstanceToRemove(personalProtection);
            }
            dataManager.commit(removeContext);
            personGroupExtDs.refresh();
        }
    }

    public void customCreate() {
        PersonalProtection personalProtection = metadata.create(PersonalProtection.class);
        AbstractEditor abstractEditor = openEditor(personalProtection, WindowManager.OpenType.DIALOG);
        abstractEditor.addCloseWithCommitListener(new CloseWithCommitListener() {
            @Override
            public void windowClosedWithCommitAction() {
                personGroupExtDs.refresh();
            }
        });
    }

    public void goToSiz() {
        PersonGroupExt personGroupExt = personGroupExtDs.getItem();
        if (personGroupExt != null) {
            openWindow("tsadv$PersonalProtectionSiz.browse", WindowManager.OpenType.THIS_TAB,
                    ParamsMap.of("personGroupExt", personGroupExt));
        }
    }
}