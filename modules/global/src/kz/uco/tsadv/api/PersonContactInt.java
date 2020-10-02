package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;

import java.util.UUID;

/**
 * Контактная информация (интеграционный объект)
 */
@NamePattern("%s|id")
@MetaClass(name = "tsadv$PersonContactInt")
public class PersonContactInt extends AbstractEntityInt {
    private static final long serialVersionUID = 7930705645655657130L;

    /**
     * Id типа контактной информации
     */
    @MetaProperty
    protected UUID contactType;

    /**
     * Тип контактной информации
     */
    @MetaProperty
    protected String contactTypeName;

    /**
     * Адрес/Номер
     */
    @MetaProperty
    protected String contactValue;



    public void setContactType(UUID contactType) {
        this.contactType = contactType;
    }

    public UUID getContactType() {
        return contactType;
    }

    public void setContactTypeName(String contactTypeName) {
        this.contactTypeName = contactTypeName;
    }

    public String getContactTypeName() {
        return contactTypeName;
    }

    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }

    public String getContactValue() {
        return contactValue;
    }


}