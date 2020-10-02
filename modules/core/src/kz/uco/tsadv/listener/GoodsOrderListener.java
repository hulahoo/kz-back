package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.PersonCoin;
import kz.uco.tsadv.modules.recognition.enums.GoodsOrderStatus;
import kz.uco.tsadv.modules.recognition.exceptions.RecognitionException;
import kz.uco.tsadv.modules.recognition.shop.*;
import kz.uco.tsadv.service.IntegrationService;
import kz.uco.tsadv.service.RecognitionService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;

import java.sql.Connection;

@Component("tsadv_GoodsOrderListener")
public class GoodsOrderListener implements BeforeInsertEntityListener<GoodsOrder>, BeforeUpdateEntityListener<GoodsOrder>, AfterUpdateEntityListener<GoodsOrder>,
        AfterInsertEntityListener<GoodsOrder> {

    protected static final Logger logger = LoggerFactory.getLogger(GoodsOrderListener.class);

    @Inject
    private Metadata metadata;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private RecognitionService recognitionService;
    @Inject
    private Persistence persistence;
    @Inject
    private Messages messages;
    @Inject
    private IntegrationConfig integrationConfig;
    @Inject
    private IntegrationService integrationService;

    @Override
    public void onBeforeInsert(GoodsOrder goodsOrder, EntityManager entityManager) {
        saveHistory(goodsOrder, entityManager);
    }

    @Override
    public void onBeforeUpdate(GoodsOrder goodsOrder, EntityManager entityManager) {
        if (persistence.getTools().isDirty(goodsOrder, "status")) {
            List<GoodsOrderDetail> details = goodsOrder.getDetails();

            GoodsOrderStatus goodsOrderStatus = goodsOrder.getStatus();

            switch (goodsOrderStatus) {
                case WAIT_DELIVERY:
                case REJECTED: {
                    PersonGroupExt goodsOrderPersonGroup = goodsOrder.getPersonGroup();

                    PersonCoin personCoin = recognitionService.loadPersonCoin(goodsOrderPersonGroup.getId());
                    if (personCoin == null) {
                        throw new RecognitionException(getMessage("receiver.wallet.hc.null"));
                    }

                    Long totalSum = 0L;
                    Long revertSum = 0L;

                    List<Goods> removeGoodsIssue = new ArrayList<>();
                    for (GoodsOrderDetail detail : details) {
                        Long total = detail.getQuantity() * detail.getGoods().getPrice().longValue();

                        if (goodsOrderStatus.equals(GoodsOrderStatus.REJECTED) || BooleanUtils.isTrue(detail.getExcluded())) {
                            removeGoodsIssue.add(detail.getGoods());
                        }

                        if (BooleanUtils.isFalse(detail.getExcluded())) {
                            totalSum += total;
                        } else {
                            revertSum += total;
                        }
                    }

                    if (!removeGoodsIssue.isEmpty()) {
                        TypedQuery<GoodsIssue> findGoodsIssueQuery = entityManager.createQuery(
                                "select e from tsadv$GoodsIssue e " +
                                        "where e.order.id = ?1 " +
                                        "and e.goods.id in ?2",
                                GoodsIssue.class);
                        findGoodsIssueQuery.setParameter(1, goodsOrder.getId());
                        findGoodsIssueQuery.setParameter(2, removeGoodsIssue);
                        findGoodsIssueQuery.setView(GoodsIssue.class, View.MINIMAL);

                        List<GoodsIssue> findGoodsIssues = findGoodsIssueQuery.getResultList();

                        if (findGoodsIssues != null && !findGoodsIssues.isEmpty()) {
                            findGoodsIssues.forEach(entityManager::remove);
                        }
                    }

                    if (goodsOrderStatus.equals(GoodsOrderStatus.REJECTED)) {
                        revertSum = totalSum;
                    }


                    int discount = goodsOrder.getDiscount();

                    if (discount != 0) {
                        /*totalSum = Math.round((double) (totalSum * discount) / 100);
                        revertSum = Math.round((double) (revertSum * discount) / 100);*/

                        totalSum = totalSum - Math.round((double) (totalSum * discount) / 100);
                        revertSum = revertSum - Math.round((double) (revertSum * discount) / 100);
                    }

                    goodsOrder.setTotalSum(totalSum);

                    /**
                     * refresh person coins
                     * */
                    personCoin.setCoins(personCoin.getCoins() + revertSum);
                    entityManager.merge(personCoin);
                    break;
                }
            }

            saveHistory(goodsOrder, entityManager);
        }
    }

    private String getMessage(String key) {
        return messages.getMainMessage(key);
    }

    private void saveHistory(GoodsOrder goodsOrder, EntityManager em) {
        GoodsOrderHistory goodsOrderHistory = metadata.create(GoodsOrderHistory.class);
        goodsOrderHistory.setStatus(goodsOrder.getStatus());
        goodsOrderHistory.setGoodsOrder(goodsOrder);
        goodsOrderHistory.setDateTime(new Date());
        goodsOrderHistory.setPersonGroup(userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP));
        em.persist(goodsOrderHistory);
    }

    @Override
    public void onAfterUpdate(GoodsOrder entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createMoveOrderOperation(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }

    @Override
    public void onAfterInsert(GoodsOrder entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createMoveOrderOperation(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }
}