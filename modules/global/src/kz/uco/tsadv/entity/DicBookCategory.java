package kz.uco.tsadv.entity;

import javax.persistence.*;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Locale;

//@NamePattern("$getInstanceName")
@Table(name = "TSADV_DIC_BOOK_CATEGORY")
@Entity(name = "tsadv$DicBookCategory")
public class DicBookCategory extends AbstractDictionary {
    private static final long serialVersionUID = 3013244475422407714L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CATEGORY_ID")
    protected DicBookCategory parentBookCategory;

    @OneToMany(mappedBy = "category")
    protected List<Book> books;

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public DicBookCategory getParentBookCategory() {
        return parentBookCategory;
    }

    public void setParentBookCategory(DicBookCategory parentBookCategory) {
        this.parentBookCategory = parentBookCategory;
    }

    public String getInstanceName(String locale) {
        final String caption;

        switch (locale) {
            case "en": {
                caption = langValue3;
                break;
            }
            case "kz": {
                caption = langValue2;
                break;
            }
            default: {
                caption = null;
                break;
            }
        }

        return ObjectUtils.defaultIfNull(caption, langValue1);
    }
}