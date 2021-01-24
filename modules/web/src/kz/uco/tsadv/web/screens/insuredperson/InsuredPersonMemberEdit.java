package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;

import javax.inject.Inject;

@UiController("tsadv$InsuredPerson.MemberEdit")
@UiDescriptor("insured-person-member-edit.xml")
@EditedEntityContainer("insuredPersonDc")
@LoadDataBeforeShow
public class InsuredPersonMemberEdit extends StandardEditor<InsuredPerson> {

    @Inject
    private CollectionLoader<DicRelationshipType> relativeDl;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        relativeDl.setParameter("employee", "PRIMARY");
    }

}