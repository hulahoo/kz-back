package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_INTERVIEW_STATUS")
@Entity(name = "tsadv$DicInterviewStatus")
public class DicInterviewStatus extends AbstractDictionary {
    private static final long serialVersionUID = 4031720737715794914L;

}