package kz.uco.tsadv.web.screens.organizationincentivemonthresult;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AggregationInfo;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.dbview.OrganizationIncentiveMonthResultView;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@UiController("tsadv_OrganizationIncentiveMonthResult.browse")
@UiDescriptor("organization-incentive-month-result-browse.xml")
@LookupComponent("organizationIncentiveMonthResultsTable")
@LoadDataBeforeShow
public class OrganizationIncentiveMonthResultBrowse extends StandardLookup<OrganizationIncentiveMonthResultView> {

    @Inject
    private GroupTable<OrganizationIncentiveMonthResultView> organizationIncentiveMonthResultsTable;
    @Inject
    private Metadata metadata;

    public class EmptyStringFormatter implements Function<Object, String> {
        @Override
        public String apply(Object o) {
            return "";
        }

        @Override
        public <V> Function<V, String> compose(Function<? super V, ?> before) {
            return null;
        }

        @Override
        public <V> Function<Object, V> andThen(Function<? super String, ? extends V> after) {
            return null;
        }
    }

    protected class AggStrategy implements AggregationStrategy<DicIncentiveResultStatus,String>{
        @Override
        public String aggregate(Collection<DicIncentiveResultStatus> propertyValues) {
            return propertyValues.stream().findAny().get().getLangValue();
        }

        @Override
        public Class<String> getResultClass() {
            return String.class;
        }
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {

        organizationIncentiveMonthResultsTable.getColumn("status").setFormatter(new EmptyStringFormatter());

        AggregationInfo info = new AggregationInfo();
        info.setPropertyPath(metadata.getClass(OrganizationIncentiveMonthResultView.class).getPropertyPath("status"));
        info.setStrategy(new AggStrategy());

        organizationIncentiveMonthResultsTable.setAggregatable(true);
        Table.Column column = organizationIncentiveMonthResultsTable.getColumn("status");
        column.setAggregation(info);
    }


}