package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_REQUISITION_ACCESS_LEVEL")
@Entity(name = "tsadv$DicRequisitionAccessLevel")
public class DicRequisitionAccessLevel extends AbstractDictionary {
    private static final long serialVersionUID = -8716944743259158047L;

}