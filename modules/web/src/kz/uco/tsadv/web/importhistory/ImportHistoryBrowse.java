package kz.uco.tsadv.web.importhistory;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.administration.importer.ImportHistory;

import javax.inject.Inject;
import java.util.Map;

public class ImportHistoryBrowse extends AbstractLookup {

    @Inject
    private GroupTable<ImportHistory> importHistoriesTable;

    @Inject
    protected ComponentsFactory componentsFactory;

    protected ImportHistory item;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        item = (ImportHistory) params.get("selectLogItem");
    }

    @Override
    public void ready() {
        super.ready();
        importHistoriesTable.setSelected(item);
    }


    public Component generateFileCell(ImportHistory entity) {
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
}