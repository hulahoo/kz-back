alter table TSADV_DIC_PROTECTION_EQUIPMENT_TYPE add constraint FK_TSADV_DIC_PROTECTION_EQUIPMENT_TYPE_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_TYPE_COMPANY on TSADV_DIC_PROTECTION_EQUIPMENT_TYPE (COMPANY_ID);
