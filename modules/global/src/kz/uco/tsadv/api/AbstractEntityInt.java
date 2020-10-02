package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@NamePattern("%s|id")
@MetaClass(name = "tsadv$AbstractEntityInt")
public class AbstractEntityInt extends BaseUuidEntity {
    private static final long serialVersionUID = 2620149051566004815L;


}