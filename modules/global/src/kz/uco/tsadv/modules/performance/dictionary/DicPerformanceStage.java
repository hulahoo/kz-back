package kz.uco.tsadv.modules.performance.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_PERFORMANCE_STAGE")
@Entity(name = "tsadv_DicPerformanceStage")
public class DicPerformanceStage extends AbstractDictionary {
    private static final long serialVersionUID = 1711198967961674926L;
}