package kz.uco.tsadv.entity.bproc;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.administration.TsadvUser;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@MetaClass(name = "tsadv_StartBprocParams")
public class StartBprocParams extends BaseUuidEntity {
    private static final long serialVersionUID = 7266277022845784235L;

    @MetaProperty
    private TsadvUser employee;

    @MetaProperty(mandatory = true)
    @NotNull
    private AbstractBprocRequest request;

    @MetaProperty(mandatory = true)
    @NotNull
    private UUID initiatorPersonGroupId;

    protected Map<String, Object> params;

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public UUID getInitiatorPersonGroupId() {
        return initiatorPersonGroupId;
    }

    public void setInitiatorPersonGroupId(UUID initiatorPersonGroupId) {
        this.initiatorPersonGroupId = initiatorPersonGroupId;
    }

    public AbstractBprocRequest getRequest() {
        return request;
    }

    public void setRequest(AbstractBprocRequest request) {
        this.request = request;
    }

    public TsadvUser getEmployee() {
        return employee;
    }

    public void setEmployee(TsadvUser employee) {
        this.employee = employee;
    }
}