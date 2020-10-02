package kz.uco.tsadv.web.generatoremployeenumberdefiner;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import kz.uco.tsadv.entity.generator.GeneratorEmployeeNumberDefiner;
import kz.uco.tsadv.service.EmployeeNumberService;

import javax.inject.Inject;

public class GeneratorEmployeeNumberDefinerEdit extends AbstractEditor<GeneratorEmployeeNumberDefiner> {

    @Inject
    private EmployeeNumberService employeeNumberService;

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (getItem().getPersonType() != null && employeeNumberService.getGeneratorEmployeeNumber(getItem().getPersonType()) != null){
            errors.add(getMessage("typeError"));
        }
    }
}