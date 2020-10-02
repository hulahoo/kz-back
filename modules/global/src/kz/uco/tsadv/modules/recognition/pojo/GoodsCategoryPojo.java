package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.LinkedList;
import java.util.List;

@NamePattern("%s|name")
@MetaClass(name = "tsadv$GoodsCategoryPojo")
public class GoodsCategoryPojo extends BaseUuidEntity {
    private static final long serialVersionUID = 5989181203108060995L;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected String categoryId;

    @MetaProperty
    protected Long goodsCount;

    @MetaProperty
    protected Integer all = 0;

    @MetaProperty
    protected Integer main = 0;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @MetaProperty
    protected List<GoodsCategoryPojo> children = new LinkedList<>();

    public Integer getMain() {
        return main;
    }

    public void setMain(Integer main) {
        this.main = main;
    }

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }

    public List<GoodsCategoryPojo> getChildren() {
        return children;
    }

    public void setChildren(List<GoodsCategoryPojo> children) {
        this.children = children;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Long goodsCount) {
        this.goodsCount = goodsCount;
    }
}