package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.learning.model.PartyExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static kz.uco.base.common.Null.nullReplace;

@NamePattern("%s|trainerFullName")
@Table(name = "TSADV_TRAINER")
@Entity(name = "tsadv$Trainer")
public class Trainer extends AbstractParentEntity {
    private static final long serialVersionUID = -6155074646105418146L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected PersonGroupExt employee;

    @Column(name = "ADD_PAYMENT_AMOUNT")
    protected Integer addPaymentAmount;

    @Column(name = "ORDER_NUMBER")
    protected String orderNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTY_ID")
    protected PartyExt party;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "trainer")
    protected List<CourseTrainer> courseTrainer;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "trainer")
    protected List<CourseTrainerAssessment> courseTrainerAssessment;

    @Transient
    @MetaProperty
    protected String trainerFullName;

    @Lob
    @Column(name = "INFORMATION_TRAINER")
    protected String informationTrainer;

    @Lob
    @Column(name = "TRAINER_GREETING")
    protected String trainerGreeting;

    @Lob
    @Column(name = "INFORMATION_TRAINER_LANG2")
    protected String informationTrainerLang2;

    @Lob
    @Column(name = "INFORMATION_TRAINER_LANG3")
    protected String informationTrainerLang3;

    @Lob
    @Column(name = "TRAINER_GREETING_LANG2")
    protected String trainerGreetingLang2;

    @Lob
    @Column(name = "TRAINER_GREETING_LANG3")
    protected String trainerGreetingLang3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private DicCompany company;

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public String getTrainerGreetingLang3() {
        return trainerGreetingLang3;
    }

    public void setTrainerGreetingLang3(String trainerGreetingLang3) {
        this.trainerGreetingLang3 = trainerGreetingLang3;
    }

    public String getTrainerGreetingLang2() {
        return trainerGreetingLang2;
    }

    public void setTrainerGreetingLang2(String trainerGreetingLang2) {
        this.trainerGreetingLang2 = trainerGreetingLang2;
    }

    public String getInformationTrainerLang3() {
        return informationTrainerLang3;
    }

    public void setInformationTrainerLang3(String informationTrainerLang3) {
        this.informationTrainerLang3 = informationTrainerLang3;
    }

    public String getInformationTrainerLang2() {
        return informationTrainerLang2;
    }

    public void setInformationTrainerLang2(String informationTrainerLang2) {
        this.informationTrainerLang2 = informationTrainerLang2;
    }

    public String getTrainerGreeting() {
        return trainerGreeting;
    }

    public void setTrainerGreeting(String trainerGreeting) {
        this.trainerGreeting = trainerGreeting;
    }

    public String getInformationTrainer() {
        return informationTrainer;
    }

    public void setInformationTrainer(String informationTrainer) {
        this.informationTrainer = informationTrainer;
    }

    public String getTrainerFullName() {
        return employee.getPerson() != null ? employee.getPerson().getFullName() : "";
    }


    public void setCourseTrainer(List<CourseTrainer> courseTrainer) {
        this.courseTrainer = courseTrainer;
    }

    public List<CourseTrainer> getCourseTrainer() {
        return courseTrainer;
    }

    public void setCourseTrainerAssessment(List<CourseTrainerAssessment> courseTrainerAssessment) {
        this.courseTrainerAssessment = courseTrainerAssessment;
    }

    public List<CourseTrainerAssessment> getCourseTrainerAssessment() {
        return courseTrainerAssessment;
    }


    public void setEmployee(PersonGroupExt employee) {
        this.employee = employee;
    }

    public PersonGroupExt getEmployee() {
        return employee;
    }

    public void setAddPaymentAmount(Integer addPaymentAmount) {
        this.addPaymentAmount = addPaymentAmount;
    }

    public Integer getAddPaymentAmount() {
        return addPaymentAmount;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setParty(PartyExt party) {
        this.party = party;
    }

    public PartyExt getParty() {
        return party;
    }

    @MetaProperty(related = {"informationTrainer", "informationTrainerLang2", "informationTrainerLang3"})
    public String getInformationTrainerLang() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (!isBaseInformationTrainerLangsFullLoaded()) {
            return null;
        }

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return informationTrainer;
                }
                case 1: {
                    return nullReplace(informationTrainerLang2, informationTrainer);
                }
                case 2: {
                    return nullReplace(informationTrainerLang3, informationTrainer);
                }
                default:
                    return informationTrainer;
            }
        }

        return informationTrainer;
    }

    @MetaProperty(related = {"trainerGreeting", "trainerGreetingLang2", "trainerGreetingLang3"})
    public String getTrainerGreetingLang() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (!isBaseTrainerGreetingLangsFullLoaded()) {
            return null;
        }

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return trainerGreeting;
                }
                case 1: {
                    return nullReplace(trainerGreetingLang2, trainerGreeting);
                }
                case 2: {
                    return nullReplace(trainerGreetingLang3, trainerGreeting);
                }
                default:
                    return trainerGreeting;
            }
        }

        return trainerGreeting;
    }

    private boolean isBaseInformationTrainerLangsFullLoaded() {
        return PersistenceHelper.isLoaded(this, "informationTrainer")
                && PersistenceHelper.isLoaded(this, "informationTrainerLang2")
                && PersistenceHelper.isLoaded(this, "informationTrainerLang3");
    }

    private boolean isBaseTrainerGreetingLangsFullLoaded() {
        return PersistenceHelper.isLoaded(this, "trainerGreeting")
                && PersistenceHelper.isLoaded(this, "trainerGreetingLang2")
                && PersistenceHelper.isLoaded(this, "trainerGreetingLang3");
    }
}