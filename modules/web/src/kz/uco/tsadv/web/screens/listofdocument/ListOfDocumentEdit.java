package kz.uco.tsadv.web.screens.listofdocument;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.ListOfDocument;

@UiController("tsadv_ListOfDocument.edit")
@UiDescriptor("list-of-document-edit.xml")
@EditedEntityContainer("listOfDocumentDc")
@LoadDataBeforeShow
public class ListOfDocumentEdit extends StandardEditor<ListOfDocument> {
}