package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_REQUISITION_TYPE")
@Entity(name = "tsadv$DicRequisitionType")
public class DicRequisitionType extends AbstractDictionary {
    private static final long serialVersionUID = 165410767531281621L;

}