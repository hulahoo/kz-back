alter table BASE_PERSON add constraint FK_BASE_PERSON_PREV_JOB_OBLIGATION foreign key (PREV_JOB_OBLIGATION_ID) references TSADV_DIC_PREV_JOB_OBLIGATION(ID);
create index IDX_BASE_PERSON_PREV_JOB_OBLIGATION on BASE_PERSON (PREV_JOB_OBLIGATION_ID);
