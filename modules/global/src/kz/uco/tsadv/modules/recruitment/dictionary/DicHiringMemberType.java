package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_HIRING_MEMBER_TYPE")
@Entity(name = "tsadv$DicHiringMemberType")
public class DicHiringMemberType extends AbstractDictionary {
    private static final long serialVersionUID = -4561823958431518303L;

}