package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.form.FormData;
import com.haulmont.addon.bproc.service.BprocFormService;
import com.haulmont.addon.bproc.service.BprocRuntimeServiceBean;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.service.BprocService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    public ProcessInstanceData startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        FormData startFormData = bprocFormService.getStartFormData(bprocService.getProcessDefinitionData(processDefinitionKey).getId());
        if (variables == null) variables = new HashMap<>();
        Map<String, Object> finalVariables = variables;
        if (startFormData != null && startFormData.getFormParams() != null) {
            startFormData.getFormParams().forEach(formParam -> finalVariables.putIfAbsent(formParam.getName(), formParam.getValue()));
        }
        User user = userSessionSource.getUserSession().getUser();
        finalVariables.putIfAbsent("initiator", user);
        return super.startProcessInstanceByKey(processDefinitionKey, businessKey, finalVariables);
    }
}
