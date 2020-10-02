package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;
import org.apache.commons.lang3.time.DateUtils;

import javax.annotation.Nullable;
import java.util.Date;


public enum CertificationPeriod implements EnumClass<Integer> {

    QUARTER(1),
    HALF_YEAR(2),
    YEAR(3),
    EVERY_TWO_YEAR(4),
    EVERY_THREE_YEAR(5),
    WEEK(6);

    private Integer id;

    CertificationPeriod(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static CertificationPeriod fromId(Integer id) {
        for (CertificationPeriod at : CertificationPeriod.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    public Date calculateNextDate(Date startDate) {
        Date nextDate = null;
        switch (this) {
            case EVERY_THREE_YEAR: {
                nextDate = DateUtils.addYears(startDate, 3);
                break;
            }
            case EVERY_TWO_YEAR: {
                nextDate = DateUtils.addYears(startDate, 2);
                break;
            }
            case YEAR: {
                nextDate = DateUtils.addYears(startDate, 1);
                break;
            }
            case HALF_YEAR: {
                nextDate = DateUtils.addMonths(startDate, 6);
                break;
            }
            case QUARTER: {
                nextDate = DateUtils.addMonths(startDate, 3);
                break;
            }
            case WEEK: {
                nextDate = DateUtils.addWeeks(startDate, 1);
                break;
            }
        }
        return nextDate;
    }
}