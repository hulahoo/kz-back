package kz.uco.tsadv.modules.information;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

@Table(name = "TSADV_NEWS_COMMENT")
@Entity(name = "tsadv_NewsComment")
public class NewsComment extends StandardEntity {
    private static final long serialVersionUID = -6206252196487472527L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEWSID_ID")
    protected News newsid;

    @Column(name = "COMMENT_LANG1", length = 2000)
    protected String commentLang1;

    @Column(name = "COMMENT_LANG2", length = 2000)
    protected String commentLang2;

    @Column(name = "COMMENT_LANG3", length = 2000)
    protected String commentLang3;

    public String getCommentLang3() {
        return commentLang3;
    }

    public void setCommentLang3(String commentLang3) {
        this.commentLang3 = commentLang3;
    }

    public String getCommentLang2() {
        return commentLang2;
    }

    public void setCommentLang2(String commentLang2) {
        this.commentLang2 = commentLang2;
    }

    public String getCommentLang1() {
        return commentLang1;
    }

    public void setCommentLang1(String commentLang1) {
        this.commentLang1 = commentLang1;
    }

    public News getNewsid() {
        return newsid;
    }

    public void setNewsid(News newsid) {
        this.newsid = newsid;
    }
}