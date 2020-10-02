package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_OFFENCE_TYPE")
@Entity(name = "tsadv$DicOffenceType")
public class DicOffenceType extends AbstractDictionary {
    private static final long serialVersionUID = 8353825638400278653L;

}