package kz.uco.tsadv.web.dicattestationevent;

import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.web.abstraction.six.AbstractDictionaryBrowse;
import kz.uco.tsadv.modules.learning.dictionary.DicAttestationEvent;

import javax.inject.Inject;
import java.util.UUID;

public class DicAttestationEventBrowse extends AbstractDictionaryBrowse<DicAttestationEvent> {

    @Inject
    private GroupDatasource<DicAttestationEvent, UUID> dicAttestationEventsDs;

//    @Override
//    public void onInit(Map<String, Object> params) {
//        super.init(params);
//        if (params.get("dicAttestationResultId") != null) {
//            dicAttestationEventsDs.setQuery("select e from tsadv$DicAttestationEvent e where e.dicAttestationResult.id = :param$dicAttestationResultId");
//        }
//    }
}