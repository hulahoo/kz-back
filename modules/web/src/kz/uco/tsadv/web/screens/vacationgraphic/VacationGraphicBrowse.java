package kz.uco.tsadv.web.screens.vacationgraphic;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.VacationGraphic;
import kz.uco.tsadv.service.EmployeeNumberService;

import javax.inject.Inject;

@UiController("tsadv_VacationGraphic.browse")
@UiDescriptor("vacation-graphic-browse.xml")
@LookupComponent("vacationGraphicsTable")
@LoadDataBeforeShow
public class VacationGraphicBrowse extends StandardLookup<VacationGraphic> {


    @Inject
    private EmployeeNumberService employeeNumberService;

    public void as(){
 //       employeeNumberService.generateNextRequestNumber(); this function generates next number ;
    }
}