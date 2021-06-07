package kz.uco.tsadv.rest.controllers;

import com.google.gson.Gson;
import com.haulmont.addon.restapi.api.controllers.EntitiesController;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.tsadv.service.portal.PortalHelperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.util.*;

/**
 * @author Alibek Berdaulet
 */
public class ExtEntitiesController extends EntitiesController {

    @Inject
    protected Metadata metadata;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected PortalHelperService portalHelperService;

    protected Gson gson = new Gson();

    @Override
    @GetMapping("/{entityName}")
    public ResponseEntity<String> loadEntitiesList(@PathVariable String entityName,
                                                   @RequestParam(required = false) String view,
                                                   @RequestParam(required = false) Integer limit,
                                                   @RequestParam(required = false) Integer offset,
                                                   @RequestParam(required = false) String sort,
                                                   @RequestParam(required = false) Boolean returnNulls,
                                                   @RequestParam(required = false) Boolean returnCount,
                                                   @RequestParam(required = false) Boolean dynamicAttributes,
                                                   @RequestParam(required = false) String modelVersion) {
        MetaClass aClass = metadata.getClass(entityName);
        if (aClass != null && AbstractDictionary.class.isAssignableFrom(aClass.getJavaClass())) {
            UserSession userSession = userSessionSource.getUserSession();
            UUID personGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);

            if (personGroupId != null) {
                List<UUID> companiesForLoadDictionary = portalHelperService.getCompaniesForLoadDictionary(personGroupId);

                Map<String, Object> filter = new HashMap<>();
                Map<String, Object> condition = new HashMap<>();
                condition.put("property", "company.id");
                condition.put("operator", "in");
                condition.put("value", companiesForLoadDictionary);
                filter.put("conditions", Collections.singletonList(condition));

                return this.searchEntitiesListGet(entityName, gson.toJson(filter), view, limit, offset, sort, returnNulls, returnCount, dynamicAttributes, modelVersion);
            }
        }
        return super.loadEntitiesList(entityName, view, limit, offset, sort, returnNulls, returnCount, dynamicAttributes, modelVersion);
    }
}
