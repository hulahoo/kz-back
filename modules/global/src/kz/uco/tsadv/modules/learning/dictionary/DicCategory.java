package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.*;

@Table(name = "TSADV_DIC_CATEGORY")
@Entity(name = "tsadv$DicCategory")
public class DicCategory extends AbstractDictionary {
    private static final long serialVersionUID = 337912736938663616L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CATEGORY_ID")
    protected kz.uco.tsadv.modules.learning.dictionary.DicCategory parentCategory;

    @Column(name = "IMAGE")
    protected byte[] image;

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }


    public kz.uco.tsadv.modules.learning.dictionary.DicCategory getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(kz.uco.tsadv.modules.learning.dictionary.DicCategory parentCategory) {
        this.parentCategory = parentCategory;
    }


}