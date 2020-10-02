package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_LEARNING_OBJECT_TYPE")
@Entity(name = "tsadv$DicLearningObjectType")
public class DicLearningObjectType extends AbstractDictionary {
    private static final long serialVersionUID = 5162136378094090741L;

}