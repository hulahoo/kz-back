package kz.uco.tsadv.global.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$PageInfo")
public class PageInfo extends BaseUuidEntity {
    private static final long serialVersionUID = 5985552384109521275L;

    @MetaProperty
    protected Long pagesCount;

    @MetaProperty
    protected Long totalRowsCount;

    public void setPagesCount(Long pagesCount) {
        this.pagesCount = pagesCount;
    }

    public Long getPagesCount() {
        return pagesCount;
    }

    public void setTotalRowsCount(Long totalRowsCount) {
        this.totalRowsCount = totalRowsCount;
    }

    public Long getTotalRowsCount() {
        return totalRowsCount;
    }

    public static long getPageCount(long totalRowsCount, int renderCount) {
        long pageCount;
        if (totalRowsCount % renderCount == 0) {
            pageCount = totalRowsCount / renderCount;
        } else {
            pageCount = totalRowsCount / renderCount + 1;
        }
        return pageCount;
    }

}