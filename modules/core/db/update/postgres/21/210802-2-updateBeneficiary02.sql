alter table TSADV_BENEFICIARY add constraint FK_TSADV_BENEFICIARY_ADDRESS_TYPE foreign key (ADDRESS_TYPE_ID) references TSADV_DIC_ADDRESS_TYPE(ID);
create index IDX_TSADV_BENEFICIARY_ADDRESS_TYPE on TSADV_BENEFICIARY (ADDRESS_TYPE_ID);