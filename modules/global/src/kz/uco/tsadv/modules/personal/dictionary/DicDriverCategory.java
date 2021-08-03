package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_DRIVER_CATEGORY")
@Entity(name = "tsadv_DicDriverCategory")
public class DicDriverCategory extends AbstractDictionary {
    private static final long serialVersionUID = 8475089858773141777L;
}