package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.tsadv.modules.personal.enums.ArticleAttribute;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_LC_ARTICLE")
@Entity(name = "tsadv$DicLCArticle")
public class DicLCArticle extends AbstractDictionary {
    private static final long serialVersionUID = 4778603708484287932L;

    @Column(name = "ARTICLE", nullable = false)
    protected String article;

    @Column(name = "ITEM")
    protected String item;

    @Column(name = "SUB_ITEM")
    protected String subItem;

    @Column(name = "ATTRIBUTE")
    protected String attribute;

    public void setAttribute(ArticleAttribute attribute) {
        this.attribute = attribute == null ? null : attribute.getId();
    }

    public ArticleAttribute getAttribute() {
        return attribute == null ? null : ArticleAttribute.fromId(attribute);
    }


    public void setArticle(String article) {
        this.article = article;
    }

    public String getArticle() {
        return article;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    public void setSubItem(String subItem) {
        this.subItem = subItem;
    }

    public String getSubItem() {
        return subItem;
    }


}