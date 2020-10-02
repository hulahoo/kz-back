package kz.uco.tsadv.importer;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.DicTalentProgramRequestStatus;
import kz.uco.tsadv.entity.TalentProgramPersonStep;
import kz.uco.tsadv.entity.TalentProgramRequest;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramStep;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.importer.utils.XlsHelper;
import kz.uco.tsadv.importer.utils.XlsImporter;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Alibek Berdaulet
 */
@Component(TalentProgramPersonStepImporter.NAME)
@Scope("prototype")
public class TalentProgramPersonStepImporter extends XlsImporter {

    public static final String NAME = "tsadv_TalentProgramPersonStepImporter";

    public static final String TALENT_PROGRAM = "talentProgram";
    public static final String TALENT_STEP = "talentStep";
    public static final String EMPLOYEE_NUMBER = "employeeNumber";
    public static final String PERSON_FULL_NAME = "personFullName";
    public static final String TALENT_RESULT = "result";
    public static final String TALENT_STEP_STATUS = "status";
    public static final String COMMENT = "comment";

    protected CommonService commonService;

    @Nonnull
    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        Map<String, Integer> columns = new HashMap<>();

        int i = 0;
        columns.put(TALENT_PROGRAM, i++);
        columns.put(TALENT_STEP, i++);
        columns.put(EMPLOYEE_NUMBER, i++);
        columns.put(PERSON_FULL_NAME, i++);
        columns.put(TALENT_RESULT, i++);
        columns.put(TALENT_STEP_STATUS, i++);
        columns.put(COMMENT, i);

