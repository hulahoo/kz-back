alter table TSADV_ACCIDENT_PERSON_TYPE add constraint FK_TSADV_ACCIDENT_PERSON_TYPE_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_ACCIDENT_PERSON_TYPE_COMPANY on TSADV_ACCIDENT_PERSON_TYPE (COMPANY_ID);