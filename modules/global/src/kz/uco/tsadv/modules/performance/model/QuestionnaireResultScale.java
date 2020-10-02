package kz.uco.tsadv.modules.performance.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_QUESTIONNAIRE_RESULT_SCALE")
@Entity(name = "tsadv$QuestionnaireResultScale")
public class QuestionnaireResultScale extends AbstractParentEntity {
    private static final long serialVersionUID = -6467064177585514346L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTIONNAIRE_ID")
    protected Questionnaire questionnaire;

    @NotNull
    @Column(name = "RESULT_LANG1", nullable = false)
    protected String resultLang1;

    @Column(name = "RESULT_LANG2")
    protected String resultLang2;

    @Column(name = "RESULT_LANG3")
    protected String resultLang3;

    @Column(name = "RESULT_LANG4")
    protected String resultLang4;

    @Column(name = "RESULT_LANG5")
    protected String resultLang5;

    @NotNull
    @Column(name = "MIN_", nullable = false)
    protected Integer min;

    @NotNull
    @Column(name = "MAX_", nullable = false)
    protected Integer max;

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setResultLang1(String resultLang1) {
        this.resultLang1 = resultLang1;
    }

    public String getResultLang1() {
        return resultLang1;
    }

    public void setResultLang2(String resultLang2) {
        this.resultLang2 = resultLang2;
    }

    public String getResultLang2() {
        return resultLang2;
    }

    public void setResultLang3(String resultLang3) {
        this.resultLang3 = resultLang3;
    }

    public String getResultLang3() {
        return resultLang3;
    }

    public void setResultLang4(String resultLang4) {
        this.resultLang4 = resultLang4;
    }

    public String getResultLang4() {
        return resultLang4;
    }

    public void setResultLang5(String resultLang5) {
        this.resultLang5 = resultLang5;
    }

    public String getResultLang5() {
        return resultLang5;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMin() {
        return min;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMax() {
        return max;
    }


}