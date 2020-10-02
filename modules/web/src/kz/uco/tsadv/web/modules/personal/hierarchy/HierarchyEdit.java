package kz.uco.tsadv.web.modules.personal.hierarchy;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.CheckBox;
import kz.uco.base.entity.shared.Hierarchy;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

public class HierarchyEdit extends AbstractEditor<Hierarchy> {

    @Inject
    @Named("fieldGroup.primaryFlag")
    private CheckBox primaryFlagField;

    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        primaryFlagField.setEnabled(!mainHierarchyExists());
    }

    private Boolean mainHierarchyExists() {
        LoadContext<Hierarchy> hierarchyLoadContext = LoadContext.create(Hierarchy.class)
                .setQuery(LoadContext.createQuery("select e from base$Hierarchy e where e.primaryFlag = TRUE"));
        List<Hierarchy> hierarchyList = dataManager.loadList(hierarchyLoadContext);

        return hierarchyList != null && !hierarchyList.isEmpty();
    }

}