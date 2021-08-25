package kz.uco.tsadv.entity.bproc;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.administration.TsadvUser;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@MetaClass(name = "tsadv_StartBprocParams")
public class StartBprocParams extends BaseUuidEntity {
    private static final long serialVersionUID = 7266277022845784235L;

    @MetaProperty(mandatory = true)
    @NotNull
    private AbstractBprocRequest request;

    @MetaProperty(mandatory = true)
    @NotNull
    private UUID employeePersonGroupId;

    @MetaProperty(mandatory = true)
    @NotNull
    private Boolean isAssistant = false;

    protected Map<String, List<TsadvUser>> defaultApprovers;

    public Map<String, List<TsadvUser>> getDefaultApprovers() {
        return defaultApprovers;
    }

    public void setDefaultApprovers(Map<String, List<TsadvUser>> defaultApprovers) {
        this.defaultApprovers = defaultApprovers;
    }

    public UUID getEmployeePersonGroupId() {
        return employeePersonGroupId;
    }

    public void setEmployeePersonGroupId(UUID employeePersonGroupId) {
        this.employeePersonGroupId = employeePersonGroupId;
    }

    public AbstractBprocRequest getRequest() {
        return request;
    }

    public void setRequest(AbstractBprocRequest request) {
        this.request = request;
    }

    public Boolean getIsAssistant() {
        return isAssistant;
    }

    public void setIsAssistant(Boolean isAssistant) {
        this.isAssistant = isAssistant;
    }
}