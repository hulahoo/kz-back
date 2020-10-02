package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_POSITION_STATUS")
@Entity(name = "tsadv$DicPositionStatus")
public class DicPositionStatus extends AbstractDictionary {
    private static final long serialVersionUID = 3634892572877440181L;

}