package kz.uco.tsadv.web.screens.organizationincentivemonthresult;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.AggregationInfo;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.dbview.OrganizationIncentiveMonthResultView;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

@UiController("tsadv_OrganizationIncentiveMonthResult.browse")
@UiDescriptor("organization-incentive-month-result-browse.xml")
@LookupComponent("organizationIncentiveMonthResultsTable")
@LoadDataBeforeShow
public class OrganizationIncentiveMonthResultBrowse extends StandardLookup<OrganizationIncentiveMonthResultView> {

    protected String PERIOD_DATE_FORMAT = "MMM yyyy";
    @Inject
    private GroupTable<OrganizationIncentiveMonthResultView> organizationIncentiveMonthResultsTable;
    @Inject
    private Metadata metadata;


    protected class LocalizedDateFormatter implements Function<Object, String>{

        protected String format;

        LocalizedDateFormatter(String format){
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        @Override
        public String apply(Object value) {
            Date date = (Date) value;
            UserSessionSource us = AppBeans.get(UserSessionSource.NAME);
            Locale currentLocale = us.getLocale();
            if(currentLocale.getLanguage().equals("kz")) currentLocale = new Locale("ru");
            DateFormat df = new SimpleDateFormat(format,currentLocale);
            return df.format(date);
        }

    }

    public class EmptyStringFormatter implements Function<Object, String> {
        @Override
        public String apply(Object o) {
            return "";
        }
    }

    protected class StatusColumnAggregationStrategy implements AggregationStrategy<DicIncentiveResultStatus,String>{
        @Override
        public String aggregate(Collection<DicIncentiveResultStatus> propertyValues) {
            if(propertyValues != null && !propertyValues.isEmpty()) {
                return propertyValues.stream().findAny().get().getLangValue();
            }
            return "";
        }

        @Override
        public Class<String> getResultClass() {
            return String.class;
        }
    }

    protected class CommentColumnAggregationStrategy implements AggregationStrategy<String,String>{
        @Override
        public String aggregate(Collection<String> propertyValues) {
            if(propertyValues != null && !propertyValues.isEmpty()) {
                return propertyValues.stream().findAny().get();
            }
            return "";
        }

        @Override
        public Class<String> getResultClass() {
            return String.class;
        }
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        initTableUI();
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        organizationIncentiveMonthResultsTable.expandAll();

    }



    protected void initTableUI(){
        organizationIncentiveMonthResultsTable.getColumn("status").setFormatter(new EmptyStringFormatter());
        organizationIncentiveMonthResultsTable.getColumn("comment").setFormatter(new EmptyStringFormatter());

        AggregationInfo statusAggInfo = new AggregationInfo();
        statusAggInfo.setPropertyPath(metadata.getClass(OrganizationIncentiveMonthResultView.class).getPropertyPath("status"));
        statusAggInfo.setStrategy(new StatusColumnAggregationStrategy());

        organizationIncentiveMonthResultsTable.setAggregatable(true);
        Table.Column statusColumn = organizationIncentiveMonthResultsTable.getColumn("status");
        statusColumn.setAggregation(statusAggInfo);

        AggregationInfo commentAggInfo = new AggregationInfo();
        commentAggInfo.setPropertyPath(metadata.getClass(OrganizationIncentiveMonthResultView.class).getPropertyPath("comment"));
        commentAggInfo.setStrategy(new CommentColumnAggregationStrategy());
        Table.Column commentColumn = organizationIncentiveMonthResultsTable.getColumn("comment");
        commentColumn.setAggregation(commentAggInfo);


        LocalizedDateFormatter localizedDateFormatter = new LocalizedDateFormatter(PERIOD_DATE_FORMAT);
        organizationIncentiveMonthResultsTable.getColumn("period").setFormatter(localizedDateFormatter);
    }


}