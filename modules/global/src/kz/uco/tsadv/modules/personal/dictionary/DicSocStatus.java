package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_SOC_STATUS")
@Entity(name = "tsadv_DicSocStatus")
public class DicSocStatus extends AbstractDictionary {
    private static final long serialVersionUID = 4497635845499974809L;
}