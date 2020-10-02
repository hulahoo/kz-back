package kz.uco.tsadv.web.modules.personal.casetype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.model.CaseType;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;

public class CaseTypeEdit extends AbstractEditor<CaseType> {

    @Inject
    private ComponentsFactory componentsFactory;

    public Component generateLanguageField(Datasource datasource, String fieldId) {
        return Utils.generateLanguageField(datasource, fieldId);
    }
}