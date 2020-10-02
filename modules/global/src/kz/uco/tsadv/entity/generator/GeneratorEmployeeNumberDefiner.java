package kz.uco.tsadv.entity.generator;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_GENERATOR_EMPLOYEE_NUMBER_DEFINER")
@Entity(name = "tsadv$GeneratorEmployeeNumberDefiner")
public class GeneratorEmployeeNumberDefiner extends StandardEntity {
    private static final long serialVersionUID = 8725333714514920679L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GENERATOR_EMPLOYEE_NUMBER_ID")
    protected GeneratorEmployeeNumber generatorEmployeeNumber;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_TYPE_ID")
    protected DicPersonType personType;

    public void setGeneratorEmployeeNumber(GeneratorEmployeeNumber generatorEmployeeNumber) {
        this.generatorEmployeeNumber = generatorEmployeeNumber;
    }

    public GeneratorEmployeeNumber getGeneratorEmployeeNumber() {
        return generatorEmployeeNumber;
    }

    public void setPersonType(DicPersonType personType) {
        this.personType = personType;
    }

    public DicPersonType getPersonType() {
        return personType;
    }


}