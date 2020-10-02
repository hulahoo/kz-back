package kz.uco.tsadv.web.jobprotectionequipment;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personprotection.JobProtectionEquipment;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobProtectionEquipmentBrowse extends AbstractLookup {

    @Inject
    private Button siz;
    @Inject
    private Button removeBtn;
    @Inject
    private GroupDatasource<JobGroup, UUID> jobGroupsDs;

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
        jobGroupsDs.addItemChangeListener(e -> {
            siz.setEnabled(e.getItem() != null);
            removeAction.setEnabled(e.getItem() != null);
        });
    }

    private void removeSiz() {
        LoadContext<JobProtectionEquipment> loadContext = LoadContext.create(JobProtectionEquipment.class);
        LoadContext.Query query = LoadContext.createQuery("select e from tsadv$JobProtectionEquipment e where e.jobGroup.id = :jgId");
        query.setParameter("jgId", jobGroupsDs.getItem().getId());
        loadContext.setQuery(query);
        loadContext.setView(View.MINIMAL);
        List<JobProtectionEquipment> list = dataManager.loadList(loadContext);
        if (list != null && !list.isEmpty()) {
            CommitContext removeContext = new CommitContext();
            for (JobProtectionEquipment jobProtectionEquipment : list) {
                removeContext.addInstanceToRemove(jobProtectionEquipment);
            }
            dataManager.commit(removeContext);
            jobGroupsDs.refresh();
        }
    }

    public void customCreate() {
        JobProtectionEquipment protectionEquipment = metadata.create(JobProtectionEquipment.class);
        AbstractEditor abstractEditor = openEditor(protectionEquipment, WindowManager.OpenType.DIALOG);
        abstractEditor.addCloseWithCommitListener(new CloseWithCommitListener() {
            @Override
            public void windowClosedWithCommitAction() {
                jobGroupsDs.refresh();
            }
        });
    }

    public void goToSiz() {
        JobGroup jobGroup = jobGroupsDs.getItem();
        if (jobGroup != null) {
            openWindow("tsadv$JobProtectionEquipmentSiz.browse", WindowManager.OpenType.THIS_TAB,
                    ParamsMap.of("jobGroup", jobGroup));
        }
    }
}