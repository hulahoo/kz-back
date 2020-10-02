package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|title")
@MetaClass(name = "tsadv$RcgFaqPojo")
public class RcgFaqPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -2238915259799253028L;

    @MetaProperty
    protected Integer order;

    @MetaProperty
    protected String title;

    @MetaProperty
    protected String content;

    @MetaProperty
    protected String code;

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}