package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.global.entity.PageInfo;

import java.util.List;

@MetaClass(name = "tsadv$ProfilePageInfo")
public class ProfilePageInfo extends BaseUuidEntity {
    private static final long serialVersionUID = -436704913422933417L;

    @MetaProperty
    protected PageInfo pageInfo;

    @MetaProperty
    protected List<ProfilePojo> profiles;

    public List<ProfilePojo> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfilePojo> profiles) {
        this.profiles = profiles;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}