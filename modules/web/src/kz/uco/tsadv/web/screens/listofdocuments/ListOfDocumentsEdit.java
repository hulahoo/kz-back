package kz.uco.tsadv.web.screens.listofdocuments;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.ListOfDocuments;

@UiController("tsadv_ListOfDocuments.edit")
@UiDescriptor("list-of-documents-edit.xml")
@EditedEntityContainer("listOfDocumentsDc")
@LoadDataBeforeShow
public class ListOfDocumentsEdit extends StandardEditor<ListOfDocuments> {
}