        return columns;
    }

    @Nullable
    @Override
    protected Map<String, Object> createDefaultValues() {
        return new HashMap<>();
    }

    @Override
    protected boolean eof(Row row) throws ImportFileEofEvaluationException {
        return eofByColumnNullValue(row, EMPLOYEE_NUMBER);
    }

    @Override
    protected List<BaseGenericIdEntity> getEntitiesToPersist(Map<String, Object> values, Map<String, Object> params) throws Exception {

        String programName = XlsHelper.getParameterStringValue(values, TALENT_PROGRAM);
        Assert.notNull(programName, TALENT_PROGRAM);

        String talentStepName = XlsHelper.getParameterStringValue(values, TALENT_STEP);
        Assert.notNull(talentStepName, TALENT_STEP);
        String employeeNumber = XlsHelper.getParameterStringValue(values, EMPLOYEE_NUMBER);
        String fio = XlsHelper.getParameterStringValue(values, PERSON_FULL_NAME);
        Assert.notNull(fio, PERSON_FULL_NAME);

        String statusStep = XlsHelper.getParameterStringValue(values, TALENT_STEP_STATUS);
        Assert.notNull(statusStep, TALENT_STEP_STATUS);
        PersonGroupExt personGroup = parsePersonGroup(fio, employeeNumber);

        Assert.notNull(personGroup, "PersonGroupExt");

        TalentProgramRequest talentProgramRequest = getTalentProgramRequest(programName, personGroup);
        if (talentProgramRequest == null) {
            throw new RuntimeException(String.format("TalentProgramRequest not found; Employee number = %s, Full name = %s", employeeNumber, fio));
        }

        DicTalentProgramRequestStatus status = getStatus(statusStep);
        Assert.notNull(status, String.format("DicTalentProgramRequestStatus not found; langValue = %s", statusStep));

        DicTalentProgramStep dicTalentProgramStep = getDicTalentProgramStep(talentStepName);

        TalentProgramPersonStep item = getTalentProgramPersonStep(dicTalentProgramStep.getId(), talentProgramRequest.getId());
        if (item == null)
            item = metadata.create(TalentProgramPersonStep.class);

        item.setDicTalentProgramStep(dicTalentProgramStep);
        item.setPersonGroup(personGroup);
        item.setTalentProgramRequest(talentProgramRequest);
        item.setStatus(status);
        item.setComment(XlsHelper.getParameterStringValue(values, COMMENT));
        item.setResult(XlsHelper.getParameterStringValue(values, TALENT_RESULT));

        talentProgramRequest.setCurrentStep(item.getDicTalentProgramStep());
        talentProgramRequest.setCurrentStepStatus(item.getStatus());

        return Arrays.asList(item, talentProgramRequest);
    }

    protected TalentProgramPersonStep getTalentProgramPersonStep(UUID dicTalentProgramStepId, UUID talentProgramRequestId) {
        return commonService.getEntity(TalentProgramPersonStep.class,
                "select e from tsadv$TalentProgramPersonStep e " +
                        "   where e.dicTalentProgramStep.id = :dicTalentProgramStep " +
                        "   and e.talentProgramRequest.id = :talentProgramRequest ",
                ParamsMap.of("dicTalentProgramStep", dicTalentProgramStepId,
                        "talentProgramRequest", talentProgramRequestId),
                "talentProgramPersonStep-view");
    }

    protected DicTalentProgramRequestStatus getStatus(String statusStep) {
        return commonService.getEntity(DicTalentProgramRequestStatus.class,
                "select e from tsadv$DicTalentProgramRequestStatus e " +
                        " where lower(e.langValue1) = :statusStep " +
                        "   or lower(e.langValue2) = :statusStep " +
                        "   or lower(e.langValue3) = :statusStep ",
                ParamsMap.of("statusStep", statusStep.toLowerCase()),
                View.LOCAL);
    }

    protected PersonGroupExt parsePersonGroup(String fullName, String employeeNumber) {
        Map<String, Object> params = new HashMap<>();
        String[] names = fullName.toLowerCase().split("\\s");
        params.put("lastName", names[0]);
        params.put("firstName", names[1]);
        if (names.length > 2)
            params.put("middleName", names[2]);
        params.put("sysdate", CommonUtils.getSystemDate());
        if (employeeNumber != null && employeeNumber.trim().length() > 0)
            params.put("employeeNumber", employeeNumber);

        String queryString = "select e " +
                " from base$PersonExt e " +
                " where lower(e.lastName) = :lastName " +
                " and lower(e.firstName) = :firstName " +
                (params.containsKey("middleName") ? " and lower(e.middleName) = :middleName " : " and e.middleName is null ") +
                " and :sysdate between e.startDate and e.endDate " +
                (params.containsKey("employeeNumber") ? " and e.employeeNumber = :employeeNumber " : " and e.employeeNumber is null ");

        PersonExt person = commonService.getEntity(PersonExt.class,
                queryString,
                params,
                "person.full");

        return person == null ? null : person.getGroup();
    }

    protected DicTalentProgramStep getDicTalentProgramStep(String talentStepName) {
        if (talentStepName == null) return null;
        return commonService.getEntity(DicTalentProgramStep.class,
                "select e from tsadv$DicTalentProgramStep e " +
                        " where lower(e.langValue1) = :talentStepName " +
                        "   or lower(e.langValue2) = :talentStepName " +
                        "   or lower(e.langValue3) = :talentStepName ",
                ParamsMap.of("talentStepName", talentStepName.toLowerCase()),
                View.LOCAL);
    }

    protected TalentProgramRequest getTalentProgramRequest(String programName, PersonGroupExt personGroup) {
        return commonService.getEntity(TalentProgramRequest.class,
                "select r from tsadv$TalentProgramRequest r " +
                        "   join r.talentProgram e " +
                        " where ( lower(e.programNameLang1) = :programName " +
                        "   or lower(e.programNameLang2) = :programName " +
                        "   or lower(e.programNameLang3) = :programName ) " +
                        "   and r.personGroup.id = :personGroupId",
                ParamsMap.of("programName", programName.toLowerCase(),
                        "personGroupId", personGroup.getId()),
                "talentProgramRequest-view");
    }
}
