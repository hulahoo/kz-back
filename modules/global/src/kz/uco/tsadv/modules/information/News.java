package kz.uco.tsadv.modules.information;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_NEWS")
@Entity(name = "tsadv_News")
public class News extends StandardEntity {
    private static final long serialVersionUID = 8857548885814265223L;

    @NotNull
    @Column(name = "NEWS_LANG1", nullable = false, length = 2000)
    protected String newsLang1;

    @Column(name = "NEWS_LANG2", length = 2000)
    protected String newsLang2;

    @Column(name = "NEWS_LANG3", length = 2000)
    protected String newsLang3;

    @NotNull
    @Column(name = "TITLE_LANG1", nullable = false, length = 256)
    protected String titleLang1;

    @Column(name = "TITLE_LANG2", length = 256)
    protected String titleLang2;

    @Column(name = "TITLE_LANG3", length = 256)
    protected String titleLang3;

    @NotNull
    @Column(name = "IS_PUBLISHED", nullable = false)
    protected Boolean isPublished = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BANNER_ID")
    protected FileDescriptor banner;

    public void setTitleLang1(String titleLang1) {
        this.titleLang1 = titleLang1;
    }

    public void setBanner(FileDescriptor banner) {
        this.banner = banner;
    }

    public FileDescriptor getBanner() {
        return banner;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public String getTitleLang3() {
        return titleLang3;
    }

    public void setTitleLang3(String titleLang3) {
        this.titleLang3 = titleLang3;
    }

    public String getTitleLang2() {
        return titleLang2;
    }

    public void setTitleLang2(String titleLang2) {
        this.titleLang2 = titleLang2;
    }

    public String getTitleLang1() {
        return titleLang1;
    }

    public String getNewsLang3() {
        return newsLang3;
    }

    public void setNewsLang3(String newsLang3) {
        this.newsLang3 = newsLang3;
    }

    public String getNewsLang2() {
        return newsLang2;
    }

    public void setNewsLang2(String newsLang2) {
        this.newsLang2 = newsLang2;
    }

    public String getNewsLang1() {
        return newsLang1;
    }

    public void setNewsLang1(String newsLang1) {
        this.newsLang1 = newsLang1;
    }
}