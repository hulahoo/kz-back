package kz.uco.tsadv.modules.bpm;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import javax.validation.constraints.NotNull;
import java.util.List;

@MetaClass(name = "tsadv_NotPersisitBprocActors")
@NamePattern("%s %s|hrRole,bprocUserTaskCode")
public class NotPersisitBprocActors extends BaseUuidEntity {
    private static final long serialVersionUID = -4927828021201921086L;

    @MetaProperty
    private DicHrRole hrRole;

    @MetaProperty
    private List<TsadvUser> users;

    @MetaProperty
    private String bprocUserTaskCode;

    @NotNull
    @MetaProperty(mandatory = true)
    private Boolean isSystemRecord = false;

    @NotNull
    @MetaProperty(mandatory = true)
    private Boolean isEditable = true;

    @MetaProperty
    private Integer order;

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean editable) {
        isEditable = editable;
    }

    public Boolean getIsSystemRecord() {
        return isSystemRecord;
    }

    public void setIsSystemRecord(Boolean isSystemRecord) {
        this.isSystemRecord = isSystemRecord;
    }

    public void setUsers(List<TsadvUser> users) {
        this.users = users;
    }

    public List<TsadvUser> getUsers() {
        return users;
    }

    public String getBprocUserTaskCode() {
        return bprocUserTaskCode;
    }

    public void setBprocUserTaskCode(String bprocUserTaskCode) {
        this.bprocUserTaskCode = bprocUserTaskCode;
    }

    public DicHrRole getHrRole() {
        return hrRole;
    }

    public void setHrRole(DicHrRole hrRole) {
        this.hrRole = hrRole;
    }

}