package kz.uco.tsadv.modules.performance.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_PARTICIPANT_TYPE")
@Entity(name = "tsadv$DicParticipantType")
public class DicParticipantType extends AbstractDictionary {
    private static final long serialVersionUID = -8818203498083823751L;

}