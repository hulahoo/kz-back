create unique index if not exists IDX_TSADV_TALENT_PROGRAM_REQUEST_UK_TALENT_PROGRAM_ID on TSADV_TALENT_PROGRAM_REQUEST (TALENT_PROGRAM_ID,PERSON_GROUP_ID) where DELETE_TS is null ;
