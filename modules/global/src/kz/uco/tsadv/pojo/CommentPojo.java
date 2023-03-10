package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CommentPojo implements Serializable {

    private String user;

    private Date date;

    private String comment;

    private Double rating;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public static final class CommentPojoBuilder {
        private String user;
        private Date date;
        private String comment;
        private Double rating;


        private CommentPojoBuilder() {
        }

        public static CommentPojoBuilder builder() {
            return new CommentPojoBuilder();
        }

        public CommentPojoBuilder user(String user) {
            this.user = user;
            return this;
        }

        public CommentPojoBuilder date(Date date) {
            this.date = date;
            return this;
        }

        public CommentPojoBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }


        public CommentPojoBuilder rating(Double rating) {
            this.rating = rating;
            return this;
        }

        public CommentPojo build() {
            CommentPojo commentPojo = new CommentPojo();
            commentPojo.setUser(user);
            commentPojo.setDate(date);
            commentPojo.setComment(comment);
            commentPojo.setRating(rating);
            return commentPojo;
        }
    }
}
