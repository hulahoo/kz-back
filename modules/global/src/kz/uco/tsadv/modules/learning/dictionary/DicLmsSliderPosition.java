package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_LMS_SLIDER_POSITION")
@Entity(name = "tsadv_DicLmsSliderPosition")
public class DicLmsSliderPosition extends AbstractDictionary {
    private static final long serialVersionUID = -5762804901668115268L;
}