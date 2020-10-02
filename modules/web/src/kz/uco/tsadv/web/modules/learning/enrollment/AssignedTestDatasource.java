package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.data.impl.CustomGroupDatasource;
import kz.uco.tsadv.modules.learning.model.AssignedTestPojo;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.service.CourseService;

import java.util.*;

/**
 * @author adilbekov.yernar
 */

public class AssignedTestDatasource extends CustomGroupDatasource<AssignedTestPojo, UUID> {
    protected Messages messages = AppBeans.get(Messages.class);
    protected List<AssignedTestPojo> allPojo = new ArrayList<>();
    private  int paramsForOrderBy = 4;
    @Override
    protected Collection<AssignedTestPojo> getEntities(Map<String, Object> params) {
        Map<String, Object> secParam = new HashMap<>();
        if (userSession.getUser() != null&&userSession.getUser().getGroup() != null&&
                userSession.getUser().getGroup().getId()!=null) {
            secParam.put("userGroupId",userSession.getUser().getGroup().getId());
        }
        CourseService courseService = AppBeans.get(CourseService.class);
        if (sortInfos != null) {
            calculateValueForParamsOrderBy(sortInfos[0].getPropertyPath().toString());
        }
        List<AssignedTestPojo> assignedTestPojos = new ArrayList<>();
        if (params.containsKey("filter")) {
            List<Map<String, String>> list = (List<Map<String, String>>) params.get("filter");
            if (params.containsKey("searchButton")){
                firstResult=0;
            }
            if (!list.isEmpty()) {
                assignedTestPojos = courseService.loadAssignedTest(firstResult, maxResults, false, paramsForOrderBy, list,userSession.getUser().getLanguage(),secParam);
                allPojo=courseService.loadAssignedTest(firstResult, maxResults, true, paramsForOrderBy, list,userSession.getUser().getLanguage(),secParam);
                return assignedTestPojos;
            } else {
                assignedTestPojos = courseService.loadAssignedTest(firstResult, maxResults, false, paramsForOrderBy,secParam);
            }
        } else {
            assignedTestPojos = courseService.loadAssignedTest(firstResult, maxResults, false, paramsForOrderBy,secParam);
        }
        allPojo = courseService.loadAssignedTest(firstResult, maxResults, true,secParam);
        return assignedTestPojos;
    }

    protected int calculateValueForParamsOrderBy(String value){
        switch (value){
            case "organization": {
                paramsForOrderBy = 4;
                break;
            }
            case "personFullName":{
                paramsForOrderBy = 3;
                break;
            }
            case "position":{
                paramsForOrderBy = 5;
                break;
            }
            case "testName":{
                paramsForOrderBy = 7;
                break;
            }
            case "courseSectionName":{
                paramsForOrderBy = 14;
                break;
            }
            case "score":{
                paramsForOrderBy = 11;
                break;
            }
            case "enrollmentStatus":{
                paramsForOrderBy = 8;
                break;
            }
            case "success":{
                paramsForOrderBy = 9;
                break;
            }
        }
        return  paramsForOrderBy;
    }


    protected boolean containsOrNot(String operation, String findValue, String value){
        if (operation.equals(messages.getMainMessage("Op.CONTAINS"))&&
                findValue!=null && !findValue.equals("")&&
                !findValue.contains(value.subSequence(0,value.length()))){
            return true;
        }else if (operation.equals(messages.getMainMessage("Op.DOES_NOT_CONTAIN"))&&
                findValue!=null && !findValue.equals("")&&
                findValue.contains(value.subSequence(0,value.length()))){
            return true;
        }
        return false;
    }

    protected boolean containsOrNotInList(String operation, UUID findValue, List<OrganizationGroupExt> organizationGroupExts) {
        if (operation.equals(messages.getMainMessage("Op.CONTAINS"))) {
            for (OrganizationGroupExt organizationGroupExt : organizationGroupExts) {
                if (organizationGroupExt.getId().equals(findValue)) {
                    return false;
                }
            }
            return true;
        } else if (operation.equals(messages.getMainMessage("Op.DOES_NOT_CONTAIN"))) {
            for (OrganizationGroupExt organizationGroupExt : organizationGroupExts) {
                if (organizationGroupExt.getId().equals(findValue)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public int getCount() {
        return allPojo.size();
    }
}
