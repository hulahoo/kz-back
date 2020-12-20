package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import kz.uco.tsadv.modules.administration.UserExt;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(BprocUtilService.NAME)
public class BprocUtilServiceBean implements BprocUtilService {

    @Inject
    private DataManager dataManager;

    @Override
    public List<UUID> getAssignee(String hrRoleCode) {
        return dataManager.load(UserExt.class)
                .query("select distinct e.user from tsadv$HrUserRole e" +
                        "           where e.role.code = :hrRoleCode")
                .parameter("hrUserRole", hrRoleCode)
                .list().stream()
                .map(keyValueEntity -> ((UUID) keyValueEntity.getValue("id")))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasAssignee(String hrRoleCode) {
        return !getAssignee(hrRoleCode).isEmpty();
    }

    @Override
    public void reject(String processId) {

    }

    @Override
    public void approve(String processId) {

    }
}