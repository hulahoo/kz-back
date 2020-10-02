package kz.uco.tsadv;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.reports.entity.ReportInputParameter;

@MetaClass(name = "tsadv$ReportInputParameterExt")
@Extends(ReportInputParameter.class)
public class ReportInputParameterExt extends ReportInputParameter {
    private static final long serialVersionUID = 7211921027359708382L;

    @MetaProperty
    protected String captionProperty;

    public String getCaptionProperty() {
        return captionProperty;
    }

    public void setCaptionProperty(String captionProperty) {
        this.captionProperty = captionProperty;
    }
}