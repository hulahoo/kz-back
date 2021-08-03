package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_GUARDIAN_TYPE")
@Entity(name = "tsadv_DicGuardianType")
public class DicGuardianType extends AbstractDictionary {
    private static final long serialVersionUID = 8168027707261425960L;
}