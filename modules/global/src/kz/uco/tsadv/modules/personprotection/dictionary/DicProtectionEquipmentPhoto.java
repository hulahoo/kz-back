package kz.uco.tsadv.modules.personprotection.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|description")
@Table(name = "TSADV_DIC_PROTECTION_EQUIPMENT_PHOTO")
@Entity(name = "tsadv$DicProtectionEquipmentPhoto")
public class DicProtectionEquipmentPhoto extends AbstractParentEntity {
    private static final long serialVersionUID = 1572812539116107059L;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIC_PROTECTION_EQUIPMENT_ID")
    protected DicProtectionEquipment dicProtectionEquipment;

    public void setDicProtectionEquipment(DicProtectionEquipment dicProtectionEquipment) {
        this.dicProtectionEquipment = dicProtectionEquipment;
    }

    public DicProtectionEquipment getDicProtectionEquipment() {
        return dicProtectionEquipment;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }


}