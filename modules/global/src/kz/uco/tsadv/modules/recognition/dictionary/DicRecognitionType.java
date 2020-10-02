package kz.uco.tsadv.modules.recognition.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_RECOGNITION_TYPE")
@Entity(name = "tsadv$DicRecognitionType")
public class DicRecognitionType extends AbstractDictionary {
    private static final long serialVersionUID = -1257290956229834105L;

    @NotNull
    @Column(name = "COINS", nullable = false)
    protected Long coins;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPTY_STICKER_ID")
    protected FileDescriptor emptySticker;

    @NotNull
    @Column(name = "ALLOW_COINS_EDIT", nullable = false)
    protected Boolean allowCoinsEdit = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STICKER_ID")
    protected FileDescriptor sticker;

    public void setEmptySticker(FileDescriptor emptySticker) {
        this.emptySticker = emptySticker;
    }

    public FileDescriptor getEmptySticker() {
        return emptySticker;
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public void setAllowCoinsEdit(Boolean allowCoinsEdit) {
        this.allowCoinsEdit = allowCoinsEdit;
    }

    public Boolean getAllowCoinsEdit() {
        return allowCoinsEdit;
    }

    public void setSticker(FileDescriptor sticker) {
        this.sticker = sticker;
    }

    public FileDescriptor getSticker() {
        return sticker;
    }

}