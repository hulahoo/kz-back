package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_EDUCATIONAL_ESTABLISHMENT")
@Entity(name = "tsadv$DicEducationalEstablishment")
public class DicEducationalEstablishment extends AbstractDictionary {
    private static final long serialVersionUID = 4331624544740964654L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDUCATIONAL_ESTABLISHMENT_TYPE_ID")
    protected DicEducationalEstablishmentType educationalEstablishmentType;

    public void setEducationalEstablishmentType(DicEducationalEstablishmentType educationalEstablishmentType) {
        this.educationalEstablishmentType = educationalEstablishmentType;
    }

    public DicEducationalEstablishmentType getEducationalEstablishmentType() {
        return educationalEstablishmentType;
    }


}