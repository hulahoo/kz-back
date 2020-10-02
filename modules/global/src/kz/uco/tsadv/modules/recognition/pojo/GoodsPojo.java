package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.UUID;

@MetaClass(name = "tsadv$GoodsPojo")
public class GoodsPojo extends BaseUuidEntity {
    private static final long serialVersionUID = 2420938309243798255L;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected String description;

    @MetaProperty
    protected String image;

    @MetaProperty
    protected String category;

    @MetaProperty
    protected Double price;

    @MetaProperty
    protected Integer inCart = 0;

    @MetaProperty
    protected Integer inWishList = 0;

    @MetaProperty
    protected Long quantity = 0L;

    @MetaProperty
    protected UUID cartId;

    public Integer getInWishList() {
        return inWishList;
    }

    public void setInWishList(Integer inWishList) {
        this.inWishList = inWishList;
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Integer getInCart() {
        return inCart;
    }

    public void setInCart(Integer inCart) {
        this.inCart = inCart;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        image = String.format("./dispatch/goods_image/%s", id.toString());
        return image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }


}