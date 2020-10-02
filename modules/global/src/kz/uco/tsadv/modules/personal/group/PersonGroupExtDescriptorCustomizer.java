package kz.uco.tsadv.modules.personal.group;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * Модификация маппинга Java-JPA-SQL-DB для извлечения актуального (на выбранный момент времени [в машине времени])
 * лица на уровне SQL и БД для возможности полноценной сортировки потом по физическим полям работника в целом,
 * и по ФИО в частности
 * @author Василий Сафронов, Felix Kamalov
 */
public class PersonGroupExtDescriptorCustomizer implements DescriptorCustomizer {

    /**
     * Имя для свойства "Дата выбранная пользователем в машине времени"
     */
    public static final String TIME_MACHINE_MOMENT_PROPERTY_NAME = "systemDate";

    public void customize(ClassDescriptor personGroupExtClassDescriptor) {
        setJoinConditionForRelevantPersonProperty(personGroupExtClassDescriptor);
    }

    /**
     * Добавляет для свойства relevantPerson дополнительную фильтрацию на основе текущей даты в машине времени.
     * Таким образом вместо списка получается всегда одно актуальное лицо
     * @param personGroupExtClassDescriptor
     */
    protected void setJoinConditionForRelevantPersonProperty(ClassDescriptor personGroupExtClassDescriptor) {
        OneToOneMapping relevantPersonPropertyMapping =
                (OneToOneMapping) personGroupExtClassDescriptor.getMappingForAttributeName("relevantPerson");
        ExpressionBuilder builder = new ExpressionBuilder();

        DatabaseField entityManagerSessionProperty = new DatabaseField();
        entityManagerSessionProperty.setName(TIME_MACHINE_MOMENT_PROPERTY_NAME);

        relevantPersonPropertyMapping.setSelectionCriteria(
                builder.getField("BASE_PERSON.GROUP_ID").equal(builder.getParameter("BASE_PERSON_GROUP.ID"))
                        .and(builder.getField("BASE_PERSON.START_DATE")
                                .lessThanEqual(builder.getProperty(entityManagerSessionProperty)))
                        .and(builder.getField("BASE_PERSON.END_DATE")
                                .greaterThanEqual(builder.getProperty(entityManagerSessionProperty)))
        );
    }
}
