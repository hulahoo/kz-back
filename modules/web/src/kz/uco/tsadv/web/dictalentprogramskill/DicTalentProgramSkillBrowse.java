package kz.uco.tsadv.web.dictalentprogramskill;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramSkill;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class DicTalentProgramSkillBrowse extends AbstractLookup {

   @Inject
   protected GroupDatasource<DicTalentProgramSkill, UUID> dicTalentProgramSkillsDs;

   @Override
   public void init(Map<String, Object> params) {
      if (params.containsKey("fromTalentProgram")) {
         dicTalentProgramSkillsDs.setQuery(String.format("select e from tsadv$DicTalentProgramSkill e " +
                 " where e.id not in (select a.skill.id from tsadv$TalentProgramStepSkill a " +
                 " where a.talentProgramStep.id='%s' )", (UUID) params.get("fromTalentProgram")));
      }

      super.init(params);
   }
}
