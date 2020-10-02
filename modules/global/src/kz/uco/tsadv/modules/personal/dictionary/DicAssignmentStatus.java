package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ASSIGNMENT_STATUS")
@Entity(name = "tsadv$DicAssignmentStatus")
public class DicAssignmentStatus extends AbstractDictionary {
    private static final long serialVersionUID = -6176614985106294178L;

    public static final String ACTIVE = "ACTIVE";   // Активное назначение
    public static final String TERMINATED = "TERMINATED";   // Прекращено (уволененый)
    public static final String SUSPENDED = "SUSPENDED";   // Приостановлено (декретный отпуск)
}