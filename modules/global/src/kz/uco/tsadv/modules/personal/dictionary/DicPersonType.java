package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_PERSON_TYPE")
@Entity(name = "tsadv$DicPersonType")
public class DicPersonType extends AbstractDictionary {
    private static final long serialVersionUID = -9036293960657685012L;

    @Column(name = "SORT_ORDER")
    protected Integer sortOrder;

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }


}