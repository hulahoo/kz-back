package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.engine.eventlistener.UserTaskCompletedEventListener;
import org.flowable.common.engine.impl.cfg.TransactionState;

/**
 * @author Alibek Berdaulet
 */
public class ExtUserTaskCompletedEventListener extends UserTaskCompletedEventListener {

    @Override
    public boolean isFailOnException() {
        return true;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return true;
    }

    @Override
    public String getOnTransaction() {
        return TransactionState.COMMITTING.name();
    }
}
