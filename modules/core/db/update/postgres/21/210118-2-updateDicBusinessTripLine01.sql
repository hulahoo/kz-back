alter table TSADV_DIC_BUSINESS_TRIP_LINE add constraint FK_TSADV_DIC_BUSINESS_TRIP_LINE_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_BUSINESS_TRIP_LINE_COMPANY on TSADV_DIC_BUSINESS_TRIP_LINE (COMPANY_ID);