package kz.uco.tsadv.pojo.pagination;

import java.io.Serializable;

/**
 * @author Alibek Berdaulet
 */
public class PaginationPojo implements Serializable {

    private int limit;

    private int offset;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
