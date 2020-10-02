package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
@MetaClass(name = "tsadv$PreferencePojo")
public class PreferencePojo extends BaseUuidEntity {

    @MetaProperty
    private UUID typeId;

    @MetaProperty
    private String typeName;

    @MetaProperty
    private String description;

    @MetaProperty
    private String reverseText;

    @MetaProperty
    private Long coins = 0L;

    @MetaProperty
    private Boolean showCoinsDescription;

    public String getReverseText() {
        return reverseText;
    }

    public void setReverseText(String reverseText) {
        this.reverseText = reverseText;
    }

    public Boolean getShowCoinsDescription() {
        return showCoinsDescription;
    }

    public void setShowCoinsDescription(Boolean showCoinsDescription) {
        this.showCoinsDescription = showCoinsDescription;
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public UUID getTypeId() {
        return typeId;
    }

    public void setTypeId(UUID typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
