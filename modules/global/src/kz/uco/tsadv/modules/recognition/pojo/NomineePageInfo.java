package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.List;
import kz.uco.tsadv.global.entity.PageInfo;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$NomineePageInfo")
public class NomineePageInfo extends BaseUuidEntity {
    private static final long serialVersionUID = 4633501022644629712L;

    @MetaProperty
    protected PageInfo pageInfo;

    @MetaProperty
    protected List<NomineePojo> nominees;

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setNominees(List<NomineePojo> nominees) {
        this.nominees = nominees;
    }

    public List<NomineePojo> getNominees() {
        return nominees;
    }


}