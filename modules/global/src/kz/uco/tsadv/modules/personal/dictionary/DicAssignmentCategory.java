package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_ASSIGNMENT_CATEGORY")
@Entity(name = "tsadv$DicAssignmentCategory")
public class DicAssignmentCategory extends AbstractDictionary {
    private static final long serialVersionUID = 2559830761973901866L;

}