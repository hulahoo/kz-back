package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_ISSUING_AUTHORITY")
@Entity(name = "tsadv_DicIssuingAuthority")
@NamePattern("%s|langValue")
public class DicIssuingAuthority extends AbstractDictionary {
    private static final long serialVersionUID = 7807920563455251416L;
}