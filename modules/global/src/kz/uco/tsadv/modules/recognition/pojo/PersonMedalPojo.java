package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@NamePattern("%s|name")
@MetaClass(name = "tsadv$PersonMedalPojo")
public class PersonMedalPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -1050250599191633218L;

    @MetaProperty
    protected String medalName;

    @MetaProperty
    protected String medalId;

    @MetaProperty
    protected String medalImage;

    @MetaProperty
    protected Long count;

    @MetaProperty
    protected Integer sort;

    public String getMedalName() {
        return medalName;
    }

    public void setMedalName(String medalName) {
        this.medalName = medalName;
    }

    public void setMedalId(String medalId) {
        this.medalId = medalId;
    }

    public String getMedalId() {
        return medalId;
    }

    public void setMedalImage(String medalImage) {
        this.medalImage = medalImage;
    }

    public String getMedalImage() {
        String pathParam = medalId;
        if (count == null || count == 0) {
            pathParam = "default-medal";
        }
        medalImage = String.format("./dispatch/rcg_medal_image/%s", pathParam);
        return medalImage;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSort() {
        return sort;
    }
}