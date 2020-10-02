package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import javax.persistence.Transient;

@MetaClass(name = "tsadv$RecognitionTypePojo")
public class RecognitionTypePojo extends BaseUuidEntity {
    private static final long serialVersionUID = 1780215588976929411L;

    @MetaProperty
    protected String name;

    @Transient
    @MetaProperty
    protected Boolean showEmpty = false;

    @MetaProperty
    protected String code;

    @MetaProperty
    protected String image;

    @MetaProperty
    protected Long count = 0L;

    @MetaProperty
    protected Long coins = 0L;

    public void setShowEmpty(Boolean showEmpty) {
        this.showEmpty = showEmpty;
    }

    public Boolean getShowEmpty() {
        return showEmpty;
    }


    public Long getCoins() {
        return coins;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        image = String.format("./dispatch/rcg_type_image/%s/%d", code, !showEmpty ? -1 : count);
        return image;
    }
}