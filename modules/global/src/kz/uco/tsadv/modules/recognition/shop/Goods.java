package kz.uco.tsadv.modules.recognition.shop;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.recognition.dictionary.DicGoodsCategory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;

@NamePattern("%s|name")
@Table(name = "TSADV_GOODS")
@Entity(name = "tsadv$Goods")
public class Goods extends AbstractParentEntity {
    private static final long serialVersionUID = -3776771463751766289L;

    @NotNull
    @Column(name = "NAME_LANG1", nullable = false, length = 500)
    protected String nameLang1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected DicGoodsCategory category;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "good")
    protected List<GoodsImage> goodsImages;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "good")
    protected List<GoodsImageForReport> goodsImagesForReport;

    @Column(name = "NAME_LANG2", length = 500)
    protected String nameLang2;

    @Column(name = "NAME_LANG3", length = 500)
    protected String nameLang3;

    @Column(name = "NAME_LANG4", length = 500)
    protected String nameLang4;

    @Column(name = "NAME_LANG5", length = 500)
    protected String nameLang5;

    @Column(name = "DESCRIPTION_LANG1", length = 2000)
    protected String descriptionLang1;

    @Column(name = "DESCRIPTION_LANG2", length = 2000)
    protected String descriptionLang2;

    @Column(name = "DESCRIPTION_LANG3", length = 2000)
    protected String descriptionLang3;

    @Column(name = "DESCRIPTION_LANG4", length = 2000)
    protected String descriptionLang4;

    @Column(name = "DESCRIPTION_LANG5", length = 2000)
    protected String descriptionLang5;

    @NotNull
    @Column(name = "PRICE", nullable = false)
    protected Double price;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    protected Boolean active = false;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECOGNITION_PROVIDER_ID")
    protected RecognitionProvider recognitionProvider;

    public void setRecognitionProvider(RecognitionProvider recognitionProvider) {
        this.recognitionProvider = recognitionProvider;
    }

    public RecognitionProvider getRecognitionProvider() {
        return recognitionProvider;
    }


    public void setNameLang1(String nameLang1) {
        this.nameLang1 = nameLang1;
    }

    public String getNameLang1() {
        return nameLang1;
    }

    public void setNameLang2(String nameLang2) {
        this.nameLang2 = nameLang2;
    }

    public String getNameLang2() {
        return nameLang2;
    }

    public void setNameLang3(String nameLang3) {
        this.nameLang3 = nameLang3;
    }

    public String getNameLang3() {
        return nameLang3;
    }

    public void setNameLang4(String nameLang4) {
        this.nameLang4 = nameLang4;
    }

    public String getNameLang4() {
        return nameLang4;
    }

    public void setNameLang5(String nameLang5) {
        this.nameLang5 = nameLang5;
    }

    public String getNameLang5() {
        return nameLang5;
    }



    public void setCategory(DicGoodsCategory category) {
        this.category = category;
    }

    public DicGoodsCategory getCategory() {
        return category;
    }


    public List<GoodsImage> getGoodsImages() {
        return goodsImages;
    }

    public void setGoodsImages(List<GoodsImage> goodsImages) {
        this.goodsImages = goodsImages;
    }

    public List<GoodsImageForReport> getGoodsImagesForReport() {
        return goodsImagesForReport;
    }

    public void setGoodsImagesForReport(List<GoodsImageForReport> goodsImagesForReport) {
        this.goodsImagesForReport = goodsImagesForReport;
    }

    public void setDescriptionLang1(String descriptionLang1) {
        this.descriptionLang1 = descriptionLang1;
    }

    public String getDescriptionLang1() {
        return descriptionLang1;
    }

    public void setDescriptionLang2(String descriptionLang2) {
        this.descriptionLang2 = descriptionLang2;
    }

    public String getDescriptionLang2() {
        return descriptionLang2;
    }

    public void setDescriptionLang3(String descriptionLang3) {
        this.descriptionLang3 = descriptionLang3;
    }

    public String getDescriptionLang3() {
        return descriptionLang3;
    }

    public void setDescriptionLang4(String descriptionLang4) {
        this.descriptionLang4 = descriptionLang4;
    }

    public String getDescriptionLang4() {
        return descriptionLang4;
    }

    public void setDescriptionLang5(String descriptionLang5) {
        this.descriptionLang5 = descriptionLang5;
    }

    public String getDescriptionLang5() {
        return descriptionLang5;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }


    @MetaProperty(related = {"nameLang1", "nameLang2", "nameLang3", "nameLang4", "nameLang5"})
    public String getName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String descriptionOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (descriptionOrder != null) {
            List<String> langs = Arrays.asList(descriptionOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return nameLang1;
                }
                case 1: {
                    return nameLang2;
                }
                case 2: {
                    return nameLang3;
                }
                case 3: {
                    return nameLang4;
                }
                case 4: {
                    return nameLang5;
                }
                default:
                    return nameLang1;
            }
        }
        return nameLang1;
    }


    @MetaProperty(related = {"descriptionLang1", "descriptionLang2", "descriptionLang3", "descriptionLang4", "descriptionLang5"})
    public String getDescription() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String descriptionOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (descriptionOrder != null) {
            List<String> langs = Arrays.asList(descriptionOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return descriptionLang1;
                }
                case 1: {
                    return descriptionLang2;
                }
                case 2: {
                    return descriptionLang3;
                }
                case 3: {
                    return descriptionLang4;
                }
                case 4: {
                    return descriptionLang5;
                }
                default:
                    return descriptionLang1;
            }
        }
        return descriptionLang1;
    }

}