package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonConfig;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

@Service(NationalIdentifierService.NAME)
public class NationalIdentifierServiceBean implements NationalIdentifierService {

    @Inject
    protected CommonService commonService;

    protected CommonConfig nationalIdentifierCheckConfig;

    public boolean checkNationalIdentifier(String nationalIdentifier, Date dateOfBirth, DicSex dicSex) {
        Configuration configuration = AppBeans.get(Configuration.class);
        nationalIdentifierCheckConfig = configuration.getConfig(CommonConfig.class);
        if (!nationalIdentifierCheckConfig.getNationalIdentifierCheckEnabled()) {
            return true;
        }

        if (nationalIdentifier.equals("000000000000")) {
            return true;
        }

        if (nationalIdentifier.length() != 12) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        if (!nationalIdentifier.substring(0, 6).equals(dateFormat.format(dateOfBirth))) {
            return false;
        }

        int sex = parseInt(nationalIdentifier.substring(6, 7));
        String sexCode;
        if (((sex % 2) == 1)) {
            sexCode = "M";
        } else
            sexCode = "F";
        if (!dicSex.getCode().equals(sexCode)) {
            return false;
        }

        int[] b1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        int[] b2 = {3, 4, 5, 6, 7, 8, 9, 10, 11, 1, 2};
        int[] a = new int[12];
        int controll = 0;
        for (int i = 0; i < 12; i++) {
            a[i] = parseInt(nationalIdentifier.substring(i, i + 1));
            if (i < 11)
                controll += a[i] * b1[i];
        }
        controll = controll % 11;
        if (controll == 10) {
            controll = 0;
            for (int i = 0; i < 11; i++) {
                controll += a[i] * b2[i];
                controll = controll % 11;
            }
        }
        return controll == a[11];
    }

    @Override
    public boolean hasDuplicate(String nationalIdentifier) {
        if (nationalIdentifier.equals("000000000000")) {
            return false;
        }
        Long count = commonService.getCount(PersonExt.class,
                "select e from base$PersonExt e where e.nationalIdentifier = :nationalidentifier",
                ParamsMap.of("nationalidentifier", nationalIdentifier));
        return count > 0;
    }

    @Override
    public boolean hasDuplicate(PersonExt personExt) {
        String nationalIdentifier = personExt != null ? personExt.getNationalIdentifier() : null;
        if (nationalIdentifier != null) {
            if (nationalIdentifierCheckConfig.getNationalIdentifierCheckEnabled()) {
                if (nationalIdentifier.equals("000000000000")) {
                    return false;
                }
                Map map = new HashMap();
                map.put("nationalIdentifier", nationalIdentifier);
                map.put("personExtId", personExt != null ? personExt.getId() : null);
                map.put("personGroupId", personExt != null ?(personExt.getGroup()!=null?personExt.getGroup().getId(): null):null);
                Long count = commonService.getCount(PersonExt.class,
                        "select e from base$PersonExt e where e.nationalIdentifier = :nationalIdentifier" +
                                "   and e.id <> :personExtId and e.group.id <> :personGroupId",
                        map);
                return count > 0;
            }
        }
        return false;
    }
}