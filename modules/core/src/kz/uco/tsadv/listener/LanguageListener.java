package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import kz.uco.tsadv.modules.personal.dictionary.DicLanguage;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @author Alibek Berdaulet
 */
@Component("tsadv_LanguageListener")
public class LanguageListener implements AfterDeleteEntityListener<DicLanguage> {

    @Override
    public void onAfterDelete(DicLanguage entity, Connection connection) {
        //empty method overrided in AA
    }
}
