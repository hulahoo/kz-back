package kz.uco.tsadv.web.modules.personal.dictionary.dicawardtype;

import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.web.abstraction.six.AbstractDictionaryBrowse;
import kz.uco.tsadv.modules.personal.dictionary.DicAwardType;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class DicAwardTypeBrowse extends AbstractDictionaryBrowse<DicAwardType> {
    @Inject
    private GroupDatasource<DicAwardType, UUID> dicAwardTypesDs;

//    @Override
//    public void init(Map<String, Object> params) {
//        super.init(params);
//        if (params.get("promotionTypeId") != null){
//            dicAwardTypesDs.setQuery("select e from tsadv$DicAwardType e where e.promotionType.id = :param$promotionTypeId");
//        }
//    }
}