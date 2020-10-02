package kz.uco.tsadv.web.talentprogram;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.*;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramSkill;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramStep;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.inject.Inject;
import java.util.*;

public class TalentProgramEdit extends AbstractEditor<TalentProgram> {

   @Inject
   protected TabSheet tabSheet;
   @Inject
   protected FieldGroup talentProgramFieldGroup;
   @Inject
   protected GroupTable<TalentProgramGrade> talentProgramGradesTable;

   @Inject
   protected GroupTable<TalentProgramStepSkill> talentProgramStepSkillTable;
   @Inject
   protected GroupDatasource<TalentProgram, UUID> activeTalentProgramsDs;
   @Inject
   protected Button addSkilButton;
   @Inject
   protected Button removeSkillButton;
   @Inject
   protected GroupDatasource<TalentProgramStep, UUID> talentProgramStepsDs;
   @Inject
   protected GroupDatasource<TalentProgramGrade, UUID> talentProgramGradesDs;
   @Inject
   private GroupDatasource<TalentProgramExceptions, UUID> talentProgramExceptionsesDs;
   @Inject
   protected Metadata metadata;
   @Inject
   protected GroupTable<TalentProgramStep> talentProgramStepsTable;
   @Inject
   protected GroupDatasource<TalentProgramStepSkill, UUID> talentProgramStepSkillsDs;
   @Inject
   protected DataManager dataManager;
   @Inject
   protected Button removeGradeButton;
   @Inject
   protected Button removeStepButton;
   protected CommitContext commitContext = new CommitContext();
   protected Map<TalentProgramStep, List<TalentProgramStepSkill>> stepSkillMap = new HashMap<>();
   protected List<TalentProgramGrade> gradeList = new ArrayList<>();
   protected List<TalentProgramStep> stepList = new ArrayList<>();
   protected List<TalentProgramExceptions> exceptionsList = new ArrayList<>();
   @Inject
   private GroupTable<TalentProgramExceptions> talentProgramExceptionsTable;

   @Override
   public void ready() {
      if (PersistenceHelper.isNew(getItem())) {
         tabSheet.getTab("tab_1").setVisible(false);
         tabSheet.getTab("tab_2").setVisible(false);
         getItem().setIsActive(false);
      }
      activeTalentProgramsDs.refresh();
      if (activeTalentProgramsDs.getItems().size() != 0 && !getItem().getIsActive()) {
         ((CheckBox) talentProgramFieldGroup.getFieldNN("isActiveField").getComponentNN()).setValue(false);
         ((CheckBox) talentProgramFieldGroup.getFieldNN("isActiveField").getComponentNN()).setEnabled(false);
         // showNotification(getMessage("activeProgramExist"));
      };
      addSkilButton.setEnabled(false);
      removeGradeButton.setEnabled(false);
      removeStepButton.setEnabled(false);
      removeSkillButton.setEnabled(false);
      talentProgramStepsDs.addItemChangeListener(e -> {
         if (e.getItem() == null) {
            removeStepButton.setEnabled(false);
            return;
         }
         removeStepButton.setEnabled(true);
         //todo
         if (PersistenceHelper.isNew(e.getItem())) {
            try {
               stepSkillMap.get(e.getItem()).forEach(a -> {
                  if (a == null) return;
                  talentProgramStepSkillsDs.addItem(a);
               });
            } catch (NullPointerException q) {
            }
         }
         if (e != null) {
            addSkilButton.setEnabled(true);
            return;
         }
         addSkilButton.setEnabled(false);
      });
      talentProgramGradesDs.addItemChangeListener(e -> {
         if (e.getItem() == null) {
            removeGradeButton.setEnabled(false);
            return;
         }
         removeGradeButton.setEnabled(true);
         if (PersistenceHelper.isNew(e.getItem())) {
            try {
               gradeList.forEach(a -> {
                  if (a == null) return;
                  talentProgramGradesDs.addItem(a);
               });
            } catch (NullPointerException q) {
            }
         }
      });
      talentProgramStepSkillsDs.addItemChangeListener(e->{
         if (e.getItem() == null) {
            removeSkillButton.setEnabled(false);
            return;
         }
         removeSkillButton.setEnabled(true);
      });
      super.ready();
   }

   @Override
   protected boolean preCommit() {
      //todo
      stepSkillMap.keySet().forEach(e -> {
         stepSkillMap.get(e).forEach(a -> {
            if (talentProgramStepSkillsDs.containsItem(a.getId()))
               return;
            commitContext.addInstanceToCommit(a);
         });
      });
      return super.preCommit();
   }

   @Override
   protected boolean postCommit(boolean committed, boolean close) {
      //todo use getDsContext().addBeforeCommitListener(commitContext -> ...);
      dataManager.commit(commitContext);
      return super.postCommit(committed, close);
   }

   public void addGrade() {
      Map<String, Object> params = new HashMap<>();
      params.put("fromTalentProgram", getItem().getId());
      if (gradeList.size() != 0) {
         StringBuilder builder = new StringBuilder(" (");
         builder.append("'").append(gradeList.get(0).getId()).append("'");
         gradeList.forEach(e -> builder.append(",'").append(e.getGradeGroup().getId()).append("'"));
         builder.append(") ");
         params.put("existingGrades", builder.toString());
      }
      openLookup("tsadv$GradeGroup.browse", items -> {
         items.forEach(e -> {
            TalentProgramGrade grade = metadata.create(TalentProgramGrade.class);
            grade.setTalentProgram(getItem());
            grade.setGradeGroup((GradeGroup) e);
            gradeList.add(grade);
         });
         gradeList.forEach(e -> talentProgramGradesDs.addItem(e));
      }, WindowManager.OpenType.THIS_TAB, params);

   }

