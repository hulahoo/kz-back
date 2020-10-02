package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_RECOGNITION_PROFILE_SETTING")
@Entity(name = "tsadv$RecognitionProfileSetting")
public class RecognitionProfileSetting extends StandardEntity {
    private static final long serialVersionUID = -2804902612698262937L;

    @NotNull
    @Column(name = "AUTOMATIC_TRANSLATE", nullable = false)
    protected Boolean automaticTranslate = false;

    @NotNull
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public void setAutomaticTranslate(Boolean automaticTranslate) {
        this.automaticTranslate = automaticTranslate;
    }

    public Boolean getAutomaticTranslate() {
        return automaticTranslate;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


}