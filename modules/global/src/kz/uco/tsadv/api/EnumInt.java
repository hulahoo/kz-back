package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;

/**
 * Created by Murat on 05.03.2018.
 */
@MetaClass(name = "tsadv$EnumInt")
public class EnumInt {
    private static final long serialVersionUID = 2888457543986701474L;

    String id;

    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
