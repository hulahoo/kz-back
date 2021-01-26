package kz.uco.tsadv.web.screens.dicmicattachmentstatus;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;

@UiController("tsadv$DicMICAttachmentStatus.browse")
@UiDescriptor("dic-mic-attachment-status-browse.xml")
@LookupComponent("dicMICAttachmentStatusesTable")
@LoadDataBeforeShow
public class DicMICAttachmentStatusBrowse extends StandardLookup<DicMICAttachmentStatus> {
}