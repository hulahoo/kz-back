package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_FIELD_OF_ACTIVITY")
@Entity(name = "tsadv_DicFieldOfActivity")
public class DicFieldOfActivity extends AbstractDictionary {
    private static final long serialVersionUID = 972348259954547616L;
}