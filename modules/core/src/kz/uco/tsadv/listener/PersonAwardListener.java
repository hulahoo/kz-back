package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.BeanValidation;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kz.uco.tsadv.modules.recognition.PersonAward;
import kz.uco.tsadv.modules.recognition.enums.AwardStatus;
import kz.uco.tsadv.service.RecognitionService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component("tsadv_PersonAwardListener")
public class PersonAwardListener implements BeforeInsertEntityListener<PersonAward>, BeforeUpdateEntityListener<PersonAward> {

    @Inject
    protected BeanValidation beanValidation;

    @Inject
    private RecognitionService recognitionService;

    @Inject
    private Messages messages;

    @Inject
    private MessageTools messageTools;

    @Override
    public void onBeforeInsert(PersonAward personAward, EntityManager entityManager) {
        validatePersonAward(personAward, true);
    }

    @Override
    public void onBeforeUpdate(PersonAward personAward, EntityManager entityManager) {
        validatePersonAward(personAward, false);
    }

    private void validatePersonAward(PersonAward personAward, boolean create) {
        List<String> errors = beanValidate(personAward);

        AwardStatus awardStatus = personAward.getStatus();
        UUID authorPersonGroupId = personAward.getAuthor().getId();
        UUID receiverPersonGroupId = personAward.getReceiver().getId();

        if (errors.isEmpty()) {
            if (authorPersonGroupId.equals(receiverPersonGroupId)) {
                errors.add(getMessage("award.your.self"));
            }

            if (awardStatus.equals(AwardStatus.NOMINATED)) {
                if (recognitionService.hasPersonAward(authorPersonGroupId, receiverPersonGroupId)) {
                    errors.add(getMessage("award.only.one"));
                }
            } else if (awardStatus.equals(AwardStatus.DRAFT)) {
                if (create) {
                    if (recognitionService.hasDraftPersonAward(authorPersonGroupId, receiverPersonGroupId)) {
                        errors.add(getMessage("award.draft.exist"));
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            StringBuilder errorsBuilder = new StringBuilder();
            errors.forEach(s -> errorsBuilder.append(s).append("\n"));

            throw new RuntimeException(errorsBuilder.toString());
        }

        transformWhyHistory(personAward);
    }

    private List<String> beanValidate(PersonAward personAward) {
        List<String> errors = new ArrayList<>();

        Validator validator = beanValidation.getValidator();
        Set<ConstraintViolation<PersonAward>> violations = validator.validate(personAward);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<PersonAward> rcgCv : violations) {
                String propertyPath = rcgCv.getPropertyPath().toString();

                ConstraintDescriptorImpl constraintDescriptor = (ConstraintDescriptorImpl) rcgCv.getConstraintDescriptor();
                Class annotationType = constraintDescriptor.getAnnotationType();
                if (annotationType != null) {
                    if (annotationType.equals(Length.class)) {
                        errors.add(rcgCv.getMessage());
                    } else {
                        errors.add(messageTools.getDefaultRequiredMessage(personAward.getMetaClass(), propertyPath));
                    }
                }
            }
        }
        return errors;
    }

    private void transformWhyHistory(PersonAward personAward) {
        personAward.setWhy(upperFirstLetter(personAward.getWhy()));
        personAward.setHistory(upperFirstLetter(personAward.getHistory()));
    }

    private String upperFirstLetter(String value) {
        if (!StringUtils.isBlank(value)) {
            value = value.trim();
            if (value.length() > 1) {
                String firstLetter = value.substring(0, 1).toUpperCase();
                value = firstLetter + value.substring(1, value.length());
            }
        }
        return value;
    }

    private String getMessage(String key) {
        return messages.getMainMessage(key);
    }
}