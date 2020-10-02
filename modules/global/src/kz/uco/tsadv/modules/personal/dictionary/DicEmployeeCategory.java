package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_EMPLOYEE_CATEGORY")
@Entity(name = "tsadv$DicEmployeeCategory")
public class DicEmployeeCategory extends AbstractDictionary {
    private static final long serialVersionUID = -6512451761169746617L;

}