package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ATTACHMENT_CATEGORY")
@Entity(name = "tsadv$DicAttachmentCategory")
public class DicAttachmentCategory extends AbstractDictionary {
    private static final long serialVersionUID = 1658048141166363544L;

}