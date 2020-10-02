package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@NamePattern("%s|bankName")
@Table(name = "TSADV_QUESTION_BANK")
@Entity(name = "tsadv$QuestionBank")
public class QuestionBank extends AbstractParentEntity {
    private static final long serialVersionUID = -3942655241637187113L;

    @Column(name = "BANK_NAME", nullable = false)
    protected String bankName;

    @Column(name = "DESCRIPTION", nullable = false, length = 4000)
    protected String description;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "bank")
    protected List<Question> questions;

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }


    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}