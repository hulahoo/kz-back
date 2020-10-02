package kz.uco.tsadv.config;


import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.defaults.DefaultInt;

/**
 * @author adilbekov.yernar
 */
@Source(type = SourceType.DATABASE)
public interface RecognitionConfig extends Config {

    @Property("recognition.daysBetweenThanks")
    @DefaultInt(30)
    int getDaysBetweenThanks();

    @Property("recognition.heartAwardDiscount")
    @DefaultInt(15)
    int getHeartAwardDiscount();

    @Property("recognition.thanksCountInMonth")
    @DefaultInt(2)
    int getThanksCountInMonth();

    @Property("recognition.hierarchyId")
    String getHierarchyId();

    @Property("recognition.aboutHeartAwardUrlRu")
    String getAboutHeartAwardUrlRu();

    @Property("recognition.aboutHeartAwardUrlEn")
    String getAboutHeartAwardUrlEn();

    @Property("recognition.menu.activateShop")
    @DefaultBoolean(false)
    Boolean getActivateShop();

    @Property("recognition.menu.activateHeartAwards")
    @DefaultBoolean(false)
    Boolean getActivateHeartAwards();

    @Property("recognition.hierarchyLevel")
    @DefaultInt(4)
    int getHierarchyLevel();

    @Property("recognition.betaTest.feedback")
    @DefaultBoolean(false)
    Boolean getBetaTestFeedback();


    @Property("recognition.compareDate")
    @DefaultInt(1)
    int getCompareDate();

    @Property("recognition.feedback.faq.ru")
    String getFeedbackFaqUrlRu();

    @Property("recognition.feedback.faq.en")
    String getFeedbackFaqUrlEn();

    @Property("recognition.QRCodeHtmlFilesURL")
    String getQRCodeHtmlFilesURL();

    @Property("recognition.betaTest.shop")
    @DefaultBoolean(false)
    Boolean getBetaTestShop();
}
