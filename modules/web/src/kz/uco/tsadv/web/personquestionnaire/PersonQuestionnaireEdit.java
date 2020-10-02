package kz.uco.tsadv.web.personquestionnaire;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.SuggestionPickerField;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonQuestionnaire;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;

public class PersonQuestionnaireEdit<T extends PersonQuestionnaire> extends AbstractEditor<T> {

    @Named("fieldGroup.appraiser")
    protected SuggestionPickerField appraiserField;

    @Named("fieldGroup.appraise")
    protected SuggestionPickerField appraiseField;

    @Named("fieldGroup.questionnaire")
    protected LookupPickerField questionnaireField;

    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        appraiseField.setSearchExecutor((searchString, searchParams) ->
                commonService.getEntities(PersonGroupExt.class,
                        "select e from base$PersonGroupExt e " +
                                " join e.list p " +
                                " where :systemDate between p.startDate and p.endDate " +
                                " and LOWER(trim(concat(p.lastName, " +
                                " concat(' ', " +
                                " concat(p.firstName , " +
                                " concat(' ',coalesce(p.middleName,''))))))) like LOWER(:searchString) ",
                        ParamsMap.of("searchString", "%" + searchString + "%",
                                "systemDate", CommonUtils.getSystemDate()),
                        "personGroupExtFullName.view"));
        if (params.containsKey("forManager")) {
            appraiserField.setVisible(false);
        } else {
            appraiserField.setSearchExecutor((searchString, searchParams) ->
                    commonService.getEntities(PersonGroupExt.class,
                            "select e from base$PersonGroupExt e " +
                                    " join e.list p " +
                                    " where :systemDate between p.startDate and p.endDate " +
                                    " and LOWER(trim(concat(p.lastName, " +
                                    " concat(' ', " +
                                    " concat(p.firstName , " +
                                    " concat(' ',coalesce(p.middleName,''))))))) like LOWER(:searchString) ",
                            ParamsMap.of("searchString", "%" + searchString + "%",
                                    "systemDate", CommonUtils.getSystemDate()),
                            "personGroupExtFullName.view"));
        }
        if (params.containsKey("questionnaire")) {
            questionnaireField.setVisible(false);
        }
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setAppraisalDate(new Date());
    }
}