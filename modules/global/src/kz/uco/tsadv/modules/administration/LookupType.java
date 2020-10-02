package kz.uco.tsadv.modules.administration;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_LOOKUP_TYPE")
@Entity(name = "tsadv$LookupType")
public class LookupType extends AbstractParentEntity {
    private static final long serialVersionUID = 4032139774758224298L;

    @NotNull
    @Column(name = "LOOKUP_TYPE", nullable = false)
    protected String lookupType;

    @NotNull
    @Column(name = "LOOKUP_NAME_LANG1", nullable = false)
    protected String lookupNameLang1;

    @Column(name = "LOOKUP_NAME_LANG2")
    protected String lookupNameLang2;

    @Column(name = "LOOKUP_NAME_LANG3")
    protected String lookupNameLang3;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "lookupType")
    protected List<LookupValue> lookupValue;

    public void setLookupValue(List<LookupValue> lookupValue) {
        this.lookupValue = lookupValue;
    }

    public List<LookupValue> getLookupValue() {
        return lookupValue;
    }


    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    public String getLookupType() {
        return lookupType;
    }

    public void setLookupNameLang1(String lookupNameLang1) {
        this.lookupNameLang1 = lookupNameLang1;
    }

    public String getLookupNameLang1() {
        return lookupNameLang1;
    }

    public void setLookupNameLang2(String lookupNameLang2) {
        this.lookupNameLang2 = lookupNameLang2;
    }

    public String getLookupNameLang2() {
        return lookupNameLang2;
    }

    public void setLookupNameLang3(String lookupNameLang3) {
        this.lookupNameLang3 = lookupNameLang3;
    }

    public String getLookupNameLang3() {
        return lookupNameLang3;
    }


}