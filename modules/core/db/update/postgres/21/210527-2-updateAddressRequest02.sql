alter table TSADV_ADDRESS_REQUEST add constraint FK_TSADV_ADDRESS_REQUEST_STREET_TYPE foreign key (STREET_TYPE_ID) references TSADV_DIC_STREET_TYPE(ID);
create index IDX_TSADV_ADDRESS_REQUEST_STREET_TYPE on TSADV_ADDRESS_REQUEST (STREET_TYPE_ID);
