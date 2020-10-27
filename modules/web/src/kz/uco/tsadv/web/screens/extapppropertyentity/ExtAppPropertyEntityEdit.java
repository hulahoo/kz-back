package kz.uco.tsadv.web.screens.extapppropertyentity;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.DatatypeRegistry;
import com.haulmont.chile.core.datatypes.impl.BooleanDatatype;
import com.haulmont.cuba.client.sys.ConfigurationClientImpl;
import com.haulmont.cuba.core.app.ConfigStorageService;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.app.core.appproperties.AppPropertiesEdit;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.administration.appproperty.AppPropertyEntityDescription;
import kz.uco.tsadv.modules.administration.appproperty.ExtAppPropertyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ExtAppPropertyEntityEdit extends AbstractWindow {

    private static final Logger log = LoggerFactory.getLogger(AppPropertiesEdit.class);

    @WindowParam
    private ExtAppPropertyEntity item;

    @Inject
    private Datasource<ExtAppPropertyEntity> appPropertyDs;

    @Inject
    private DataManager dataManager;

    @Named("fieldGroup.displayedDefaultValue")
    protected TextField displayedDefaultValueField;

    @Inject
    private Label<String> cannotEditValueLabel;

    @Inject
    private Metadata metadata;

    @Inject
    private DatatypeRegistry datatypeRegistry;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private UiComponents uiComponents;

    private boolean newDescription = false;

    @SuppressWarnings("deprecation")
    @Override
    public void init(Map<String, Object> params) {

        cannotEditValueLabel.setVisible(item.getOverridden());

        if (item.getDescription() == null) {
            AppPropertyEntityDescription description = dataManager.create(AppPropertyEntityDescription.class);
            description.setAppPropertyName(item.getName());
            newDescription = true;
            item.setDescription(description);
        }

        Datatype datatype = item.getEnumValues() != null ?
                datatypeRegistry.getNN(String.class) : datatypeRegistry.get(item.getDataTypeName());
        fieldGroup.addCustomField("description", (datasource, propertyId) -> {
            return createDescriptionField(item.getDescription().getValue());
        });
        fieldGroup.addCustomField("currentValue", (datasource, propertyId) -> {
            if (item.getOverridden()) {
                TextField<String> textField = uiComponents.create(TextField.NAME);
                textField.setValue(item.getDisplayedCurrentValue());
                textField.setEditable(false);
                return textField;
            }
            if (item.getEnumValues() != null) {
                return createLookupField(Arrays.asList(item.getEnumValues().split(",")), item.getCurrentValue());
            } else {
                if (datatype instanceof BooleanDatatype) {
                    return createLookupField(Arrays.asList(Boolean.TRUE.toString(), Boolean.FALSE.toString()), item.getCurrentValue());
                } else {
                    if (Boolean.TRUE.equals(item.getSecret())) {
                        PasswordField passwordField = uiComponents.create(PasswordField.class);
                        passwordField.setValue(item.getCurrentValue());
                        passwordField.addValueChangeListener(e -> {
                            appPropertyDs.getItem().setCurrentValue(e.getValue());
                        });
                        return passwordField;
                    } else {
                        TextField<Object> textField = uiComponents.create(TextField.NAME);
                        textField.setValue(item.getCurrentValue());

                        try {
                            Object value = datatype.parse(item.getCurrentValue(), userSessionSource.getLocale());
                            textField.setDatatype(datatype);
                            if (value != null) {
                                item.setCurrentValue(value.toString());
                            }
                        } catch (ParseException e) {
                            // do not assign datatype then
                            log.trace("Localized parsing by datatype cannot be used for value {}", item.getCurrentValue());
                        }

                        textField.addValueChangeListener(e -> {
                            appPropertyDs.getItem().setCurrentValue(e.getValue() == null ? null : e.getValue().toString());
                        });
                        return textField;
                    }
                }
            }
        });

        Function<String, String> defaultValueFormatter = (value) -> {
            if (datatype instanceof BooleanDatatype) {
                return value;
            }

            try {
                Object parsedDefaultValue = datatype.parse(value);
                return datatype.format(parsedDefaultValue, userSessionSource.getLocale());
            } catch (ParseException e) {
                log.trace("Localized parsing by datatype cannot be used for value {}", value, e);
            }
            return value;
        };
        displayedDefaultValueField.setFormatter(defaultValueFormatter);

        appPropertyDs.setItem(metadata.getTools().copy(item));
    }

    public void ok() {
        ExtAppPropertyEntity extAppPropertyEntity = appPropertyDs.getItem();

        // Save property through the client-side cache to ensure it is updated in the cache immediately
        Configuration configuration = AppBeans.get(Configuration.class);
        ConfigStorageService configStorageService = ((ConfigurationClientImpl) configuration).getConfigStorageService();
        configStorageService.setDbProperty(extAppPropertyEntity.getName(), extAppPropertyEntity.getCurrentValue());
        dataManager.commit(extAppPropertyEntity.getDescription());
        this.close(COMMIT_ACTION_ID);
    }

    public void cancel() {
        this.closeWithDiscard();
    }

    private Component createLookupField(List<String> values, String currentValue) {
        LookupField<String> lookupField = uiComponents.create(LookupField.NAME);
        lookupField.setOptionsList(values);
        lookupField.setValue(currentValue);
        lookupField.addValueChangeListener(e -> {
            appPropertyDs.getItem().setCurrentValue(e.getValue());
        });
        return lookupField;
    }

    private Component createDescriptionField(String initialValue) {
        final int ROWS_COUNT = 5;
        TextArea textArea = uiComponents.create(TextArea.class);
        textArea.setSizeAuto();
        try {
            textArea.setValue(initialValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        textArea.addTextChangeListener(e -> {
            appPropertyDs.getItem().getDescription().setValue(e.getText());
        });
        return textArea;
    }
}