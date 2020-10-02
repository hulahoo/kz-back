package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.global.entity.PageInfo;

import java.util.List;

@MetaClass(name = "tsadv$RecognitionPageInfo")
public class RecognitionPageInfo extends BaseUuidEntity {
    private static final long serialVersionUID = -149690253963683744L;

    @MetaProperty
    protected PageInfo pageInfo;

    @MetaProperty
    protected List<RecognitionPojo> recognitions;

    public List<RecognitionPojo> getRecognitions() {
        return recognitions;
    }

    public void setRecognitions(List<RecognitionPojo> recognitions) {
        this.recognitions = recognitions;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}