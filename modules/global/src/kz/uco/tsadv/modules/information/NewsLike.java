package kz.uco.tsadv.modules.information;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_NEWS_LIKE")
@Entity(name = "tsadv_NewsLike")
public class NewsLike extends StandardEntity {
    private static final long serialVersionUID = 8545679941508987276L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NEWS_ID_ID")
    protected News newsId;

    public News getNewsId() {
        return newsId;
    }

    public void setNewsId(News newsId) {
        this.newsId = newsId;
    }
}