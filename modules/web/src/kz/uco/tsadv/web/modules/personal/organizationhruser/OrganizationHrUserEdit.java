package kz.uco.tsadv.web.modules.personal.organizationhruser;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.ValidationErrors;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrganizationHrUserEdit extends AbstractEditor<OrganizationHrUser> {
    @Inject
    private FieldGroup fieldGroup;
    @Named("fieldGroup.counter")
    private TextField counterField;
    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Utils.customizeLookup(fieldGroup.getField("user").getComponent(),
                "tsadv$UserExt.hrUserLookup",
                WindowManager.OpenType.DIALOG,
                getParamsMapToPath(params, "excludedUsers"));
    }

    @Override
    protected void initNewItem(OrganizationHrUser item) {
        super.initNewItem(item);
        item.setDateFrom(new Date());
        item.setDateTo(CommonUtils.getEndOfTime());
    }

    private Map<String, Object> getParamsMapToPath(Map<String, Object> params, String... keys) {
        Map<String, Object> map = new HashMap<>();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                if (params.containsKey(keys[i])) {
                    map.put(keys[i], params.get(keys[i]));
                }
            }
        }
        return map;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        Integer counter = getItem().getCounter();
        if (foundSameRole()){
            errors.add(getMessage("sameRoleError"));
        }
        if (counter != null && (counter != 0 && counter != 1)) {
            errors.add(getMessage("error.counter"));
        }
    }

    protected boolean foundSameRole() {
        return !commonService.getEntities(OrganizationHrUser.class,
                " select t from tsadv$OrganizationHrUser t      \n" +
                        "   where t.id <> :id       \n" +
                        "       and t.organizationGroup.id= :organizationGroupId      \n" +
                        "       and t.dateFrom <= :dateFrom      \n" +
                        "       and t.dateTo >= :dateTo        \n" +
                        "       and t.user.id = :userId  ",
                ParamsMap.of("userId", getItem().getUser().getId(),
                        "dateFrom", getItem().getDateFrom(),
                        "organizationGroupId",getItem().getOrganizationGroup().getId(),
                        "dateTo", getItem().getDateTo(),
                        "id", getItem().getId()),
                View.MINIMAL).isEmpty();
    }

}
