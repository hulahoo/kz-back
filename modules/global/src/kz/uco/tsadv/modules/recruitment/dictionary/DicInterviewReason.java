package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_INTERVIEW_REASON")
@Entity(name = "tsadv$DicInterviewReason")
public class DicInterviewReason extends AbstractDictionary {
    private static final long serialVersionUID = -1425194855560560066L;

}