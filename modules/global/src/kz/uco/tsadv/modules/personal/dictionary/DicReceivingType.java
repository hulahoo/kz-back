package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_RECEIVING_TYPE")
@Entity(name = "tsadv_DicReceivingType")
public class DicReceivingType extends AbstractDictionary {
    private static final long serialVersionUID = -8799803798448775037L;
}