package kz.uco.tsadv.web.screens.listofdocument;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.ListOfDocument;

@UiController("tsadv_ListOfDocument.browse")
@UiDescriptor("list-of-document-browse.xml")
@LookupComponent("listOfDocumentsTable")
@LoadDataBeforeShow
public class ListOfDocumentBrowse extends StandardLookup<ListOfDocument> {
}