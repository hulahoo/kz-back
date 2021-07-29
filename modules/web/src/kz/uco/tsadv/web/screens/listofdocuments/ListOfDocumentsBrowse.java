package kz.uco.tsadv.web.screens.listofdocuments;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.ListOfDocuments;

@UiController("tsadv_ListOfDocuments.browse")
@UiDescriptor("list-of-documents-browse.xml")
@LookupComponent("listOfDocumentsTable")
@LoadDataBeforeShow
public class ListOfDocumentsBrowse extends StandardLookup<ListOfDocuments> {
}