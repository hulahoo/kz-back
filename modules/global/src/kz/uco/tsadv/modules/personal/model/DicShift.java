package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_SHIFT")
@Entity(name = "tsadv_DicShift")
public class DicShift extends AbstractDictionary {
    private static final long serialVersionUID = 6054910510941564235L;
}