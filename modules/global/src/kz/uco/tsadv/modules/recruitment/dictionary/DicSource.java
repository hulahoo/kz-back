package kz.uco.tsadv.modules.recruitment.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_SOURCE")
@Entity(name = "tsadv$DicSource")
public class DicSource extends AbstractDictionary {
    private static final long serialVersionUID = 7350073675136430829L;

}
