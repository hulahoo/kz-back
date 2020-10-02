package kz.uco.tsadv.modules.learning.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_LEARNING_TYPE")
@Entity(name = "tsadv$DicLearningType")
public class DicLearningType extends AbstractDictionary {
    private static final long serialVersionUID = -4830818302698618418L;

}