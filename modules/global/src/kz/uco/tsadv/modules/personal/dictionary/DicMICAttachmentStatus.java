package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_MIC_ATTACHMENT_STATUS")
@Entity(name = "tsadv$DicMICAttachmentStatus")
public class DicMICAttachmentStatus extends AbstractDictionary {
    private static final long serialVersionUID = -8174461225689372730L;
}