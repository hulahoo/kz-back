package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.recruitment.dictionary.DicRcQuestionnaireStatus;
import kz.uco.tsadv.modules.recruitment.model.RcQuestionnaireQuestion;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.recruitment.dictionary.DicRcQuestionnaireCategory;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import com.haulmont.chile.core.annotations.MetaProperty;

@NamePattern("%s|name")
@Table(name = "TSADV_RC_QUESTIONNAIRE")
@Entity(name = "tsadv$RcQuestionnaire")
public class RcQuestionnaire extends AbstractParentEntity {
    private static final long serialVersionUID = 1026388394817171704L;

    @Column(name = "NAME", nullable = false)
    protected String name;

    @Transient
    @MetaProperty(related = "name,name2,name3,name4,name5")
    protected String langName;

    @Column(name = "NAME2")
    protected String name2;

    @Column(name = "NAME3")
    protected String name3;

    @Column(name = "NAME4")
    protected String name4;

    @Column(name = "NAME5")
    protected String name5;

    @Column(name = "PASSING_SCORE")
    protected Double passingScore;

    @Lob
    @Column(name = "INSTRUCTION", nullable = false)
    protected String instruction;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected DicRcQuestionnaireStatus status;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    protected DicRcQuestionnaireCategory category;

    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "questionnaire")
    protected List<RcQuestionnaireQuestion> questions;

    public void setLangName(String langName) {
        this.langName = langName;
    }

    public String getLangName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return name;
                }
                case 1: {
                    return name2;
                }
                case 2: {
                    return name3;
                }
                case 3: {
                    return name4;
                }
                case 4: {
                    return name5;
                }
                default:
                    return name;
            }
        }

        return name;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName2() {
        return name2;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getName3() {
        return name3;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }

    public String getName4() {
        return name4;
    }

    public void setName5(String name5) {
        this.name5 = name5;
    }

    public String getName5() {
        return name5;
    }


    public void setPassingScore(Double passingScore) {
        this.passingScore = passingScore;
    }

    public Double getPassingScore() {
        return passingScore;
    }


    public void setQuestions(List<RcQuestionnaireQuestion> questions) {
        this.questions = questions;
    }

    public List<RcQuestionnaireQuestion> getQuestions() {
        return questions;
    }


    public void setStatus(DicRcQuestionnaireStatus status) {
        this.status = status;
    }

    public DicRcQuestionnaireStatus getStatus() {
        return status;
    }

    public void setCategory(DicRcQuestionnaireCategory category) {
        this.category = category;
    }

    public DicRcQuestionnaireCategory getCategory() {
        return category;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getInstruction() {
        return instruction;
    }


}