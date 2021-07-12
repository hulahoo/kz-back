package kz.uco.tsadv.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_IMAGE_SIZE")
@Entity(name = "tsadv_ImageSize")
public class ImageSize extends StandardEntity {
    private static final long serialVersionUID = 1636617249466634036L;

    @NotNull
    @Column(name = "WIDTH", nullable = false)
    private Integer width;

    @NotNull
    @Column(name = "HEIGHT", nullable = false)
    private Integer height;

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
}