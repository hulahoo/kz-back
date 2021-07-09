package kz.uco.tsadv.pojo.pagination;

import java.io.Serializable;

/**
 * @author Alibek Berdaulet
 */
public class EntitiesPaginationResult implements Serializable {

    protected String entities;
    protected Long count;

    public EntitiesPaginationResult() {
    }

    public EntitiesPaginationResult(String entities, Long count) {
        this.entities = entities;
        this.count = count;
    }

    public String getEntities() {
        return entities;
    }

    public void setEntities(String entities) {
        this.entities = entities;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
