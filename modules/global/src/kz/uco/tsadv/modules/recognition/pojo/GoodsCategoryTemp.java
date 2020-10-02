package kz.uco.tsadv.modules.recognition.pojo;

import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
public class GoodsCategoryTemp {

    private UUID id;
    private UUID parentId;
    private String name;
    private Long goodsCount = 0L;
    private boolean visited = false;

    public GoodsCategoryTemp(UUID id, UUID parentId, String name,Long goodsCount) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.goodsCount = goodsCount;
    }

    public Long getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Long goodsCount) {
        this.goodsCount = goodsCount;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
