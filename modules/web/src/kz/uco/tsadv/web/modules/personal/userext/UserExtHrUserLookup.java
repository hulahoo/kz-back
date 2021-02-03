package kz.uco.tsadv.web.modules.personal.userext;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.model.HrUserRole;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserExtHrUserLookup extends AbstractLookup {

    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private CommonService commonService;

    public Component generateRolesList(Entity entity) {
        Label label = componentsFactory.createComponent(Label.class);
        List<String> userRoles;
        if (!(userRoles = getUserRoles((TsadvUser) entity)).isEmpty()) {
            if (userRoles.size() == 1) {
                label.setValue(userRoles.get(0));
            } else {
                StringBuilder sb = new StringBuilder();
                for (String role : userRoles) {
                    sb.append(role);
                    sb.append(", ");
                }
                String rolesList = sb.toString();
                rolesList = rolesList.substring(0, rolesList.lastIndexOf(","));
                label.setValue(rolesList);
            }
        }
        return label;
    }

    protected List<String> getUserRoles(TsadvUser user) {
        List<String> result = new ArrayList<>();
        if (user != null) {
            String queryString = "SELECT e FROM tsadv$HrUserRole e " +
                    "WHERE e.user.id = :userId" +
                    " and :sysDate between e.dateFrom and e.dateTo";
            Map<String, Object> params = new HashMap<>();
            params.put("userId", user.getId());
            params.put("sysDate", CommonUtils.getSystemDate());
            List<HrUserRole> roles = commonService.getEntities(HrUserRole.class, queryString, params, "hrUserRole.view");
            if (!roles.isEmpty()) {
                for (HrUserRole role : roles) {
                    result.add(role.getRole().getLangValue());
                }
            }
        }
        return result;
    }
}