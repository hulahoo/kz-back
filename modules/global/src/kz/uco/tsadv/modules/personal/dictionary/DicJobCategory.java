package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_JOB_CATEGORY")
@Entity(name = "tsadv$DicJobCategory")
public class DicJobCategory extends AbstractDictionary {
    private static final long serialVersionUID = -6763098765596159609L;

}