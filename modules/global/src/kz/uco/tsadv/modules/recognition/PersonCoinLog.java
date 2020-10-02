package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import kz.uco.tsadv.modules.recognition.enums.LogActionType;
import kz.uco.tsadv.modules.recognition.enums.PointOperationType;
import kz.uco.tsadv.modules.recognition.enums.RecognitionCoinType;
import kz.uco.tsadv.modules.recognition.shop.Goods;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

@Table(name = "TSADV_PERSON_COIN_LOG")
@Entity(name = "tsadv$PersonCoinLog")
public class PersonCoinLog extends StandardEntity {
    private static final long serialVersionUID = -50678852738063049L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "COMMENT_", length = 2000)
    protected String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANOTHER_PERSON_GROUP_ID")
    protected PersonGroupExt anotherPersonGroup;

    @NotNull
    @Column(name = "COIN_TYPE", nullable = false)
    protected String coinType;

    @NotNull
    @Column(name = "ACTION_TYPE", nullable = false)
    protected String actionType;

    @NotNull
    @Column(name = "OPERATION_TYPE", nullable = false)
    protected String operationType;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECOGNITION_ID")
    protected Recognition recognition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GOODS_ID")
    protected Goods goods;


    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    protected Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COIN_DISTRIBUTION_RULE_ID")
    protected CoinDistributionRule coinDistributionRule;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


    public void setAnotherPersonGroup(PersonGroupExt anotherPersonGroup) {
        this.anotherPersonGroup = anotherPersonGroup;
    }

    public PersonGroupExt getAnotherPersonGroup() {
        return anotherPersonGroup;
    }


    public void setCoinType(RecognitionCoinType coinType) {
        this.coinType = coinType == null ? null : coinType.getId();
    }

    public RecognitionCoinType getCoinType() {
        return coinType == null ? null : RecognitionCoinType.fromId(coinType);
    }


    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setCoinDistributionRule(CoinDistributionRule coinDistributionRule) {
        this.coinDistributionRule = coinDistributionRule;
    }

    public CoinDistributionRule getCoinDistributionRule() {
        return coinDistributionRule;
    }


    public void setActionType(LogActionType actionType) {
        this.actionType = actionType == null ? null : actionType.getId();
    }

    public LogActionType getActionType() {
        return actionType == null ? null : LogActionType.fromId(actionType);
    }


    public void setOperationType(PointOperationType operationType) {
        this.operationType = operationType == null ? null : operationType.getId();
    }

    public PointOperationType getOperationType() {
        return operationType == null ? null : PointOperationType.fromId(operationType);
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setRecognition(Recognition recognition) {
        this.recognition = recognition;
    }

    public Recognition getRecognition() {
        return recognition;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Goods getGoods() {
        return goods;
    }


}