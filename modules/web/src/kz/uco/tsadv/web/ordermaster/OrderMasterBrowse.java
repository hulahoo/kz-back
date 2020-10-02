package kz.uco.tsadv.web.ordermaster;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.model.OrderMaster;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class OrderMasterBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<OrderMaster, UUID> orderMastersDs;

    @Inject
    private Button runOrderBuilder;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        runOrderBuilder.setEnabled(false);

        orderMastersDs.addItemChangeListener(new Datasource.ItemChangeListener<OrderMaster>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<OrderMaster> e) {
                runOrderBuilder.setEnabled(e.getItem() != null);
            }
        });
    }

    @Override
    public void ready() {
        super.ready();

        orderMastersDs.addCollectionChangeListener(new CollectionDatasource.CollectionChangeListener<OrderMaster, UUID>() {
            @Override
            public void collectionChanged(CollectionDatasource.CollectionChangeEvent<OrderMaster, UUID> e) {
                switch (e.getOperation()) {
                    case ADD:
                    case UPDATE: {
                        orderMastersDs.refresh();
                        break;
                    }
                }
            }
        });
    }

    public void runOrderBuilder() {
        openWindow("order-builder",
                WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("orderMaster", orderMastersDs.getItem()));
    }
}