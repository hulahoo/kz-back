package kz.uco.tsadv.web.modules.recognition.entity.awardprogram;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.tsadv.modules.recognition.AwardProgram;
import kz.uco.tsadv.modules.recognition.pojo.AwardProgramPerson;
import kz.uco.tsadv.service.RecognitionService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
public class AwardProgramPersonsDs extends CustomCollectionDatasource<AwardProgramPerson, UUID> {

    protected RecognitionService recognitionService = AppBeans.get(RecognitionService.class);

    protected AwardProgram awardProgram;

    @Override
    protected Collection<AwardProgramPerson> getEntities(Map<String, Object> params) {
        if (params.containsKey("awardProgram") && params.containsKey("filter")) {
            awardProgram = (AwardProgram) params.get("awardProgram");
            return recognitionService.loadAwardProgramPersonsWithFilter(awardProgram, firstResult, maxResults, (String) params.get("filter"));
        }else if (params.containsKey("awardProgram")) {
            awardProgram = (AwardProgram) params.get("awardProgram");
            return recognitionService.loadAwardProgramPersons(awardProgram, firstResult, maxResults);
        }
        return Collections.emptyList();
    }

    @Override
    public int getCount() {
        return recognitionService.getAwardProgramPersonsCount(awardProgram);
    }
}
