alter table TSADV_ADDRESS add constraint FK_TSADV_ADDRESS_KATO foreign key (KATO_ID) references TSADV_DIC_KATO(ID);
create index IDX_TSADV_ADDRESS_KATO on TSADV_ADDRESS (KATO_ID);
