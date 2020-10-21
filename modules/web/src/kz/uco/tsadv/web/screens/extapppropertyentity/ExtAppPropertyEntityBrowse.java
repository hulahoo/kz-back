package kz.uco.tsadv.web.screens.extapppropertyentity;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.config.AppPropertyEntity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.settings.Settings;
import kz.uco.tsadv.datasource.ExtAppPropertiesDatasource;
import kz.uco.tsadv.modules.administration.appproperty.ExtAppPropertyEntity;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtAppPropertyEntityBrowse extends AbstractLookup {
    @Inject
    private ExtAppPropertiesDatasource paramsDs;
    @Inject
    private TreeTable<ExtAppPropertyEntity> paramsTable;
    @Named("paramsTable.editValue")
    private Action editValueAction;
    @Named("paramsTable.refresh")
    private RefreshAction refreshAction;

    @Inject
    private Button exportBtn;
    @Inject
    private TextField<String> searchField;
    @Inject
    private HBoxLayout hintBox;


    private ExtAppPropertyEntity lastSelected;

    @Override
    public void init(Map<String, Object> params) {
        paramsDs.addItemChangeListener(e -> {
            boolean enabled = e.getItem() != null && !e.getItem().getCategory();
            editValueAction.setEnabled(enabled);
            exportBtn.setEnabled(enabled);
        });
        paramsTable.setItemClickAction(editValueAction);

        paramsTable.sort("name", Table.SortDirection.ASCENDING);

        searchField.addValueChangeListener(e -> {
            paramsDs.refresh(ParamsMap.of("name", e.getValue()));

            if (StringUtils.isNotEmpty(e.getValue())) {
                paramsTable.expandAll();
            }
        });

        refreshAction.setBeforeRefreshHandler(() ->
                lastSelected = paramsTable.getSingleSelected()
        );
        refreshAction.setAfterRefreshHandler(() -> {
            if (StringUtils.isNotEmpty(searchField.getValue())) {
                paramsTable.expandAll();
            }

            if (lastSelected != null) {
                for (ExtAppPropertyEntity entity : paramsDs.getItems()) {
                    if (entity.getName().equals(lastSelected.getName())) {
                        paramsTable.expand(entity.getId());
                        paramsTable.setSelected(entity);
                    }
                }
            }
        });
    }

    public void exportAsSql() {
        List<AppPropertyEntity> exported = paramsTable.getSelected().stream()
                .filter(appPropertyEntity -> !appPropertyEntity.getCategory())
                .collect(Collectors.toList());
        if (!exported.isEmpty()) {
            openWindow("appPropertiesExport", WindowManager.OpenType.DIALOG, ParamsMap.of("exported", exported));
        }
    }

    public void editValue() {
        Window editor = openWindow("tsadv_ExtAppPropertyEntity.edit", WindowManager.OpenType.DIALOG,
                ParamsMap.of("item", paramsDs.getItem()));
        editor.addCloseWithCommitListener(() -> {
            List<ExtAppPropertyEntity> entities = paramsDs.loadAppPropertyEntities();
            for (ExtAppPropertyEntity entity : entities) {
                if (entity.getName().equals(paramsDs.getItem().getName())) {
                    paramsDs.getItem().setCurrentValue(entity.getCurrentValue());
                    paramsDs.getItem().setUpdateTs(entity.getUpdateTs());
                    paramsDs.getItem().setUpdatedBy(entity.getUpdatedBy());
                    paramsDs.getItem().setDescription(entity.getDescription());
                    break;
                }
            }
        });
    }

    @Override
    public void applySettings(Settings settings) {
        super.applySettings(settings);
        String visible = settings.get(hintBox.getId()).attributeValue("visible");
        if (visible != null)
            hintBox.setVisible(Boolean.parseBoolean(visible));
    }

}