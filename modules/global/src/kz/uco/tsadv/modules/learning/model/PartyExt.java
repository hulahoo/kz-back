package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Extends;
import kz.uco.base.entity.shared.Party;

import java.util.List;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

@Extends(Party.class)
@Entity(name = "base$PartyExt")
public class PartyExt extends Party {
    private static final long serialVersionUID = 4717987707992607755L;


    @OneToMany(mappedBy = "party")
    protected List<Course> course;

    @NotNull
    @Column(name = "TRAINING_PROVIDER", nullable = false)
    protected Boolean trainingProvider = false;

    @Column(name = "BIN")
    protected String bin;

    @Column(name = "RNN")
    protected String rnn;

    @Column(name = "SIGNER")
    protected String signer;

    @Column(name = "JOB")
    protected String job;

    @Column(name = "REASON")
    protected String reason;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "partyExt")
    protected List<PartyContactPerson> partyContactPerson;

    public void setPartyContactPerson(List<PartyContactPerson> partyContactPerson) {
        this.partyContactPerson = partyContactPerson;
    }

    public List<PartyContactPerson> getPartyContactPerson() {
        return partyContactPerson;
    }


    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getSigner() {
        return signer;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


    public void setTrainingProvider(Boolean trainingProvider) {
        this.trainingProvider = trainingProvider;
    }

    public Boolean getTrainingProvider() {
        return trainingProvider;
    }


    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getBin() {
        return bin;
    }

    public void setRnn(String rnn) {
        this.rnn = rnn;
    }

    public String getRnn() {
        return rnn;
    }


    public List<Course> getCourse() {
        return course;
    }

    public void setCourse(List<Course> course) {
        this.course = course;
    }
}