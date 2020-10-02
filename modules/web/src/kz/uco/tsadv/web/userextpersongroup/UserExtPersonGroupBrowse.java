package kz.uco.tsadv.web.userextpersongroup;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.global.entity.UserExtPersonGroup;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class UserExtPersonGroupBrowse extends AbstractLookup {
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private GroupDatasource<UserExtPersonGroup, UUID> userExtPersonGroupsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.get("procInstanceId") != null && !params.containsKey("EMPLOYEE")) {
            userExtPersonGroupsDs.setQuery("select distinct e from tsadv$UserExtPersonGroup e \n" +
                    "                  join e.personGroup.list p " +
                    "join bpm$ProcActor a \n" +
                    "where a.procInstance.id = '" + params.get("procInstanceId") + "' \n" +
                    "       and e.userExt.id <> :session$user " +
                    "       and (e.userExt = a.user or a.procInstance.createdBy = e.userExt.login )\n" +
                    " order by a.order \n");
        } else if (Boolean.TRUE.equals(params.get("EMPLOYEE"))) {
            userExtPersonGroupsDs.setQuery("select distinct e from tsadv$UserExtPersonGroup e " +
                    "                   join e.personGroup.list p " +
                    "                        on :session$systemDate between p.startDate and p.endDate " +
                    "                        and p.type.code = 'EMPLOYEE' " +
                    "                   where e.personGroup is not null " +
                    "                        and e.userExt is not null");
        }
    }

    public Component login(Entity entity) {
        Label label = componentsFactory.createComponent(Label.class);
//        label.setCaption(getMessage("login"));
        UserExt userExt = entity.getValue("userExt");
        label.setValue(userExt.getFullName() + " [" + userExt.getLogin() + "]");
        return label;
    }
}