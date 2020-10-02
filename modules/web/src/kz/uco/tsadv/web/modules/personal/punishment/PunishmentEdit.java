package kz.uco.tsadv.web.modules.personal.punishment;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.config.PunishmentDatesConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicLCArticle;
import kz.uco.tsadv.modules.personal.enums.ArticleAttribute;
import kz.uco.tsadv.modules.personal.model.Punishment;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishmentEdit extends AbstractEditor<Punishment> {

    @Named("fieldGroup2.period")
    private DateField periodField;

    @Named("fieldGroup.orderDate")
    private DateField<Date> orderDateField;

    @Inject
    private CollectionDatasource<DicLCArticle, UUID> lawArticlesDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Configuration configuration = AppBeans.get(Configuration.NAME);
        PunishmentDatesConfig config = configuration.getConfig(PunishmentDatesConfig.class);

        boolean isNeedToPlusMonths = config.getEnabled();
        if (isNeedToPlusMonths) {
            int monthsCount = config.getMonthsCount();
            orderDateField.addValueChangeListener(e -> {
                Date date = (Date) e.getValue();
                Date newDate = DateUtils.addMonths(date, monthsCount);
                newDate = DateUtils.addDays(newDate, -1);
                periodField.setValue(newDate);
            });
        }
       lawArticlesFilter();
    }

    protected void lawArticlesFilter(){
        Map<String, Object> customMap = new HashMap<>();
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langValueByLang = "select e from tsadv$DicLCArticle e where e.attribute = :custom$articleName";
        if (language.equals("ru")){
            langValueByLang+= " order by e.langValue1";
        }else if (language.equals("en")){
            langValueByLang+= " order by e.langValue2";
        }else if (language.equals("kz")){
            langValueByLang+=" order by e.langValue3";
        }
        lawArticlesDs.setQuery(langValueByLang);
        customMap.put("articleName", ArticleAttribute.PUNISHMENT);
        lawArticlesDs.refresh(customMap);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && close){
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), Frame.NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }
}