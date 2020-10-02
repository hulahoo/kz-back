package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$CoinLogPojo")
public class CoinLogPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -8434051629635236281L;

    @MetaProperty
    protected String actionType;

    @MetaProperty
    protected String coinType;

    @MetaProperty
    protected String coins;

    @MetaProperty
    protected String target;

    @MetaProperty
    protected String targetId;

    @MetaProperty
    protected String operationType;

    @MetaProperty
    protected String date;

    @MetaProperty
    protected String comment;

    public String getComment() {
        return comment;
    }


    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}