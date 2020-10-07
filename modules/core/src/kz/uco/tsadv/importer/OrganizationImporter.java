package kz.uco.tsadv.importer;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.dictionary.DicOrgType;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.importer.utils.DbHelper;
import kz.uco.tsadv.importer.utils.XlsHelper;
import kz.uco.tsadv.importer.utils.XlsImporter;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

/**
 * @author veronika.buksha
 */

@Component(OrganizationImporter.NAME)
@Scope("prototype")
public class OrganizationImporter extends XlsImporter {

    public static final String NAME = "tsadv_OrganizationImporter";

    public static final String LEGACY_ID = "legacyId";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String ORGANIZATION_NAME_RU = "organizationNameRu";
    public static final String ORGANIZATION_NAME_KZ = "organizationNameKz";
    public static final String ORGANIZATION_NAME_EN = "organizationNameEn";
    public static final String TYPE_CODE = "typeCode";
    public static final String LOCATION_CODE = "locationCode";
    public static final String PAYROLL_CODE = "payrollCode";
    public static final String COST_CENTER_CODE = "costCenterCode";
    public static final String INTERNAL = "internal";

    @Inject
    protected CommonService commonService;

    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        Map<String, Integer> columns = new HashMap<>();

        int i = 0;
        columns.put(LEGACY_ID, i++);
        columns.put(START_DATE, i++);
        columns.put(END_DATE, i++);
        columns.put(ORGANIZATION_NAME_RU, i++);
        columns.put(ORGANIZATION_NAME_KZ, i++);
        columns.put(ORGANIZATION_NAME_EN, i++);
        columns.put(TYPE_CODE, i++);
        columns.put(LOCATION_CODE, i++);
        columns.put(PAYROLL_CODE, i++);
        columns.put(COST_CENTER_CODE, i++);
        columns.put(INTERNAL, i);

        return columns;
    }

    @Override
    protected Map<String, Object> createDefaultValues() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put(START_DATE, CommonUtils.getSystemDate());
        defaults.put(END_DATE, CommonUtils.getEndOfTime());
        return defaults;
    }

    @Override
    protected boolean eof(Row row) throws ImportFileEofEvaluationException {
        return eofByColumnNullValue(row, LEGACY_ID);
    }

    @Override
    protected List<BaseGenericIdEntity> getEntitiesToPersist(Map<String, Object> values, Map<String, Object> params) throws Exception {
        List<BaseGenericIdEntity> result = new ArrayList<>();
        Persistence persistence = getPersistence();

        try (Transaction trx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            List<OrganizationExt> organizations = DbHelper.existedEntities(em, OrganizationExt.class, ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                    "deleteTs", null));

            OrganizationGroupExt organizationGroup = organizations != null && !organizations.isEmpty() ? organizations.get(0).getGroup() : null;
            OrganizationExt organization = DbHelper.existedEntity(em, OrganizationExt.class, ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                    START_DATE, XlsHelper.getParameterValue(values, START_DATE),
                    END_DATE, XlsHelper.getParameterValue(values, END_DATE),
                    "deleteTs", null));

            if (organizationGroup == null) {
                organizationGroup = metadata.create(OrganizationGroupExt.class);
            }
            if (organization == null) {
                organization = metadata.create(OrganizationExt.class);
                organization.setGroup(organizationGroup);
            }

            organization.setOrganizationNameLang1(XlsHelper.getParameterStringValue(values, ORGANIZATION_NAME_RU));
            organization.setOrganizationNameLang2(XlsHelper.getParameterStringValue(values, ORGANIZATION_NAME_KZ));
            organization.setOrganizationNameLang3(XlsHelper.getParameterStringValue(values, ORGANIZATION_NAME_EN));

            organization.setStartDate(XlsHelper.getParameterValue(values, START_DATE));
            organization.setEndDate(XlsHelper.getParameterValue(values, END_DATE));
            organization.setLegacyId(XlsHelper.getParameterStringValue(values, LEGACY_ID));
            organization.setType(commonService.getEntity(DicOrgType.class, XlsHelper.getParameterStringValue(values, TYPE_CODE)));
            organization.setLocation(commonService.getEntity(DicLocation.class, XlsHelper.getParameterStringValue(values, LOCATION_CODE)));
            organization.setPayroll(commonService.getEntity(DicPayroll.class, XlsHelper.getParameterStringValue(values, PAYROLL_CODE)));
            organization.setCostCenter(commonService.getEntity(DicCostCenter.class, XlsHelper.getParameterStringValue(values, COST_CENTER_CODE)));

            String internalValue = XlsHelper.getParameterStringValue(values, INTERNAL);
            organization.setInternal(StringUtils.isNotBlank(internalValue) && internalValue.equalsIgnoreCase("y"));

            result.add(organizationGroup);
            result.add(organization);

            return result;
        }
    }

    @Override
    protected void afterEntitiesPersisted(List<BaseGenericIdEntity> persistedEntities) {
        super.afterEntitiesPersisted(persistedEntities);
        for (BaseGenericIdEntity e : persistedEntities) {
            if (e instanceof OrganizationExt)
                logHelper.info("Created organization: ", "base$OrganizationExt", "base$Organization.edit", (UUID) e.getId());
            else if (e instanceof OrganizationGroupExt)
                logHelper.info("Created organizationGroup: " + e.getId(), null, null, null);
        }
    }
}
