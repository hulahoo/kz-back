package kz.uco.tsadv.entity.generator;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_GENERATOR_EMPLOYEE_NUMBER")
@Entity(name = "tsadv$GeneratorEmployeeNumber")
public class GeneratorEmployeeNumber extends StandardEntity {
    private static final long serialVersionUID = -6324427168086203720L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "PREFIX")
    protected String prefix;

    @Column(name = "SUFFIX")
    protected String suffix;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }


}