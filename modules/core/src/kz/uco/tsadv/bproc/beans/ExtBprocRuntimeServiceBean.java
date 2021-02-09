package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.form.FormData;
import com.haulmont.addon.bproc.service.BprocFormService;
import com.haulmont.addon.bproc.service.BprocRuntimeServiceBean;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.service.BprocService;

import javax.inject.Inject;
import java.util.*;

/**
 * @author Alibek Berdaulet
 */
public class ExtBprocRuntimeServiceBean extends BprocRuntimeServiceBean {

    @Inject
    protected BprocFormService bprocFormService;

    @Inject
    protected BprocService bprocService;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;

    @Override
    public ProcessInstanceData startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        FormData startFormData = bprocFormService.getStartFormData(bprocService.getProcessDefinitionData(processDefinitionKey).getId());
        if (variables == null) variables = new HashMap<>();
        Object entity = variables.get("entity");
        if (entity instanceof Map) {
            Entity<UUID> one = load((Map<?, ?>) entity);
            variables.put("entity", one);
        }

        Object rolesLinks = variables.get("rolesLinks");
        if (rolesLinks instanceof ArrayList) {
            List<Object> links = new ArrayList<>();
            for (Object rolesLink : ((ArrayList) rolesLinks)) {
                if (rolesLink instanceof Map) {
                    links.add(load((Map<?, ?>) rolesLink));
                } else links.add(rolesLink);
            }
            variables.put("rolesLinks", links);
        }

        Map<String, Object> finalVariables = variables;
        if (startFormData != null && startFormData.getFormParams() != null) {
            startFormData.getFormParams().forEach(formParam -> finalVariables.putIfAbsent(formParam.getName(), formParam.getValue()));
        }
        User user = userSessionSource.getUserSession().getUser();
        finalVariables.putIfAbsent("initiator", user);
        return super.startProcessInstanceByKey(processDefinitionKey, businessKey, finalVariables);
    }

    protected Entity<UUID> load(Map<?, ?> entity) {
        return dataManager.load(Id.of(UUID.fromString(entity.get("id").toString()), metadata.getClassNN((String) entity.get("_entityName")).getJavaClass())).one();
    }
}
