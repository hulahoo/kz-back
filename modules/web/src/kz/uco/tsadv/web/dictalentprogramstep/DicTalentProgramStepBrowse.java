package kz.uco.tsadv.web.dictalentprogramstep;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramStep;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class DicTalentProgramStepBrowse extends AbstractLookup {

   @Inject
   protected GroupDatasource<DicTalentProgramStep, UUID> dicTalentProgramStepsDs;

   @Override
   public void init(Map<String, Object> params) {
      if (params.containsKey("fromTalentProgram")) {
         String query = String.format("select e from tsadv$DicTalentProgramStep e " +
                 " where (e.id not in (select a.step.id from tsadv$TalentProgramStep a " +
                 " where a.talentProgram.id='%s' )) ", (UUID) params.get("fromTalentProgram"));
         if (params.containsKey("existingSteps")){
            query = query.concat(String.format(" and (e.id not in %s)",params.get("existingSteps")));
         }
         dicTalentProgramStepsDs.setQuery(query);
      }

      super.init(params);
   }
}
