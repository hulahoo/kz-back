package kz.uco.tsadv.entity;

import javax.persistence.*;

import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_BOOK_CATEGORY")
@Entity(name = "tsadv$DicBookCategory")
public class DicBookCategory extends AbstractDictionary {
    private static final long serialVersionUID = 3013244475422407714L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CATEGORY_ID")
    protected DicBookCategory parentBookCategory;

    public DicBookCategory getParentBookCategory() {
        return parentBookCategory;
    }

    public void setParentBookCategory(DicBookCategory parentBookCategory) {
        this.parentBookCategory = parentBookCategory;
    }
}