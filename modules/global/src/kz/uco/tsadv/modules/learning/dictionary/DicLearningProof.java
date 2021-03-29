package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_LEARNING_PROOF")
@Entity(name = "tsadv_DicLearningProof")
public class DicLearningProof extends AbstractDictionary {
    private static final long serialVersionUID = -2257166362482118521L;
}