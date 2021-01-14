package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Table(name = "TSADV_ADDITIONAL_FILE")
@Entity(name = "tsadv_AdditionalFile")
@NamePattern("%s|file")
public class AdditionalFile extends AbstractParentEntity {
    private static final long serialVersionUID = 1694665674475581469L;

    @JoinColumn(name = "FILE_ID")
    @NotNull
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private FileDescriptor file;

    @NotNull
    @Column(name = "RELATION_ENTITY_ID", nullable = false)
    private UUID relationEntityId;

    public UUID getRelationEntityId() {
        return relationEntityId;
    }

    public void setRelationEntityId(UUID relationEntityId) {
        this.relationEntityId = relationEntityId;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }

}