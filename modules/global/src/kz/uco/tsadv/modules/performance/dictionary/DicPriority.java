package kz.uco.tsadv.modules.performance.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_PRIORITY")
@Entity(name = "tsadv$DicPriority")
public class DicPriority extends AbstractDictionary {
    private static final long serialVersionUID = -6897765251283536826L;

}