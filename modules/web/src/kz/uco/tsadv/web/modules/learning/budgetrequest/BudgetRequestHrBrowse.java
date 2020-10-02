package kz.uco.tsadv.web.modules.learning.budgetrequest;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.entity.shared.Company;
import kz.uco.tsadv.modules.learning.dictionary.DicBudgetStatus;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningType;
import kz.uco.tsadv.modules.learning.model.Budget;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.base.web.components.CustomFilter;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class BudgetRequestHrBrowse extends AbstractLookup {

    @Inject
    private VBoxLayout filterBox;

    @Inject
    private GroupDatasource<BudgetRequest, UUID> budgetRequestsDs;

    private Map<String, CustomFilter.Element> filterMap;

    private CustomFilter customFilter;

    @Inject
    private CollectionDatasource<DicLearningType, UUID> learningTypesDs;

    @Inject
    private CollectionDatasource<Company, UUID> companiesDs;

    @Inject
    private CollectionDatasource<DicBudgetStatus, UUID> budgetStatusesDs;

    @Inject
    private CollectionDatasource<Budget, UUID> budgetsDs;

    @Inject
    private CollectionDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;
    @Named("budgetRequestsTable.edit")
    private EditAction budgetRequestsTableEdit;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Button historyButton;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Map<String, Object> editorParams = new HashMap<>();
        editorParams.put(BudgetRequestEdit.MODE, BudgetRequestEdit.MODE_HR_EDIT);
        budgetRequestsTableEdit.setWindowParams(editorParams);

        initFilterMap();

        customFilter = CustomFilter.init(budgetRequestsDs, budgetRequestsDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());

        customFilter.selectFilter("budget");
        customFilter.selectFilter("status");

        CssLayout cssLayout = componentsFactory.createComponent(CssLayout.class);
        cssLayout.setStyleName("custom-filter-bordered");
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setSpacing(true);

        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(messages.getMessage("kz.uco.tsadv.web", "filter.organizationStructure"));
        hBoxLayout.add(label);
        Label hidden = componentsFactory.createComponent(Label.class);
        hidden.setVisible(false);
        hidden.setId("hiddenOrganizationGroupIdStr");
        hBoxLayout.add(hidden);
        LookupPickerField<Entity> lookupPickerField = componentsFactory.createComponent(LookupPickerField.class);
        lookupPickerField.setOptionsDatasource(organizationGroupsDs);
        lookupPickerField.setCaptionMode(CaptionMode.PROPERTY);
        lookupPickerField.setCaptionProperty("organization.organizationName");
        lookupPickerField.addLookupAction();
        lookupPickerField.getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);
        lookupPickerField.addValueChangeListener(e -> {
            hidden.setValue(e.getValue() == null ? null : ((OrganizationGroupExt) e.getValue()).getId().toString());
        });
        hBoxLayout.add(lookupPickerField);
        cssLayout.add(hBoxLayout);

        customFilter.addFilter(cssLayout, 1);

        historyButton.setCaption(getMessage("Budget.history.button"));
    }

    private void initFilterMap() {

        filterMap = new LinkedHashMap<>();

        filterMap.put("budget",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.learning", "BudgetRequest.budget"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", budgetsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "name")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.budget.id ?")
        );

        filterMap.put("status",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.learning", "BudgetRequest.status"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsDatasource", budgetStatusesDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.status.id ?"));

        filterMap.put("courseName",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.learning", "BudgetRequest.course"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter(" lower(coalesce(e.courseName, c.name, '')) ?"));

        filterMap.put("learningType",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.learning", "BudgetRequest.learningType"))
                        .setComponentClass(LookupField.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .addComponentAttribute("optionsDatasource", learningTypesDs)
                        .setQueryFilter(" e.learningType.id ?"));

        filterMap.put("provider",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.learning", "BudgetRequest.providerCompany"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", companiesDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "companyName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.providerCompany.id ?"));

        filterMap.put("month",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.learning", "BudgetRequest.month"))
                        .setComponentClass(DatePicker.class)
                        .addComponentAttribute("resolution", DatePicker.Resolution.MONTH)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.month ?"));

    }
}