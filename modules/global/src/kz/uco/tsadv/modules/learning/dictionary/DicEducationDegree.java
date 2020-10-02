package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_EDUCATION_DEGREE")
@Entity(name = "tsadv$DicEducationDegree")
public class DicEducationDegree extends AbstractDictionary {
    private static final long serialVersionUID = -3909571674658488429L;

}