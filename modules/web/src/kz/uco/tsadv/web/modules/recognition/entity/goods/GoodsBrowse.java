package kz.uco.tsadv.web.modules.recognition.entity.goods;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.recognition.shop.Goods;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class GoodsBrowse extends AbstractLookup {

    public static final String DEFAULT_GOODS_IMAGE = "images/recognition/default-goods.png";
}