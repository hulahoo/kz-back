package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.tsadv.entity.dbview.AbsenceBalanceV;
import kz.uco.tsadv.service.AbsenceBalanceService;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class AbsenceBalanceDatasource extends CustomCollectionDatasource<AbsenceBalanceV, UUID> {

    protected UUID personGroupId;
    protected AbsenceBalanceService absenceBalanceService = AppBeans.get(AbsenceBalanceService.class);

    @Override
    protected Collection<AbsenceBalanceV> getEntities(Map<String, Object> params) {
        Assert.notNull(personGroupId, "Absence balance: Person group id is null!");
        return absenceBalanceService.getAbsenceBalance(personGroupId);
    }

    public UUID getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(UUID personGroupId) {
        this.personGroupId = personGroupId;
    }
}