   public void addException() {
      Map<String, Object> params = new HashMap<>();
      params.put("fromTalentProgram", getItem().getId());
      if (exceptionsList.size() != 0) {
         StringBuilder builder = new StringBuilder(" (");
         builder.append("'").append(exceptionsList.get(0).getId()).append("'");
         exceptionsList.forEach(e -> builder.append(",'").append(e.getPersonGroup().getId()).append("'"));
         builder.append(") ");
         params.put("existingExceptions", builder.toString());
      }
      openLookup("base$PersonGroup.browse", items -> {
         items.forEach(e -> {
            TalentProgramExceptions exceptions = metadata.create(TalentProgramExceptions.class);
            exceptions.setTalentProgram(getItem());
            exceptions.setPersonGroup((PersonGroupExt) e);
            exceptionsList.add(exceptions);
         });
         exceptionsList.forEach(e -> talentProgramExceptionsesDs.addItem(e));
      }, WindowManager.OpenType.THIS_TAB, params);

   }


   public void addSteps() {
      Map<String, Object> params = new HashMap<>();
      params.put("fromTalentProgram", getItem().getId());
      if (stepList.size() != 0) {
         StringBuilder builder = new StringBuilder(" (");
         builder.append("'").append(stepList.get(0).getId()).append("'");
         stepList.forEach(e -> builder.append(",'").append(e.getStep().getId()).append("'"));
         builder.append(") ");
         params.put("existingSteps", builder.toString());
      }
      openLookup("tsadv$DicTalentProgramStep.browse", items -> {
         items.forEach(e -> {
            TalentProgramStep step = metadata.create(TalentProgramStep.class);
            step.setTalentProgram(getItem());
            step.setStep((DicTalentProgramStep) e);
            step.setOrderNum(talentProgramStepsDs.getItems().size() + 1);
            talentProgramStepsDs.addItem(step);
            stepList.add(step);
         });
      }, WindowManager.OpenType.THIS_TAB, params);
   }

   public void addSkill() {
      if (PersistenceHelper.isNew(talentProgramStepsTable.getSingleSelected())) {
         addSkillToNewStep();
         return;
      }
      addSkillToOldStep();
   }

   private void addSkillToNewStep() {
      Map<String, Object> params = new HashMap<>();
      params.put("fromTalentProgram", talentProgramStepsTable.getSingleSelected().getId());
      openLookup("tsadv$DicTalentProgramSkill.browse", items -> {
         List<TalentProgramStepSkill> stepSkillsList = new ArrayList<>();
         items.forEach(e -> {
            TalentProgramStepSkill stepSkill = metadata.create(TalentProgramStepSkill.class);
            TalentProgramStep step = talentProgramStepsTable.getSingleSelected();
            stepSkill.setTalentProgramStep(step);
            stepSkill.setSkill((DicTalentProgramSkill) e);
            stepSkill.setOrderNumber(talentProgramStepSkillsDs.getItems().size() + 1);
            stepSkillsList.add(stepSkill);
         });
         stepSkillMap.put(talentProgramStepsTable.getSingleSelected(), stepSkillsList);
         stepSkillsList.forEach(e -> {
            talentProgramStepSkillsDs.addItem(e);
         });
      }, WindowManager.OpenType.THIS_TAB, params);

   }

   public void addSkillToOldStep() {
      Map<String, Object> params = new HashMap<>();
      params.put("fromTalentProgram", talentProgramStepsTable.getSingleSelected().getId());
      openLookup("tsadv$DicTalentProgramSkill.browse", items -> {
         items.forEach(e -> {
            TalentProgramStepSkill stepSkill = metadata.create(TalentProgramStepSkill.class);
            TalentProgramStep step = talentProgramStepsTable.getSingleSelected();
            stepSkill.setTalentProgramStep(step);
            stepSkill.setSkill((DicTalentProgramSkill) e);
            stepSkill.setOrderNumber(talentProgramStepSkillsDs.getItems().size() + 1);
            talentProgramStepSkillsDs.addItem(stepSkill);
         });
         talentProgramStepSkillsDs.commit();
      }, WindowManager.OpenType.THIS_TAB, params);
   }


   public void removeGrade() {
      TalentProgramGrade grade = talentProgramGradesTable.getSingleSelected();
      gradeList.remove(grade);
      talentProgramGradesDs.removeItem(grade);
   }

   public void removeExceptions() {
      TalentProgramExceptions exceptions = talentProgramExceptionsTable.getSingleSelected();
      exceptionsList.remove(exceptions);
      talentProgramExceptionsesDs.removeItem(exceptions);
   }

   public void removeStep() {
      TalentProgramStep step = talentProgramStepsTable.getSingleSelected();
      stepList.remove(step);
      talentProgramStepsDs.removeItem(step);
   }

   public void removeSkill() {
      TalentProgramStep step = talentProgramStepsTable.getSingleSelected();
      TalentProgramStepSkill skill = talentProgramStepSkillTable.getSingleSelected();
      stepSkillMap.get(step).remove(skill);
      talentProgramStepSkillsDs.removeItem(skill);
   }
}
