alter table TSADV_ADDRESS add constraint FK_TSADV_ADDRESS_CITY foreign key (CITY_ID) references BASE_DIC_CITY(ID);
create index IDX_TSADV_ADDRESS_CITY on TSADV_ADDRESS (CITY_ID);
