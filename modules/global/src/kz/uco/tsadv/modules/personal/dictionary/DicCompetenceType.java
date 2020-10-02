package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_COMPETENCE_TYPE")
@Entity(name = "tsadv$DicCompetenceType")
public class DicCompetenceType extends AbstractDictionary {
    private static final long serialVersionUID = -8035403286204522626L;

}