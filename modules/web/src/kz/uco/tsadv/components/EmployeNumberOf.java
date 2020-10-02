package kz.uco.tsadv.components;

import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class EmployeNumberOf implements Consumer<PersonExt>{

    @Inject
    private CommonService commonService;

    @Override
    public void accept(final PersonExt personExt) {
        Optional.ofNullable(commonService.emNativeQuerySingleResult(
                Integer.class,
                String.join(
                        " ",
                        "select  REGEXP_REPLACE(COALESCE(employee_number, '0'), '[^0-9]*' ,'0')::integer as number  from base_person  ",
                        "where employee_number is not null and",
                        "textregexeq(employee_number,'^[[:digit:]]+(\\\\.[[:digit:]]+)?$')",
                        "order by number desc limit 1"
                ),
                Collections.emptyMap()
                )
        ).map(number-> String.valueOf(number+1))
         .ifPresent(personExt::setEmployeeNumber);

    }
}
