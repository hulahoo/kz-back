package kz.uco.tsadv.global.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;


@Table(name = "TSADV_DIC_MONTH")
@Entity(name = "tsadv$DicMonth")
public class DicMonth extends AbstractDictionary {
    private static final long serialVersionUID = -3737571487973864937L;

}