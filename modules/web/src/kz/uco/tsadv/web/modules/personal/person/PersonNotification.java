package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.OptionsGroup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.base.web.init.App;
import kz.uco.tsadv.web.screens.ExtAppMainWindow;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class PersonNotification extends AbstractWindow {

    @Inject
    private GroupDatasource<SendingNotification, UUID> notificationsDs;
    @Inject
    private Table<SendingNotification> notificationsTable;
    @Inject
    private DataManager dataManager;
    @Inject
    private OptionsGroup filterRead;
    @Named("notificationsTable.view")
    private Action notificationsTableView;
    @Named("notificationsTable.remove")
    private RemoveAction notificationsTableRemove;
    @Named("notificationsTable.markAsRead")
    private Action notificationsTableMarkAsRead;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        notificationsTableView.setEnabled(false);
        notificationsTableRemove.setEnabled(false);
        notificationsTableMarkAsRead.setEnabled(false);

        notificationsDs.addItemChangeListener(e -> {
            notificationsTableView.setEnabled(notificationsTable.getSelected().size() == 1);
            notificationsTableRemove.setEnabled(!notificationsTable.getSelected().isEmpty());
            notificationsTableMarkAsRead.setEnabled(!notificationsTable.getSelected().isEmpty() && !"READ".equals(filterRead.getValue()));
        });

        notificationsTableRemove.setAfterRemoveHandler(items -> {
            notificationsDs.refresh();
            updateCounters();
        });

        Map<String, Object> optionsMap = new LinkedHashMap<>();
        optionsMap.put(messages.getMainMessage("filterRead.ALL"), "ALL");
        optionsMap.put(messages.getMainMessage("filterRead.READ"), "READ");
        optionsMap.put(messages.getMainMessage("filterRead.NOT_READ"), "NOT_READ");

        filterRead.setOptionsMap(optionsMap);
        filterRead.setValue("NOT_READ");
        filterRead.addValueChangeListener(e -> notificationsDs.refresh());

    }

    public void viewNotification() {
        AbstractWindow abstractWindow = openWindow("person-notification-view",
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("notification", notificationsDs.getItem()));

        abstractWindow.addCloseListener(actionId -> {
            if (actionId.equalsIgnoreCase("ok")) {
                notificationsDs.refresh();
                updateCounters();
            }
        });
    }

    public void markAsRead() {
        if (!notificationsTable.getSelected().isEmpty()) {
            List<Entity> commitInstances = new ArrayList<>();
            notificationsTable.getSelected().forEach(n -> {
                n.setReaded(true);
                commitInstances.add(n);
            });
            dataManager.commit(new CommitContext(commitInstances));
            notificationsDs.refresh();
            updateCounters();
        }
    }

    private void updateCounters() {
        App app = AppBeans.get(App.NAME);
        ExtAppMainWindow extAppMainWindow = (ExtAppMainWindow) app.getTopLevelWindow();
        extAppMainWindow.updateCounters();
    }
}