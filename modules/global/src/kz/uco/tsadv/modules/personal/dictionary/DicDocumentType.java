package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_DOCUMENT_TYPE")
@Entity(name = "tsadv$DicDocumentType")
public class DicDocumentType extends AbstractDictionary {
    private static final long serialVersionUID = -6405004865430479661L;

}