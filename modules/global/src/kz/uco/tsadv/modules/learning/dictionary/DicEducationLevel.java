package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_EDUCATION_LEVEL")
@Entity(name = "tsadv$DicEducationLevel")
public class DicEducationLevel extends AbstractDictionary {
    private static final long serialVersionUID = -6077695223105055465L;

}