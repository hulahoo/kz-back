package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_JOB_REQUEST_STATUS")
@Entity(name = "tsadv$DicJobRequestStatus")
public class DicJobRequestStatus extends AbstractDictionary {
    private static final long serialVersionUID = -7767248938857563989L;

}