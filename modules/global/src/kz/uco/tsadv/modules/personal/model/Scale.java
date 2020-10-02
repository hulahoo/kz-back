package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.personal.model.ScaleLevel;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@NamePattern("%s|scaleName")
@Table(name = "TSADV_SCALE")
@Entity(name = "tsadv$Scale")
public class Scale extends AbstractParentEntity {
    private static final long serialVersionUID = 8436374158179974618L;

    @Column(name = "SCALE_NAME", nullable = false, length = 250)
    protected String scaleName;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "scale")
    protected List<ScaleLevel> scaleLevels;

    public void setScaleLevels(List<ScaleLevel> scaleLevels) {
        this.scaleLevels = scaleLevels;
    }

    public List<ScaleLevel> getScaleLevels() {
        return scaleLevels;
    }


    public void setScaleName(String scaleName) {
        this.scaleName = scaleName;
    }

    public String getScaleName() {
        return scaleName;
    }


}