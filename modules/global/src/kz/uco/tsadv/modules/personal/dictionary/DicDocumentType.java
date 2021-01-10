package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_DOCUMENT_TYPE")
@Entity(name = "tsadv$DicDocumentType")
public class DicDocumentType extends AbstractDictionary {
    private static final long serialVersionUID = -6405004865430479661L;

    @NotNull
    @Column(name = "FOREIGNER", nullable = false, columnDefinition = "Boolean default false")
    private Boolean foreigner = false;

    public Boolean getForeigner() {
        return foreigner;
    }

    public void setForeigner(Boolean foreigner) {
        this.foreigner = foreigner;
    }
}