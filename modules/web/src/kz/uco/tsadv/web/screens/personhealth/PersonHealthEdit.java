package kz.uco.tsadv.web.screens.personhealth;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.modules.personal.model.PersonHealth;

import javax.inject.Inject;

@UiController("tsadv_PersonHealth.edit")
@UiDescriptor("person-health-edit.xml")
@EditedEntityContainer("personHealthDc")
@LoadDataBeforeShow
public class PersonHealthEdit extends StandardEditor<PersonHealth> {
    @Inject
    protected InstanceContainer<PersonHealth> personHealthDc;
    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected Metadata metadata;
    protected boolean changed = false;

    @Subscribe(id = "personHealthDc", target = Target.DATA_CONTAINER)
    protected void onPersonHealthDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<PersonHealth> event) {
        changed = true;
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit(DataContext.PreCommitEvent event) {
        if (changed && !personHealthDc.getItem().getStartDateHistory().equals(BaseCommonUtils.getSystemDate())) {
            personHealthDc.getItem().setStartDateHistory(BaseCommonUtils.getSystemDate());
        }
    }

}