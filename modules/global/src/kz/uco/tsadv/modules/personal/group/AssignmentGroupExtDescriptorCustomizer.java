package kz.uco.tsadv.modules.personal.group;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.OneToOneMapping;

import static kz.uco.tsadv.modules.personal.group.PersonGroupExtDescriptorCustomizer.TIME_MACHINE_MOMENT_PROPERTY_NAME;

/**
 * Модификация маппинга Java-JPA-SQL-DB для извлечения актуального (на выбранный момент времени [в машине времени])
 * назначения на уровне SQL и БД для возможности полноценной сортировки потом, например по полям работника
 * @author Василий Сафронов, Felix Kamalov
 */
public class AssignmentGroupExtDescriptorCustomizer implements DescriptorCustomizer {

    public void customize(ClassDescriptor assignmentGroupExtClassDescriptor) {
        setJoinConditionForRelevantAssignmentProperty(assignmentGroupExtClassDescriptor);
    }

    /**
     * Добавляет для свойства relevantAssignment дополнительную фильтрацию на основе текущей даты в машине времени.
     * Таким образом вместо списка получается всегда одно актуальное назначение
     * @param assignmentGroupExtClassDescriptor
     */
    protected void setJoinConditionForRelevantAssignmentProperty(ClassDescriptor assignmentGroupExtClassDescriptor) {
        OneToOneMapping relevantAssignmentPropertyMapping =
                (OneToOneMapping) assignmentGroupExtClassDescriptor.getMappingForAttributeName("relevantAssignment");
        ExpressionBuilder builder = new ExpressionBuilder();

        DatabaseField entityManagerSessionProperty = new DatabaseField();
        entityManagerSessionProperty.setName(TIME_MACHINE_MOMENT_PROPERTY_NAME);

        relevantAssignmentPropertyMapping.setSelectionCriteria(
                builder.getField("BASE_ASSIGNMENT.GROUP_ID").equal(builder.getParameter("BASE_ASSIGNMENT_GROUP.ID"))
                        .and(builder.getField("BASE_ASSIGNMENT.START_DATE")
                                .lessThanEqual(builder.getProperty(entityManagerSessionProperty)))
                        .and(builder.getField("BASE_ASSIGNMENT.END_DATE")
                                .greaterThanEqual(builder.getProperty(entityManagerSessionProperty)))
        );
    }
}

