package kz.uco.tsadv.web.modules.recognition.entity.recognition;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.Recognition;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

public class RecognitionEdit extends AbstractEditor<Recognition> {

    @Inject
    private UserSession userSession;

    private PersonGroupExt currentPersonGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        currentPersonGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
    }

    @Override
    protected void initNewItem(Recognition item) {
        super.initNewItem(item);

        item.setAuthor(currentPersonGroup);
        item.setRecognitionDate(new Date());
        item.setNotifyManager(false);
    }
}