package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_READINESS_LEVEL")
@Entity(name = "tsadv$DicReadinessLevel")
public class DicReadinessLevel extends AbstractDictionary {
    private static final long serialVersionUID = -1394267061101798815L;

}