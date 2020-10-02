package kz.uco.tsadv.web.goodsorderdetail;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.ResizableTextArea;
import kz.uco.tsadv.modules.recognition.shop.GoodsOrderDetail;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Named;

public class GoodsOrderDetailExclude extends AbstractEditor<GoodsOrderDetail> {

    @Named("fieldGroup.excluded")
    private CheckBox excludedField;
    @Named("fieldGroup.comment")
    private ResizableTextArea commentField;

    @Override
    protected void postInit() {
        super.postInit();

        excludedField.addValueChangeListener(e -> {
            boolean checked = e.getValue() != null && BooleanUtils.isTrue((Boolean) e.getValue());
            if (checked) {
                getItem().setComment(null);
            }

            checkRequiredComment();
        });

        checkRequiredComment();
    }

    private void checkRequiredComment() {
        commentField.setRequired(BooleanUtils.isTrue(getItem().getExcluded()));
    }
}