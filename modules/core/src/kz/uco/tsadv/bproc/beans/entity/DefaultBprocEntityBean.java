package kz.uco.tsadv.bproc.beans.entity;

import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import org.springframework.stereotype.Component;

/**
 * @author Alibek Berdaulet
 */
@Component(DefaultBprocEntityBean.NAME)
public class DefaultBprocEntityBean extends AbstractBprocEntityBean<AbstractBprocRequest> {

    public static final String NAME = "tsadv_DefaultBprocEntityBean";

    @Override
    public boolean instanceOf(Class<? extends AbstractBprocRequest> tClass) {
        return false;
    }
}
