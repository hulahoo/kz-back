package kz.uco.tsadv.bproc.beans;

import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Alibek Berdaulet
 */
@Component(BprocUserListProviderWithoutRedirect.NAME)
public class BprocUserListProviderWithoutRedirect extends BprocUserListProvider {

    public static final String NAME = "tsadv_BprocUserListProviderWithoutRedirect";

    @Override
    public List<User> get(String executionId) {
        return getActors(executionId);
    }
}
