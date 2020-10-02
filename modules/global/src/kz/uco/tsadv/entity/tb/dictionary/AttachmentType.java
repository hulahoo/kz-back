package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_ATTACHMENT_TYPE")
@Entity(name = "tsadv$AttachmentType")
public class AttachmentType extends AbstractDictionary {
    private static final long serialVersionUID = -4232458907255390432L;

}