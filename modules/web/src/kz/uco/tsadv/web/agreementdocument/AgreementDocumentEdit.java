package kz.uco.tsadv.web.agreementdocument;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.personal.model.Agreement;
import kz.uco.tsadv.modules.personal.model.AgreementDocument;

public class AgreementDocumentEdit extends AbstractEditor<AgreementDocument> {

    /*@WindowParam
    protected Agreement agreement;
*/
    @Override
    protected void initNewItem(AgreementDocument item) {
        super.initNewItem(item);
        /*if (agreement!=null) {
            item.setAgreement(agreement);
        }*/
    }


}