package kz.uco.tsadv.web.modules.learning.test.v68;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.TextField;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.dictionary.DicTestType;
import kz.uco.tsadv.modules.learning.model.Test;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@Deprecated
public class TestEditV68 extends AbstractEditor<Test> {

    @Named("fieldGroup.questionPerPage")
    protected TextField questionPerPageField;
    @Named("fieldGroup.showSectionNewPage")
    protected CheckBox showSectionNewPageField;
    @Inject
    protected CommonService commonService;

    @Override
    protected void initNewItem(Test item) {
        super.initNewItem(item);
        DicTestType recruiting = commonService.getEntity(DicTestType.class, "RECRUITING");
        if (recruiting!= null){
            item.setType(recruiting);
        }
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

//        showSectionNewPageField.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                initQuestionPerPage();
//            }
//        });

        initQuestionPerPage();
    }

    protected void initQuestionPerPage() {
        boolean isShowSectionNewPage = BooleanUtils.isTrue(showSectionNewPageField.getValue());
        questionPerPageField.setVisible(!isShowSectionNewPage);
        questionPerPageField.setRequired(!isShowSectionNewPage);
    }
}