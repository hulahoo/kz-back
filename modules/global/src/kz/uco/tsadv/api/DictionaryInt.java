package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.api.AbstractEntityInt;

@NamePattern("%s|code")
@MetaClass(name = "tsadv$DictionaryInt")
public class DictionaryInt extends AbstractEntityInt {
    private static final long serialVersionUID = 2888457533980701474L;

    @MetaProperty
    protected String code;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected String competenceTypeCode;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCompetenceTypeCode() {
        return competenceTypeCode;
    }

    public void setCompetenceTypeCode(String competenceTypeCode) {
        this.competenceTypeCode = competenceTypeCode;
    }
}