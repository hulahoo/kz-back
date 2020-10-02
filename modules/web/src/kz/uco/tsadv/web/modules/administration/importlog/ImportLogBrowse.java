package kz.uco.tsadv.web.modules.administration.importlog;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.administration.importer.ImportLog;
import kz.uco.tsadv.modules.administration.importer.ImportLogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ImportLogBrowse extends AbstractLookup {

    private static final Logger log = LoggerFactory.getLogger(ImportLogBrowse.class);

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private GroupTable<ImportLog> importLogsTable;

    @WindowParam(name = "selectLogItem")
    private ImportLog selectLogItem;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Override
    public void ready() {
        super.ready();

        if (selectLogItem != null) {
            importLogsTable.sortBy(importLogsTable.getDatasource().getMetaClass().getPropertyPath("started"), false);
            importLogsTable.expandPath(selectLogItem);
            importLogsTable.setSelected(selectLogItem);
        }
    }

    public Component generateFileCell(ImportLog entity) {
        if (entity.getFile() != null) {
            final ExportDisplay exportDisplay = AppBeans.get(ExportDisplay.NAME);
            Button button = componentsFactory.createComponent(Button.class);
            button.setStyleName("link");
            button.setAction(new AbstractAction("download") {
                @Override
                public void actionPerform(Component component) {
                    exportDisplay.show(entity.getFile());
                }

                @Override
                public String getCaption() {
                    return entity.getFile().getName();
                }
            });
            return button;
        }

        return null;
    }

    public Component generateMessageCell(ImportLogRecord entity) {
        if (entity.getLinkEntityId() != null && entity.getLinkScreen() != null && entity.getLinkEntityName() != null) {
            LinkButton link = componentsFactory.createComponent(LinkButton.class);
            link.setAction(new BaseAction("click") {
                @Override
                public void actionPerform(Component component) {
                    Entity linkEntity = dataManager.load(new LoadContext(metadata.getClass(entity.getLinkEntityName())).setId(entity.getLinkEntityId()));
                    AbstractEditor editor = openEditor(entity.getLinkScreen(), linkEntity, WindowManager.OpenType.DIALOG);
                }
            });
            link.setCaption(entity.getMessage() + entity.getLinkEntityId());
            return link;
        } else {
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(entity.getMessage());
            return label;
        }
    }
}