package kz.uco.tsadv.web.screens.vacationgraphic;

import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.VacationGraphic;
import kz.uco.tsadv.service.EmployeeNumberService;
import org.apache.poi.hpsf.Date;

import javax.inject.Inject;

@UiController("tsadv_VacationGraphic.edit")
@UiDescriptor("vacation-graphic-edit.xml")
@EditedEntityContainer("vacationGraphicDc")
@LoadDataBeforeShow
public class VacationGraphicEdit extends StandardEditor<VacationGraphic> {

    @Inject
    private InstanceContainer<VacationGraphic> vacationGraphicDc;
    @Inject
    private EmployeeNumberService employeeNumberService;
    @Inject
    private Notifications notifications;

    @Subscribe("commitAndCloseBtn")
    public void onCommitAndCloseBtnClick(Button.ClickEvent event) {
      /*  String name =   vacationGraphicDc.getItem().getValue("name");
        String surname  =   vacationGraphicDc.getItem().getValue("surname");
        String middlename =   vacationGraphicDc.getItem().getValue("middlename");
        String division =   vacationGraphicDc.getItem().getValue("division");
        String duty = vacationGraphicDc.getItem().getValue("duty");
        String comments =   vacationGraphicDc.getItem().getValue("comments");
*/
        VacationGraphic vacationGraphic = vacationGraphicDc.getItem();
        // we check if the vacation request already has requestNumber or not
        boolean is_vacation_req_set = vacationGraphic.getRequestNumber() instanceof  Long;
            if(!is_vacation_req_set){
                vacationGraphic.setRequestNumber(employeeNumberService.generateNextRequestNumber());
            }

      /*  vacationGraphic.setRequestNumber(employeeNumberService.generateNextRequestNumber());
        vacationGraphic.setComments(comments);
        vacationGraphic.setName(name);
        vacationGraphic.setStartDate(vacationGraphicDc.getItem().getValue("startDate"));
        vacationGraphic.setEndDate(vacationGraphicDc.getItem().getValue("endDate"));
        vacationGraphic.setSurname(surname);
        vacationGraphic.setMiddlename(middlename);
        vacationGraphic.setDivision(division);
        vacationGraphic.setDuty(duty);*/
        vacationGraphicDc.setItem(vacationGraphic);
    //    notifications.create().withCaption(middlename).show();
        closeWithCommit();



    }

}