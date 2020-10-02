package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_REQUISITION_STATUS")
@Entity(name = "tsadv$DicRequisitionStatus")
public class DicRequisitionStatus extends AbstractDictionary {
    private static final long serialVersionUID = -2785601308791217742L;

}