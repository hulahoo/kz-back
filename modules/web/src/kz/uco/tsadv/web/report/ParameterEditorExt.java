package kz.uco.tsadv.web.report;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.gui.parameter.edit.ParameterEditor;
import kz.uco.tsadv.ReportInputParameterExt;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ParameterEditorExt extends ParameterEditor {

    @Inject
    protected Label captionPropertyLabel;

    @Inject
    protected LookupField captionProperty;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        metaClass.addValueChangeListener(e -> initMetaClassProperties());
        captionProperty.addValueChangeListener(e -> refreshDefaultValuePickerField());
    }

    @Override
    protected void initDefaultValueField() {
        super.initDefaultValueField();

        refreshDefaultValuePickerField();
    }

    private void refreshDefaultValuePickerField() {
        ReportInputParameterExt parameterExt = (ReportInputParameterExt) getItem();
        if (StringUtils.isNotBlank(parameterExt.getCaptionProperty())) {
            try {
                Component fieldComponent = defaultValueBox.getComponent(0);
                if (fieldComponent != null) {
                    if (fieldComponent instanceof PickerField) {
                        PickerField pickerField = ((PickerField) fieldComponent);
                        pickerField.setCaptionMode(CaptionMode.PROPERTY);
                        pickerField.setCaptionProperty(parameterExt.getCaptionProperty());
                        pickerField.setValue(null);
                    }
                }
            } catch (Exception ex) {
                //ignore this Exception
            }
        }
    }

    @Override
    protected void enableControlsByParamType(ParameterType type) {
        super.enableControlsByParamType(type);

        boolean isEntity = type == ParameterType.ENTITY || type == ParameterType.ENTITY_LIST;
        captionPropertyLabel.setVisible(isEntity);
        captionProperty.setVisible(isEntity);
    }

    private void initMetaClassProperties() {
        String entityMetaClass = getItem().getEntityMetaClass();
        captionProperty.setOptionsMap(Collections.emptyMap());
        captionProperty.setValue(null);

        if (StringUtils.isNotBlank(entityMetaClass)) {
            Map<String, String> propertiesMap = new TreeMap<>();
            MetaClass metaClass = metadata.getClassNN(entityMetaClass);
            Collection<MetaProperty> metaProperties = metaClass.getProperties();
            if (metaProperties != null && !metaProperties.isEmpty()) {
                for (MetaProperty metaProperty : metaProperties) {
                    if (!metaProperty.getRange().isClass()) {
                        propertiesMap.put(messages.getTools().getPropertyCaption(metaProperty), metaProperty.getName());
                    }
                }
                captionProperty.setOptionsMap(propertiesMap);
            }
        }
    }
}
