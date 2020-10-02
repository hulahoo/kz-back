package kz.uco.tsadv.modules.recognition.feedback;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.global.entity.PageInfo;

import java.util.List;

@MetaClass(name = "tsadv$RcgFeedbackPageInfo")
public class RcgFeedbackPageInfo extends BaseUuidEntity {
    private static final long serialVersionUID = 7517601038805979026L;

    @MetaProperty
    protected PageInfo pageInfo;

    @MetaProperty
    protected List<RcgFeedbackPojo> feedback;

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<RcgFeedbackPojo> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<RcgFeedbackPojo> feedback) {
        this.feedback = feedback;
    }
}