package kz.uco.tsadv.global.entity;

/**
 * @author adilbekov.yernar
 */
public enum PageRangeInfo {

    RECOGNITION(10),
    PROFILE(25),
    COMMENT(10),
    LOG(10),
    GOODS(20),
    CART(20),
    ORDERS(20),
    NOMINEE(20),
    FEEDBACK(10);

    private int perPageCount;

    PageRangeInfo(int perPageCount) {
        this.perPageCount = perPageCount;
    }

    public int getPerPageCount() {
        return perPageCount;
    }
}
