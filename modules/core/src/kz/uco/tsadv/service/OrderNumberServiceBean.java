package kz.uco.tsadv.service;

import com.haulmont.cuba.core.Persistence;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Service(OrderNumberService.NAME)
public class OrderNumberServiceBean implements OrderNumberService {

    @Inject
    protected Persistence persistence;

    @Nonnull
    @Override
    public Integer getLastAssignmentOrderNumber() {
        return getLastOrderNumberForEntity("base_assignment");
    }

    @Nonnull
    @Override
    public Integer getLastDismissalOrderNumber() {
        return getLastOrderNumberForEntity("tsadv_dismissal");
    }

    @Nonnull
    @Override
    public Integer getLastAbsenceOrderNumber() {
        return getLastOrderNumberForEntity("tsadv_absence");
    }

    private int getLastOrderNumberForEntity(String entityTableName) {
        Integer result;
        String queryString;

        if (entityTableName.equals("tsadv_absence")) {
            queryString = "SELECT MAX(COALESCE((CASE WHEN a.order_num ~ '^\\d+$' THEN a.order_num::integer ELSE 0 end),0))\n" +
                    "FROM tsadv_absence a\n" +
                    "JOIN tsadv_dic_absence_type dat ON dat.id = a.type_id\n" +
                    "WHERE dat.IS_REQUIRED_ORDER_NUMBER is true " +
                    " and date_part('year', a.order_date) = date_part('year', current_date)";
        } else {
            queryString = "SELECT MAX(COALESCE((CASE WHEN order_number ~ '^\\d+$' THEN order_number::integer ELSE 0 end),0)) \n" +
                    "FROM " + entityTableName;
        }

        result = persistence.callInTransaction(em -> (Integer) em.createNativeQuery(queryString).getSingleResult());
        return result != null ? result : 0;
    }
}