alter table TSADV_TALENT_PROGRAM_EXCEPTIONS rename column assignment_group_id to assignment_group_id__u78656 ;
drop index IDX_TSADV_TALENT_PROGRAM_EXCEPTIONS_ASSIGNMENT_GROUP ;
alter table TSADV_TALENT_PROGRAM_EXCEPTIONS drop constraint FK_TSADV_TALENT_PROGRAM_EXCEPTIONS_ASSIGNMENT_GROUP ;
alter table TSADV_TALENT_PROGRAM_EXCEPTIONS add column PERSON_GROUP_ID uuid ;
