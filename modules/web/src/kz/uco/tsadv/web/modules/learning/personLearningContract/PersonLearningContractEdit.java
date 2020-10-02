package kz.uco.tsadv.web.modules.learning.personLearningContract;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.learning.model.PersonLearningContract;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class PersonLearningContractEdit<T extends PersonLearningContract> extends AbstractEditor<T> {
    @Inject
    protected Datasource<PersonLearningContract> personLearningContractDs;
    

    @Inject
    @Named("fieldGroup.personGroup")
    protected PickerField personGroupField;

    @Override
    public void init(Map<String, Object> params) {
        PickerField.LookupAction lookupAction = personGroupField.addLookupAction();
        lookupAction.setLookupScreen("base$PersonGroupAllPerson.browse");
    }
}