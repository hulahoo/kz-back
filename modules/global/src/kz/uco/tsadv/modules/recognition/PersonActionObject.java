package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.FileDescriptor;

@Table(name = "TSADV_PERSON_ACTION_OBJECT")
@Entity(name = "tsadv$PersonActionObject")
public class PersonActionObject extends StandardEntity {
    private static final long serialVersionUID = 3193021737188598312L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTION_ID")
    protected PersonAction action;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OBJECT_ID")
    protected FileDescriptor object;

    public void setObject(FileDescriptor object) {
        this.object = object;
    }

    public FileDescriptor getObject() {
        return object;
    }


    public void setAction(PersonAction action) {
        this.action = action;
    }

    public PersonAction getAction() {
        return action;
    }


}