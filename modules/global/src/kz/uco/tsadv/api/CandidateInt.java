package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;

import java.util.UUID;

@NamePattern("%s|id")
@MetaClass(name = "tsadv$CandidateInt")
public class CandidateInt extends AbstractEntityInt {
    private static final long serialVersionUID = -5194972989461670866L;

    @MetaProperty
    protected UUID requisitionId;

    @MetaProperty
    protected UUID userExtId;

    public void setRequisitionId(UUID requisitionId) {
        this.requisitionId = requisitionId;
    }

    public UUID getRequisitionId() {
        return requisitionId;
    }

    public void setUserExtId(UUID userExtId) {
        this.userExtId = userExtId;
    }

    public UUID getUserExtId() {
        return userExtId;
    }


}