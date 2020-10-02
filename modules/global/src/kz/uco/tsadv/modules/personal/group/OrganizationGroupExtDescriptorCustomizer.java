package kz.uco.tsadv.modules.personal.group;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.OneToOneMapping;

import static kz.uco.tsadv.modules.personal.group.PersonGroupExtDescriptorCustomizer.TIME_MACHINE_MOMENT_PROPERTY_NAME;

/**
 * Модификация маппинга Java-JPA-SQL-DB для извлечения актуального (на выбранный момент времени [в машине времени])
 * подразделения на уровне SQL и БД для возможности полноценной сортировки потом, по полям подразделения
 * @author Василий Сафронов, Felix Kamalov
 */
public class OrganizationGroupExtDescriptorCustomizer implements DescriptorCustomizer {

    public void customize(ClassDescriptor organizationGroupExtClassDescriptor) {
        setJoinConditionForRelevantOrganizationProperty(organizationGroupExtClassDescriptor);
    }

    /**
     * Добавляет для свойства relevantOrganization дополнительную фильтрацию на основе текущей даты в машине времени.
     * Таким образом вместо списка получается всегда одно актуальное подразделение
     * @param organizationGroupExtClassDescriptor
     */
    protected void setJoinConditionForRelevantOrganizationProperty(ClassDescriptor organizationGroupExtClassDescriptor) {
        OneToOneMapping relevantOrganizationPropertyMapping =
                (OneToOneMapping) organizationGroupExtClassDescriptor.getMappingForAttributeName("relevantOrganization");
        ExpressionBuilder builder = new ExpressionBuilder();

        DatabaseField entityManagerSessionProperty = new DatabaseField();
        entityManagerSessionProperty.setName(TIME_MACHINE_MOMENT_PROPERTY_NAME);

        relevantOrganizationPropertyMapping.setSelectionCriteria(
                builder.getField("BASE_ORGANIZATION.GROUP_ID").equal(builder.getParameter("BASE_ORGANIZATION_GROUP.ID"))
                        .and(builder.getField("BASE_ORGANIZATION.START_DATE")
                                .lessThanEqual(builder.getProperty(entityManagerSessionProperty)))
                        .and(builder.getField("BASE_ORGANIZATION.END_DATE")
                                .greaterThanEqual(builder.getProperty(entityManagerSessionProperty)))
        );
    }
}

