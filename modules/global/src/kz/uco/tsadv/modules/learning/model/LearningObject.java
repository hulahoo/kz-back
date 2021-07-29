package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.enums.ContentType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NamePattern("%s|objectName")
@Table(name = "TSADV_LEARNING_OBJECT")
@Entity(name = "tsadv$LearningObject")
public class LearningObject extends AbstractParentEntity {
    private static final long serialVersionUID = -5941821204696144587L;

    @Column(name = "OBJECT_NAME", nullable = false)
    protected String objectName;

    @Column(name = "DESCRIPTION", length = 4000)
    protected String description;

    @Column(name = "URL", length = 4000)
    protected String url;

    @NotNull
    @Column(name = "CONTENT_TYPE", nullable = false)
    protected String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Lob
    @Column(name = "HTML")
    protected String html;

    @Lob
    @Column(name = "TEXT")
    protected String text;

    @Column(name = "PASSING_SCORE")
    private BigDecimal passingScore;

    public BigDecimal getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(BigDecimal passingScore) {
        this.passingScore = passingScore;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType == null ? null : contentType.getId();
    }

    public ContentType getContentType() {
        return contentType == null ? null : ContentType.fromId(contentType);
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }


    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }


}