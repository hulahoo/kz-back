package kz.uco.tsadv.gui.userext;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.SplitPanel;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.entity.UserExtPersonGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class ExtUserEditor extends kz.uco.base.gui.userext.ExtUserEditor {

    @Inject
    protected GroupBoxLayout propertiesBox;

    @Inject
    protected SplitPanel split;

    @Inject
    protected SplitPanel vSplit;

    @Inject
    protected PickerField<PersonGroupExt> pickerPersonGroup;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    private UserExtPersonGroup oldUserExtPersonGroup = null;

    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

//        pickerPersonGroup.getLookupAction().setLookupScreen("base$PersonGroup.browse");
        Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("executePersonGroup", true);
        PickerField.LookupAction lookupAction = pickerPersonGroup.getLookupAction();
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG);
        lookupAction.setLookupScreenParams(paramMap);
        lookupAction.setLookupScreen("base$PersonGroup.browse");
//        pickerPersonGroup.getLookupAction().setLookupScreenParams(Collections.singletonMap("personTypeFilter", " and (p.type.code not in ('EXEMPLOYEE') or p.type.code is null) "));

        remove(propertiesBox);
        remove(split);

        vSplit.add(propertiesBox);
        vSplit.add(split);
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (!PersistenceHelper.isNew(getItem())) {
            UserExtPersonGroup userExtPersonGroup = getPersonGroup();
            if (userExtPersonGroup != null) {
                oldUserExtPersonGroup = userExtPersonGroup;
                pickerPersonGroup.setValue(userExtPersonGroup.getPersonGroup());
            }
        }
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        PersonGroupExt personGroup = pickerPersonGroup.getValue();
        if (personGroup == null) {
            if (oldUserExtPersonGroup != null) {
                removePersonGroup(oldUserExtPersonGroup);
            }
        } else {
            boolean hasChange = false;
            UserExtPersonGroup userExtPersonGroup;
            if (oldUserExtPersonGroup != null) {
                userExtPersonGroup = oldUserExtPersonGroup;
                if (!userExtPersonGroup.getPersonGroup().equals(personGroup)) {
                    userExtPersonGroup.setPersonGroup(personGroup);
                    hasChange = true;
                }
            } else {
                userExtPersonGroup = metadata.create(UserExtPersonGroup.class);
                userExtPersonGroup.setUserExt((UserExt) getItem());
                userExtPersonGroup.setPersonGroup(personGroup);
                hasChange = true;
            }

            if (hasChange) {
                dataManager.commit(userExtPersonGroup);
            }
        }

        return super.postCommit(committed, close);
    }

    private void removePersonGroup(UserExtPersonGroup userExtPersonGroup) {
        commonService.removeEntity(userExtPersonGroup);
    }

    protected UserExtPersonGroup getPersonGroup() {
        UserExt userExt = (UserExt) getItem();
        LoadContext<UserExtPersonGroup> loadContext = LoadContext.create(UserExtPersonGroup.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e " +
                        "from tsadv$UserExtPersonGroup e " +
                        "where e.userExt.id = :uId");
        query.setParameter("uId", userExt.getId());
        loadContext.setQuery(query);
        loadContext.setView("userExtPersonGroup.edit");
        return dataManager.load(loadContext);
    }
}