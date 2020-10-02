package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.List;
import kz.uco.tsadv.global.entity.PageInfo;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$CoinLogPageInfo")
public class CoinLogPageInfo extends BaseUuidEntity {
    private static final long serialVersionUID = -8409914455701757650L;

    @MetaProperty
    protected PageInfo pageInfo;

    @MetaProperty
    protected List<CoinLogPojo> coinLogs;

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setCoinLogs(List<CoinLogPojo> coinLogs) {
        this.coinLogs = coinLogs;
    }

    public List<CoinLogPojo> getCoinLogs() {
        return coinLogs;
    }


}