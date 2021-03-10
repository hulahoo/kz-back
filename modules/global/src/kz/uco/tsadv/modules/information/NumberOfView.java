package kz.uco.tsadv.modules.information;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Table(name = "TSADV_NUMBER_OF_VIEW")
@Entity(name = "tsadv_NumberOfView")
public class NumberOfView extends StandardEntity {
    private static final long serialVersionUID = -1010701463034415167L;

    @NotNull
    @Column(name = "ENTITY_NAME", nullable = false)
    protected String entityName;

    @NotNull
    @Column(name = "ENTITY_ID", nullable = false)
    protected UUID entityId;

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}