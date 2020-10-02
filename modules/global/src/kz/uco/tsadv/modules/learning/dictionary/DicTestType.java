package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_TEST_TYPE")
@Entity(name = "tsadv$DicTestType")
public class DicTestType extends AbstractDictionary {
    private static final long serialVersionUID = 1091538609826849161L;

}