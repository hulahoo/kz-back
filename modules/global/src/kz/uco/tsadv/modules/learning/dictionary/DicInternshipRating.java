package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_INTERNSHIP_RATING")
@Entity(name = "tsadv$DicInternshipRating")
public class DicInternshipRating extends AbstractDictionary {
    private static final long serialVersionUID = -1038777552606207594L;

}