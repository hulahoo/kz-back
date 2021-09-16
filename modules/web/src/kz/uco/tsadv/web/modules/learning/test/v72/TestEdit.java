package kz.uco.tsadv.web.modules.learning.test.v72;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.dictionary.DicTestType;
import kz.uco.tsadv.modules.learning.model.Test;
import kz.uco.tsadv.modules.learning.model.TestSection;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;

@UiController("tsadv$Test.edit")
@UiDescriptor("test-edit.xml")
@EditedEntityContainer("testDc")
@LoadDataBeforeShow
public class TestEdit extends StandardEditor<Test> {

    @Inject
    protected CommonService commonService;
    @Inject
    protected CheckBox showSectionNewPageField;
    @Inject
    protected TextField<Integer> questionPerPageField;
    @Inject
    protected DataManager dataManager;

    @Subscribe
    protected void onInitEntity(InitEntityEvent<Test> event) {
        Test item = event.getEntity();

        DicTestType recruiting = commonService.getEntity(DicTestType.class, "RECRUITING");
        if (recruiting != null) {
            item.setType(recruiting);
        }
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        initQuestionPerPage();
    }

    protected void initQuestionPerPage() {
        boolean isShowSectionNewPage = BooleanUtils.isTrue(showSectionNewPageField.getValue());
        questionPerPageField.setVisible(!isShowSectionNewPage);
        questionPerPageField.setRequired(!isShowSectionNewPage);
    }

    @Install(to = "sectionsTable.edit", subject = "transformation")
    protected TestSection sectionsTableEditTransformation(TestSection testSection) {
        if (PersistenceHelper.isNew(testSection)) return testSection;
        return dataManager.reload(testSection, "testSection.edit");
    }
}