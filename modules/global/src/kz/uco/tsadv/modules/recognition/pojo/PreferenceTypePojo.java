package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@MetaClass(name = "tsadv$PreferenceTypePojo")
public class PreferenceTypePojo extends BaseUuidEntity {
    private static final long serialVersionUID = 4519591876114413774L;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected String code;

    @MetaProperty
    protected Long coins;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public Long getCoins() {
        return coins;
    }


}