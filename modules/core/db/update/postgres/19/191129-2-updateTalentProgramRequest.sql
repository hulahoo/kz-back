alter table TSADV_TALENT_PROGRAM_REQUEST add column if not exists CURRENT_STEP_ID uuid ;
alter table TSADV_TALENT_PROGRAM_REQUEST add column if not exists CURRENT_STEP_STATUS_ID uuid ;
