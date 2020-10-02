package kz.uco.tsadv.modules.personprotection.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.dictionary.DicMeasureType;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import kz.uco.tsadv.modules.performance.dictionary.DicUOM;
import kz.uco.tsadv.modules.recruitment.enums.HS_Periods;
import kz.uco.tsadv.entity.tb.dictionary.UOM;

@Table(name = "TSADV_DIC_PROTECTION_EQUIPMENT")
@Entity(name = "tsadv$DicProtectionEquipment")
public class DicProtectionEquipment extends AbstractDictionary {
    private static final long serialVersionUID = 7508103512079160691L;

    @Column(name = "GOST")
    protected String gost;


    @Column(name = "REPLACEMENT_DURATION")
    protected Integer replacementDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected DicProtectionEquipmentType type;

    @NotNull
    @Column(name = "IS_SINGLE", nullable = false)
    protected Boolean isSingle = false;

    @Column(name = "REPLACEMENT_UOM")
    protected Integer replacementUom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIT_OF_MEASURE_ID")
    protected UOM unitOfMeasure;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "dicProtectionEquipment")
    protected List<DicProtectionEquipmentPhoto> dicProtectionEquipmentPhoto;
    public UOM getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UOM unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }



    public HS_Periods getReplacementUom() {
        return replacementUom == null ? null : HS_Periods.fromId(replacementUom);
    }

    public void setReplacementUom(HS_Periods replacementUom) {
        this.replacementUom = replacementUom == null ? null : replacementUom.getId();
    }





    public void setDicProtectionEquipmentPhoto(List<DicProtectionEquipmentPhoto> dicProtectionEquipmentPhoto) {
        this.dicProtectionEquipmentPhoto = dicProtectionEquipmentPhoto;
    }

    public List<DicProtectionEquipmentPhoto> getDicProtectionEquipmentPhoto() {
        return dicProtectionEquipmentPhoto;
    }


    public void setReplacementDuration(Integer replacementDuration) {
        this.replacementDuration = replacementDuration;
    }

    public Integer getReplacementDuration() {
        return replacementDuration;
    }

    public void setType(DicProtectionEquipmentType type) {
        this.type = type;
    }

    public DicProtectionEquipmentType getType() {
        return type;
    }

    public void setIsSingle(Boolean isSingle) {
        this.isSingle = isSingle;
    }

    public Boolean getIsSingle() {
        return isSingle;
    }






    public void setGost(String gost) {
        this.gost = gost;
    }

    public String getGost() {
        return gost;
    }




}