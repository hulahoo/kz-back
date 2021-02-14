package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@NamePattern("%s %s %s|book,author,postDate")
@Table(name = "TSADV_BOOK_REVIEW")
@Entity(name = "tsadv$BookReview")
public class BookReview extends StandardEntity {
    private static final long serialVersionUID = -4597826871426524319L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BOOK_ID")
    protected Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;     // todo anuar 0 тут нужно указать personGroup

    @Temporal(TemporalType.DATE)
    @Column(name = "POST_DATE")
    protected Date postDate;

    @Column(name = "REVIEW_TEXT", length = 2000)
    protected String reviewText;

    @Column(name = "RATING")
    protected BigDecimal rating;

    public void setAuthor(PersonGroupExt author) {
        this.author = author;
    }

    public PersonGroupExt getAuthor() {
        return author;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public BigDecimal getRating() {
        if (rating == null) return BigDecimal.ZERO;     // todo anuar 1 лучше на уровне определения свойства чтобы БД не отличалась
        else return rating;
    }


    public void setBook(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return book;
    }


    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getReviewText() {
        return reviewText;
    }
}