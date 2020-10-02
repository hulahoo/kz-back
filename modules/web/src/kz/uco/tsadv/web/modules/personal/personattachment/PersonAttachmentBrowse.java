package kz.uco.tsadv.web.modules.personal.personattachment;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.modules.recruitment.model.PersonAttachment;

public class PersonAttachmentBrowse extends AbstractLookup {

    public Component getPersonAttachmentDownloadBtn(PersonAttachment entity) {
        return Utils.getFileDownload(entity.getAttachment(), PersonAttachmentBrowse.this);
    }
}