package kz.uco.tsadv.web.modules.personal.dictionary.dicdocumenttype;

import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.web.abstraction.six.AbstractDictionaryBrowse;
import kz.uco.tsadv.modules.personal.dictionary.DicDocumentType;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class DicDocumentTypeBrowse extends AbstractDictionaryBrowse<DicDocumentType> {
    @Inject
    protected GroupDatasource<DicDocumentType, UUID> dicDocumentTypesDs;

    @Override
    public void init(Map<String, Object> params) {
        if (params.containsKey("isForeigner")) {
            dicDocumentTypesDs.setQuery("select e from tsadv$DicDocumentType e where e.foreigner = :param$isForeigner");
        }
        super.init(params);
    }
}