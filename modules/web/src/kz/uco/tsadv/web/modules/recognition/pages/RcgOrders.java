package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.enums.GoodsOrderStatus;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrder;
import kz.uco.tsadv.web.modules.events.RcgOrdersEvent;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RcgOrders extends AbstractRcgPage {

    @Inject
    private GroupDatasource<GoodsOrder, UUID> goodsOrdersDs;

    @Override
    protected void loadPage(Map<String, Object> params) {
        UUID hierarchyId = null;
        if (StringUtils.isNotBlank(recognitionConfig.getHierarchyId())) {
            hierarchyId = UUID.fromString(recognitionConfig.getHierarchyId());
        }

        PersonGroupExt personGroupExt = currentAssignmentExt.getPersonGroup();
        if (personGroupExt == null) {
            throw new NullPointerException("");
        }

        List<UUID> profilesId = recognitionService.getPersonGroupEmployee(hierarchyId, personGroupExt.getId(), true);
        if (profilesId != null && !profilesId.isEmpty()) {
            String sqlIds = profilesId.stream()
                    .map(uuid -> "'" + uuid.toString() + "'")
                    .collect(Collectors.joining(","));

            goodsOrdersDs.setQuery(String.format("select e from tsadv$GoodsOrder e " +
                    "join e.personGroup pg " +
                    "where pg.id in (%s) and e.status = :custom$onApproval order by e.orderDate", sqlIds));

            goodsOrdersDs.refresh(ParamsMap.of("onApproval", GoodsOrderStatus.ON_APPROVAL));
        }
    }

    public void confirmGoodsOrder(GoodsOrder goodsOrder, String name) {
        AbstractEditor editor = openEditor("goods-order-confirm",
                goodsOrder,
                WindowManager.OpenType.DIALOG);

        editor.addCloseListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                goodsOrdersDs.refresh();
                events.publish(new RcgOrdersEvent(this, goodsOrdersDs.getItems().size()));
            }
        });
        //editor.addCloseWithCommitListener(() -> goodsOrdersDs.refresh());
    }
}