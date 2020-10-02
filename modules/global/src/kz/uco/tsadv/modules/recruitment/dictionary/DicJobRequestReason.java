package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_JOB_REQUEST_REASON")
@Entity(name = "tsadv$DicJobRequestReason")
public class DicJobRequestReason extends AbstractDictionary {
    private static final long serialVersionUID = 8679060965662467579L;

}