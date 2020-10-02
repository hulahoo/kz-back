package kz.uco.tsadv.aggregation;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;

import java.util.Collection;
import java.util.List;

/**
 * @author veronika.buksha
 */
public class StringAggregation implements AggregationStrategy<List<Object>, String> {
    @Override
    public String aggregate(Collection<List<Object>> propertyValues) {
        Messages messages = AppBeans.get(Messages.NAME);

        return messages.getMainMessage("table.Total");
    }

    @Override
    public Class<String> getResultClass() {
        return String.class;
    }
}
