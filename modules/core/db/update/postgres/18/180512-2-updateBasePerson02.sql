alter table BASE_PERSON add constraint FK_BASE_PERSON_NONRESIDENT_TYPE foreign key (NONRESIDENT_TYPE_ID) references TSADV_DIC_NONRESIDENT_TYPE(ID);
create index IDX_BASE_PERSON_NONRESIDENT_TYPE on BASE_PERSON (NONRESIDENT_TYPE_ID);
