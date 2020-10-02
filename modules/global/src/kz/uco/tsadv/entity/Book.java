package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.enums.Language;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@NamePattern("%s|bookNameLang1")
@Table(name = "TSADV_BOOK")
@Entity(name = "tsadv$Book")
public class Book extends StandardEntity {
    private static final long serialVersionUID = 9014957317368305579L;

    @Column(name = "BOOK_NAME_LANG1")
    protected String bookNameLang1;

    @Column(name = "BOOK_DESCRIPTION_LANG1", length = 2000)
    protected String bookDescriptionLang1;

    @Column(name = "AUTHOR_LANG1")
    protected String authorLang1;

    @Temporal(TemporalType.DATE)
    @Column(name = "PUBLISH_DATE")
    protected Date publishDate;

    @Column(name = "ISBN")
    protected String isbn;

    @Column(name = "ACTIVE")
    protected Boolean active;   // todo anuar 1 boolean переменные лучше делать обязательными всегда и указывать значение по умолчанию

    @DecimalMax(message = "{msg://maxValue}", value = "5.00")
    @DecimalMin(message = "{msg://minValue}", value = "0.00")
    @Column(name = "AVERAGE_SCORE")
    protected BigDecimal averageScore;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    protected DicBookCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    protected FileDescriptor image;

    @Column(name = "LANGUAGE_")
    protected String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FB2_ID")
    protected FileDescriptor fb2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EPUB_ID")
    protected FileDescriptor epub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOBI_ID")
    protected FileDescriptor mobi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KF8_ID")
    protected FileDescriptor kf8;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PDF_ID")
    protected FileDescriptor pdf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DJVU_ID")
    protected FileDescriptor djvu;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "book")
    protected List<BookReview> reviews;

    public void setReviews(List<BookReview> reviews) {
        this.reviews = reviews;
    }

    public List<BookReview> getReviews() {
        return reviews;
    }


    public void setDjvu(FileDescriptor djvu) {
        this.djvu = djvu;
    }

    public FileDescriptor getDjvu() {
        return djvu;
    }


    public void setKf8(FileDescriptor kf8) {
        this.kf8 = kf8;
    }

    public FileDescriptor getKf8() {
        return kf8;
    }

    public void setPdf(FileDescriptor pdf) {
        this.pdf = pdf;
    }

    public FileDescriptor getPdf() {
        return pdf;
    }


    public void setCategory(DicBookCategory category) {
        this.category = category;
    }

    public DicBookCategory getCategory() {
        return category;
    }

    public void setEpub(FileDescriptor epub) {
        this.epub = epub;
    }

    public FileDescriptor getEpub() {
        return epub;
    }

    public void setMobi(FileDescriptor mobi) {
        this.mobi = mobi;
    }

    public FileDescriptor getMobi() {
        return mobi;
    }


    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }


    public void setBookNameLang1(String bookNameLang1) {
        this.bookNameLang1 = bookNameLang1;
    }

    public String getBookNameLang1() {
        return bookNameLang1;
    }

    public void setBookDescriptionLang1(String bookDescriptionLang1) {
        this.bookDescriptionLang1 = bookDescriptionLang1;
    }

    public String getBookDescriptionLang1() {
        return bookDescriptionLang1;
    }

    public void setAuthorLang1(String authorLang1) {
        this.authorLang1 = authorLang1;
    }

    public String getAuthorLang1() {
        return authorLang1;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public BigDecimal getAverageScore() {
        if (averageScore == null) return BigDecimal.ZERO;   // todo anuar 1 лучше это на сеттер добавлять чтобы база не отличалась от интерфейса или даже лучше на определение свойства
        else return averageScore;
    }

    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public FileDescriptor getImage() {
        return image;
    }

    public void setLanguage(Language language) {
        this.language = language == null ? null : language.getId();
    }

    public Language getLanguage() {
        return language == null ? null : Language.fromId(language);
    }

    public void setFb2(FileDescriptor fb2) {
        this.fb2 = fb2;
    }

    public FileDescriptor getFb2() {
        return fb2;
    }


}