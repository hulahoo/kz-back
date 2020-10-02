package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_LEARNING_FORM")
@Entity(name = "tsadv$DicLearningForm")
public class DicLearningForm extends AbstractDictionary {
    private static final long serialVersionUID = 4647510378673986483L;

}