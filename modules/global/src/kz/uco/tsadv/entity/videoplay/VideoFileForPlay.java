package kz.uco.tsadv.entity.videoplay;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Column;
import kz.uco.tsadv.entity.videoplay.enums.VideoFileConvertStatus;

@NamePattern("%s|source")
@Table(name = "TSADV_VIDEO_FILE_FOR_PLAY")
@Entity(name = "tsadv$VideoFileForPlay")
public class VideoFileForPlay extends AbstractParentEntity {
    private static final long serialVersionUID = 8083940828351364495L;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SOURCE_ID")
    protected FileDescriptor source;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OUTPUT_FILE_ID")
    protected FileDescriptor outputFile;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    public void setStatus(VideoFileConvertStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public VideoFileConvertStatus getStatus() {
        return status == null ? null : VideoFileConvertStatus.fromId(status);
    }


    public void setSource(FileDescriptor source) {
        this.source = source;
    }

    public FileDescriptor getSource() {
        return source;
    }

    public void setOutputFile(FileDescriptor outputFile) {
        this.outputFile = outputFile;
    }

    public FileDescriptor getOutputFile() {
        return outputFile;
    }


}