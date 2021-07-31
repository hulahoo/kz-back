package kz.uco.tsadv.global.common;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.performance.model.CalibrationMember;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonContact;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toList;

/**
 * @author adilbekov.yernar
 */
public class CommonUtils extends BaseCommonUtils {

    private static StringBuilder returnStr = new StringBuilder();

    public static String toJson(List<CalibrationMember> list, String byType, String lang) {
        Messages messages = AppBeans.get(Messages.class);
        Locale locale = new Locale(lang);

        boolean isCompetence = byType.equalsIgnoreCase("competence");
        int first = 1, second = 2, third = 3;
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("{");
        addHeader(stringBuilder, messages, locale);
        stringBuilder.append("\"lists\":[");

        List<CalibrationMember> filteredList = list.parallelStream().filter(assessment -> isCompetence ?
                assessment.getCompetenceOverall() == first : assessment.getGoalOverall() == first).collect(toList());

        addList(messages.getMainMessage("scrum.bad", locale), "1", "style-1", isCompetence, filteredList, stringBuilder);
        stringBuilder.append(",");

        filteredList = list.parallelStream().filter(
                assessment -> isCompetence ?
                        assessment.getCompetenceOverall() == second : assessment.getGoalOverall() == second).collect(toList());

        addList(messages.getMainMessage("scrum.normal", locale), "2", "style-2", isCompetence, filteredList, stringBuilder);

        filteredList = list.parallelStream().filter(assessment -> isCompetence ?
                assessment.getCompetenceOverall() == third : assessment.getGoalOverall() == third).collect(toList());

        stringBuilder.append(",");
        addList(messages.getMainMessage("scrum.good", locale), "3", "style-3", isCompetence, filteredList, stringBuilder);

        stringBuilder.append("]");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    private static void addList(String name, String listId, String defaultStyle, boolean isCompetence, List<CalibrationMember> items, StringBuilder stringBuilder) {
        stringBuilder.append(String.format("{\"%s\":\"%s\",", "title", name));
        stringBuilder.append(String.format("\"%s\":\"%s\",", "listId", listId));
        stringBuilder.append(String.format("\"%s\":\"%s\",", "defaultStyle", defaultStyle));
        stringBuilder.append("\"items\":[");
        int count = 0;
        for (CalibrationMember item : items) {
            count++;
            addItem(item, stringBuilder, isCompetence);
            if (count != items.size()) stringBuilder.append(",");
        }
        stringBuilder.append("]");
        stringBuilder.append("}");
    }

    private static void addItem(CalibrationMember item, StringBuilder stringBuilder, boolean isCompetence) {
        String webAppUrl = AppContext.getProperty("cuba.webAppUrl");
        PersonGroupExt personGroup = item.getPerson();
        stringBuilder.append(String.format("{\"%s\":\"%s\",", "id", personGroup.getId()));
        /*stringBuilder.append(String.format("\"%s\":\"%s\",", "url", "/tal/image_api?userId=" + personGroup.getPerson().getId()));*/
        stringBuilder.append(String.format("\"%s\":\"%s\",", "url", webAppUrl + "/dispatch/person_image/" + personGroup.getPerson().getId()));
        stringBuilder.append(String.format("\"%s\":\"%s\",", "fio", personGroup.getPerson().getFioWithEmployeeNumber()));
        stringBuilder.append(String.format("\"%s\":\"%s\"}", "assessment", isCompetence ? item.getCompetenceOverall() : item.getGoalOverall()));
    }

    private static void addHeader(StringBuilder stringBuilder, Messages messages, Locale locale) {
        stringBuilder.append("\"columns\":[{\"name\":\"" + messages.getMainMessage("scrum.name", locale) + "\"},{\"name\":\"" + messages.getMainMessage("scrum.assessment", locale) + "\"}],");
        stringBuilder.append("\"userActions\":[{\"title\":\"" + messages.getMainMessage("scrum.profile", locale) + "\",\"name\":\"person-card\"}," +
                "{\"title\":\"" + messages.getMainMessage("scrum.addToSuccessionPlanning", locale) + "\",\"name\":\"action2\"}," +
                "{\"title\":\"" + messages.getMainMessage("scrum.organizationStructure", locale) + "\",\"name\":\"beautyTree\"}],");
    }

    public static String toJson(AssignmentExt model, String lang) {
        returnStr = new StringBuilder();
        Locale locale = new Locale(lang);
        Messages messages = AppBeans.get(Messages.class);
        recursionFn(model.getChildren(), model, null, messages, locale);
        return modifyStr(returnStr.toString());
    }

    private static void recursionFn(List<AssignmentExt> list, AssignmentExt node, AssignmentExt parentNode, Messages messages, Locale locale) {
        mappingNode(node, parentNode, messages, locale);

        if (!list.isEmpty()) {
            returnStr.append(",\"children\":[");

            for (AssignmentExt aChildList : list) {
                recursionFn(aChildList.getChildren(), aChildList, node, messages, locale);
            }

            returnStr.append("]},");
        } else {
            returnStr.append("},");
        }
    }

    private static void mappingNode(AssignmentExt node, AssignmentExt parentNode, Messages messages, Locale locale) {
        String webAppUrl = AppContext.getProperty("cuba.webAppUrl");
        PersonGroupExt personGroup = node.getPersonGroup();

        StringBuilder emailBuilder = new StringBuilder();
        StringBuilder phoneBuilder = new StringBuilder();

        if (personGroup.getPersonContacts() != null) {
            for (PersonContact personContact : personGroup.getPersonContacts()) {
                if (personContact.getType().getCode() != null) {
                    if (personContact.getType().getCode().equalsIgnoreCase("email")) {
                        emailBuilder.append(personContact.getContactValue());
                    } else if (personContact.getType().getCode().equalsIgnoreCase("mobile")) {
                        phoneBuilder.append(personContact.getContactValue()).append(", ");
                    }
                }
            }
        }

        String email = removeLastSymbol(emailBuilder.toString());
        String phones = removeLastSymbol(phoneBuilder.toString());

        returnStr.append("{\"id\":");
        returnStr.append("\"" + personGroup.getPerson().getId() + "\"");
        returnStr.append(",\"image\":");
        /*returnStr.append("\"/tal/image_api?userId=" + personGroup.getPerson().getId() + "\"");*/
        returnStr.append("\"" + webAppUrl + "/dispatch/person_image/" + personGroup.getPerson().getId() + "\"");
        returnStr.append(",\"name\":");
        returnStr.append("\"" + personGroup.getPerson().getFirstName() + "\"");
        returnStr.append(",\"lastName\":");
        returnStr.append("\"" + personGroup.getPerson().getLastName() + "\"");
        returnStr.append(",\"middleName\":");
        returnStr.append("\"" + replaceNull(personGroup.getPerson().getMiddleName()) + "\"");
        returnStr.append(",\"position\":");
        returnStr.append("\"" + node.getPositionGroup().getPosition().getPositionName() + "\"");
        returnStr.append(",\"contact\":");
        returnStr.append("\"" + phones + "\"");
        returnStr.append(",\"email\":");
        returnStr.append("\"" + email + "\"");
        returnStr.append(",\"parentName\":");
        returnStr.append("\"" + (parentNode != null ? parentNode.getPersonGroup().getPerson().getFullName() : "") + "\"");
        returnStr.append(",\"parentId\":");
        returnStr.append("\"" + (parentNode != null ? parentNode.getPersonGroup().getPerson().getId() : "") + "\"");
        returnStr.append(",\"organization\":");
        returnStr.append("\"" + (node.getOrganizationGroup().getOrganization().getOrganizationName()) + "\"");
        returnStr.append(",\"grade\":");
        returnStr.append("\"" + replaceNull(node.getPositionGroup().getPosition().getGradeGroup().getGrade().getGradeName()) + "\"");
        returnStr.append(",\"location\":");
        returnStr.append("\"" + replaceNull(node.getLocation().getLangValue()) + "\"");
        returnStr.append(",\"footer\":");
        returnStr.append("\"" + node.getChildren().size() + " подчиненных\"");
    }

    public static Date getBeginOfTime() {
        Calendar cal = java.util.Calendar.getInstance();
        cal.set(1950, Calendar.JANUARY, 1);
        cal.set(1950, Calendar.JANUARY, 1);
        return cal.getTime();
    }
}
