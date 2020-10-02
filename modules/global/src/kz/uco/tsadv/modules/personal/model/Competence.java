package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.personal.model.Scale;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;

import javax.persistence.*;
import kz.uco.tsadv.modules.personal.dictionary.DicCompetenceType;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@NamePattern("%s|competenceName")
@Table(name = "TSADV_COMPETENCE")
@Entity(name = "tsadv$Competence")
public class Competence extends AbstractTimeBasedEntity {
    private static final long serialVersionUID = 2235194058271165645L;


    @Transient
    @MetaProperty(related = "competenceNameLang1,competenceNameLang2,competenceNameLang3,competenceNameLang4,competenceNameLang5")
    protected String competenceName;


    @NotNull
    @Column(name = "COMPETENCE_NAME_LANG1", nullable = false, length = 1000)
    protected String competenceNameLang1;

    @Column(name = "COMPETENCE_NAME_LANG2", length = 1000)
    protected String competenceNameLang2;

    @Column(name = "COMPETENCE_NAME_LANG3", length = 1000)
    protected String competenceNameLang3;

    @Column(name = "COMPETENCE_NAME_LANG4", length = 1000)
    protected String competenceNameLang4;

    @Column(name = "COMPETENCE_NAME_LANG5", length = 1000)
    protected String competenceNameLang5;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "open"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPETECE_TYPE_ID")
    protected DicCompetenceType competeceType;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SCALE_ID")
    protected Scale scale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected CompetenceGroup group;


    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    @Column(name = "IS_RC_AVAILABLE")
    protected Boolean isRcAvailable;
    public void setCompetenceNameLang1(String competenceNameLang1) {
        this.competenceNameLang1 = competenceNameLang1;
    }

    public String getCompetenceNameLang1() {
        return competenceNameLang1;
    }

    public void setCompetenceNameLang2(String competenceNameLang2) {
        this.competenceNameLang2 = competenceNameLang2;
    }

    public String getCompetenceNameLang2() {
        return competenceNameLang2;
    }

    public void setCompetenceNameLang3(String competenceNameLang3) {
        this.competenceNameLang3 = competenceNameLang3;
    }

    public String getCompetenceNameLang3() {
        return competenceNameLang3;
    }

    public void setCompetenceNameLang4(String competenceNameLang4) {
        this.competenceNameLang4 = competenceNameLang4;
    }

    public String getCompetenceNameLang4() {
        return competenceNameLang4;
    }

    public void setCompetenceNameLang5(String competenceNameLang5) {
        this.competenceNameLang5 = competenceNameLang5;
    }

    public String getCompetenceNameLang5() {
        return competenceNameLang5;
    }


    public void setCompeteceType(DicCompetenceType competeceType) {
        this.competeceType = competeceType;
    }

    public DicCompetenceType getCompeteceType() {
        return competeceType;
    }


    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }



    public void setIsRcAvailable(Boolean isRcAvailable) {
        this.isRcAvailable = isRcAvailable;
    }

    public Boolean getIsRcAvailable() {
        return isRcAvailable;
    }


    public FileDescriptor getAttachment() {
        return attachment;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }





    public void setGroup(CompetenceGroup group) {
        this.group = group;
    }

    public CompetenceGroup getGroup() {
        return group;
    }


    public void setCompetenceName(String competenceName) {
        this.competenceName = competenceName;
    }

    public String getCompetenceName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0:
                    return competenceNameLang1;
                case 1:
                    return competenceNameLang2;
                case 2:
                    return competenceNameLang3;
                case 3:
                    return competenceNameLang4;
                case 4:
                    return competenceNameLang5;
                default:
                    return competenceNameLang1;
            }
        }
        return competenceNameLang1;
    }
}