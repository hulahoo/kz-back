package kz.uco.tsadv.web.modules.recruitment.hiringstepmember;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.recruitment.dictionary.DicHiringMemberType;
import kz.uco.tsadv.modules.recruitment.model.HiringStepMember;
import kz.uco.base.web.abstraction.AbstractDictionaryDatasource;

import javax.inject.Inject;
import javax.inject.Named;

public class HiringStepMemberEdit extends AbstractEditor<HiringStepMember> {

    @Inject
    @Named("fieldGroup.hiringMemberType")
    LookupField<Object> hiringMemberType;

    @Inject
    @Named("fieldGroup.role")
    PickerField role;

    @Inject
    @Named("fieldGroup.userPersonGroup")
    PickerField userPersonGroup;

    @Inject
    private AbstractDictionaryDatasource dicHiringMemberTypesDs;

    @Override
    protected void postInit() {
        hiringMemberType.addValueChangeListener((e) -> {
            if (e.getValue() != null) {
                DicHiringMemberType dicHiringMemberType = (DicHiringMemberType) e.getValue();
                changeMemberType(dicHiringMemberType);
            }
        });

        changeMemberType(getItem().getHiringMemberType());

        super.postInit();
    }

    private void changeMemberType(DicHiringMemberType dicHiringMemberType) {
        if (dicHiringMemberType != null)
            if ("USER".equals(dicHiringMemberType.getCode())) {
                getItem().setRole(null);
                role.setEditable(false);
                userPersonGroup.setEditable(true);
            } else if ("ROLE".equals(dicHiringMemberType.getCode())) {
                getItem().setUserPersonGroup(null);
                role.setEditable(true);
                userPersonGroup.setEditable(false);
            } else {
                role.setEditable(true);
                userPersonGroup.setEditable(true);
            }
    }
}