alter table TSADV_PERSON_LEARNING_HISTORY add column INCOMPLETE boolean ^
update TSADV_PERSON_LEARNING_HISTORY set INCOMPLETE = false where INCOMPLETE is null ;
alter table TSADV_PERSON_LEARNING_HISTORY alter column INCOMPLETE set not null ;
alter table TSADV_PERSON_LEARNING_HISTORY add column BUDGET_ID uuid ;
