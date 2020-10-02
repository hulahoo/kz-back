package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_BANNER")
@Entity(name = "tsadv$Banner")
public class Banner extends StandardEntity {
    private static final long serialVersionUID = 7488618885431793713L;

    @NotNull
    @Column(name = "PAGE", nullable = false)
    protected String page;

    @NotNull
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IMAGE_LANG1_ID")
    protected FileDescriptor imageLang1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_LANG2_ID")
    protected FileDescriptor imageLang2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_LANG3_ID")
    protected FileDescriptor imageLang3;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    protected Boolean active = false;

    public void setPage(String page) {
        this.page = page;
    }

    public String getPage() {
        return page;
    }

    public void setImageLang1(FileDescriptor imageLang1) {
        this.imageLang1 = imageLang1;
    }

    public FileDescriptor getImageLang1() {
        return imageLang1;
    }

    public void setImageLang2(FileDescriptor imageLang2) {
        this.imageLang2 = imageLang2;
    }

    public FileDescriptor getImageLang2() {
        return imageLang2;
    }

    public void setImageLang3(FileDescriptor imageLang3) {
        this.imageLang3 = imageLang3;
    }

    public FileDescriptor getImageLang3() {
        return imageLang3;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }


}