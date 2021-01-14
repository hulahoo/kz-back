package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.*;

@Table(name = "TSADV_DIC_MILITARY_RANK")
@Entity(name = "tsadv$DicMilitaryRank")
public class DicMilitaryRank extends AbstractDictionary {
    private static final long serialVersionUID = 1561579145679648436L;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TROOP_TYPE_ID")
    protected DicTroopType troopType;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_COMPOSITION_ID")
    protected DicTroopsStructure categoryComposition;

    public DicTroopsStructure getCategoryComposition() {
        return categoryComposition;
    }

    public void setCategoryComposition(DicTroopsStructure categoryComposition) {
        this.categoryComposition = categoryComposition;
    }


    public void setTroopType(DicTroopType troopType) {
        this.troopType = troopType;
    }

    public DicTroopType getTroopType() {
        return troopType;
    }


}