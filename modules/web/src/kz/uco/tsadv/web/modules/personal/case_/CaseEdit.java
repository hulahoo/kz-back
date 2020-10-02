package kz.uco.tsadv.web.modules.personal.case_;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.modules.personal.model.Case;

import java.util.Map;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;

public class CaseEdit extends AbstractEditor<Case> {

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Datasource<Case> caseDs;

    @Override
    public void init(Map<String, Object> params) {

    }

    public Component generateLanguageField(Datasource datasource, String fieldId) {
        LookupField<Object> lookupField = (LookupField) Utils.generateLanguageField(datasource, fieldId);
        lookupField.addValueChangeListener(e -> {
            if (caseDs.getItem().getCaseType() != null && !e.getValue().equals(caseDs.getItem().getCaseType().getLanguage()))
            caseDs.getItem().setCaseType(null);
        });
        return lookupField;
    }
}